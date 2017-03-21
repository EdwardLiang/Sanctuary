package com.edward.sanctuary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static com.edward.sanctuary.Card.createList;

public class ManageDecks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_decks);
        setTitle("Manage Decks");

        RecyclerView recList = (RecyclerView) findViewById(R.id.manage_deck_list);
        ManageDeckAdapter ca = new ManageDeckAdapter(createList(30));
        recList.setAdapter(ca);

        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setAutoMeasureEnabled(false);
        recList.setLayoutManager(llm);
    }

    @Override
    public void onBackPressed(){
        System.out.println("Save stuff for manage decks here");
        finish();
    }
}
