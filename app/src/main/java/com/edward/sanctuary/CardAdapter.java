package com.edward.sanctuary;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    SparseBooleanArray selectedItems;
    private List<Card> cardList;

    public CardAdapter(List<Card> cardList){
        this.cardList = cardList;
        selectedItems = new SparseBooleanArray();
    }

    public void toggleSelection(int pos){
        System.out.println("Card " + pos + " added or removed from selected list");
        if(selectedItems.get(pos, false)){
            selectedItems.delete(pos);
        }
        else{
            selectedItems.put(pos, false);
        }
    }

    @Override
    public int getItemCount(){
        return cardList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        Card ci = cardList.get(i);
        cardViewHolder.vName.setText(ci.getCard_name());
        cardViewHolder.vDescription.setText(ci.getCard_description());
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card, viewGroup, false);
        itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
        CardViewHolder cH = new CardViewHolder(itemView);

        return cH;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vDescription;


        public CardViewHolder(View itemView) {
            super(itemView);
            vName = (TextView)itemView.findViewById(R.id.textView5);
            vDescription = (TextView)itemView.findViewById(R.id.textView6);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(v.isSelected()){
                        System.out.println("unselected");
                        v.setBackgroundColor(Color.WHITE);
                        toggleSelection(pos);
                        v.setSelected(false);
                    }
                    else{
                        System.out.println("selected");
                        v.setBackgroundColor(Color.LTGRAY);
                        toggleSelection(pos);
                        v.setSelected(true);
                    }

                }
            });
        }
    }


}
