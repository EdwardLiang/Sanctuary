package com.edward.sanctuary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static com.edward.sanctuary.Card.createList;

public class SelectCardForDeck extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_card_for_deck);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardForDeckList);
        CardAdapterForDeck ca = new CardAdapterForDeck(createList(30), this);
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);
        setTitle("Select Base Card");
    }

}
