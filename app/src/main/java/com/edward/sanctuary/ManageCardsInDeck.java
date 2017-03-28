package com.edward.sanctuary;

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
import android.view.View;

import java.util.List;

import static com.edward.sanctuary.Card.createList;

public class ManageCardsInDeck extends AppCompatActivity {
    private Card card;
    private List<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
        }

        card = (Card)getIntent().getSerializableExtra("Card");
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
                Intent intent = new Intent(ManageCardsInDeck.this, SelectCardsForDeck.class);
                intent.putExtra("Card", card);
                startActivity(intent);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab4);
        fab2.setOnClickListener(new View.OnClickListener() {
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
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cards in " + card.getCard_name());

        RecyclerView recList = (RecyclerView) findViewById(R.id.manageCardList);
        cards = createList(20);
        final CardAdapter ca = new CardAdapter(cards, this);
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

                        cards.remove(cards.size() - 1);
                        ca.notifyItemRemoved(cards.size());

                        Card c = new Card();
                        c.setCard_name("Loaded Card");
                        cards.add(c);
                        ca.notifyItemInserted(cards.size() - 1);
                        ca.setIsLoading(false);
                    }
                };
                handler.postDelayed(r,2000);
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


    }

}
