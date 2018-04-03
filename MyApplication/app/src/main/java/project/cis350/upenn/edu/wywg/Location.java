package project.cis350.upenn.edu.wywg;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by abhaved on 2/14/17.
 */
@Parcel
public class Location implements Comparable<Location> {

    boolean notified;

    String wheretogo;
    String whattodo;

//    public boolean ifNotified(){
//        return notified;
//    }

//    public void Notify(){
//        notified = true;
//    }

    public void setNotified(){
        notified = false;
    }

    String p;
    public void setP(String pic){
        p = pic;
    }

    public String getP(){
        return p;
    }

     String name;
    double temp;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    double rating;

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    double cost;


    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

     String description ="";
     LatLng coordinates;
     double longi;
     double lat;

   // private ArrayList<Drawable> images;
    public Location() {

    }


     boolean been;
     String journal = "";
     List<String> pics = new ArrayList<String>();
     List<String> users = new ArrayList<String>();

     Date timeAdded = null;
     String date = null;
     Calendar cal = null;





    public Location(String name, String description, double lati, double longit, Boolean been1, String journal1) {
        this.name = name;
        this.description = description;
        lat = lati;
        longi = longit;
        this.coordinates = new LatLng(lati, longit);


        if (been1 == null) {
            this.been = false;
        } else {
            this.been = been1;
        }

        if (journal1 == null) {
            this.journal = "";
        } else {
            this.journal = journal1;
        }
    }


    public Location(String name, String description, double lati, double longit, Boolean been1, String journal1, String where, String what) {
        this.name = name;
        this.description = description;
        lat = lati;
        longi = longit;
        this.coordinates = new LatLng(lati, longit);
        this.wheretogo = where;
        this.whattodo = what;


        if (been1 == null) {
            this.been = false;
        } else {
            this.been = been1;
        }

        if (journal1 == null) {
            this.journal = "";
        } else {
            this.journal = journal1;
        }
    }


//
//    public ArrayList<Drawable> getImages() {
//        return images;
//    }
//
//    public void addImage(Drawable image) {
//        images.add(image);
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public boolean getBeen() {
        return been;
    }

    public void setBeen(boolean been) {
        this.been = been;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public void addPic(String pic) {
        pics.add(pic);
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void addUser(String user) {
        users.add(user);
    }

    public Date getTimeAdded() { return timeAdded; }

    public void setTimeAdded(Date timeAdded) { this.timeAdded = timeAdded; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public Calendar getCal() { return cal; }

    public void setCal(Calendar cal) { this.cal = cal; }

    @Override
    public int compareTo(Location o) {
        return o.getCal().compareTo(cal);
    }

    public double getTemperature() {
        return temp;
    }

    public void setTemp(double t) {
        temp = t;
    }

    public void setWhere(String where) {
        this.wheretogo = where;
    }

    public String getWhere() {
        return this.wheretogo;
    }

    public void setWhat(String what) {
        this.whattodo = what;
    }

    public String getWhat() {
        return this.whattodo;
    }


}
