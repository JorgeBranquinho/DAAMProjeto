package almapenada.daam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import almapenada.daam.utility.EventAdapter;
import almapenada.daam.R;


public class EventListFragment extends Fragment {

    private View rootView;
    private EventAdapter adapter;

    @Override
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

        final ListView list_friends = (ListView) rootView.findViewById(R.id.event_list);
        list_friends.setAdapter(adapter);

        return rootView;
    }

}
