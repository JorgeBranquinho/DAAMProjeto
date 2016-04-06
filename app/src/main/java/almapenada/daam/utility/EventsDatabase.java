package almapenada.daam.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventsDatabase {
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

    private SQLiteDatabase db;
    private Helper helper;

    public EventsDatabase(Context context) {

        helper = new Helper(context, DB_EVENT, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertEvent(Event e)
    {
        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, e.getEventName());
        values.put(FIELD_WEEKDAY, e.getWeekDay());
        values.put(FIELD_DATE, e.getDate());
        values.put(FIELD_PRICE, e.getPrice());
        values.put(FIELD_HOURS, e.getHours());
        values.put(FIELD_LOCATION, e.getLocation());
        if(e.getLocation_URI()!=null)
            values.put(FIELD_LOCATION_URI, e.getLocation_URI().toString());
        else
            values.put(FIELD_LOCATION_URI,"");
        values.put(FIELD_GOING, e.isGoing());
        values.put(FIELD_NEW, e.isNewEvent());
        return db.insert(TABLE_EVENT, null, values);
    }

    public Cursor getEventByName(String text)
    {
        return db.query(TABLE_EVENT, new String[]{FIELD_ID, FIELD_NAME, FIELD_WEEKDAY, FIELD_DATE, FIELD_PRICE, FIELD_HOURS, FIELD_LOCATION, FIELD_LOCATION_URI, FIELD_GOING, FIELD_NEW},
                FIELD_NAME + "=" + text, null, null, null, null);
    }

    public Cursor getEventById(int id){
        return db.query(TABLE_EVENT, new String[]{FIELD_ID, FIELD_NAME, FIELD_WEEKDAY, FIELD_DATE, FIELD_PRICE, FIELD_HOURS, FIELD_LOCATION, FIELD_LOCATION_URI, FIELD_GOING, FIELD_NEW},
                FIELD_ID + "=" + id, null, null, null, null);
    }

    public boolean isEmpty(){
        String count = "SELECT count(*) FROM " + TABLE_EVENT;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        if(mcursor.getInt(0)>0) return false;
        else return true;
    }

    private class Helper extends SQLiteOpenHelper
    {

        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_EVENT+" ("+
                    FIELD_ID+" integer primary key autoincrement, "+ FIELD_NAME +" text, " +
                    FIELD_WEEKDAY + " text, " + FIELD_DATE+ " date, " + FIELD_PRICE + " text, " +
                    FIELD_HOURS + " text, " + FIELD_LOCATION + " text, " + FIELD_LOCATION_URI + " text, " +
                    FIELD_GOING + " boolean, " + FIELD_NEW + " boolean);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
