package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;


public class EventListFragment extends Fragment {

    private View rootView;
    private EventAdapter adapter;
    private ArrayList<Event> events_to_display =new ArrayList<Event>();

    @Override
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        events_to_display.add(new Event("Snoop Dogg & vinho verde", "monday", "2/2/2012", "3€", "15h", "ISCTE", null, false, false));//teste
        adapter=new EventAdapter(getActivity(), events_to_display);

        final ListView event_list = (ListView) rootView.findViewById(R.id.event_list);
        event_list.setAdapter(adapter);

        return rootView;
    }

}
