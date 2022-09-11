package com.edward.sanctuary.cardactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.SelectCardsForDeckAdapter;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectCardsForDeck extends CardActivitySelect {
    protected Card card;

    @Override
    protected void setCardAdapter(){
        HashMap<String, Card> inDeck = Database.getCardsInDeck(this, Session.getInstance(this).getUserId(), card);
        ca = new SelectCardsForDeckAdapter(inDeck, cards, this);
    }

    @Override
    protected void doSetContentView() {
        setContentView(R.layout.activity_select_cards_for_deck);
    }

    protected SelectCardsForDeckAdapter getSelectCardsForDeckAdapter(){
        return (SelectCardsForDeckAdapter)ca;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Card before super, since loading requires card.
        card = (Card)getIntent().getSerializableExtra("Card");
        setTitle("Select For: " + card.getCard_name());
        super.onCreate(savedInstanceState);

        final String prev = getIntent().getStringExtra("Intent");
        final boolean fromHome = getIntent().getBooleanExtra("FromHome", false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Cards in deck updated");
                HashSet<Card> unselected = ((SelectCardsForDeckAdapter)ca).getNewlyUnselected();
                for(Card b: unselected){
                    Database.deleteCardFromDeck(SelectCardsForDeck.this, card, b);
                }
                List<Card> selected = getCardAdapterSelect().getSelected();
                getSelectCardsForDeckAdapter().clearSelected();

                for(Card a: selected){
                    Database.addCardToDeck(card.getCard_id(), a.getCard_id(), Session.getInstance(SelectCardsForDeck.this).getUserId(), SelectCardsForDeck.this);
                }
                if(Database.getCardsInDeck(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), card).size() == 0){
                    Database.setIsDeck(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), false, SelectCardsForDeck.this);
                    Database.setInDrawer(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), false, SelectCardsForDeck.this);
                    System.out.println(card.getCard_name() + " set as not deck and not in drawer");
                }
                else {
                    Database.setIsDeck(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), true, SelectCardsForDeck.this);
                    System.out.println(card.getCard_name() + " set as deck");
                }
                if(prev != null && prev.equals("AddDeck") && fromHome){
                    Intent intent = new Intent(SelectCardsForDeck.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                Intent intent = getIntent();
                setResult(876);

                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Since this class depends on a card for reloading, cards will not be auto-loaded in super.
        reloadCards();
        addNoMoreCard();
        ca.setCardList(cards);
        ca.notifyDataSetChanged();

    }

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(SelectCardsForDeck.this, queryText, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmChangesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void confirmChangesDialog(){
        if(getSelectCardsForDeckAdapter().getChanged()){
            Context con = this;
            if(Session.getInstance(this).darkModeSet()) {
                //con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
                con = new ContextThemeWrapper(this, com.google.android.material.R.style.Theme_AppCompat_Dialog_Alert);
            }
            new AlertDialog.Builder(con)
                    .setTitle("Save Changes")
                    .setMessage("Do you want to save your changes to the deck?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                                HashSet<Card> unselected = getSelectCardsForDeckAdapter().getNewlyUnselected();
                                for(Card b: unselected){
                                    Database.deleteCardFromDeck(SelectCardsForDeck.this, card, b);
                                }
                                List<Card> selected = getCardAdapterSelect().getSelected();
                                getCardAdapterSelect().clearSelected();
                                for(Card a: selected){
                                    Database.addCardToDeck(card.getCard_id(), a.getCard_id(), Session.getInstance(SelectCardsForDeck.this).getUserId(), SelectCardsForDeck.this);
                                }
                                if(Database.getCardsInDeck(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), card).size() == 0){
                                    Database.setIsDeck(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), false, SelectCardsForDeck.this);
                                    Database.setInDrawer(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), false, SelectCardsForDeck.this);
                                }
                                else {
                                    Database.setIsDeck(card, Session.getInstance(SelectCardsForDeck.this).getUserId(), true, SelectCardsForDeck.this);
                                    System.out.println(card.getCard_name() + " set as deck");
                                }
                                reloading.unlock();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = getIntent();
                            setResult(877);

                            finish();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            setResult(877);
                            finish();
                        }
                    })
                    .show();
        }
        else{
            finish();
        }

    }

    public void onBackPressed(){
        confirmChangesDialog();
    }
}
