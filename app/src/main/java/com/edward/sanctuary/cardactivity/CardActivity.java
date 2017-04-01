package com.edward.sanctuary.cardactivity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.OnMoreLoadListener;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapter;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by edward on 3/30/17.
 */

public abstract class CardActivity extends AppCompatActivity {
    protected List<Card> cards;
    protected int pagesLoaded;
    protected int pagesLoadedQuery;
    protected boolean querying;
    protected String queryText;
    protected boolean end;
    protected double seed;
    protected Lock reloading;
    protected final int CARDS_PER_PAGE = 10;
    protected SwipeRefreshLayout swl;
    protected CardAdapter ca;
    protected LinearLayoutManager llm;
    protected RecyclerView recList;
    protected Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        darkModeSetup();

        super.onCreate(savedInstanceState);
        pagesLoaded = 1;
        pagesLoadedQuery = 1;
        seed = Database.generateSeed();
        end = false;
        reloading = new ReentrantLock();

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);

        doSetContentView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recList = (RecyclerView) findViewById(R.id.cardList);
        reloadCards();
        addNoMoreCard();
        System.out.println("Card reloading and add no more card not completed in super step.");

        setCardAdapter();
        recList.setAdapter(ca);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(llm);

        ca.setOnMoreLoadListener(new InfiniteLoadListener());
        recList.addOnScrollListener(new InfiniteScrollListener());

        SearchView view = (SearchView)findViewById(R.id.search);
        view.setOnQueryTextListener(new QueryCards());

        swl = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swl.setOnRefreshListener(new CardRefresh());
    }

    protected void setCardAdapter(){
        ca = new CardAdapter(cards, this);
    }

    protected void darkModeSetup(){
        if(Session.getInstance(this).darkModeSet()){
            this.getApplication().setTheme(R.style.Theme_Night_NoActionBar);
            this.setTheme(R.style.Theme_Night_NoActionBar);
            //toolbar.setPopupTheme(R.style.Night);
        }
    }

    abstract protected void doSetContentView();

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
    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(this, queryText, Session.getInstance(this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(this, Session.getInstance(this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    public class InfiniteScrollListener extends RecyclerView.OnScrollListener{
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
    }

    public class InfiniteLoadListener implements OnMoreLoadListener {
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

    }
    public class CardRefresh implements SwipeRefreshLayout.OnRefreshListener{
        @Override
        public void onRefresh() {
            try {
                reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                onRefreshCards();
                reloading.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onRefreshCards(){
        pagesLoaded = 1;
        seed = Database.generateSeed();
        reloadCards();
        end = false;
        addNoMoreCard();
        //ca.clearSelected();
        ca.setCardList(cards);
        ca.notifyDataSetChanged();
        swl.setRefreshing(false);
    }

    public class QueryCards implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            try {
                reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                queryChange(newText);
                reloading.unlock();
                return false;
            } catch (InterruptedException e) {
                System.out.println("Lock Bound! Cannot load based on query!");
                e.printStackTrace();
            }
            return false;
        }
    }

    protected void queryChange(String newText){
        //ca.clearSelected();
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

    }

}
