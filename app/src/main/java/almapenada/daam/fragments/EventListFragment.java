package almapenada.daam.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ListIterator;

import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;
import almapenada.daam.utility.EventsDatabase;


public class EventListFragment extends Fragment {

    private View rootView;
    private EventAdapter adapter;
    private ArrayList<Event> events_to_display = new ArrayList<Event>();
    private ArrayList<Event> full_event_list = new ArrayList<Event>();
    private ArrayList<Event> temp_events = new ArrayList<Event>();
    private ArrayList<Integer> owned_events = new ArrayList<>();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;
    private EventsDatabase database;
    private LocationManager locationMangaer = null;
    private MyLocationListener locationListener;
    private boolean track = false;
    private Location userLocation = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        database = new EventsDatabase(getActivity());
        //if (database.isEmpty())
            //database.populateWithExample();

        populateEventList(database.getAllEvents());
        populateEventListRemote();

        adapter = new EventAdapter(getActivity(), full_event_list);

        event_list = (ListView) rootView.findViewById(R.id.event_list);
        event_list.setAdapter(adapter);

        return rootView;
    }

    private void populateEventListRemote() {
        new DownloadEvents().execute();
    }

    class DownloadEvents extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String response = "";
            //Toast.makeText(getActivity().getApplicationContext(), "A procurar novos eventos...", Toast.LENGTH_LONG).show();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/getAll/events/byUser/1"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                temp_events.add(jsonToEvent(response));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(getActivity().getApplicationContext(), "Eventos atualizados", Toast.LENGTH_LONG).show();
            full_event_list = temp_events;
            orderByRecent();
        }

        private Event jsonToEvent(String response) {
            JSONObject jobj = null;
            try {
                jobj = new JSONObject(response);
                if (jobj.get("status").toString().compareTo("OK") == 0) {
                    JSONArray poi = jobj.getJSONArray("teste");
                    database = new EventsDatabase(getActivity());
                    for (int i = 0; i < poi.length(); i++) {
                        JSONObject t_poi = poi.getJSONObject(i);
                        if (!owned_events.contains(Integer.parseInt(t_poi.getString("id")))) {
                            SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat  format2 = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = format.parse(t_poi.getString("date").substring(0,10));
                            String dataevento = format2.format(date);
                            Event x = new Event(Integer.parseInt(t_poi.getString("id")), t_poi.getString("eventName"), Integer.parseInt(t_poi.getString("isPublic")) == 1 ? true : false, t_poi.getString("weekDay"), dataevento, Integer.parseInt(t_poi.getString("isEndDate")) == 1 ? true : false, t_poi.getString("endDate"), Integer.parseInt(t_poi.getString("isPrice")) == 1 ? true : false, t_poi.getString("price"), t_poi.getString("hours"), Integer.parseInt(t_poi.getString("isLocation")) == 1 ? true : false, t_poi.getString("location_latlng"), Integer.parseInt(t_poi.getString("isFriendsInvitable")) == 1 ? true : false, false, false);
                            database.insertEvent(x);
                            full_event_list.add(x);
                        }
                    }
                    database.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void populateEventList(Cursor cursor) {
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = (new EnumDatabase()).cursorToEvent(cursor);
                full_event_list.add(e);
                owned_events.add(e.getId());
                cursor.moveToNext();
            }
        }
    }

    public void orderByRecent() {
        refresh();
        events_to_display = (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
        if (events_to_display.size() == 0) return;
        Collections.sort(events_to_display, new Comparator<Event>() {
            @Override
            public int compare(Event event2, Event event1) {
                try {
                    return format.parse(event1.getDate()).before(format.parse(event2.getDate())) ? 1 : -1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("[error] erro a ordernar por recente");
                return -1;
            }
        });
        if (adapter == null) adapter = new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByNear() {
        if (displayGpsStatus() && track && userLocation != null) {
            refresh();
            events_to_display = (ArrayList<Event>) full_event_list.clone();
            if (events_to_display.size() == 0) return;
            Collections.sort(events_to_display, new Comparator<Event>() {
                @Override
                public int compare(Event event2, Event event1) {
                    if (!event1.isLocation()) return 1;
                    if (!event2.isLocation()) return -1;

                    Location location1 = new Location("");
                    location1.setLatitude(event1.getLocation_latlng().latitude);
                    location1.setLongitude(event1.getLocation_latlng().longitude);

                    Location location2 = new Location("");
                    location2.setLatitude(event2.getLocation_latlng().latitude);
                    location2.setLongitude(event2.getLocation_latlng().longitude);

                    return userLocation.distanceTo(location2) < userLocation.distanceTo(location1) ? -1 : 1;
                }
            });
            adapter = new EventAdapter(getActivity(), events_to_display);
            event_list.setAdapter(adapter);
        } else Toast.makeText(getContext(), R.string.GPSNotFound, Toast.LENGTH_SHORT).show();
    }

    public void orderByCheaper() {
        refresh();
        events_to_display = (ArrayList<Event>) full_event_list.clone();
        if (events_to_display.size() == 0) return;
        Collections.sort(events_to_display, new Comparator<Event>() {
            @Override
            public int compare(Event event2, Event event1) {
                if (event1.getPrice().equals("") || event1.getPrice().equals(" - ")) return 1;
                if (event2.getPrice().equals("") || event2.getPrice().equals(" - ")) return -1;
                return Double.parseDouble(String.valueOf(event2.getPrice())) < Double.parseDouble(String.valueOf(event1.getPrice())) ? -1 : 1;
            }
        });
        adapter = new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByGoing() {
        refresh();
        events_to_display = (ArrayList<Event>) full_event_list.clone();
        if (events_to_display.size() == 0) return;
        ListIterator<Event> iter = events_to_display.listIterator();
        while (iter.hasNext()) {
            if (!iter.next().isGoing()) {
                iter.remove();
            }
        }
        adapter = new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByNotGoing() {
        refresh();
        events_to_display = (ArrayList<Event>) full_event_list.clone();
        if (events_to_display.size() == 0) return;
        ListIterator<Event> iter = events_to_display.listIterator();
        while (iter.hasNext()) {
            if (iter.next().isGoing()) {
                iter.remove();
            }
        }
        adapter = new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getActivity().getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            userLocation = new Location("");
            userLocation.setLatitude(loc.getLatitude());
            userLocation.setLongitude(loc.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void refresh() {
        full_event_list = new ArrayList<Event>();
        database = new EventsDatabase(getActivity());
        populateEventList(database.getAllEvents());
        adapter = new EventAdapter(getActivity(), full_event_list);
        event_list.setAdapter(adapter);
        database.close();
    }
}
