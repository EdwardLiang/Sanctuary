package com.edward.sanctuary.cardactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.edward.sanctuary.AddCard;
import com.edward.sanctuary.Card;
import com.edward.sanctuary.LoginActivity;
import com.edward.sanctuary.R;
import com.edward.sanctuary.cardadapter.CardAdapterSelect;
import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;
import com.edward.sanctuary.settings.SettingsActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends CardActivitySelect
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private List<Card> drawerDecks;

    @Override
    protected void doSetContentView(){
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void darkModeSetup(){
        if(Session.getInstance(this).darkModeSet()){
            this.getApplication().setTheme(R.style.Theme_Night_NoActionBar);
            this.setTheme(R.style.Theme_Night_NoActionBar);
            //toolbar.setPopupTheme(R.style.Night);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Sanctuary");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("Welcome: " + Session.getInstance(this).getUsername());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddCard.class);
                startActivityForResult(intent, 2301);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Cards")
                        .setMessage("Are you sure you want to delete selected cards?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                                    List<Card> toDelete = ((CardAdapterSelect)ca).getSelected();
                                    int count = 0;
                                    for(Card a : toDelete){
                                        Database.deleteCard(MainActivity.this, a);
                                        count++;
                                    }
                                    reloadCards();
                                    addNoMoreCard();
                                    getCardAdapterSelect().clearSelected();
                                    getCardAdapterSelect().setCardList(cards);
                                    getCardAdapterSelect().notifyDataSetChanged();
                                    Snackbar snackbar = Snackbar.make(navigationView, count + " Cards Deleted", Snackbar.LENGTH_LONG); // Don’t forget to show!
                                    snackbar.show();
                                    reloadDecks();
                                    reloading.unlock();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Add decks to drawer
        drawerDecks = Database.getDrawerDecks(this, Session.getInstance(this).getUserId());
        addDecks(drawerDecks);
    }

    public void addDecks(List<Card> decks){
        SubMenu sm = navigationView.getMenu().findItem(R.id.decks).getSubMenu();
        sm.clear();
        for(Card entry : decks) {
            final Card card = entry;
            sm.add(Menu.NONE, 1, Menu.NONE, card.getCard_name()).setIcon(R.drawable.ic_library_books_black_24dp).setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            System.out.println("Deck clicked");
                            Intent intent = new Intent(MainActivity.this, ManageCardsInDeck.class);
                            intent.putExtra("Card", card);
                            startActivityForResult(intent, 130);
                            return false;
                        }
                        //public MenuItem.OnMenuItemClickListener init(Card card){
                       //     this.card = card;
                          //  return this;
                       // }
                    }//.init(entry)
            );
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //SubMenu deckMenu = menu.addSubMenu("Decks");
        /*MenuItem menuItem = menu.findItem(R.id.decks);
        SubMenu sm = menuItem.getSubMenu();
        sm.add(Menu.NONE, 1, Menu.NONE, "Deck Name").setIcon(R.drawable.ic_library_books_black_24dp);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_deck) {
            startActivity(new Intent(this,SelectCardForDeck.class));
        }
        if (id == R.id.nav_manage_decks) {
            Intent intent = new Intent(this,ManageDecks.class);
            startActivityForResult(intent, 211);
        }
        if (id == R.id.logout) {
            if(Database.getSecurityEnabled(Session.getInstance(this).getUserId(),this)) {
                startActivity(new Intent(this, LoginActivity.class));
                Session.destroyInstance();
                finish();
            }
            else{
                Toast.makeText(this, "Security not enabled", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void reloadDecks(){
        drawerDecks = Database.getDrawerDecks(this, Session.getInstance(this).getUserId());
        addDecks(drawerDecks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2301 && resultCode == 188){
            end = false;
            reloadCards();
            addNoMoreCard();
            getCardAdapterSelect().setCardList(cards);
            getCardAdapterSelect().notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(navigationView, "Card Created", Snackbar.LENGTH_LONG); // Don’t forget to show!
            snackbar.show();
        }
        if(resultCode == 197){
            reloadDecks();
        }

    }

}
