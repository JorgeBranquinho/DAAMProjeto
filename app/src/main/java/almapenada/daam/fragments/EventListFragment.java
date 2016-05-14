package almapenada.daam.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import almapenada.daam.utility.EnumDatabase;
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

        database.close();
        return rootView;
    }

    private void populateEventList(Cursor cursor) {
        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = (new EnumDatabase()).cursorToEvent(cursor);
                full_event_list.add(e);
                cursor.moveToNext();
            }
        }
    }

    public void orderByRecent(){
        refresh();
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
        Toast.makeText(getContext(), "Opcção não disponivel", Toast.LENGTH_SHORT).show();
    }

    public void orderByCheaper() {
        refresh();
        events_to_display= (ArrayList<Event>) full_event_list.clone();
        if(events_to_display.size()==0)return;
        Collections.sort(events_to_display, new Comparator<Event>() {
            @Override
            public int compare(Event event2, Event event1) {
                if (event1.getPrice().equals("") || event1.getPrice().equals(" - ")) return 1;
                if (event2.getPrice().equals("") || event2.getPrice().equals(" - ")) return -1;
                return Double.parseDouble(String.valueOf(event2.getPrice()))<Double.parseDouble(String.valueOf(event1.getPrice()))?-1:1;
            }
        });
        adapter=new EventAdapter(getActivity(), events_to_display);
        event_list.setAdapter(adapter);
    }

    public void orderByGoing() {
        refresh();
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
        refresh();
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

    private void refresh(){
        full_event_list=new ArrayList<Event>();
        database = new EventsDatabase(getActivity());
        populateEventList(database.getAllEvents());
        adapter=new EventAdapter(getActivity(), full_event_list);
        event_list.setAdapter(adapter);
        database.close();
    }
}
