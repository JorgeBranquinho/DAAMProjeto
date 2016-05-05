package almapenada.daam.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import almapenada.daam.R;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;

public class CreateEventFragment extends Fragment {

    private CreateEventFragment self= this;
    public static Button date_picker;
    public static Button date_end_picker;
    private static final int SELECT_IMAGE = 1;
    private String filePath="";
    private Button event_img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);
        event_img = (Button) v.findViewById(R.id.event_img);
        final TextView event_name = (TextView) v.findViewById(R.id.event_name);
        final RadioButton eventPrivate = (RadioButton) v.findViewById(R.id.eventPrivate);
        final RadioButton eventPublic = (RadioButton) v.findViewById(R.id.eventPublic);
        final RadioButton event_price = (RadioButton) v.findViewById(R.id.event_price);
        final RadioButton event_location = (RadioButton) v.findViewById(R.id.event_location);
        date_picker = (Button) v.findViewById(R.id.date_picker);
        final Switch switch1 = (Switch) v.findViewById(R.id.switch1);
        final TextView event_end = (TextView) v.findViewById(R.id.event_end);
        date_end_picker = (Button) v.findViewById(R.id.date_end_picker);
        final Button event_location_input = (Button) v.findViewById(R.id.event_location_input);
        final EditText event_price_input = (EditText) v.findViewById(R.id.event_price_input);
        final EditText event_description_input = (EditText) v.findViewById(R.id.event_description_input);
        Button event_people = (Button) v.findViewById(R.id.event_people);
        CheckBox event_invitable_friends = (CheckBox) v.findViewById(R.id.event_invitable_friends);
        Button event_done = (Button) v.findViewById(R.id.event_done);


        event_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });

        event_description_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText(getContext());
                alert.setTitle("Insert a description");
                alert.setView(edittext);
                edittext.setText(event_description_input.getText());
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        event_description_input.setText(edittext.getText().toString());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //nao fazer nada
                    }
                });
                alert.show();
            }
        });

        event_name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

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
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final String regexStr = "^[0-9]*$";
                final EditText lat = new EditText(getContext());
                final EditText lng = new EditText(getContext());
                lat.setText("latitude");
                lng.setText("longitude");
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                alert.setTitle("Insert latitude and longitude");
                layout.addView(lat);
                layout.addView(lng);
                alert.setView(layout);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(lat.getText().toString().trim().matches(regexStr) && lng.getText().toString().trim().matches(regexStr))
                            event_location_input.setText(lat.getText().toString() + "|" + lng.getText().toString());
                        else event_location_input.setText("invalid");
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //nao fazer nada
                    }
                });
                alert.show();
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
        event_price.setOnClickListener(new View.OnClickListener() {
            private boolean checked=false;
            @Override
            public void onClick(View v) {
                if (!checked) {
                    event_price.setChecked(true);
                    event_price_input.setEnabled(true);
                }else {
                    event_price.setChecked(false);
                    event_price_input.setEnabled(false);
                }
                checked=!checked;
            }
        });
        event_location.setOnClickListener(new View.OnClickListener() {
            private boolean checked=false;
            @Override
            public void onClick(View v) {
                if (!checked) {
                    event_location.setChecked(true);
                    event_location_input.setEnabled(true);
                }else {
                    event_location.setChecked(false);
                    event_location_input.setEnabled(false);
                }
                checked=!checked;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//se a API é 15+
                        event_img.setBackground(new BitmapDrawable(getContext().getResources(),yourSelectedImage));
                    }
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
//TODO: botao para as horas