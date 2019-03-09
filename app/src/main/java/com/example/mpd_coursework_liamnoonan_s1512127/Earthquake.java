package com.example.mpd_coursework_liamnoonan_s1512127;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Earthquake {

    private String title;
    private String description;
    private String location;
    private String link;
    private String pubDate;
    private String category;
    private String colour;
    private float magnitude;
    private float latitude;
    private float longitude;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        //System.out.println(description);
        if(description.contains(";") && description.contains(":")){
            parseDescription(description);
        }

        //System.out.println(Arrays.toString(vals));
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


    /**
     * Used to parse additional data from the description tag such as the magnitude
     * @param description
     */
    private void parseDescription(String description){
        String[] pairs = description.split(";");
        //System.out.println("Got to parse description method");

        Map<String, String> map = new HashMap<String, String>();

        for(String s : pairs) {
            if (!s.contains("Origin")) {

                String[] vals = s.split(":");
                for (int i = 0; i < vals.length; i += 2) {
                    map.put(vals[i], vals[i + 1]);
                }
            }
        }

        for (String s : map.keySet()) {
            if(s.contains("Magnitude")){
                this.magnitude = Float.parseFloat(map.get(s));
                //System.out.println(Float.parseFloat(map.get(s)));
                if(magnitude > -1 && magnitude < 0.9){
                    this.colour = "green";
                }
                if(magnitude > 0.9 && magnitude < 1.5){
                    this.colour = "orange";
                }
                if(magnitude > 1.5){
                    this.colour = "red";
                }
            }
            if(s.contains("Location")){
                this.location = map.get(s);
            }

            //System.out.println(s + " is " + map.get(s));
        }
    }

    @Override
    public String toString(){
        return this.getTitle() + "\n" +
                this.getDescription() + "\n" +
                this.getLocation() + "\n" +
                this.getLink() + "\n" +
                this.getPubDate() + "\n" +
                this.getCategory() + "\n" +
                this.getLatitude() + "\n" +
                this.getLongitude() + "\n" +
                this.getMagnitude() + "\n" +
                this.getColour() + "\n";
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
