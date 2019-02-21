/*  Starter project for Mobile Platform Development in Semester B Session 2018/2019
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Liam Noonan
// Student ID           S1512127
// Programme of Study   Computing
//

// Update the package name to include your Student Identifier
package com.example.mpd_coursework_liamnoonan_s1512127;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    private String result = "";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        // More Code goes here
    }

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task()).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {


        public Task(){}

        @Override
        public void run(){

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            Log.e("Position","in run method");

            try
            {
                Log.e("Position","in try");

                /**
                 * Using SAX parsing method, this is an event based method of parsing xml which
                 * avoids having to load the entire XML into memory (twice)
                 * https://docs.oracle.com/javase/7/docs/api/javax/xml/parsers/SAXParser.html
                 */
                SAXParser saxParser = saxParserFactory.newSAXParser();
                //Define handler - this creates the rules by which the XML will be parsed
                MyHandler handler = new MyHandler();
                //Parse the xml from the source using our handler
                saxParser.parse(urlSource, handler);
                //Get Item list from the handler
                List<Item> itemList = handler.getItemList();
                //Print items in the list
                for(Item it : itemList) {
                    Log.e("Data", it.toString());
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("Position", "I am the UI thread");
                    rawDataDisplay.setText(result);

                }
            });
        }
    }

    /**
     * Item class
     */
    public class Item {

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

    /**
     * Handler class - this class defines what to do when the SAX XML parser reaches certain
     * xml tags as well as creating the list of Items and providing functionality to retrieve the list
     *
     */
    public class MyHandler extends DefaultHandler {

        // List to hold Items
        private List<Item> itemList = null;
        private Item item = new Item();
        private StringBuilder data = null;

        // getter method for  list of items
        public List<Item> getItemList() {
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
                item = new Item();
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

}
