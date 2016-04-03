package almapenada.daam;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import almapenada.daam.fragments.CreateEventFragment;
import almapenada.daam.fragments.EventDetailsFragment;
import almapenada.daam.fragments.EventsFragment;
import almapenada.daam.fragments.FriendsFragment;
import almapenada.daam.fragments.HomeFragment;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.SuggestionSimpleCursorAdapter;
import almapenada.daam.utility.SuggestionsDatabase;
import almapenada.daam.utility.User;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragManager;
    private FragmentTransaction transaction;
    private Fragment currentFragment;
    private User user=null;
    private ImageView nav_img;
    private int id_menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle b = getIntent().getExtras();
        user = (User) b.getSerializable("User");
        if(user!=null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);
            TextView nav_user = (TextView) hView.findViewById(R.id.nomegrande);
            nav_user.setText(user.getFirstName() + " " + user.getLastName());
            TextView nav_user2 = (TextView) hView.findViewById(R.id.nomepqueno);
            nav_user2.setText("");
            nav_img = (ImageView) hView.findViewById(R.id.imageView);
            if(user.getPictureURL()!=null)new DownloadImageTask().execute(user.getPictureURL());
        }else
            Toast.makeText(this.getApplicationContext(), "nao vieste do face", Toast.LENGTH_SHORT).show();

        // Botao que flutoa na actividade
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_menuItem == R.id.nav_events){
                    viewFragment(new CreateEventFragment(), "Create New Event", false, -1);
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final SuggestionsDatabase database = new SuggestionsDatabase(this);
        if(database.isEmpty()) database.insertSuggestion("ola");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                Toast.makeText(getBaseContext(), "procuraste por " + text, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                Cursor cursor = database.getSuggestions(text);
                if(cursor.getCount() != 0)
                {
                    String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
                    int[] columnTextId = new int[] { android.R.id.text1};

                    SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                            android.R.layout.simple_list_item_1, cursor,
                            columns , columnTextId
                            , 0);

                    searchView.setSuggestionsAdapter(simple);
                    return true;
                }
                else
                {
                    return false;
                }
                //Toast.makeText(getBaseContext(), "mudaste para " + text, Toast.LENGTH_SHORT).show();
                //return false;
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
        id_menuItem = item.getItemId();

        if (id_menuItem == R.id.nav_profile) {
            // Handle the camera action
        } else if (id_menuItem == R.id.nav_home) {
            viewFragment(new HomeFragment(), getResources().getString(R.string.title_home), true, -1);
            /*showFabIcon();
            transaction.remove(currentFragment);
            transaction = fragManager.beginTransaction();
            currentFragment = new HomeFragment();
            transaction.replace(R.id.frame, currentFragment);
            transaction.addToBackStack(null);
            setTitle(getResources().getString(R.string.title_home));
            transaction.commit();*/
        } else if (id_menuItem == R.id.nav_events) {
            viewFragment(new EventsFragment(), getResources().getString(R.string.title_events), true, R.drawable.plus);
        } else if (id_menuItem == R.id.nav_friends) {
            viewFragment(new FriendsFragment(), getResources().getString(R.string.title_friends), true, -1);
            /*showFabIcon();
            transaction.remove(currentFragment);
            transaction = fragManager.beginTransaction();
            currentFragment = new FriendsFragment();
            transaction.replace(R.id.frame, currentFragment);
            transaction.addToBackStack(null);
            setTitle(getResources().getString(R.string.title_friends));
            transaction.commit();*/
        } else if (id_menuItem == R.id.nav_settings) {

        }  else if (id_menuItem == R.id.nav_about) {

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
        EventDetailsFragment frag = new EventDetailsFragment();
        frag.setArguments(bundle);
        viewFragment(frag, e.getEventName(), false, -1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }







    //img do face
    private class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
        Bitmap bitmap = null;

        protected Bitmap doInBackground(URL... url) {
            try {
                bitmap = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
                bitmap = scaleBitmap(bitmap, 200, 200);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        private Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
            Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Matrix m = new Matrix();
            m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
            canvas.drawBitmap(bitmap, m, new Paint());
            return output;
        }

        protected void onPostExecute(Bitmap result) {
            user.setPicture(result);
            nav_img.setImageBitmap(result);
        }
    }
}
