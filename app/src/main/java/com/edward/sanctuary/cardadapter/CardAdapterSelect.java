package com.edward.sanctuary.cardadapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardactivity.CardActivitySelect;
import com.edward.sanctuary.settings.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapterSelect extends CardAdapter {

    protected SparseBooleanArray selectedItems;
    protected boolean selecting;
    protected boolean first;


    public CardAdapterSelect(List<Card> cardList, Context context){
        super(cardList, context);
        selectedItems = new SparseBooleanArray();
        selecting = false;
        first = false;
    }
    public List<Card> getSelected(){
        List<Card> cL = new ArrayList<Card>();
        for(int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            cL.add(cardList.get(key));
        }
        return cL;
    }
    public void clearSelected(){
        selectedItems.clear();
        selecting = false;
    }

    public boolean getSelecting(){
        return selecting;
    }

    public void toggleSelection(int pos){
        System.out.println("Card " + pos + " added or removed from selected list");
        if(selectedItems.get(pos, false)){
            selectedItems.delete(pos);
        }
        else{
            selectedItems.put(pos, true);
        }
    }

    @Override
    protected void onBindCardViewHolderExtras(RecyclerView.ViewHolder cardViewHolder, int i){
        if (selectedItems.get(i, false)) {
            cardViewHolder.itemView.setSelected(true);
            setSelectedBackgroundColor(cardViewHolder.itemView);
        } else {
            cardViewHolder.itemView.setSelected(false);
            setUnselectedBackgroundColor(cardViewHolder.itemView);
        }
    }

    @Override
    public CardViewHolder createCardViewHolder(View itemView){
        return new CardViewHolderSelect(itemView);
    }

    protected class CardViewHolderSelect extends CardViewHolder{

        public void onClickNotSelect(int pos){
            //ADD STUFF HERE.
        }

        public CardViewHolderSelect(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (cardList.get(pos).getCard_id() != -1) {
                        if(!v.isSelected() && !selecting) {
                            first = true;
                            selecting = true;
                            System.out.println("selected");
                            Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(20);
                            setSelectedBackgroundColor(v);
                            toggleSelection(pos);
                            ((CardActivitySelect)context).setActionBarSelecting(true);
                            v.setSelected(true);
                        }
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() == -1){
                        //-1 when the card is the "no more cards" card
                        return;
                    }
                    if (selecting & !first) {
                        if (v.isSelected()) {
                            setUnselectedBackgroundColor(v);
                            toggleSelection(pos);
                            v.setSelected(false);
                            if(selectedItems.size() == 0){
                                selecting = false;
                                ((CardActivitySelect)context).setActionBarSelecting(false);
                            }
                        } else {
                            setSelectedBackgroundColor(v);
                            toggleSelection(pos);
                            v.setSelected(true);
                        }
                        ((CardActivitySelect)context).notifyNumSelectedChanged();
                    }
                    else if(!selecting){
                        onClickNotSelect(pos);
                    }
                    first = false;
                }
            });
        }
    }

    protected void setSelectedBackgroundColor(View v) {
        if (Session.getInstance(context).darkModeSet()) {
            v.setBackgroundColor(Color.BLACK);
        } else {
            v.setBackgroundColor(Color.LTGRAY);
        }
    }

    protected void setUnselectedBackgroundColor(View v) {
        if (Session.getInstance(context).darkModeSet()) {
            v.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }
    }


}
