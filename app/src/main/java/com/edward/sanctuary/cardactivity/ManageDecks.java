package com.edward.sanctuary.cardactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterSelect;
import com.edward.sanctuary.cardadapter.ManageDeckAdapter;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ManageDecks extends CardActivitySelect {

    @Override
    protected void setCardAdapter(){
        ca = new ManageDeckAdapter(cards, this);
    }

    @Override
    protected void doSetContentView() {
        setContentView(R.layout.activity_manage_decks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Decks");
        setupActionBar();
        setOnDeletePressedMessage("Do you want to PERMANENTLY DELETE the selected title card(s) and ALL cards in the deck(s)?");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDialog();
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageDecks.this, SelectCardForDeck.class);
                intent.putExtra("ManageDecks", true);
                startActivityForResult(intent, 180);
            }
        });

    }

    @Override
    public void onDeletePressed(){
        List<Card> toDelete = ((CardAdapterSelect)ca).getSelected();
        int count = 0;
        for(Card a : toDelete){
            List<Card> b = Database.getCardsInDeckList(this, Session.getInstance(this).getUserId(), a);
            for(Card c: b) {
               count += Database.deleteCard(this, c);
            }
            count += Database.deleteCard(this, a);
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

    public void reloadCards(){
        if(querying){
            cards = Database.getDecksSearch(this, queryText, Session.getInstance(this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            this.cards = Database.getRandomDecks(this, Session.getInstance(this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = getIntent();
        setResult(197, intent);
        finish();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                setResult(197, intent);
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            reloading.tryLock(1000, TimeUnit.MILLISECONDS);
            reloadCards();
            addNoMoreCard();
            ca.setCardList(cards);
            getCardAdapterSelect().clearSelected();
            if(am != null) {
                am.finish();
            }
            ca.notifyDataSetChanged();
            reloading.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void removeDialog() {
        Context con = this;
        if(Session.getInstance(this).darkModeSet()) {
            con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
        }
        new AlertDialog.Builder(con)
                .setTitle("Remove Decks")
                .setMessage("Are you sure you want to unbind the selected decks? (Individual cards will still exist)")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            reloading.tryLock(1000, TimeUnit.MILLISECONDS);

                            List<Card> selected = getCardAdapterSelect().getSelected();
                            int count = 0;
                            for(Card c : selected){
                                Database.deleteDeck(ManageDecks.this, Session.getInstance(ManageDecks.this).getUserId(), c);
                                count++;
                            }
                            reloadCards();
                            addNoMoreCard();
                            getCardAdapterSelect().clearSelected();
                            if(am != null) {
                                am.finish();
                            }
                            ca.setCardList(cards);
                            ca.notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), count + " Decks Unbound", Snackbar.LENGTH_LONG); // Don’t forget to show!
                            snackbar.show();
                            reloading.unlock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
