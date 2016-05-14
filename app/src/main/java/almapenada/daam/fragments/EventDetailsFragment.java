package almapenada.daam.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

import almapenada.daam.DrawerActivity;
import almapenada.daam.R;
import almapenada.daam.utility.EnumDatabase;
import almapenada.daam.utility.Event;
import almapenada.daam.utility.EventsDatabase;

public class EventDetailsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_details, container, false);
        final Event e = (Event) this.getArguments().getSerializable("evento");

        ((DrawerActivity) getActivity()).HideFabIcon();

        ImageView event_img = (ImageView) v.findViewById(R.id.event_img);
        TextView event_name = (TextView) v.findViewById(R.id.event_name);
        TextView event_date= (TextView) v.findViewById(R.id.event_date);
        TextView event_price= (TextView) v.findViewById(R.id.event_price);
        TextView event_time= (TextView) v.findViewById(R.id.event_time);
        TextView event_location= (TextView) v.findViewById(R.id.event_location);
        TextView event_guests= (TextView) v.findViewById(R.id.event_guests);
        TextView event_description= (TextView) v.findViewById(R.id.event_description);
        Button event_comments= (Button) v.findViewById(R.id.event_comments);

        if(e.isNewEvent()){
            updateNewEvent(e);
        }

        event_name.setText(e.getEventName());
        if(!e.getWeekDay().equals("") || !e.getDate().equals(""))
            event_date.setText(e.getWeekDay() + ", " + e.getDate());
        if(e.isPrice() || !e.getPrice().equals(""))
            event_price.setText("Price: " + e.getPrice());
        else
            event_price.setText("Price: N/A");
        event_time.setText(e.getHours());
        event_location.setText(e.getLocation());
        event_description.setText(e.getDescription());
        File img = new File(e.getFilepath());
        if(img.exists()) {
            Bitmap btmp= BitmapFactory.decodeFile(img.getAbsolutePath());
            event_img.setImageBitmap(btmp);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_event);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                if(e.isLocation()) {
                    Marker mMarker = googleMap.addMarker(new MarkerOptions()
                            .title(e.getEventName())
                            .position(e.getLocation_latlng()));
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(e.getLocation_latlng().latitude, e.getLocation_latlng().longitude));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }else{
                    System.out.println("Este evento nao tem gps");
                }
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

}
