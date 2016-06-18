package almapenada.daam.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import almapenada.daam.R;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.utility.EventsDatabase;
import almapenada.daam.utility.User;

public class ProfileFriendFragment extends Fragment {
    private View rootView;

    //private EventAdapter adapter;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> events_to_display = new ArrayList<Event>();
    private ArrayList<Event> full_event_list = new ArrayList<Event>();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;
    private EventsDatabase database;
    private Button addRemove;
    private ArrayList<String> lista = new ArrayList<String>();
    private User user;
    private TextView username;
    private Button image;
    private TextView mail;
    private TextView descrPerfil;
    private TextView numPerfil;
    private List<Event> events = new ArrayList<Event>();
    private int id;
    private View mProgressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_friend, container, false);

        username = (TextView) rootView.findViewById(R.id.nomePerfil);
        image = (Button) rootView.findViewById(R.id.imageFriend);
        event_list = (ListView) rootView.findViewById(R.id.eventos);
        mail = (TextView) rootView.findViewById(R.id.textView2);
        descrPerfil = (TextView) rootView.findViewById(R.id.descrPerfil);
        numPerfil = (TextView) rootView.findViewById(R.id.numPerfil);

        Bundle b = this.getArguments();
        user = (User) b.getSerializable("friendUser");
        if (user != null) {
            id=user.getIdUser();
            new DownloadEvents().execute();
            username.setText(user.getFirstName() + " " + user.getLastName());
            username.setClickable(false);
            username.setFocusable(false);
            mail.setText(user.getEmail());
            mail.setClickable(false);
            mail.setFocusable(false);
            descrPerfil.setText(user.getDescricao());
            descrPerfil.setClickable(false);
            descrPerfil.setFocusable(false);
            numPerfil.setText(user.getPhone());
            numPerfil.setClickable(false);
            numPerfil.setFocusable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                image.setBackground(user.getPictureDrawable());
            }
        }

        database = new EventsDatabase(getActivity());
        if (database.isEmpty())
            database.populateWithExample();

        //populateEventList(database.getAllEvents());

        //adapter=new EventAdapter(getActivity(), full_event_list);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.activity_list_view, lista);

        event_list.setAdapter(adapter);


        /*
        addRemove = (Button) rootView.findViewById(R.id.addRemove);
        if (se nao for amigo) {
            addRemove.setText("Adicionar");
        } else{
            addRemove.setText("Remover");
        }
        */
        return rootView;
    }

    /*private void populateEventList(Cursor cursor) {
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = (new EnumDatabase()).cursorToEvent(cursor);
                full_event_list.add(e);
                cursor.moveToNext();
            }
        }

        for (int i = 0; i < full_event_list.size(); i++) {
            lista.add(full_event_list.get(i).getEventName());
        }
    }*/


    class DownloadEvents extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String response = "";
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/getAll/events/byUser/" + id));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                jsonToEvent(response);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showProgress(false);
            for (int i = 0; i < events.size(); i++) {
                lista.add(events.get(i).getEventName());
            }
        }

        private Event jsonToEvent(String response) {
            JSONObject jobj = null;
            try {
                jobj = new JSONObject(response);
                if (jobj.get("status").toString().compareTo("OK") == 0) {
                    JSONArray poi = jobj.getJSONArray("teste");
                    for (int i = 0; i < poi.length(); i++) {
                        JSONObject t_poi = poi.getJSONObject(i);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = format.parse(t_poi.getString("date").substring(0, 10));
                        String dataevento = format2.format(date);
                        Event x = new Event(Integer.parseInt(t_poi.getString("id")), t_poi.getString("eventName"), Integer.parseInt(t_poi.getString("isPublic")) == 1 ? true : false, t_poi.getString("weekDay"), dataevento, Integer.parseInt(t_poi.getString("isEndDate")) == 1 ? true : false, t_poi.getString("endDate"), Integer.parseInt(t_poi.getString("isPrice")) == 1 ? true : false, t_poi.getString("price"), t_poi.getString("hours"), Integer.parseInt(t_poi.getString("isLocation")) == 1 ? true : false, t_poi.getString("location_latlng"), Integer.parseInt(t_poi.getString("isFriendsInvitable")) == 1 ? true : false, false, false);
                        events.add(x);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mProgressView = rootView.findViewById(R.id.login_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            event_list.setVisibility(show ? View.GONE : View.VISIBLE);
            event_list.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    event_list.setVisibility(show ? View.GONE : View.VISIBLE);
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
            event_list.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

