package com.edward.sanctuary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManageCardsInDeck extends AppCompatActivity {
    private Card card;
    List<Card> cards;
    private final int CARDS_PER_PAGE = 10;
    private CardAdapterSelect ca;
    private int pagesLoaded;
    private int pagesLoadedQuery;
    private boolean querying;
    private String queryText;
    private boolean end;
    private double seed;
    private Lock reloading;
    private SwipeRefreshLayout swl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
        }
        super.onCreate(savedInstanceState);
        pagesLoaded = 1;
        pagesLoadedQuery = 1;
        seed = Database.generateSeed();
        end = false;
        querying = false;
        reloading = new ReentrantLock();

        card = (Card)getIntent().getSerializableExtra("Card");
        reloadCards();
        addNoMoreCard();
        setContentView(R.layout.activity_manage_cards_in_deck);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Session.getInstance(this).darkModeSet()){
            this.getSupportActionBar().hide();
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ca.clearSelected();
                ca.notifyDataSetChanged();
                Intent intent = new Intent(ManageCardsInDeck.this, SelectCardsForDeck.class);
                intent.putExtra("Card", card);
                startActivityForResult(intent, 765);
            }
        });
      /*  fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ManageCardsInDeck.this)
                        .setTitle("Remove Cards")
                        .setMessage("Are you sure you want to remove selected cards from the deck?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: STUFF HERE
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });*/


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cards in " + card.getCard_name());

        RecyclerView recList = (RecyclerView) findViewById(R.id.manageCardList);
        //cards = createList(20);
        ca = new CardAdapterSelect(cards, this);
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab4);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDialog();
            }
        });


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
                //handler.postDelayed(r,1000);
                handler.post(r);
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
                    ca.clearSelected();
                    if(newText.equals("")){
                        end = false;
                        querying = false;
                        swl.setEnabled(true);
                    }
                    else{
                        swl.setEnabled(false);
                        end = false;
                        querying = true;
                        pagesLoadedQuery = 1;
                        queryText = newText;
                    }
                    reloadCards();
                    addNoMoreCard();
                    ca.setCardList(cards);
                    ca.notifyDataSetChanged();
                    reloading.unlock();
                    return false;
                } catch (InterruptedException e) {
                    System.out.println("Lock Bound! Cannot load based on query!");
                    e.printStackTrace();
                }
                return false;
            }
        });


        swl = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                    pagesLoaded = 1;
                    seed = Database.generateSeed();
                    reloadCards();
                    end = false;
                    addNoMoreCard();
                    ca.clearSelected();
                    ca.setCardList(cards);
                    ca.notifyDataSetChanged();
                    swl.setRefreshing(false);
                    reloading.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                            List<Card> toDelete = ca.getSelected();
                            int count = 0;
                            for(Card a : toDelete){
                                Database.deleteCardFromDeck(ManageCardsInDeck.this, card, a);
                                count++;
                            }
                            reloadCards();
                            addNoMoreCard();
                            ca.clearSelected();
                            ca.setCardList(cards);
                            ca.notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout), count + " Cards Deleted", Snackbar.LENGTH_LONG); // Don’t forget to show!
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

    public void addNoMoreCard(){
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //removeDialog();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
