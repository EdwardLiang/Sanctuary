package com.edward.sanctuary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import android.support.v7.app.ActionBar;
import androidx.appcompat.app.ActionBar;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.edward.sanctuary.settings.Session;

/**
 * Created by edward on 3/31/17.
 */

public class CardDetailActivity extends AppCompatActivity{

    private Card card;
    private boolean editing;

    protected void onCreate(Bundle savedInstanceState) {
        card = (Card)(getIntent().getSerializableExtra("Card"));
        if(Session.getInstance(this).darkModeSet()){
            this.getApplication().setTheme(R.style.Theme_Night_NoActionBar);
            this.setTheme(R.style.Theme_Night_NoActionBar);
        }
        editing = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardDetailFragment frag = (CardDetailFragment) getSupportFragmentManager().
                        findFragmentById(R.id.item_detail_container);
                if(editing == false) {
                    frag.setEditable(true);
                    editing = true;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp, CardDetailActivity.this.getTheme()));
                }
                else if(editing){
                    if(!frag.validTitle()){
                        frag.setTitleError();
                    }
                    else {
                        frag.setEditable(false);
                        frag.saveChangesToDatabase();
                        editing = false;
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp, CardDetailActivity.this.getTheme()));
                    }
                }

                //Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable("Card", card);

            CardDetailFragment fragment = new CardDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }
    public void confirmChangesDialog(){
        final CardDetailFragment frag = (CardDetailFragment) getSupportFragmentManager().
                findFragmentById(R.id.item_detail_container);
        if(frag.getChanged()){
            Context con = this;
            if(Session.getInstance(this).darkModeSet()) {
                //con = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert);
                con = new ContextThemeWrapper(this, com.google.android.material.R.style.Theme_AppCompat_Dialog_Alert);
            }
            new AlertDialog.Builder(con)
                    .setTitle("Save Changes")
                    .setMessage("Do you want to save your changes to the card?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            if(!frag.validTitle()){
                                frag.setTitleError();
                            }
                            else {
                                frag.saveChangesToDatabase();
                                setResult(197);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            setResult(877);
                            finish();
                        }
                    })
                    .show();
        }
        else{
            setResult(197);
            finish();
        }

    }

    @Override
    public void onBackPressed(){
        confirmChangesDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = getIntent();
            confirmChangesDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
