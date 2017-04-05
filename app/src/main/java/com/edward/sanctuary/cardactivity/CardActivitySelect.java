package com.edward.sanctuary.cardactivity;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterSelect;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by edward on 3/30/17.
 */

public abstract class CardActivitySelect extends CardActivity {
    protected ActionMode am;
    protected String message;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        message = "Are you sure you want to permanently and completely delete(NOT archive or remove from deck) the selected cards?";
    }

    @Override
    protected void setCardAdapter(){
        ca = new CardAdapterSelect(cards, this);
    }

    protected CardAdapterSelect getCardAdapterSelect(){
        return (CardAdapterSelect)ca;
    }

    public void notifyNumSelectedChanged(){
        if(am != null) {
            am.setTitle(getCardAdapterSelect().getSelected().size() + " Selected");
        }
    }

    @Override
    protected void queryChange(String newText) {
        ((CardAdapterSelect)ca).clearSelected();
        if(am != null) {
            am.finish();
        }
        super.queryChange(newText);
    }
    @Override
    protected void onRefreshCards(){
        ((CardAdapterSelect)ca).clearSelected();
        if(am != null) {
            am.finish();
        }
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

    public void confirmDeleteDialog(){
        Context con = this;
        if(Session.getInstance(this).darkModeSet()) {
            con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
        }
        new AlertDialog.Builder(con)
                .setTitle("Delete Cards")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                            onDeletePressed();
                            reloading.unlock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void setOnDeletePressedMessage(String m){
        message = m;
    }

    public void onDeletePressed(){
        List<Card> toDelete = ((CardAdapterSelect)ca).getSelected();
        int count = 0;
        for(Card a : toDelete){
            Database.deleteCard(CardActivitySelect.this, a);
            count++;
        }
        reloadCards();
        addNoMoreCard();
        getCardAdapterSelect().clearSelected();
        if(am != null){
            am.finish();
        }
        getCardAdapterSelect().setCardList(cards);
        getCardAdapterSelect().notifyDataSetChanged();
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), count + " Cards Deleted", Snackbar.LENGTH_LONG); // Don’t forget to show!
        snackbar.show();
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
            switch(item.getItemId()){
                case R.id.delete:
                    confirmDeleteDialog();
                    break;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getCardAdapterSelect().clearSelected();
            ca.notifyDataSetChanged();
        }
    }

}
