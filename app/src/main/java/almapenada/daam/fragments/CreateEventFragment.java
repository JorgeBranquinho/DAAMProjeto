package almapenada.daam.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;
import almapenada.daam.utility.ScalingUtilies;

public class CreateEventFragment extends Fragment {

    private CreateEventFragment self= this;
    public static Button date_picker;
    public static Button date_end_picker;
    private static final int SELECT_IMAGE = 1;
    private String filePath="";
    private Button event_img;
    private static final int SELECT_PICTURE = 1;

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
        final CheckBox event_invitable_friends = (CheckBox) v.findViewById(R.id.event_invitable_friends);
        Button event_done = (Button) v.findViewById(R.id.event_done);


        event_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
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
                //final String regexStr = "^[0-9]*$";
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
                        //if(lat.getText().toString().trim().matches(regexStr) && lng.getText().toString().trim().matches(regexStr))
                            event_location_input.setText(lat.getText().toString() + " " + lng.getText().toString());
                        //else event_location_input.setText("invalid");
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

                    String dayOfTheWeek="";
                    String date="";
                    String hours="";
                    if(!date_picker.getText().toString().equals("Pick Date")) {
                        String[] datetime = date_picker.getText().toString().split(" ");

                        if (datetime.length >= 2)
                            hours = datetime[1];
                        else
                            hours = " - ";

                        dayOfTheWeek = getDayOfTheWeek(datetime[0]);
                        date=datetime[0];
                    }
                    String dateEnd;
                    if(!date_end_picker.getText().toString().equals("Pick Date") || switch1.isChecked())
                        dateEnd=date_end_picker.getText().toString();
                    else
                        dateEnd=" - ";

                    String price;
                    if(!event_price_input.getText().toString().equals("Price") || event_price.isChecked())
                        price=event_price_input.getText().toString();
                    else
                        price=" - ";

                    Event e=new Event(0, event_name.getText().toString(), eventPublic.isChecked(), dayOfTheWeek, date, switch1.isActivated(), dateEnd, event_price.isChecked(), price, hours, event_location.isChecked(), event_location_input.getText().toString(), event_invitable_friends.isChecked(), true, false, filePath, event_description_input.getText().toString());
                    long id = database.insertEvent(e);
                    EventAddID(database, e, (int) id, event_location_input.getText().toString());
                    database.close();
                    ((DrawerActivity) getActivity()).viewFragment(new EventsFragment(), getResources().getString(R.string.title_events), true, R.drawable.plus);
                }else{
                    Toast.makeText(getContext(), "Event name is Empty", Toast.LENGTH_SHORT);
                }
            }
        });
        return v;
    }

    private void EventAddID(EventsDatabase database, Event e, int id, String event_location_input) {//so se obtem o id depois de add o evento. É preciso fazer update.
        System.out.println(event_location_input);
        e.setId(id);
        ContentValues values = new ContentValues();
        values.put(EnumDatabase.FIELD_ID, e.getId());
        values.put(EnumDatabase.FIELD_NAME, e.getEventName());
        values.put(EnumDatabase.FIELD_isPUBLIC, e.isPublic());
        values.put(EnumDatabase.FIELD_WEEKDAY, e.getWeekDay());
        values.put(EnumDatabase.FIELD_DATE, e.getDate());
        values.put(EnumDatabase.FIELD_isENDDATE, e.isEndDate());
        values.put(EnumDatabase.FIELD_ENDDATE, e.getEnddate());
        values.put(EnumDatabase.FIELD_isPRICE, e.isPrice());
        values.put(EnumDatabase.FIELD_PRICE, e.getPrice());
        values.put(EnumDatabase.FIELD_HOURS, e.getHours());
        values.put(EnumDatabase.FIELD_isLOCATION, e.isLocation());
        values.put(EnumDatabase.FIELD_LOCATION_latlng, event_location_input);
        values.put(EnumDatabase.FIELD_FRIENDS_INVITE, e.isFriendsInvitable());
        values.put(EnumDatabase.FIELD_GOING, e.isGoing());
        values.put(EnumDatabase.FIELD_NEW, e.isNewEvent());
        database.update(e.getId(), values);
    }

    private String getDayOfTheWeek(String string) {
        String dayOfTheWeek="";
        try {
            SimpleDateFormat dmy=new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat mdy=new SimpleDateFormat("MM/dd/yyyy");
            Date date=dmy.parse(string);
            String outputDateStr = mdy.format(date);
            Date d = mdy.parse(outputDateStr);//new Date(outputDateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            dayOfTheWeek = sdf.format(d);
        } catch (ParseException e) {
            dayOfTheWeek=" - ";
        }
        return dayOfTheWeek;
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
            final String formattedDate = sdf.format(c.getTime());

            if(type==0) date_picker.setText(formattedDate);
            if(type==1) date_end_picker.setText(formattedDate);

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                 @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                     String datetime=formattedDate + " " + selectedHour + ":" + selectedMinute;
                     if(type==0) date_picker.setText(datetime);
                     if(type==1) date_end_picker.setText(datetime);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == SELECT_IMAGE) {
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
        }*/
        if (resultCode == getActivity().RESULT_OK) {
            filePath = "";
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (data != null) {
                    filePath = getPath(selectedImageUri);
                    filePath = decodeFile(filePath,125,125);
                    Bitmap yourSelectedImage = getImageBitmap(filePath);
                    //Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        event_img.setBackground(new BitmapDrawable(getContext().getResources(), yourSelectedImage));
                    }
                }
            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        Cursor cursor;
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
        }else{
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        }
        //Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        //Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap getImageBitmap(String filePath) {
        Bitmap bm = null;
        bm = BitmapFactory.decodeFile(filePath);
        return bm;
    }

    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = almapenada.daam.utility.ScalingUtilies.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = almapenada.daam.utility.ScalingUtilies.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilies.ScalingLogic.FIT);
            } else {
               if (unscaledBitmap != null && !unscaledBitmap.isRecycled()) {
                    unscaledBitmap.recycle();
                }
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
            if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
               scaledBitmap.recycle();
            }
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    class CreateEventTask extends AsyncTask<Void, String, String> {

        private String name;
        private String email;
        private String password;
        private String telefone;
        private String image;
        private String host = "https://eventservice-daam.rhcloud.com";
        private String methodInsert = "/insert/event/";

        public CreateEventTask(String name, String email, String password, String telefone, String image) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.telefone = telefone;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String response = "";
            try {
                Boolean validEmail = true;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(host + methodInsert);

                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("email", email);
                json.put("password", password);
                json.put("telephone", telefone);
                json.put("image", image);

                String message = json.toString();

                post.setEntity(new StringEntity(message, "UTF8"));
                post.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(post);
                if (httpResponse != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                    response = reader.readLine();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != "") {
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(result);
                    System.out.println("Resposta: " + jobj.get("status").toString());

                    if (jobj.get("status").toString().compareTo("OK") == 0) {
                        //showDialogMessage("Utilizador criado com sucesso!", true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}