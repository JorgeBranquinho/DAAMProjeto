package almapenada.daam.utility;

import android.app.Activity;
import android.database.Cursor;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Asus on 10/04/2016.
 */
public class EnumDatabase {
    //isto nao é um enum mas é um sitio para guardar constantes

    /**EventsDB**/
    public static final String DB_EVENT = "EVENT_DB";
    public final static String TABLE_EVENT= "EVENT_TB";

    public final static String FIELD_ID = "_id";
    public final static String FIELD_NAME = "event_name";
    public final static String FIELD_isPUBLIC = "event_isPublic";
    public final static String FIELD_WEEKDAY = "event_weekday";
    public final static String FIELD_DATE = "event_date";
    public final static String FIELD_isENDDATE = "event_isEndDate";
    public final static String FIELD_ENDDATE = "event_endDate";
    public final static String FIELD_isPRICE = "event_isPrice";
    public final static String FIELD_PRICE = "event_price";
    public final static String FIELD_HOURS = "event_hours";
    public final static String FIELD_isLOCATION = "event_isLocation";
    //public final static String FIELD_LOCATION = "event_location";
    //public final static String FIELD_LOCATION_lat = "event_locationLat";
    //public final static String FIELD_LOCATION_lng = "event_locationLng";
    public final static String FIELD_LOCATION_latlng = "event_locationLatLng";
    //public final static String FIELD_LOCATION_URI = "event_locationURI";//nao usado
    public final static String FIELD_FRIENDS_INVITE = "event_freindsInvite";
    public final static String FIELD_GOING = "event_going";
    public final static String FIELD_NEW = "event_new";
    public final static String FIELD_FILEPATH = "event_filepath";
    public final static String FIELD_DESCRIPTION = "event_description";


    /**SuggestionsDB**/
    public static final String DB_SUGGESTION = "SUGGESTION_DB";
    public final static String TABLE_SUGGESTION = "SUGGESTION_TB";

    public final static String FIELD_ID_sug = "_id";
    public final static String FIELD_SUGGESTION = "suggestion";


    public static Event cursorToEvent(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex("event_name"));
        boolean isPublic;
        if(cursor.getInt(cursor.getColumnIndex("event_isPublic"))==0)
            isPublic = false;
        else
            isPublic = true;
        String weekday = cursor.getString(cursor.getColumnIndex("event_weekday"));
        String date = cursor.getString(cursor.getColumnIndex("event_date"));
        boolean isEndDate;
        if(cursor.getInt(cursor.getColumnIndex("event_endDate"))==0)
            isEndDate = false;
        else
            isEndDate = true;
        String endDate = cursor.getString(cursor.getColumnIndex("event_isEndDate"));
        boolean isPrice;
        if(cursor.getInt(cursor.getColumnIndex("event_isPrice"))==0)
            isPrice = false;
        else
            isPrice = true;
        String price = cursor.getString(cursor.getColumnIndex("event_price"));
        String hours = cursor.getString(cursor.getColumnIndex("event_hours"));
        boolean isLocation;
        if(cursor.getInt(cursor.getColumnIndex("event_isLocation"))==0)
            isLocation = false;
        else
            isLocation = true;
        String locationLatLng = cursor.getString(cursor.getColumnIndex("event_locationLatLng"));
        boolean friends_invite;
        if(cursor.getInt(cursor.getColumnIndex("event_freindsInvite"))==0)
            friends_invite=false;
        else
            friends_invite=true;
        boolean going;
        if(cursor.getInt(cursor.getColumnIndex("event_going"))==0)
            going=false;
        else
            going=true;
        boolean new_event;
        if(cursor.getInt(cursor.getColumnIndex("event_new"))==0)
            new_event = false;
        else
            new_event = true;
        String description = cursor.getString(cursor.getColumnIndex("event_description"));
        String filepath = cursor.getString(cursor.getColumnIndex("event_filepath"));
        return new Event(id, name, isPublic, weekday, date, isEndDate, endDate, isPrice, price, hours, isLocation, locationLatLng, friends_invite, going, new_event, filepath, description);
    }

    public int getScreenDensity(Activity a){
        int density= a.getResources().getDisplayMetrics().densityDpi;
        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW:
                return 1;
            case DisplayMetrics.DENSITY_MEDIUM:
                return 2;
            case DisplayMetrics.DENSITY_HIGH:
                return 3;
            case DisplayMetrics.DENSITY_XHIGH:
                return 4;
            case DisplayMetrics.DENSITY_XXHIGH:
                return 5;
            default: return 5;
        }
    }
}
