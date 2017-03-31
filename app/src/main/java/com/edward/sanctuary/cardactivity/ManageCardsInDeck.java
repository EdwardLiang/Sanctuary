package com.edward.sanctuary.cardactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterSelect;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ManageCardsInDeck extends CardActivitySelect {
    private Card card;

    @Override
    protected void doSetContentView(){
        setContentView(R.layout.activity_manage_cards_in_deck);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Card before super, since loading requires card.
        card = (Card)getIntent().getSerializableExtra("Card");
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Session.getInstance(this).darkModeSet()){
            this.getSupportActionBar().hide();
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCardAdapterSelect().clearSelected();
                getCardAdapterSelect().notifyDataSetChanged();
                Intent intent = new Intent(ManageCardsInDeck.this, SelectCardsForDeck.class);
                intent.putExtra("Card", card);
                startActivityForResult(intent, 765);
            }
        });

        setTitle("Cards in " + card.getCard_name());

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab4);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDialog();
            }
        });
    }

    private void removeDialog() {
        Context con = this;
        if(Session.getInstance(this).darkModeSet()) {
            con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
        }
        new AlertDialog.Builder(con)
                .setTitle("Remove Cards")
                .setMessage("Are you sure you want to remove selected cards from the deck?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                            List<Card> toDelete = ((CardAdapterSelect)ca).getSelected();
                            int count = 0;
                            for(Card a : toDelete){
                                Database.deleteCardFromDeck(ManageCardsInDeck.this, card, a);
                                count++;
                            }
                            if(Database.getCardsInDeck(ManageCardsInDeck.this, Session.getInstance(ManageCardsInDeck.this).getUserId(), card).size() == 0){
                                Database.setIsDeck(card, Session.getInstance(ManageCardsInDeck.this).getUserId(), false, ManageCardsInDeck.this);
                                Database.setInDrawer(card, Session.getInstance(ManageCardsInDeck.this).getUserId(), false, ManageCardsInDeck.this);
                                System.out.println(card.getCard_name() + " set as not deck and not in drawer");
                            }
                            reloadCards();
                            addNoMoreCard();
                            ((CardAdapterSelect)ca).clearSelected();
                            ca.setCardList(cards);
                            ca.notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout), count + " Cards Removed", Snackbar.LENGTH_LONG); // Don’t forget to show!
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

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsInDeckSearch(this, card, queryText, Session.getInstance(this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getCardsInDeckByRandom(this, Session.getInstance(this).getUserId(), card, pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 765 && resultCode == 876){
            try {
                System.out.println("returned from selecting cards changed");
                reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                reloadCards();
                addNoMoreCard();
                ca.setCardList(cards);
                ca.notifyDataSetChanged();
                reloading.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = getIntent();
        setResult(197);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                setResult(197);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
