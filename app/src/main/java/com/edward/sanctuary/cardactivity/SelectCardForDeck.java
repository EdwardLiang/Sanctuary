package com.edward.sanctuary.cardactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterForDeck;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

public class SelectCardForDeck extends CardActivity {

    @Override
    protected void doSetContentView() {
        setContentView(R.layout.activity_select_card_for_deck);
    }

    @Override
    protected void setCardAdapter(){
        ca = new CardAdapterForDeck(cards, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Select Base Card");
        setupActionBar();

    }

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(SelectCardForDeck.this, queryText, Session.getInstance(SelectCardForDeck.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(SelectCardForDeck.this, Session.getInstance(SelectCardForDeck.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
