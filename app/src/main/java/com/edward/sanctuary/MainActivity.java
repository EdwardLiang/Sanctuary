package com.edward.sanctuary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.edward.sanctuary.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int CARDS_PER_PAGE = 10;

    private NavigationView navigationView;
    private List<Card> cards;
    private CardAdapter ca;
    private int pagesLoaded;
    private int pagesLoadedQuery;
    private boolean querying;
    private String queryText;
    private boolean end;
    private double seed;
    private Lock reloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagesLoaded = 1;
        pagesLoadedQuery = 1;
        seed = Database.generateSeed();
        end = false;
        setTitle("Sanctuary");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reloading = new ReentrantLock();

        System.out.println("Welcome: " + Session.getInstance(this).getUsername());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), AddCard.class);
                startActivityForResult(intent, 2301);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Cards")
                        .setMessage("Are you sure you want to delete selected cards?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                                    List<Card> toDelete = ca.getSelected();
                                    int count = 0;
                                    for(Card a : toDelete){
                                        Database.deleteCard(MainActivity.this, a);
                                        count++;
                                    }
                                    reloadCards();
                                    addNoMoreCard();
                                    ca.clearSelected();
                                    ca.setCardList(cards);
                                    ca.notifyDataSetChanged();
                                    Snackbar snackbar = Snackbar.make(navigationView, count + " Cards Deleted", Snackbar.LENGTH_LONG); // Don’t forget to show!
                                    snackbar.show();
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

        //TEMPORARY: ADD decks for sidebar here
        List<Card> decks = new ArrayList<Card>();

        Card card = new Card();
        card.setCard_name("Deck 1");
        Card card2 = new Card();
        card2.setCard_name("Deck 2");
        Card card3 = new Card();
        card3.setCard_name("Deck 3");
        decks.add(card);
        decks.add(card2);
        decks.add(card3);

        addDecks(decks);
        //TEMPORARY: ADD decks for sidebar here

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        reloadCards();
        addNoMoreCard();

        //cards = Database.getCardsSearch(MainActivity.this, "N", Session.getInstance(MainActivity.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);

        ca = new CardAdapter(cards);
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
                        try {
                            reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                            if(cards.size() > 0 && cards.get(cards.size() - 1) == null) {
                                cards.remove(cards.size() - 1);
                                ca.notifyItemRemoved(cards.size());
                            }

                            //cards = Database.getCards(MainActivity.this, Session.getInstance(MainActivity.this).getUserId(), pagesLoaded*CARDS_PER_PAGE);
                            if(querying){
                                pagesLoadedQuery++;
                            }
                            else{
                                pagesLoaded++;
                            }
                            reloadCards();

                            addNoMoreCard();

                            ca.setCardList(cards);
                            ca.notifyDataSetChanged();
                            ca.setIsLoading(false);
                            reloading.unlock();

                        } catch (InterruptedException e) {
                            System.out.println("Could not reload! Lock is bound!");
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(r,1000);
            }
        });

        recList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = llm.getItemCount();
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int visibleThreshold = 5;

                if (!ca.isLoading() && lastVisibleItem >= totalItemCount - 1 && !end) {
                    if (ca.getOnMoreLoadListener() != null) {
                        ca.getOnMoreLoadListener().onLoadMore();
                    }
                    System.out.println("Set is loading true");
                    ca.setIsLoading(true);
                }
            }
        });

        SearchView view = (SearchView)findViewById(R.id.search);
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    reloading.tryLock(1000, TimeUnit.MILLISECONDS);
                    if(newText.equals("")){
                        end = false;
                        querying = false;
                    }
                    else{
                        end = false;
                        querying = true;
                        pagesLoadedQuery = 1;
                        queryText = newText;
                    }
                    reloadCards();
                    addNoMoreCard();
                    ca.setCardList(cards);
                    ca.notifyDataSetChanged();
                    reloading.unlock();
                    return false;
                } catch (InterruptedException e) {
                    System.out.println("Lock Bound! Cannot load based on query!");
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    public void reloadCards(){
        if(querying){
            cards = Database.getCardsSearch(MainActivity.this, queryText, Session.getInstance(MainActivity.this).getUserId(), pagesLoadedQuery*CARDS_PER_PAGE);
        }
        else{
            cards = Database.getRandomCards(MainActivity.this, Session.getInstance(MainActivity.this).getUserId(), pagesLoaded*CARDS_PER_PAGE, seed);
        }
    }

    public void addNoMoreCard(){
        if(cards.size() < pagesLoaded*CARDS_PER_PAGE){
            Card card = new Card();
            card.setCard_name("No More Cards!");
            card.setCard_description("You've reached the end");
            card.setCard_id(-1);
            cards.add(card);
            end = true;
        }
    }

    public void addDecks(List<Card> decks){
        SubMenu sm = navigationView.getMenu().findItem(R.id.decks).getSubMenu();
        for(Card entry : decks) {
            final Card card = entry;
            sm.add(Menu.NONE, 1, Menu.NONE, card.getCard_name()).setIcon(R.drawable.ic_library_books_black_24dp).setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            System.out.println("Deck clicked");
                            Intent intent = new Intent(MainActivity.this, ManageCardsInDeck.class);
                            intent.putExtra("Card", card);
                            startActivity(intent);
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
            startActivity(new Intent(this,ManageDecks.class));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2301 && resultCode == 188){
            end = false;
            reloadCards();
            addNoMoreCard();
            ca.setCardList(cards);
            ca.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(navigationView, "Card Created", Snackbar.LENGTH_LONG); // Don’t forget to show!
            snackbar.show();
        }
    }
}
