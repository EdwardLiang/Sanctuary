package com.edward.sanctuary.cardactivity;

import com.edward.sanctuary.cardadapter.CardAdapterSelect;

/**
 * Created by edward on 3/30/17.
 */

public abstract class CardActivitySelect extends CardActivity {

    @Override
    protected void setCardAdapter(){
        ca = new CardAdapterSelect(cards, this);
    }

    protected CardAdapterSelect getCardAdapterSelect(){
        return (CardAdapterSelect)ca;
    }

    @Override
    protected void queryChange(String newText) {
        ((CardAdapterSelect)ca).clearSelected();
        super.queryChange(newText);
    }
    @Override
    protected void onRefreshCards(){
        ((CardAdapterSelect)ca).clearSelected();
        super.onRefreshCards();
    }
}
