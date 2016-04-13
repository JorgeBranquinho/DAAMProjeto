package almapenada.daam.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

public class EventsDatabase {
        private SQLiteDatabase db;
    private Helper helper;

    public EventsDatabase(Context context) {

        helper = new Helper(context, EnumDatabase.DB_EVENT, null, 1);
        db = helper.getWritableDatabase();
    }

    public void populateWithExample(){
        insertEvent(new Event(0, "festa do Seixo Paulo", "monday", "1/2/2012", "15", "15h", "ISCTE", new LatLng(38.748753, -9.153692), null, false, false));//teste
        insertEvent(new Event(1, "frango assado", "monday", "3/2/2012", "1", "15h", "ISCTE", new LatLng(-9.153692, 38.748753), null, false, false));//teste
        insertEvent(new Event(2, "Snoop Dogg & vinho verde", "monday", "2/2/2012", "3", "15h", "ISCTE", null, null, false, false));//teste
        insertEvent(new Event(3, "festa de azeite", "monday", "2/2/2012", "3", "15h", "ISCTE", null, null, false, false));//teste
        insertEvent(new Event(4, "makumba", "monday", "4/2/2012", "", "15h", "ISCTE", null, null, false, false));//teste
    }

    public long insertEvent(Event e)
    {
        ContentValues values = new ContentValues();
        values.put(EnumDatabase.FIELD_ID, e.getId());
        values.put(EnumDatabase.FIELD_NAME, e.getEventName());
        values.put(EnumDatabase.FIELD_WEEKDAY, e.getWeekDay());
        values.put(EnumDatabase.FIELD_DATE, e.getDate());
        values.put(EnumDatabase.FIELD_PRICE, e.getPrice());
        values.put(EnumDatabase.FIELD_HOURS, e.getHours());
        values.put(EnumDatabase.FIELD_LOCATION, e.getLocation());
        if(e.getLocation_latlng()==null){
            values.put(EnumDatabase.FIELD_LOCATION_lat, -1);
            values.put(EnumDatabase.FIELD_LOCATION_lng, -1);
        }else {
            values.put(EnumDatabase.FIELD_LOCATION_lat, e.getLocation_latlng().latitude);
            values.put(EnumDatabase.FIELD_LOCATION_lng, e.getLocation_latlng().longitude);
        }
        if(e.getLocation_URI()!=null)
            values.put(EnumDatabase.FIELD_LOCATION_URI, e.getLocation_URI().toString());
        else
            values.put(EnumDatabase.FIELD_LOCATION_URI,"");
        values.put(EnumDatabase.FIELD_GOING, e.isGoing());
        values.put(EnumDatabase.FIELD_NEW, e.isNewEvent());
        return db.insert(EnumDatabase.TABLE_EVENT, null, values);
    }

    public boolean update(int id, ContentValues values ){
        long i = db.update(EnumDatabase.TABLE_EVENT, values, EnumDatabase.FIELD_ID + "=" + id, null );
        return i>0;
    }

    public boolean deleteById(int id){
        return db.delete(EnumDatabase.TABLE_EVENT, EnumDatabase.FIELD_ID + "=" + id, null) > 0;
    }

    public Cursor getEventByName(String text)
    {
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_LOCATION, EnumDatabase.FIELD_LOCATION_lat, EnumDatabase.FIELD_LOCATION_lng, EnumDatabase.FIELD_LOCATION_URI, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                EnumDatabase.FIELD_NAME + "=" + text, null, null, null, null);
    }

    public Cursor getEventById(int id){
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_LOCATION, EnumDatabase.FIELD_LOCATION_lat, EnumDatabase.FIELD_LOCATION_lng, EnumDatabase.FIELD_LOCATION_URI, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                EnumDatabase.FIELD_ID + "=" + id, null, null, null, null);
    }

    public Cursor getAllEvents(){
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_LOCATION, EnumDatabase.FIELD_LOCATION_lat, EnumDatabase.FIELD_LOCATION_lng, EnumDatabase.FIELD_LOCATION_URI, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                null, null, null, null, null);
    }

    public boolean isEmpty(){
        String count = "SELECT count(*) FROM " + EnumDatabase.TABLE_EVENT;
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
            db.execSQL("CREATE TABLE "+ EnumDatabase.TABLE_EVENT+" ("+
                    EnumDatabase.FIELD_ID+" integer primary key autoincrement, "+ EnumDatabase.FIELD_NAME +" text, " +
                    EnumDatabase.FIELD_WEEKDAY + " text, " + EnumDatabase.FIELD_DATE+ " date, " + EnumDatabase.FIELD_PRICE + " text, " +
                    EnumDatabase.FIELD_HOURS + " text, " + EnumDatabase.FIELD_LOCATION + " text, " + EnumDatabase.FIELD_LOCATION_lat + " double, "
                    + EnumDatabase.FIELD_LOCATION_lng + " double, " + EnumDatabase.FIELD_LOCATION_URI + " text, " + EnumDatabase.FIELD_GOING +
                    " boolean, " + EnumDatabase.FIELD_NEW + " boolean);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
