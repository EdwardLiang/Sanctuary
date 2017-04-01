package com.edward.sanctuary.cardactivity;


import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterSelect;

/**
 * Created by edward on 3/30/17.
 */

public abstract class CardActivitySelect extends CardActivity {
    protected ActionMode am;

    @Override
    protected void setCardAdapter(){
        ca = new CardAdapterSelect(cards, this);
    }

    protected CardAdapterSelect getCardAdapterSelect(){
        return (CardAdapterSelect)ca;
    }

    public void notifyNumSelectedChanged(){
        am.setTitle(getCardAdapterSelect().getSelected().size() + " Selected");
    }

    @Override
    protected void queryChange(String newText) {
        ((CardAdapterSelect)ca).clearSelected();
        am.finish();
        super.queryChange(newText);
    }
    @Override
    protected void onRefreshCards(){
        ((CardAdapterSelect)ca).clearSelected();
        am.finish();
        super.onRefreshCards();
    }

    public void setActionBarSelecting(boolean selecting){
        if(selecting){
            am = startSupportActionMode(new ActionBarSelect());
        }
        else{
            if(am != null){
                am.finish();
            }
        }
    }

    public class ActionBarSelect implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.select_actionbar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(getCardAdapterSelect().getSelected().size() + " Selected");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getCardAdapterSelect().clearSelected();
            ca.notifyDataSetChanged();
        }
    }

}
