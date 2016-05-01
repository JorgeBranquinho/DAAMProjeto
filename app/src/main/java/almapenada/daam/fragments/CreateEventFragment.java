package almapenada.daam.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import almapenada.daam.R;
//import almapenada.daam.utility.DatePickerFragment;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;

public class CreateEventFragment extends Fragment {

    private CreateEventFragment self= this;
    public static Button date_picker;
    public static Button date_end_picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);
        ImageView event_img = (ImageView) v.findViewById(R.id.event_img);
        final TextView event_name = (TextView) v.findViewById(R.id.event_name);
        final RadioButton eventPrivate = (RadioButton) v.findViewById(R.id.eventPrivate);
        final RadioButton eventPublic = (RadioButton) v.findViewById(R.id.eventPublic);
        date_picker = (Button) v.findViewById(R.id.date_picker);
        final Switch switch1 = (Switch) v.findViewById(R.id.switch1);
        final TextView event_end = (TextView) v.findViewById(R.id.event_end);
        date_end_picker = (Button) v.findViewById(R.id.date_end_picker);
        final EditText event_location_input = (EditText) v.findViewById(R.id.event_location_input);
        final EditText event_price_input = (EditText) v.findViewById(R.id.event_price_input);
        final EditText event_description_input = (EditText) v.findViewById(R.id.event_description_input);
        Button event_people = (Button) v.findViewById(R.id.event_people);
        CheckBox event_invitable_friends = (CheckBox) v.findViewById(R.id.event_invitable_friends);
        Button event_done = (Button) v.findViewById(R.id.event_done);

        event_location_input.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        event_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_name.setText("");
            }
        });
        event_price_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_price_input.setText("");
            }
        });
        event_location_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_location_input.setText("");
            }
        });
        event_description_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_description_input.setText("");
            }
        });

        eventPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventPrivate.isChecked())
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
                DialogFragment picker = new DatePickerFragment2(0);
                picker.show(getFragmentManager(), "datePicker");
            }
        });
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked()) {
                    date_end_picker.setEnabled(true);
                    event_end.setEnabled(true);
                } else {
                    date_end_picker.setEnabled(false);
                    event_end.setEnabled(false);
                }
            }
        });
        date_end_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment2(1);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        event_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!event_name.getText().toString().equals("")) {
                    EventsDatabase database = new EventsDatabase(getActivity().getBaseContext());
                    database.insertEvent(new Event(0, event_name.getText().toString(), "monday", date_picker.getText().toString(), event_price_input.getText().toString(), "15h", event_location_input.getText().toString(), new LatLng(38.748753, -9.153692), null, false, false));
                    //TODO:mudar de pagina
                }else{
                    Toast.makeText(getContext(), "Event name is Empty", Toast.LENGTH_SHORT);
                }
            }
        });

        return v;
    }


    public static class DatePickerFragment2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private int type=-1;

        public DatePickerFragment2(){};

        public DatePickerFragment2(int i) {
            this.type=i;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");//"yyyy-MM-dd");
            String formattedDate = sdf.format(c.getTime());
            if(type==0) date_picker.setText(formattedDate);
            if(type==1) date_end_picker.setText(formattedDate);
        }

    }
}
//TODO: botao para as horas