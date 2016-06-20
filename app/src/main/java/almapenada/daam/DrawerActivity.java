package almapenada.daam;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import almapenada.daam.fragments.AboutFragment;
import almapenada.daam.fragments.CreateEventFragment;
import almapenada.daam.fragments.CreateNewGroup;
import almapenada.daam.fragments.EventDetailsFragment;
import almapenada.daam.fragments.EventsFragment;
import almapenada.daam.fragments.FriendsFragment;
import almapenada.daam.fragments.HomeFragment;
import almapenada.daam.fragments.ProfileFragment;
import almapenada.daam.fragments.SettingsFragment;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;
import almapenada.daam.utility.SuggestionSimpleCursorAdapter;
import almapenada.daam.utility.SuggestionsDatabase;
import almapenada.daam.utility.User;

//import android.widget.CursorAdapter;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragManager;
    private FragmentTransaction transaction;
    private Fragment currentFragment;
    private ImageView nav_img;
    private int id_menuItem;
    private GoogleApiClient client;
    private Activity activity = this;
    private User user = null;
    private List<User> friends = new ArrayList<User>();
    private boolean showSearch=true;
    private boolean showSubmit=false;
    private boolean showSettings=false;
    private ProfileFragment pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        user = (User) b.getSerializable("User");
        if (user != null) {
            if (user.getIdUser() == 0) user.setIdUser(1);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);
            TextView nav_user = (TextView) hView.findViewById(R.id.nomegrande);
            nav_user.setText(user.getFirstName() + " " + user.getLastName());
            TextView nav_user2 = (TextView) hView.findViewById(R.id.nomepqueno);
            nav_user2.setText(user.getEmail());
            nav_img = (ImageView) hView.findViewById(R.id.imageView);
            if (user.getPictureURL() != null) new DownloadImageTask().execute(user.getPictureURL());
        }

        // Botao que flutoa na actividade
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTitle().equals(getString(R.string.title_friends))) {
                    final String[] opcoes = {getString(R.string.opcao1), getString(R.string.opcao2)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setItems(opcoes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                viewFragment(new CreateEventFragment(), "Create New Event", false, -1);
                            }else{
                                viewFragment(new CreateNewGroup(), getString(R.string.Createngroup), false, -1);
                            }
                        }
                    });
                    builder.show();
                }else
                    viewFragment(new CreateEventFragment(), getString(R.string.createnevent), false, -1);
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
        transaction.addToBackStack(null);
        setTitle(getResources().getString(R.string.title_home));
        transaction.commit();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //saveToSharedPreferences(user.getFirstName() + " " + user.getLastName() );
    }

    /*private void saveToSharedPreferences(String name) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, name);
        editor.commit();
    }*/

    @Override
    public void onBackPressed() {
        /*try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (IllegalStateException e) {
            //nao fazer nada, ja esta no home
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        viewFragment(new HomeFragment(), getResources().getString(R.string.title_home), true, -1);
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem action_submit = menu.findItem(R.id.action_submit);
        MenuItem action_settings = menu.findItem(R.id.action_settings);
        menu.findItem(R.id.action_submit).setVisible(false);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if(!showSearch){
            searchItem.setVisible(false);
        }else{
            searchItem.setVisible(true);
        }
        if(!showSubmit){
            action_submit.setVisible(false);
        }else{
            action_submit.setVisible(true);
        }
        if(!showSettings){
            action_settings.setVisible(false);
        }else{
            action_settings.setVisible(true);
        }
        final SuggestionsDatabase database = new SuggestionsDatabase(this);
        database.removeAll();
        if (database.isEmpty()) {
            //TODO:falta add amigos e outras cenas se quiserem
            EventsDatabase database2 = new EventsDatabase(this.getBaseContext());
            Cursor cursor = database2.getAllEvents();
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    database.insertSuggestion(cursor.getString(cursor.getColumnIndex("event_name")), cursor.getInt(cursor.getColumnIndex("_id")), 1);//depois aqui metesse um indicador se é evento ou outra coisa
                    cursor.moveToNext();
                }
            }
            database2.close();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                Toast.makeText(getBaseContext(), "procuraste por " + text, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                Cursor cursor = database.getSuggestions(text);
                if (cursor.getCount() != 0) {
                    String[] columns = new String[]{SuggestionsDatabase.FIELD_SUGGESTION};
                    int[] columnTextId = new int[]{android.R.id.text1};

                    final SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(), R.layout.li_query_suggestion, cursor, columns, columnTextId, 0);
                    searchView.setSuggestionsAdapter(simple);
                    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                        @Override
                        public boolean onSuggestionSelect(int position) {
                            return false;
                        }

                        @Override
                        public boolean onSuggestionClick(int position) {
                            Cursor c = (Cursor) simple.getItem(position);
                            if (c.moveToFirst()) {
                                EventsDatabase database2 = new EventsDatabase(activity);
                                Cursor c2 = database2.getEventById(c.getInt(c.getColumnIndex(SuggestionsDatabase.FIELD_IDEXT)));
                                if (c2.moveToFirst()) {
                                    Event e = EnumDatabase.cursorToEvent(c2);
                                    viewEventDetails(e);
                                }
                                database2.close();
                            }
                            return false;
                        }
                    });
                    return true;
                } else {
                    return false;
                }
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                System.out.println("ADEUSSSSSSSSSS");
                if(pf!=null){
                    pf.editProfile(true);
                    showSubmit=true;
                    invalidateOptionsMenu();
                }

                /*if(!editProfile){
                    editProfile=true;
                }else{

                    ((MenuItem) findViewById(R.id.action_submit)).setVisible(true);
                }*/

                return true;

            case R.id.action_submit:
                pf.getUpdatedProfile();
                /*if(){
                   update com sucesso
                }else{
                    update falhado
                }*/
                Toast.makeText(getBaseContext(), "Update com sucesso!", Toast.LENGTH_SHORT).show();
                //fazer update na base de dados o q devera fazer update no utilizador na app
                pf.editProfile(false);
                showSubmit=false;
                invalidateOptionsMenu();
                return true;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id_menuItem = item.getItemId();
        //showSearch=true;
        if (id_menuItem == R.id.nav_profile) {
            if (user != null) {
                //search;submit;settings
                viewMyProfile(user);
                pf= new ProfileFragment();
                viewFragment(pf, getResources().getString(R.string.MyProfile), false, -1);
            }
        } else if (id_menuItem == R.id.nav_home) {
            //search;submit;settings
            viewFragment(new HomeFragment(), getResources().getString(R.string.title_home), true, -1);
        } else if (id_menuItem == R.id.nav_events) {
            //search;submit;settings
            viewFragment(new EventsFragment(), getResources().getString(R.string.title_events), true, R.drawable.plus);
        } else if (id_menuItem == R.id.nav_friends) {
            //search;submit;settings
            viewFragment(new FriendsFragment(), getResources().getString(R.string.title_friends), true, -1);
        } else if (id_menuItem == R.id.nav_settings) {
            //search;submit;settings
            viewSettings(user);
        } else if (id_menuItem == R.id.nav_about) {
            //search;submit;settings
            viewFragment(new AboutFragment(), getResources().getString(R.string.title_about), false, -1);
        } else if (id_menuItem == R.id.log_out) {
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFabIcon() {
        ((FloatingActionButton) findViewById(R.id.fab)).show();
    }

    public void HideFabIcon() {
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

    public void viewFragment(Fragment frag, String title, Boolean showFabIcon, int fabIcon) {
        if (showFabIcon) showFabIcon();
        else HideFabIcon();
        if (fabIcon != -1) setFabIcon(fabIcon);
        transaction.remove(currentFragment);
        transaction = fragManager.beginTransaction();
        transaction.replace(R.id.frame, frag);
        transaction.addToBackStack(null);
        setTitle(title);
        transaction.commit();
    }

    public void viewMyProfile(User u) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", u);
        ProfileFragment frag = new ProfileFragment();
        frag.setArguments(bundle);
        viewFragment(frag, getResources().getString(R.string.MyProfile), false, -1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewSettings(User u) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", u);
        SettingsFragment frag = new SettingsFragment();
        frag.setArguments(bundle);
        viewFragment(frag, getResources().getString(R.string.title_settings), false, -1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void viewEventDetails(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("evento", e);
        EventDetailsFragment frag = new EventDetailsFragment();
        frag.setArguments(bundle);
        viewFragment(frag, e.getEventName(), false, -1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Drawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://almapenada.daam/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Drawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://almapenada.daam/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    //img do face
    private class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {
        Bitmap bitmap = null;

        protected Bitmap doInBackground(URL... url) {
            try {
                bitmap = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
                int sizevar = new EnumDatabase().getScreenDensity(activity);
                int size;
                switch (sizevar) {
                    case 1:
                        size = 150;
                        break;//150
                    case 2:
                        size = 100;
                        break;//100
                    case 3:
                        size = 200;
                        break;//200
                    case 4:
                        size = 200;
                        break;//238
                    case 5:
                        size = 400;
                        break;//400
                    default:
                        size = 400;
                        break;//400
                }
                bitmap = scaleBitmap(bitmap, size, size);
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
            //user.setPicture(result);
            //System.out.println("ueueu" + user.getPicture());
            nav_img.setImageBitmap(result);
        }
    }


    private class DownloadFriendsTask extends AsyncTask<URL, Void, Void> {

        protected Void doInBackground(URL... url) {
            String response = "";
            Bundle b = getIntent().getExtras();
            User user = (User) b.getSerializable("User");
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/getAll/friends/byUser/" + user.getIdUser()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                jsonToUser(response);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SuggestionsDatabase database = new SuggestionsDatabase(getBaseContext());
            for (User x : friends) {
                database.insertSuggestion(x.getFirstName() + " " + x.getLastName(), x.getIdUser(), 0);
            }
            database.close();
        }
    }

    private void jsonToUser(String response) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(response);
            if (jobj.get("status").toString().compareTo("OK") == 0) {
                JSONArray poi = jobj.getJSONArray("teste");
                for (int i = 0; i < poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);
                    User x = new User();
                    x.setIdUser(Integer.parseInt(t_poi.getString("id")));
                    x.setFirstName(t_poi.getString("name").substring(0, t_poi.getString("name").lastIndexOf(" ")));
                    x.setLastName(t_poi.getString("name").substring(t_poi.getString("name").lastIndexOf(" ") + 1));
                    x.setEmail(t_poi.getString("email"));
                    x.setPhone(t_poi.getString("telephone"));
                    if (t_poi.getString("description") != "null")
                        x.setDescricao(t_poi.getString("description"));
                    x.setGender(t_poi.getString("gender"));
                    friends.add(x);
                }
            }
            //dummy_users = (User[]) friends.toArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeBar(boolean showSearch, boolean showSubmit, boolean showSettings){
        this.showSearch = showSearch;
        this.showSubmit = showSubmit;
        this.showSettings= showSettings;
        invalidateOptionsMenu();
    }

}