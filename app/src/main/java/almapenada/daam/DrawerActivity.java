package almapenada.daam;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import almapenada.daam.fragments.EventDetailsFragment;
import almapenada.daam.fragments.EventsFragment;
import almapenada.daam.fragments.FriendsFragment;
import almapenada.daam.fragments.HomeFragment;
import almapenada.daam.utility.Event;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragManager;
    private FragmentTransaction transaction;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Botao que flutoa na actividade
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragment instanceof EventsFragment){
                    Toast.makeText(getBaseContext(), "Aqui tipo add novo evento....", Toast.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(view, "Replace with your own action ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        // Inicializa o drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Criacao primeiro fragment
        currentFragment = new HomeFragment();
        fragManager = getSupportFragmentManager();
        transaction = fragManager.beginTransaction();

        transaction.add(R.id.frame, currentFragment);
        transaction.commit();



    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }catch(IllegalStateException e){
            //nao fazer nada, ja esta no home
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String text) {
                    Toast.makeText(getBaseContext(), "procuraste por " + text, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String text) {
                    Toast.makeText(getBaseContext(), "mudaste para " + text, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_home) {
            showFabIcon();
            transaction.remove(currentFragment);
            transaction = fragManager.beginTransaction();
            currentFragment = new HomeFragment();
            transaction.replace(R.id.frame, currentFragment);
            transaction.addToBackStack(null);
            setTitle(getResources().getString(R.string.title_home));
            transaction.commit();
        } else if (id == R.id.nav_events) {
            viewFragment(new EventsFragment(), getResources().getString(R.string.title_events), true, R.drawable.plus);
            /*showFabIcon();
            setFabIcon(R.drawable.plus);
            transaction.remove(currentFragment);
            transaction = fragManager.beginTransaction();
            currentFragment = new EventsFragment();
            transaction.replace(R.id.frame, currentFragment);
            transaction.addToBackStack(null);
            setTitle(getResources().getString(R.string.title_events));
            transaction.commit();*/
        } else if (id == R.id.nav_friends) {
            showFabIcon();
            transaction.remove(currentFragment);
            transaction = fragManager.beginTransaction();
            currentFragment = new FriendsFragment();
            transaction.replace(R.id.frame, currentFragment);
            transaction.addToBackStack(null);
            setTitle(getResources().getString(R.string.title_friends));
            transaction.commit();
        } else if (id == R.id.nav_settings) {

        }  else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFabIcon(){
        ((FloatingActionButton) findViewById(R.id.fab)).show();
    }

    public void HideFabIcon(){
        ((FloatingActionButton) findViewById(R.id.fab)).hide();
    }

    private void setFabIcon(final int resId) {
        ((FloatingActionButton) findViewById(R.id.fab)).hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                fab.setImageResource(resId);
                fab.show();
            }
        });
    }

    public void viewFragment(Fragment frag, String title, Boolean showFabIcon, int fabIcon){
        if(showFabIcon) showFabIcon();else HideFabIcon();
        if(fabIcon!=-1)setFabIcon(fabIcon);
        transaction.remove(currentFragment);
        transaction = fragManager.beginTransaction();
        transaction.replace(R.id.frame, frag);
        transaction.addToBackStack(null);
        setTitle(title);
        transaction.commit();
    }

    public void viewEventDetails(Event e){
        Bundle bundle = new Bundle();
        bundle.putSerializable("evento", e);
        transaction.remove(currentFragment);
        transaction = fragManager.beginTransaction();
        currentFragment = new EventDetailsFragment();
        currentFragment.setArguments(bundle);
        transaction.replace(R.id.frame, currentFragment);
        transaction.addToBackStack(null);
        setTitle(e.getEventName());
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }




}
