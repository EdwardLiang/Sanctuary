package com.edward.sanctuary.cardactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.edward.sanctuary.Card;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterForDeckFromMain;
import com.edward.sanctuary.cardadapter.SelectCardsForDeckAdapter;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.List;

public class SelectCardForDeckFromMain extends CardActivity {
    List<Card> selected;

    @Override
    protected void doSetContentView() {
        setContentView(R.layout.activity_select_card_for_deck_main);
    }

    @Override
    protected void setCardAdapter(){

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        selected =
                (List<Card>)bundle.getSerializable("cards");

        ca = new CardAdapterForDeckFromMain(cards, this, selected);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Select Base Card");
        setupActionBar();
    }

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(SelectCardForDeckFromMain.this, queryText, Session.getInstance(SelectCardForDeckFromMain.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(SelectCardForDeckFromMain.this, Session.getInstance(SelectCardForDeckFromMain.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
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
                Intent intent = getIntent();
                setResult(197);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(resultCode == 876){
            setResult(197);
            finish();
        }CardAdapterForDeckFromMain*/

        super.onActivityResult(requestCode, resultCode, data);
    }

}
