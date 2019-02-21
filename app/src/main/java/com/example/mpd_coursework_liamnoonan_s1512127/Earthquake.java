package com.example.mpd_coursework_liamnoonan_s1512127;

public class Earthquake {

    private String title;
    private String description;
    private String link;
    private String pubDate;
    private String category;
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

    @Override
    public String toString(){
        return this.getTitle() + "\n" +
                this.getDescription() + "\n" +
                this.getLink() + "\n" +
                this.getPubDate() + "\n" +
                this.getCategory() + "\n" +
                this.getLatitude() + "\n" +
                this.getLongitude() + "\n";
    }

}
