package com.example.mpd_coursework_liamnoonan_s1512127;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeHandler extends DefaultHandler {

    // List to hold Items
    private List<Earthquake> itemList = null;
    private Earthquake item = new Earthquake();
    private StringBuilder data = null;

    // getter method for  list of items
    public List<Earthquake> getItemList() {
        return itemList;
    }

    /**
     * The following bool values are used to show wether that tag has been found for the item.
     * This helps to avoid items we dont want such as the header information. The header tags
     * do not create an item of the format we want so will not be added to the list.
     */
    boolean bTitle = false;
    boolean bDescription = false;
    boolean bLink = false;
    boolean bPubDate = false;
    boolean bCategory = false;
    boolean bLatitude = false;
    boolean bLongitude = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("item")) {
            //Initialise an empty item
            item = new Earthquake();
            // initialize list if the list doesnt already exist
            if (itemList == null) {
                itemList = new ArrayList<>();
            }
            //Check if the item has each tag we want and mark them accordingly
        } else if (qName.equalsIgnoreCase("title")) {
            bTitle = true;
        } else if (qName.equalsIgnoreCase("description")) {
            bDescription = true;
        } else if (qName.equalsIgnoreCase("link")) {
            bLink = true;
        } else if (qName.equalsIgnoreCase("pubdate")) {
            bPubDate = true;
        } else if (qName.equalsIgnoreCase("category")) {
            bCategory = true;
        } else if (qName.equalsIgnoreCase("geo:lat")) {
            bLatitude = true;
        }else if (qName.equalsIgnoreCase("geo:long")) {
            bLongitude = true;
        }
        // create the data container
        data = new StringBuilder();
    }

    /**
     * When we get to the end element we populate the Item object so long as values exist for
     * the corresponding tag
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (bTitle) {
            item.setTitle(data.toString());
            bTitle = false;
        } else if (bDescription) {
            item.setDescription(data.toString());
            bDescription = false;
        } else if (bPubDate) {
            item.setPubDate(data.toString());
            bPubDate = false;
        } else if (bLink) {
            item.setLink(data.toString());
            bLink = false;
        } else if (bCategory) {
            item.setCategory(data.toString());
            bCategory = false;
        } else if (bLatitude) {
            item.setLatitude(Float.parseFloat(data.toString()));
            bLatitude = false;
        } else if (bLongitude) {
            item.setLongitude(Float.parseFloat(data.toString()));
            bLongitude = false;
        }

        if (qName.equalsIgnoreCase("item")) {
            // add item object to list
            itemList.add(item);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        data.append(new String(ch, start, length));
    }
}

