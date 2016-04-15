package almapenada.daam.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import almapenada.daam.R;
import almapenada.daam.utility.DatePickerFragment;

public class CreateEventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);
        ImageView event_img=(ImageView) v.findViewById(R.id.event_img);
        TextView event_name = (TextView)v.findViewById(R.id.event_name);
        final RadioButton eventPrivate=(RadioButton)v.findViewById(R.id.eventPrivate);
        final RadioButton eventPublic=(RadioButton)v.findViewById(R.id.eventPublic);
        Button date_picker=(Button)v.findViewById(R.id.date_picker);
        final Switch switch1 = (Switch)v.findViewById(R.id.switch1);
        final TextView event_end = (TextView)v.findViewById(R.id.event_end);
        final Button date_end_picker=(Button)v.findViewById(R.id.date_end_picker);
        EditText event_location_input=(EditText)v.findViewById(R.id.event_location_input);
        EditText event_price_input=(EditText)v.findViewById(R.id.event_price_input);
        EditText event_description_input=(EditText)v.findViewById(R.id.event_description_input);
        Button event_people=(Button)v.findViewById(R.id.event_people);
        CheckBox event_invitable_friends = (CheckBox)v.findViewById(R.id.event_invitable_friends);
        Button event_done=(Button)v.findViewById(R.id.event_done);


        eventPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventPrivate.isChecked())
                    eventPublic.setChecked(false);
            }
        });
        eventPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventPublic.isChecked())
                    eventPrivate.setChecked(false);
            }
        });
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked()){
                    date_end_picker.setEnabled(true);
                    event_end.setEnabled(true);
                }else{
                    date_end_picker.setEnabled(false);
                    event_end.setEnabled(false);
                }
            }
        });
        date_end_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        return v;
    }

}
