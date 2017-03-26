package com.edward.sanctuary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.edward.sanctuary.database.Database;

public class AddCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Database.addCard(name.getText().toString(), description.getText().toString(),
                        Session.getInstance(AddCard.this).getUserId(), AddCard.this);
                Intent intent = getIntent();
                setResult(188, intent);
                finish();
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
