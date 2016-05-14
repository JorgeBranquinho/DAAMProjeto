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
        insertEvent(new Event(1,"Festa no ISCTE", true, "Segunda-feira", "09/05/2016", false, "", true, "3", "18:30", true, "38.748753 -9.153692", true, true, false));//teste
        insertEvent(new Event(2,"Frango assado", false, "Segunda-feira", "09/05/2016", false, "", false, "", "", false, "", false, false, false));//teste
    }

    public long insertEvent(Event e){
        ContentValues values = new ContentValues();
        //values.put(EnumDatabase.FIELD_ID, e.getId());
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
        if(!e.isLocation()){
            values.put(EnumDatabase.FIELD_LOCATION_latlng, "");
        }else {
            values.put(EnumDatabase.FIELD_LOCATION_latlng, e.getLocation_latlng().latitude + " " + e.getLocation_latlng().longitude);
        }
        values.put(EnumDatabase.FIELD_FRIENDS_INVITE, e.isFriendsInvitable());
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
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_isPUBLIC, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_isENDDATE, EnumDatabase.FIELD_ENDDATE, EnumDatabase.FIELD_isPRICE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_isLOCATION, EnumDatabase.FIELD_LOCATION_latlng, EnumDatabase.FIELD_FRIENDS_INVITE, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                EnumDatabase.FIELD_NAME + "=" + text, null, null, null, null);
    }

    public Cursor getEventById(int id){
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_isPUBLIC, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_isENDDATE, EnumDatabase.FIELD_ENDDATE, EnumDatabase.FIELD_isPRICE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_isLOCATION, EnumDatabase.FIELD_LOCATION_latlng, EnumDatabase.FIELD_FRIENDS_INVITE, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                EnumDatabase.FIELD_ID + "=" + id, null, null, null, null);
    }

    public Cursor getAllEvents(){
        return db.query(EnumDatabase.TABLE_EVENT, new String[]{EnumDatabase.FIELD_ID, EnumDatabase.FIELD_NAME, EnumDatabase.FIELD_isPUBLIC, EnumDatabase.FIELD_WEEKDAY, EnumDatabase.FIELD_DATE, EnumDatabase.FIELD_isENDDATE, EnumDatabase.FIELD_ENDDATE, EnumDatabase.FIELD_isPRICE, EnumDatabase.FIELD_PRICE, EnumDatabase.FIELD_HOURS, EnumDatabase.FIELD_isLOCATION, EnumDatabase.FIELD_LOCATION_latlng, EnumDatabase.FIELD_FRIENDS_INVITE, EnumDatabase.FIELD_GOING, EnumDatabase.FIELD_NEW},
                null, null, null, null, null);
    }

    public boolean isEmpty(){
        String count = "SELECT count(*) FROM " + EnumDatabase.TABLE_EVENT;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        if(mcursor.getInt(0)>0) return false;
        else return true;
    }

    public void close(){
        db.close();
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
                    EnumDatabase.FIELD_ID+" integer primary key autoincrement, "+ EnumDatabase.FIELD_NAME +" text, " +  EnumDatabase.FIELD_isPUBLIC +" boolean, " +
                    EnumDatabase.FIELD_WEEKDAY + " text, " + EnumDatabase.FIELD_DATE+ " date, " + EnumDatabase.FIELD_isENDDATE +" boolean, " + EnumDatabase.FIELD_ENDDATE +" text, " +
                    EnumDatabase.FIELD_isPRICE + " boolean, " + EnumDatabase.FIELD_PRICE +" text, " + EnumDatabase.FIELD_HOURS + " text, " + EnumDatabase.FIELD_isLOCATION + " boolean, " +
                    EnumDatabase.FIELD_LOCATION_latlng + " text, " + EnumDatabase.FIELD_FRIENDS_INVITE +" boolean, " +
                    EnumDatabase.FIELD_GOING +" boolean, " + EnumDatabase.FIELD_NEW + " boolean);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
