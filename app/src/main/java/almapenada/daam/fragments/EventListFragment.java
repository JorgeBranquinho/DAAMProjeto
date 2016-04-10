package almapenada.daam.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;
import almapenada.daam.utility.EventsDatabase;


public class EventListFragment extends Fragment {

    private View rootView;
    private EventAdapter adapter;
    private ArrayList<Event> events_to_display =new ArrayList<Event>();
    private ArrayList<Event> full_event_list =new ArrayList<Event>();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;
    private EventsDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        database = new EventsDatabase(getActivity());
        if(database.isEmpty())
            database.populateWithExample();

        populateEventList(database.getAllEvents());

        adapter=new EventAdapter(getActivity(), full_event_list);

        event_list = (ListView) rootView.findViewById(R.id.event_list);
        event_list.setAdapter(adapter);

        return rootView;
    }

    private void populateEventList(Cursor cursor) {
        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = cursorToEvent(cursor);
                full_event_list.add(e);
                cursor.moveToNext();
            }
        }
    }

    private Event cursorToEvent(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex("event_name"));
        String weekday = cursor.getString(cursor.getColumnIndex("event_weekday"));
        String date = cursor.getString(cursor.getColumnIndex("event_date"));
        String price = cursor.getString(cursor.getColumnIndex("event_price"));
        String hours = cursor.getString(cursor.getColumnIndex("event_hours"));
        String location = cursor.getString(cursor.getColumnIndex("event_location"));
        URI locationURI = null;
        try {
            locationURI = new URI(cursor.getString(cursor.getColumnIndex("event_locationURI")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        boolean going;
        if(cursor.getInt(cursor.getColumnIndex("event_going"))==0)
            going=false;
        else
            going=true;
        boolean new_event;
        if(cursor.getInt(cursor.getColumnIndex("event_new"))==0)
            new_event = false;
        else
            new_event = true;
        return new Event(id, name, weekday, date, price, hours, location, locationURI, going, new_event);
    }

    public void orderByRecent(){
        events_to_display= (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
        if(events_to_display.size()==0)return;
        Collections.sort(events_to_display, new Comparator<Event>() {
            @Override
            public int compare(Event event2, Event event1) {
                try {
                    return format.parse(event1.getDate()).before(format.parse(event2.getDate()))?1:-1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("[error] erro a ordernar por recente");
                return -1;
            }
        });
        if(adapter==null)adapter=new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByNear() {
        //TODO:este é lixado xD
    }

    public void orderByCheaper() {
        events_to_display= (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
        if(events_to_display.size()==0)return;
        Collections.sort(events_to_display, new Comparator<Event>() {
            @Override
            public int compare(Event event2, Event event1) {
                if (event1.getPrice().equals("")) return 1;
                if (event2.getPrice().equals("")) return -1;
                return Integer.parseInt(String.valueOf(event2.getPrice().charAt(0)))<Integer.parseInt(String.valueOf(event1.getPrice().charAt(0)))?-1:1;
            }
        });
        adapter=new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByGoing() {
        events_to_display= (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
        if(events_to_display.size()==0)return;
        ListIterator<Event> iter = events_to_display.listIterator();
        while(iter.hasNext()){
            if(!iter.next().isGoing()){
                iter.remove();
            }
        }
        adapter=new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByNotGoing() {
        events_to_display= (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
        if(events_to_display.size()==0)return;
        ListIterator<Event> iter = events_to_display.listIterator();
        while(iter.hasNext()){
            if(iter.next().isGoing()){
                iter.remove();
            }
        }
        adapter=new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }
}
