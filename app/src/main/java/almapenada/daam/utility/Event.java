package almapenada.daam.utility;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.net.URI;

public class Event implements Serializable {
    private String eventName="";
    private String weekDay="";
    private String date="";
    private String price="";
    private String hours="";
    private String location="";//nao usado
    private LatLng location_latlng;
    private URI location_URI;//nao usado
    private boolean going=false;
    private boolean newEvent=false;
    private int id=-1;

    private boolean isPublic=false;
    private boolean isEndDate=false;
    private String enddate="";
    private boolean isLocation=false;
    private boolean isPrice=false;
    private String description="";
    //TODO:falta os amigos
    private boolean isFriendsInvitable=false;

/**construtor "normal"**/
    public Event(int id, String eventName, boolean isPublic, String weekDay, String date, boolean isEndDate, String enddate, boolean isPrice, String price, String hours, boolean isLocation, String location_latlng, boolean isFriendsInvitable, boolean going, boolean newEvent){
        this.setId(id);
        this.setIsPublic(isPublic);
        this.eventName=eventName;
        this.weekDay=weekDay;
        this.date=date;
        this.isEndDate=isEndDate;
        this.enddate=enddate;
        this.isPrice=isPrice;
        this.price=price;
        this.hours=hours;
        this.isLocation=isLocation;
        if(isLocation) {
            try {
                String[] latlng = location_latlng.split(" ");
                this.location_latlng = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            }catch (java.lang.NumberFormatException e){
                this.isLocation=false;
                this.location_latlng=null;
            }
        }
        this.isFriendsInvitable=isFriendsInvitable;
        this.going=going;
        this.newEvent=newEvent;
    }

/**construtor mais simples para testes (assume algumas coisas)**/
    public Event(int id, String eventName, String weekDay, String date, String price, String hours, String location, LatLng location_latlng, URI location_URI, boolean going, boolean newEvent){
        this.setId(id);
        this.eventName=eventName;
        this.weekDay=weekDay;
        this.date=date;
        if(price.equals(""))
            this.isPrice=false;
        else
            this.isPrice=true;
        this.price=price;
        this.hours=hours;
        this.location=location;//nao usado
        if(location_latlng!=null && location_URI!=null) {
            this.isLocation=true;
            this.location_latlng = location_latlng;
            this.location_URI = location_URI;//nao usado
        }
        this.going=going;
        this.newEvent=newEvent;
    }



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

    public LatLng getLocation_latlng() {
        return location_latlng;
    }

    public void setLocation_latlng(LatLng location_latlng) {
        this.location_latlng = location_latlng;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isEndDate() {
        return isEndDate;
    }

    public void setIsEndDate(boolean isEndDate) {
        this.isEndDate = isEndDate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public boolean isFriendsInvitable() {
        return isFriendsInvitable;
    }

    public void setIsFriendsInvitable(boolean isFriendsInvitable) {
        this.isFriendsInvitable = isFriendsInvitable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLocation(){
        return this.isLocation;
    }

    public boolean isPrice(){
        return this.isPrice;
    }
}
