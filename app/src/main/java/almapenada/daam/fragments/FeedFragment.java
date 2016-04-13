package almapenada.daam.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import almapenada.daam.R;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventFeedAdapter;


public class FeedFragment extends Fragment {

    private ArrayList<Event> events_to_display_on_feed =new ArrayList<Event>();
    private ListView list;
    private EventFeedAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        events_to_display_on_feed.add(new Event(0,"Snoop Dogg & vinho verde", "monday", "2/2/2012", "3", "15h", "ISCTE", null, null, false, false));//teste

        adapter = new EventFeedAdapter(getActivity(), events_to_display_on_feed);

        list = (ListView) rootView.findViewById(R.id.feed_event_list);
        list.setAdapter(adapter);

        return rootView;
    }

}
