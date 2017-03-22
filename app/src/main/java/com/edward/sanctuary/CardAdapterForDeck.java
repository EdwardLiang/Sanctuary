package com.edward.sanctuary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapterForDeck extends RecyclerView.Adapter<CardAdapterForDeck.CardViewHolder> {

    private List<Card> cardList;
    private Context context;

    public CardAdapterForDeck(List<Card> cardList, Context context){
        this.cardList = cardList;
        this.context = context;
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
                    Card card = cardList.get(pos);
                    Intent intent = new Intent(context, SelectCardsForDeck.class);
                    intent.putExtra("Card", (Serializable) card);
                    intent.putExtra("Intent", "AddDeck");
                    context.startActivity(intent);
                }
            });
        }
    }


}
