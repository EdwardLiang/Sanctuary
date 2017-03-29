package com.edward.sanctuary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.edward.sanctuary.database.Database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SelectCardsForDeck extends AppCompatActivity {

    private final int CARDS_PER_PAGE = 10;
    private List<Card> cards;
    private Card card;
    private SelectCardsForDeckAdapter ca;
    private int pagesLoaded;
    private boolean end;
    private double seed;
    private int pagesLoadedQuery;
    private boolean querying;
    private String queryText;
    private Lock reloading;
    //private SwipeRefreshLayout swl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_select_cards_for_deck);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(Session.getInstance(this).darkModeSet()){
            this.getSupportActionBar().hide();
        }
        final String prev = getIntent().getStringExtra("Intent");

        pagesLoaded = 1;
        end = false;
        seed = Database.generateSeed();
        pagesLoadedQuery = 1;
        querying = false;
        reloading = new ReentrantLock();

        card = (Card)getIntent().getSerializableExtra("Card");
        setTitle("Select For: " + card.getCard_name());

        HashMap<String, Card> inDeck = Database.getCardsInDeck(this, Session.getInstance(this).getUserId(), card);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Cards in deck updated");
                HashSet<Card> unselected = ca.getNewlyUnselected();
                for(Card b: unselected){
                    Database.deleteCardFromDeck(SelectCardsForDeck.this, card, b);
                }
                List<Card> selected = ca.getSelected();
                ca.clearSelected();
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
                if(prev != null && prev.equals("AddDeck")){
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


        RecyclerView recList = (RecyclerView) findViewById(R.id.cardForDeckList);
        cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        ca = new SelectCardsForDeckAdapter(inDeck, cards, this);
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);

        ca.setOnMoreLoadListener(new OnMoreLoadListener() {
            @Override
            public void onLoadMore() {
                System.out.println("Loading more..");

                Handler handler = new Handler();

                final Runnable r2 = new Runnable(){
                    public void run(){
                        cards.add(null);
                        ca.notifyItemInserted(cards.size() - 1);
                    }
                };
                handler.post(r2);

                final Runnable r = new Runnable() {
                    public void run() {
                        try {
                            reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                            if(cards.size() > 0 && cards.get(cards.size() - 1) == null) {
                                cards.remove(cards.size() - 1);
                                ca.notifyItemRemoved(cards.size());
                            }

                            //cards = Database.getCards(MainActivity.this, Session.getInstance(MainActivity.this).getUserId(), pagesLoaded*CARDS_PER_PAGE);
                            if(querying){
                                pagesLoadedQuery++;
                            }
                            else{
                                pagesLoaded++;
                            }
                            reloadCards();
                            addNoMoreCard();
                            ca.setCardList(cards);
                            ca.notifyDataSetChanged();
                            ca.setIsLoading(false);
                            reloading.unlock();

                        } catch (InterruptedException e) {
                            System.out.println("Could not reload! Lock is bound!");
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(r,1000);
            }
        });

        recList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = llm.getItemCount();
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int visibleThreshold = 5;

                if (!ca.isLoading() && lastVisibleItem >= totalItemCount - 1 && !end) {
                    if (ca.getOnMoreLoadListener() != null) {
                        ca.getOnMoreLoadListener().onLoadMore();
                    }
                    System.out.println("Set is loading true");
                    ca.setIsLoading(true);
                }
            }
        });

        SearchView view = (SearchView)findViewById(R.id.search);
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                    if(newText.equals("")){
                        end = false;
                        querying = false;
                        cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
                        ca.setCardList(cards);
                        ca.notifyDataSetChanged();
                        //swl.setEnabled(true);
                    }
                    else{
                        end = false;
                        querying = true;
                        pagesLoadedQuery = 1;
                        queryText = newText;
                        cards = Database.getCardsSearch(SelectCardsForDeck.this, newText, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
                        ca.setCardList(cards);
                        ca.notifyDataSetChanged();
                        //swl.setEnabled(false);
                    }
                    reloading.unlock();
                    return false;
                } catch (InterruptedException e) {
                    System.out.println("Lock Bound! Cannot load based on query!");
                    e.printStackTrace();
                }
                return false;
            }
        });
       /* swl = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                    pagesLoaded = 1;
                    seed = Database.generateSeed();
                    reloadCards();
                    addNoMoreCard();
                    ca.setCardList(cards);
                    ca.notifyDataSetChanged();
                    swl.setRefreshing(false);
                    reloading.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/


    }

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(SelectCardsForDeck.this, queryText, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    public void addNoMoreCard() {
        int val = 1;
        if(querying){
            val = pagesLoadedQuery;
        }
        else{
            val = pagesLoaded;
        }
        if(cards.size() < val*CARDS_PER_PAGE){
            Card card = new Card();
            card.setCard_name("No More Cards!");
            card.setCard_description("You've reached the end");
            card.setCard_id(-1);
            cards.add(card);
            end = true;
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
        if(ca.getChanged()){
            Context con = this;
            if(Session.getInstance(this).darkModeSet()) {
                con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
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
                                HashSet<Card> unselected = ca.getNewlyUnselected();
                                for(Card b: unselected){
                                    Database.deleteCardFromDeck(SelectCardsForDeck.this, card, b);
                                }
                                List<Card> selected = ca.getSelected();
                                ca.clearSelected();
                                for(Card a: selected){
                                    Database.addCardToDeck(card.getCard_id(), a.getCard_id(), Session.getInstance(SelectCardsForDeck.this).getUserId(), SelectCardsForDeck.this);
                                    System.out.println(card.getCard_name() + " set as not deck and not in drawer");
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
                            setResult(876);

                            finish();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            setResult(876);
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
