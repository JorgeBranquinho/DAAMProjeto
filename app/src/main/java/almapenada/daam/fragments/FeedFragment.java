package almapenada.daam.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import almapenada.daam.R;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventFeedAdapter;
import almapenada.daam.utility.User;


public class FeedFragment extends Fragment {

    private ArrayList<Event> events_to_display_on_feed =new ArrayList<Event>();
    private ArrayList<String> messages_to_display = new ArrayList<String>();
    private ListView list;
    private EventFeedAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        //events_to_display_on_feed.add(new Event(1,"Festa no ISCTE", true, "Segunda-feira", "09/05/2016", false, "", true, "3€", "18:30", true, "38.748753 -9.153692", true, true, false));//teste
        new refreshFeed().execute();
        list = (ListView) rootView.findViewById(R.id.feed_event_list);


        return rootView;
    }

    public void setAdapter() {
        adapter = new EventFeedAdapter(getActivity(), events_to_display_on_feed, messages_to_display);
        list.setAdapter(adapter);

    }

    class refreshFeed extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String response = "";
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                Bundle b = getActivity().getIntent().getExtras();
                User user = (User) b.getSerializable("User");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/refresh/feed/1"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();

            } catch (Exception e) {
                //e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JSONObject jobj = null;
            String msg = "";
            try {
                jobj = new JSONObject(response);
                if (jobj.get("status").toString().compareTo("OK") == 0) {
                    JSONArray poi = jobj.getJSONArray("teste");

                    for (int i = 0; i < poi.length(); i++) {
                        JSONObject t_poi = poi.getJSONObject(i);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat  format2 = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = format.parse(t_poi.getString("date").substring(0,10));
                        String dataevento = format2.format(date);
                        events_to_display_on_feed.add(new Event(Integer.parseInt(t_poi.getString("id")), t_poi.getString("eventName"), Integer.parseInt(t_poi.getString("isPublic")) == 1 ? true : false, t_poi.getString("weekDay"), dataevento, Integer.parseInt(t_poi.getString("isEndDate")) == 1 ? true : false, t_poi.getString("endDate"), Integer.parseInt(t_poi.getString("isPrice")) == 1 ? true : false, t_poi.getString("price"), t_poi.getString("hours"), Integer.parseInt(t_poi.getString("isLocation")) == 1 ? true : false, t_poi.getString("location_latlng"), Integer.parseInt(t_poi.getString("isFriendsInvitable")) == 1 ? true : false, false, false));

                        if ( t_poi.getString("type").toString().equals("create") ) {
                            msg = "O " + t_poi.getString("name").toString() + " criou o evento " + t_poi.getString("eventName").toString();
                        } else {
                            msg = "O " + t_poi.getString("name").toString() + " aderiu ao evento " + t_poi.getString("eventName").toString();
                        }
                        messages_to_display.add(msg);

                    }
                    setAdapter();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private void jsonToEvent(String response) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(response);
            if (jobj.get("status").toString().compareTo("OK") == 0) {
                JSONArray poi = jobj.getJSONArray("teste");

                for (int i = 0; i < poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat  format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = format.parse(t_poi.getString("date").substring(0,10));
                    String dataevento = format2.format(date);
                    events_to_display_on_feed.add(new Event(Integer.parseInt(t_poi.getString("id")), t_poi.getString("eventName"), Integer.parseInt(t_poi.getString("isPublic")) == 1 ? true : false, t_poi.getString("weekDay"), dataevento, Integer.parseInt(t_poi.getString("isEndDate")) == 1 ? true : false, t_poi.getString("endDate"), Integer.parseInt(t_poi.getString("isPrice")) == 1 ? true : false, t_poi.getString("price"), t_poi.getString("hours"), Integer.parseInt(t_poi.getString("isLocation")) == 1 ? true : false, t_poi.getString("location_latlng"), Integer.parseInt(t_poi.getString("isFriendsInvitable")) == 1 ? true : false, false, false));

                }
                setAdapter();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
