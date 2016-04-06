package almapenada.daam.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.Event;

public class EventDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_details, container, false);
        Event e = (Event) this.getArguments().getSerializable("evento");

        ((DrawerActivity) getActivity()).HideFabIcon();// Para anular:((DrawerActivity) getActivity()).showFabIcon();

        TextView event_name = (TextView) v.findViewById(R.id.event_name);
        TextView event_date= (TextView) v.findViewById(R.id.event_date);
        TextView event_price= (TextView) v.findViewById(R.id.event_price);
        TextView event_time= (TextView) v.findViewById(R.id.event_time);
        TextView event_location= (TextView) v.findViewById(R.id.event_location);
        TextView event_guests= (TextView) v.findViewById(R.id.event_guests);
        TextView event_description= (TextView) v.findViewById(R.id.event_description);
        Button event_comments= (Button) v.findViewById(R.id.event_comments);

        event_name.setText(e.getEventName());
        event_date.setText(e.getWeekDay() + ", " + e.getDate());
        if(e.getPrice().equals(""))
            event_price.setText("Price: " + e.getPrice());
        else
            event_price.setText("Price N/A");
        event_time.setText(e.getHours());
        event_location.setText(e.getLocation());


        return v;
    }



}
