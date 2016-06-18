package almapenada.daam.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import almapenada.daam.DrawerActivity;
import almapenada.daam.MainActivity;
import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;
import almapenada.daam.utility.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private ListView list_friends;
    private CustomRowAdapter adapter;
    private View rootView;
    private EditText inputSearch;
    //private String[] friends_list_names = new String[5];
    //private Drawable[] friends_list_images = new Drawable[5];
    //private String[] dummy_friends = { "Jorge Branquinho", "Ivo Silva", "Daniela Costa", "Andre Carvalho", "Diogo Leo" };
    private User[] dummy_users;
    private List<User> friends = new ArrayList<User>();
    private View mProgressView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        showProgress(true);
        //createDummys();
        new DownloadFriendsTask().execute();



        /*for ( int i = 0 ; i < dummy_users.length ; i++ ) {
            friends_list_images[i] = rootView.getResources().getDrawable(R.drawable.user);
        }*/

        //adapter = new CustomRowAdapter(getActivity(), dummy_users, true, getActivity());
        //list_friends.setAdapter(adapter);

        /*list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((DrawerActivity)(getActivity())).viewMyProfile(adapter.getItem(position));
                //Toast.makeText(getContext(), "Hello " + friends_list_names[position], Toast.LENGTH_LONG).show();
            }
        });*/


        return rootView;
    }

    /*private void createDummys() {
        dummy_users = new User[3];
        dummy_users[0] = new User();
        dummy_users[0].setFirstName("Ze");
        dummy_users[0].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
        dummy_users[1] = new User();
        dummy_users[1].setFirstName("Maria");
        dummy_users[1].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
        dummy_users[2] = new User();
        dummy_users[2].setFirstName("Snoop Dogg");
        dummy_users[2].setPictureDrawable(rootView.getResources().getDrawable(R.drawable.user));
    }*/

    private class DownloadFriendsTask extends AsyncTask<URL, Void, Void> {

        protected Void doInBackground(URL... url) {
            String response = "";
            Bundle b = getActivity().getIntent().getExtras();
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
            showProgress(false);
            dummy_users = friends.toArray(new User[friends.size()]);
            final ListView list_friends = (ListView) rootView.findViewById(R.id.friends_listview);
            adapter = new CustomRowAdapter(getActivity(), dummy_users, true, getActivity());
            list_friends.setAdapter(adapter);
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
                    if(t_poi.getString("description")!="null")
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        final ListView list_friends = (ListView) rootView.findViewById(R.id.friends_listview);
        mProgressView = rootView.findViewById(R.id.login_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            list_friends.setVisibility(show ? View.GONE : View.VISIBLE);
            list_friends.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    list_friends.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            list_friends.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
