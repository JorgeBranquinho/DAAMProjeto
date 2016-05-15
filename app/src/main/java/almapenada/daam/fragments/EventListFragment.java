package almapenada.daam.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

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
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;
    private EventsDatabase database;
    private LocationManager locationMangaer = null;
    private MyLocationListener locationListener;
    private boolean track = false;
    private Location userLocation=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        database = new EventsDatabase(getActivity());
        if (database.isEmpty())
            database.populateWithExample();

        populateEventList(database.getAllEvents());

        adapter = new EventAdapter(getActivity(), full_event_list);

        event_list = (ListView) rootView.findViewById(R.id.event_list);
        event_list.setAdapter(adapter);

        database.close();
        if (displayGpsStatus()) {
            track = true;
            locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
        }
        return rootView;
    }

    private void populateEventList(Cursor cursor) {
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = (new EnumDatabase()).cursorToEvent(cursor);
                full_event_list.add(e);
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
        if (displayGpsStatus() && track && userLocation!=null) {
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
        }else Toast.makeText(getContext(), "Localização GPS não disponivel", Toast.LENGTH_SHORT).show();
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
        events_to_display = (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
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
        events_to_display = (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
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

    private void refresh() {
        full_event_list = new ArrayList<Event>();
        database = new EventsDatabase(getActivity());
        populateEventList(database.getAllEvents());
        adapter = new EventAdapter(getActivity(), full_event_list);
        event_list.setAdapter(adapter);
        database.close();
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
}
