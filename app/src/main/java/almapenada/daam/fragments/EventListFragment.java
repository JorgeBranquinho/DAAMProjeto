package almapenada.daam.fragments;

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

import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;


public class EventListFragment extends Fragment {

    private View rootView;
    private EventAdapter adapter;
    private ArrayList<Event> events_to_display =new ArrayList<Event>();
    private ArrayList<Event> full_event_list =new ArrayList<Event>();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

                    full_event_list.add(new Event("festa do Seixo Paulo", "monday", "1/2/2012", "15€", "15h", "ISCTE", null, false, false));//teste
                    full_event_list.add(new Event("frango assado", "monday", "3/2/2012", "1€", "15h", "ISCTE", null, false, false));//teste
                    full_event_list.add(new Event("Snoop Dogg & vinho verde", "monday", "2/2/2012", "3€", "15h", "ISCTE", null, false, false));//teste
                    full_event_list.add(new Event("festa de azeite", "monday", "2/2/2012", "3€", "15h", "ISCTE", null, false, false));//teste
                    full_event_list.add(new Event("makumba", "monday", "4/2/2012", "", "15h", "ISCTE", null, false, false));//teste
        //TODO:ia a DB buscar eventos...
        adapter=new EventAdapter(getActivity(), full_event_list);

        event_list = (ListView) rootView.findViewById(R.id.event_list);
        event_list.setAdapter(adapter);

        return rootView;
    }

    public void orderByRecent(){
        events_to_display= (ArrayList<Event>) full_event_list.clone();//TODO: verificar se isto ainda esta atual com a DB
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
