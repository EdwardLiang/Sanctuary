package com.edward.sanctuary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 * The difference between this and CardAdapterDeck is that cards are selected onClick instead of
 * long press and there are some cards that are pre-selected.
 */

public class SelectCardsForDeckAdapter extends CardAdapterSelect {

    private HashMap<String, Card> alreadyInDeck;
    private HashSet<Card> newlyUnselected;
    private boolean changed;

    public SelectCardsForDeckAdapter(HashMap<String, Card> inDeck, List<Card> cardList, Context context){
        super(cardList, context);
        alreadyInDeck = inDeck;
        this.newlyUnselected = new HashSet<Card>();
        changed = false;
    }
    public HashSet<Card> getNewlyUnselected(){
        return newlyUnselected;
    }
    public boolean getChanged(){
        return changed;
    }

    @Override
    protected void onBindCardViewHolderExtras(RecyclerView.ViewHolder cardViewHolder, int i){
        Card ci = cardList.get(i);
        if(alreadyInDeck.containsKey(ci.getCard_name())){
            selectedItems.put(i, true);
        }
        super.onBindCardViewHolderExtras(cardViewHolder, i);
    }

    @Override
    public CardViewHolder createCardViewHolder(View itemView){
        return new CardViewHolderSelectForDeck(itemView);
    }

    public class CardViewHolderSelectForDeck extends CardViewHolder {

        public CardViewHolderSelectForDeck(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() != -1) {
                        changed = true;
                        //-1 when the card is the "no more cards" card
                        if (v.isSelected()) {
                            System.out.println("unselected");
                            setUnselectedBackgroundColor(v);
                            newlyUnselected.add(cardList.get(pos));
                            if(alreadyInDeck.containsKey(cardList.get(pos).getCard_name())){
                                alreadyInDeck.remove(cardList.get(pos).getCard_name());
                            }
                            toggleSelection(pos);
                            v.setSelected(false);
                        } else {
                            System.out.println("selected");
                            if(newlyUnselected.contains(cardList.get(pos))){
                                newlyUnselected.remove(cardList.get(pos));
                            }
                            setSelectedBackgroundColor(v);
                            toggleSelection(pos);
                            v.setSelected(true);
                        }
                    }
                }
            });
        }
    }


}
