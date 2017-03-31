package com.edward.sanctuary.cardadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardactivity.ManageCardsInDeck;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.HashMap;
import java.util.List;

import static com.edward.sanctuary.R.layout.card_with_switch;

/**
 * Created by edward on 3/19/17.
 */

public class ManageDeckAdapter extends CardAdapterSelect {

    private SparseBooleanArray checkedItems;
    private HashMap<String, Card> inDrawer;
    //private boolean changed;

    public ManageDeckAdapter(List<Card> cardList, Context context){
        super(cardList, context);
        inDrawer = Database.getDrawerDecksMap(context, Session.getInstance(context).getUserId());
        checkedItems = new SparseBooleanArray();
       // changed = false;
    }

    @Override
    protected void onBindCardViewHolderExtras(RecyclerView.ViewHolder cVH, int i){
        CardViewHolderManageDeck cardViewHolder = (CardViewHolderManageDeck)cVH;
        Card ci = cardList.get(i);

        if(inDrawer.containsKey(ci.getCard_name())){
            checkedItems.put(i, true);
        }
        if(checkedItems.get(i, false)){
            cardViewHolder.switch1.setChecked(true);
        }
        else{
            cardViewHolder.switch1.setChecked(false);
        }
        super.onBindCardViewHolderExtras(cardViewHolder, i);
    }


    public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(card_with_switch, viewGroup, false);
        itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
        CardViewHolder cH = createCardViewHolder(itemView);
        return cH;
    }

    public CardViewHolder createCardViewHolder(View itemView){
        return new CardViewHolderManageDeck(itemView);
    }

    public class CardViewHolderManageDeck extends CardViewHolderSelect{
        protected Switch switch1;

        public void onClickNotSelect(int pos){
            Intent intent = new Intent(context, ManageCardsInDeck.class);
            intent.putExtra("Card", cardList.get(pos));
            ((Activity)context).startActivityForResult(intent, 100);
        }

        public CardViewHolderManageDeck(View itemView) {
            super(itemView);
            switch1 = (Switch)itemView.findViewById(R.id.switch1);
            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    System.out.println("switched");
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() == -1){
                        switch1.setChecked(false);
                        return;
                    }
                    if(isChecked) {
                        checkedItems.put(pos, true);
                        Database.setInDrawer(cardList.get(pos), Session.getInstance(context).getUserId(), true, context);
                      //  changed = true;
                    }
                    else{
                        if(checkedItems.get(pos, false)){
                            if(inDrawer.containsKey(cardList.get(pos).getCard_name())){
                                inDrawer.remove(cardList.get(pos).getCard_name());
                                Database.setInDrawer(cardList.get(pos), Session.getInstance(context).getUserId(), false, context);
                         //       changed = true;
                            }
                            checkedItems.delete(pos);
                        }
                    }
                }
            });
        }
    }


}
