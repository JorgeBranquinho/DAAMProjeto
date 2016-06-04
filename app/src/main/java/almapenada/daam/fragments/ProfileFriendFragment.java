package almapenada.daam.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import almapenada.daam.R;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventAdapter;
import almapenada.daam.utility.EventsDatabase;

public class ProfileFriendFragment extends Fragment {
    private View rootView;

    //private EventAdapter adapter;
    private ArrayAdapter<String> adapter;
    private ArrayList<Event> events_to_display =new ArrayList<Event>();
    private ArrayList<Event> full_event_list =new ArrayList<Event>();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ListView event_list;
    private EventsDatabase database;
    private Button addRemove;
    private ArrayList<String> lista =new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_friend, container, false);



        database = new EventsDatabase(getActivity());
        if(database.isEmpty())
            database.populateWithExample();

        populateEventList(database.getAllEvents());

        //adapter=new EventAdapter(getActivity(), full_event_list);
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.activity_list_view,lista);
        event_list = (ListView) rootView.findViewById(R.id.eventos);
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

    private void populateEventList(Cursor cursor) {
        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Event e = (new EnumDatabase()).cursorToEvent(cursor);
                full_event_list.add(e);
                cursor.moveToNext();
            }
        }

        for(int i=0; i<full_event_list.size(); i++){
            lista.add(full_event_list.get(i).getEventName());
        }
        }


}

