package com.edward.sanctuary.cardadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.cardactivity.MainActivity;
import com.edward.sanctuary.cardactivity.SelectCardForDeckFromMain;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapterForDeckFromMain extends CardAdapter {
    List<Card> forNewDeck;

    public CardAdapterForDeckFromMain(List<Card> cardList, Context context, List<Card> forNewDeck){
        super(cardList, context);
        this.forNewDeck = forNewDeck;
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
////////////////////////////////////

                    int pos = getAdapterPosition();
                    Card card;
                    if(cardList.get(pos).getCard_id() != -1) {
                        //-1 when the card is the "no more cards" card
                        card = cardList.get(pos);

                        for(Card a: forNewDeck){
                            Database.addCardToDeck(card.getCard_id(), a.getCard_id(), Session.getInstance(context).getUserId(), context);
                        }
                        if(Database.getCardsInDeck(context, Session.getInstance(context).getUserId(), card).size() == 0){
                            Database.setIsDeck(card, Session.getInstance(context).getUserId(), false, context);
                            Database.setInDrawer(card, Session.getInstance(context).getUserId(), false, context);
                            System.out.println(card.getCard_name() + " set as not deck and not in drawer");
                        }
                        else {
                            Database.setIsDeck(card, Session.getInstance(context).getUserId(), true, context);
                            System.out.println(card.getCard_name() + " set as deck");
                        }
                   }

                    (((Activity)context)).finish();



//////////////////////////////////////////////////
                }
            });
        }
    }


}
