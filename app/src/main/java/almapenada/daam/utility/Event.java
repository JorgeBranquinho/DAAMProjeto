package almapenada.daam.utility;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by Asus on 20/03/2016.
 */
public class Event implements Serializable {
    private String eventName="";
    private String weekDay="";
    private String date="";
    private String price="";
    private String hours="";
    private String location="";
    private URI location_URI;
    private boolean going=false;
    private boolean newEvent=false;
    private int id=-1;

    public Event(int id, String eventName, String weekDay, String date, String price, String hours, String location, URI location_URI, boolean going, boolean newEvent){
        this.setId(id);
        this.eventName=eventName;
        this.weekDay=weekDay;
        this.date=date;
        this.price=price;
        this.hours=hours;
        this.location=location;
        if(location_URI!=null) this.location_URI=location_URI;
        this.going=going;
        this.newEvent=newEvent;
    }

    /*public Event(String eventName, String weekDay, String date, String price, String hours, String location, URI location_URI, boolean going, boolean newEvent){
        this.eventName=eventName;
        this.weekDay=weekDay;
        this.date=date;
        this.price=price;
        this.hours=hours;
        this.location=location;
        if(location_URI!=null) this.location_URI=location_URI;
        this.going=going;
        this.newEvent=newEvent;
    }*/

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URI getLocation_URI() {
        return location_URI;
    }

    public void setLocation_URI(URI location_URI) {
        this.location_URI = location_URI;
    }

    public boolean isGoing() {
        return going;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public boolean isNewEvent() {
        return newEvent;
    }

    public void setNewEvent(boolean newEvent) {
        this.newEvent = newEvent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
