package com.edward.sanctuary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

public class AddCardDirectDeck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Card card = (Card)bundle.getSerializable("DeckCard");

        super.onCreate(savedInstanceState);
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
        }
        else{
            this.setTheme(R.style.Light_Actionbar2);
        }
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_add_card);
        setTitle("Add Card");
        Button add = (Button)findViewById(R.id.addcard);
        final EditText name = (EditText)findViewById(R.id.name);
        final EditText description = (EditText)findViewById(R.id.description);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                if(Database.newCard(AddCardDirectDeck.this, Session.getInstance(AddCardDirectDeck.this).getUserId(), name.getText().toString())){
                    Database.addCard(name.getText().toString(), description.getText().toString(),
                            Session.getInstance(AddCardDirectDeck.this).getUserId(), AddCardDirectDeck.this);
                    Card newCard = Database.getCardByName(name.getText().toString(), AddCardDirectDeck.this);
                    //System.out.println("THIS: " + newCard.getCard_name() + " id:" + newCard.getCard_id());
                    Database.addCardToDeck(card.getCard_id(), newCard.getCard_id(),
                            Session.getInstance(AddCardDirectDeck.this).getUserId(), AddCardDirectDeck.this);
                     Intent intent = getIntent();
                     setResult(766, intent);
                     finish();
                }
                else{
                    name.setError("Duplicate Card Name!");
                    name.requestFocus();
                }
            }
        });
        setupActionBar();
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
