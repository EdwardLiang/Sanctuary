package com.edward.sanctuary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.edward.sanctuary.database.Database;

import java.util.List;

public class SelectCardsForDeck extends AppCompatActivity {

    private final int CARDS_PER_PAGE = 10;
    private List<Card> cards;
    private CardAdapter ca;
    private int pagesLoaded;
    private boolean end;
    private double seed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cards_for_deck);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String prev = getIntent().getStringExtra("Intent");

        pagesLoaded = 1;
        end = false;
        seed = Database.generateSeed();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TODO: Cards in deck updated");
                if(prev != null && prev.equals("AddDeck")){
                    Intent intent = new Intent(SelectCardsForDeck.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Card card = (Card)getIntent().getSerializableExtra("Card");
        setTitle("Select For: " + card.getCard_name());

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardForDeckList);
        cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        ca = new CardAdapter(cards);
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
                        pagesLoaded++;
                        cards.remove(cards.size() - 1);
                        ca.notifyItemRemoved(cards.size());

                        //cards = Database.getCards(MainActivity.this, Session.getInstance(MainActivity.this).getUserId(), pagesLoaded*CARDS_PER_PAGE);
                        cards = Database.getRandomCards(SelectCardsForDeck.this, Session.getInstance(SelectCardsForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);

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


}
