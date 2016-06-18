package almapenada.daam.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.CustomRowAdapter;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;
import almapenada.daam.utility.User;

public class EventDetailsFragment extends Fragment {

    private List<User> friends = new ArrayList<User>();
    private String[] dummy_users;
    private boolean sync=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_details, container, false);
        final Event e = (Event) this.getArguments().getSerializable("evento");

        ((DrawerActivity) getActivity()).HideFabIcon();
        new DownloadFriendsTask().execute();

        ImageView event_img = (ImageView) v.findViewById(R.id.event_img);
        TextView event_name = (TextView) v.findViewById(R.id.event_name);
        TextView event_date = (TextView) v.findViewById(R.id.event_date);
        TextView event_price = (TextView) v.findViewById(R.id.event_price);
        TextView event_time = (TextView) v.findViewById(R.id.event_time);
        TextView event_location = (TextView) v.findViewById(R.id.event_location);
        TextView event_guests = (TextView) v.findViewById(R.id.event_guests);
        TextView event_description = (TextView) v.findViewById(R.id.event_description);
        //Button event_comments= (Button) v.findViewById(R.id.event_comments);


        if (e.isNewEvent()) {
            updateNewEvent(e);
        }

        event_name.setText(e.getEventName());
        if (!e.getWeekDay().equals("") || !e.getDate().equals(""))
            event_date.setText(e.getWeekDay() + ", " + e.getDate());
        if (e.isPrice() || !e.getPrice().equals(""))
            event_price.setText("Price: " + e.getPrice());
        else
            event_price.setText("Price: N/A");
        event_time.setText(e.getHours());
        event_location.setText(e.getLocation());
        event_description.setText(e.getDescription());
        File img = new File(e.getFilepath());
        if (img.exists()) {
            Bitmap btmp = BitmapFactory.decodeFile(img.getAbsolutePath());
            event_img.setImageBitmap(btmp);
        }
        final LayoutInflater inflaterfinal = inflater;
        event_guests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CharSequence guests[] = new CharSequence[] {"Ze", "Maria", "To Ze", "Snoop Dogg"};
                if(sync) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(dummy_users, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                        }
                    });
                    builder.show();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_event);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                if (e.isLocation()) {
                    Marker mMarker = googleMap.addMarker(new MarkerOptions()
                            .title(e.getEventName())
                            .position(e.getLocation_latlng()));
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(e.getLocation_latlng().latitude, e.getLocation_latlng().longitude));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }/*else{
                    System.out.println("Este evento nao tem gps");
                }*/
            }
        });


        return v;
    }

    private void updateNewEvent(Event e) {
        EventsDatabase database = new EventsDatabase(getActivity().getBaseContext());
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
        values.put(EnumDatabase.FIELD_LOCATION_latlng, e.getLocation_latlng().latitude + " " + e.getLocation_latlng().longitude);
        values.put(EnumDatabase.FIELD_FRIENDS_INVITE, e.isFriendsInvitable());
        values.put(EnumDatabase.FIELD_GOING, e.isGoing());
        values.put(EnumDatabase.FIELD_NEW, false);
        database.update(e.getId(), values);
        database.close();
    }

    private class DownloadFriendsTask extends AsyncTask<URL, Void, Void> {

        protected Void doInBackground(URL... url) {
            String response = "";
            Bundle b = getActivity().getIntent().getExtras();
            User user = (User) b.getSerializable("User");
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://eventservice-daam.rhcloud.com/getAll/friends/byUser/1"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                response = reader.readLine();
                jsonToUser(response);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            User[] temp_dummy_users = friends.toArray(new User[friends.size()]);
            dummy_users = new String[temp_dummy_users.length];
            for (int i = 0; i < temp_dummy_users.length; i++) {
                dummy_users[i] = temp_dummy_users[i].getFirstName() + " " + temp_dummy_users[i].getLastName();
            }
            sync=true;
        }
    }

    private void jsonToUser(String response) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(response);
            if (jobj.get("status").toString().compareTo("OK") == 0) {
                JSONArray poi = jobj.getJSONArray("teste");
                for (int i = 0; i < poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);
                    User x = new User();
                    //x.setIdUser(Integer.parseInt(t_poi.getString("id")));
                    x.setFirstName(t_poi.getString("name").substring(0, t_poi.getString("name").lastIndexOf(" ")));
                    x.setLastName(t_poi.getString("name").substring(t_poi.getString("name").lastIndexOf(" ") + 1));
                    friends.add(x);
                }
            }
            //dummy_users = (User[]) friends.toArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
