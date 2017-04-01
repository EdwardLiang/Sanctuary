package com.edward.sanctuary.cardadapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.cardactivity.SelectCardsForDeck;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapterForDeck extends CardAdapter {

    public CardAdapterForDeck(List<Card> cardList, Context context){
        super(cardList, context);
    }

    public CardViewHolder createCardViewHolder(View itemView){
        return new CardViewHolderForDeck(itemView);
    }

    public class CardViewHolderForDeck extends CardViewHolder{

        public CardViewHolderForDeck(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() != -1) {
                        //-1 when the card is the "no more cards" card
                        Card card = cardList.get(pos);
                        Intent intent = new Intent(context, SelectCardsForDeck.class);
                        intent.putExtra("Card", (Serializable) card);
                        intent.putExtra("Intent", "AddDeck");
                        context.startActivity(intent);
                    }
                }
            });
        }
    }


}
