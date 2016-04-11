package almapenada.daam.utility;

import android.database.Cursor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Asus on 10/04/2016.
 */
public class EnumEventsDatabase {
    //isto nao é um enum mas é um sitio para guardar constantes
    public static final String DB_EVENT = "EVENT_DB";
    public final static String TABLE_EVENT= "EVENT_TB";

    public final static String FIELD_ID = "_id";
    public final static String FIELD_NAME = "event_name";
    public final static String FIELD_WEEKDAY = "event_weekday";
    public final static String FIELD_DATE = "event_date";
    public final static String FIELD_PRICE = "event_price";
    public final static String FIELD_HOURS = "event_hours";
    public final static String FIELD_LOCATION = "event_location";
    public final static String FIELD_LOCATION_URI = "event_locationURI";
    public final static String FIELD_GOING = "event_going";
    public final static String FIELD_NEW = "event_new";

    public Event cursorToEvent(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex("event_name"));
        String weekday = cursor.getString(cursor.getColumnIndex("event_weekday"));
        String date = cursor.getString(cursor.getColumnIndex("event_date"));
        String price = cursor.getString(cursor.getColumnIndex("event_price"));
        String hours = cursor.getString(cursor.getColumnIndex("event_hours"));
        String location = cursor.getString(cursor.getColumnIndex("event_location"));
        URI locationURI = null;
        try {
            locationURI = new URI(cursor.getString(cursor.getColumnIndex("event_locationURI")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        return new Event(id, name, weekday, date, price, hours, location, locationURI, going, new_event);
    }
}
