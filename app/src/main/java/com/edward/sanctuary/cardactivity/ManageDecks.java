package com.edward.sanctuary.cardactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.ManageDeckAdapter;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

public class ManageDecks extends CardActivitySelect {

    @Override
    protected void setCardAdapter(){
        ca = new ManageDeckAdapter(cards, this);
    }

    @Override
    protected void doSetContentView() {
        setContentView(R.layout.activity_manage_decks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Session.getInstance(this).darkModeSet()){
            this.getSupportActionBar().hide();
        }
        setTitle("Manage Decks");

        setupActionBar();
    }

    public void reloadCards(){
        if(querying){
            cards = Database.getDecksSearch(this, queryText, Session.getInstance(this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            this.cards = Database.getRandomDecks(this, Session.getInstance(this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = getIntent();
        setResult(197, intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == 197){
            reloadCards();
            addNoMoreCard();
            ca.setCardList(cards);
            ca.notifyDataSetChanged();
        }
    }


}
