package com.edward.sanctuary;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.edward.sanctuary.database.Database;

import java.util.List;

public class SelectCardForDeck extends AppCompatActivity {

    private final int CARDS_PER_PAGE = 10;
    private List<Card> cards;
    private CardAdapterForDeck ca;
    private int pagesLoaded;
    private boolean end;
    private double seed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_card_for_deck);
        pagesLoaded = 1;
        end = false;
        seed = Database.generateSeed();

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardForDeckList);
        cards = Database.getRandomCards(SelectCardForDeck.this, Session.getInstance(SelectCardForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        ca = new CardAdapterForDeck(cards, this);
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);
        setTitle("Select Base Card");

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
                        pagesLoaded++;
                        cards.remove(cards.size() - 1);
                        ca.notifyItemRemoved(cards.size());

                        //cards = Database.getCards(MainActivity.this, Session.getInstance(MainActivity.this).getUserId(), pagesLoaded*CARDS_PER_PAGE);
                        cards = Database.getRandomCards(SelectCardForDeck.this, Session.getInstance(SelectCardForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);

                        if(cards.size() < pagesLoaded*CARDS_PER_PAGE){
                            Card card = new Card();
                            card.setCard_name("No More Cards!");
                            card.setCard_description("You've reached the end");
                            card.setCard_id(-1);
                            cards.add(card);
                            end = true;
                        }
                        ca.setCardList(cards);
                        ca.notifyDataSetChanged();
                        ca.setIsLoading(false);
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
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
