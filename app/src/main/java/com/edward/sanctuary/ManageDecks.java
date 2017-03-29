package com.edward.sanctuary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.SearchView;

import com.edward.sanctuary.database.Database;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManageDecks extends AppCompatActivity {

    List<Card> cards;
    private final int CARDS_PER_PAGE = 10;
    private ManageDeckAdapter ca;
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
        super.onCreate(savedInstanceState);
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
            this.getSupportActionBar().hide();
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pagesLoaded = 1;
        pagesLoadedQuery = 1;
        seed = Database.generateSeed();
        end = false;
        querying = false;
        reloading = new ReentrantLock();

        setContentView(R.layout.activity_manage_decks);
        setTitle("Manage Decks");

        RecyclerView recList = (RecyclerView) findViewById(R.id.manage_deck_list);

        reloadCards();
        addNoMoreCard();
        System.out.println("# of decks: " + cards.size());
        ca = new ManageDeckAdapter(cards, this);
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);

        setupActionBar();

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

                if (!ca.isLoading() && lastVisibleItem >= totalItemCount - 1) {
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
                   //     swl.setEnabled(true);
                    }
                    else{
//                        swl.setEnabled(false);
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
                    ca.clearSelected();
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
            cards = Database.getDecksSearch(this, queryText, Session.getInstance(this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            this.cards = Database.getRandomDecks(this, Session.getInstance(this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
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
    public void onBackPressed(){
        System.out.println("Save stuff for manage decks here");
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

}
