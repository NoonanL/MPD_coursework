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

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private Button startButton;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);


        // More Code goes here
    }

    /**
     * On click listener
     * @param aview
     */
    public void onClick(View aview)
    {
        startProgress();
    }

    /**
     * Run background task in new thread to fetch data from xml feed.
     */
    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task()).start();
    }


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
                EarthquakeHandler handler = new EarthquakeHandler();
                //Parse the xml from the source using our handler
                saxParser.parse(urlSource, handler);
                //Get Item list from the handler
                List<Earthquake> itemList = handler.getItemList();
                //Print items in the list
                for(Earthquake it : itemList) {
                    Log.e("Data", it.toString());
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("Position", "I am the UI thread");

                }
            });
        }
    }



}
