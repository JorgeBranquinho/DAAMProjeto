package almapenada.daam.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventsDatabase {
        private SQLiteDatabase db;
    private Helper helper;

    public EventsDatabase(Context context) {

        helper = new Helper(context, EnumEventsDatabase.DB_EVENT, null, 1);
        db = helper.getWritableDatabase();
    }

    public void populateWithExample(){
        insertEvent(new Event(0,"festa do Seixo Paulo", "monday", "1/2/2012", "15€", "15h", "ISCTE", null, false, false));//teste
        insertEvent(new Event(1,"frango assado", "monday", "3/2/2012", "1€", "15h", "ISCTE", null, false, false));//teste
        insertEvent(new Event(2,"Snoop Dogg & vinho verde", "monday", "2/2/2012", "3€", "15h", "ISCTE", null, false, false));//teste
        insertEvent(new Event(3,"festa de azeite", "monday", "2/2/2012", "3€", "15h", "ISCTE", null, false, false));//teste
        insertEvent(new Event(4,"makumba", "monday", "4/2/2012", "", "15h", "ISCTE", null, false, false));//teste
    }

    public long insertEvent(Event e)
    {
        ContentValues values = new ContentValues();
        values.put(EnumEventsDatabase.FIELD_ID, e.getId());
        values.put(EnumEventsDatabase.FIELD_NAME, e.getEventName());
        values.put(EnumEventsDatabase.FIELD_WEEKDAY, e.getWeekDay());
        values.put(EnumEventsDatabase.FIELD_DATE, e.getDate());
        values.put(EnumEventsDatabase.FIELD_PRICE, e.getPrice());
        values.put(EnumEventsDatabase.FIELD_HOURS, e.getHours());
        values.put(EnumEventsDatabase.FIELD_LOCATION, e.getLocation());
        if(e.getLocation_URI()!=null)
            values.put(EnumEventsDatabase.FIELD_LOCATION_URI, e.getLocation_URI().toString());
        else
            values.put(EnumEventsDatabase.FIELD_LOCATION_URI,"");
        values.put(EnumEventsDatabase.FIELD_GOING, e.isGoing());
        values.put(EnumEventsDatabase.FIELD_NEW, e.isNewEvent());
        return db.insert(EnumEventsDatabase.TABLE_EVENT, null, values);
    }

    public boolean update(int id, ContentValues values ){
        long i = db.update(EnumEventsDatabase.TABLE_EVENT, values, EnumEventsDatabase.FIELD_ID + "=" + id, null );
        return i>0;
    }

    public boolean deleteById(int id){
        return db.delete(EnumEventsDatabase.TABLE_EVENT, EnumEventsDatabase.FIELD_ID + "=" + id, null) > 0;
    }

    public Cursor getEventByName(String text)
    {
        return db.query(EnumEventsDatabase.TABLE_EVENT, new String[]{EnumEventsDatabase.FIELD_ID, EnumEventsDatabase.FIELD_NAME, EnumEventsDatabase.FIELD_WEEKDAY, EnumEventsDatabase.FIELD_DATE, EnumEventsDatabase.FIELD_PRICE, EnumEventsDatabase.FIELD_HOURS, EnumEventsDatabase.FIELD_LOCATION, EnumEventsDatabase.FIELD_LOCATION_URI, EnumEventsDatabase.FIELD_GOING, EnumEventsDatabase.FIELD_NEW},
                EnumEventsDatabase.FIELD_NAME + "=" + text, null, null, null, null);
    }

    public Cursor getEventById(int id){
        return db.query(EnumEventsDatabase.TABLE_EVENT, new String[]{EnumEventsDatabase.FIELD_ID, EnumEventsDatabase.FIELD_NAME, EnumEventsDatabase.FIELD_WEEKDAY, EnumEventsDatabase.FIELD_DATE, EnumEventsDatabase.FIELD_PRICE, EnumEventsDatabase.FIELD_HOURS, EnumEventsDatabase.FIELD_LOCATION, EnumEventsDatabase.FIELD_LOCATION_URI, EnumEventsDatabase.FIELD_GOING, EnumEventsDatabase.FIELD_NEW},
                EnumEventsDatabase.FIELD_ID + "=" + id, null, null, null, null);
    }

    public Cursor getAllEvents(){
        return db.query(EnumEventsDatabase.TABLE_EVENT, new String[]{EnumEventsDatabase.FIELD_ID, EnumEventsDatabase.FIELD_NAME, EnumEventsDatabase.FIELD_WEEKDAY, EnumEventsDatabase.FIELD_DATE, EnumEventsDatabase.FIELD_PRICE, EnumEventsDatabase.FIELD_HOURS, EnumEventsDatabase.FIELD_LOCATION, EnumEventsDatabase.FIELD_LOCATION_URI, EnumEventsDatabase.FIELD_GOING, EnumEventsDatabase.FIELD_NEW},
                null, null, null, null, null);
    }

    public boolean isEmpty(){
        String count = "SELECT count(*) FROM " + EnumEventsDatabase.TABLE_EVENT;
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
            db.execSQL("CREATE TABLE "+EnumEventsDatabase.TABLE_EVENT+" ("+
                    EnumEventsDatabase.FIELD_ID+" integer primary key autoincrement, "+ EnumEventsDatabase.FIELD_NAME +" text, " +
                    EnumEventsDatabase.FIELD_WEEKDAY + " text, " + EnumEventsDatabase.FIELD_DATE+ " date, " + EnumEventsDatabase.FIELD_PRICE + " text, " +
                    EnumEventsDatabase.FIELD_HOURS + " text, " + EnumEventsDatabase.FIELD_LOCATION + " text, " + EnumEventsDatabase.FIELD_LOCATION_URI + " text, " +
                    EnumEventsDatabase.FIELD_GOING + " boolean, " + EnumEventsDatabase.FIELD_NEW + " boolean);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
