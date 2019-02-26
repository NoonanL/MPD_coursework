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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private Button startButton;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private ArrayList<Earthquake> earthquakeList;
    private ListView listView;
    private String result = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        listView = findViewById(R.id.listView);
        startProgress();




        // More Code goes here
    }



    /**
     * On click listener
     * @param aview
     */
    public void onClick(View aview)
    {
        Log.e("UserEvent", "Button Clicked!");
        //startProgress();
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

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(urlSource);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    //Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            earthquakeList = parseData(result);


            //Log.e("Position","in run method");



            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                   // Log.d("Position", "I am the UI thread");

                    ListViewAdapter adapter = new ListViewAdapter(earthquakeList, getApplicationContext());
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Earthquake dataModel= earthquakeList.get(position);
                            //On item click, so here you can redirect to the next page perhaps for more detail?
                            Log.e("UserEvent", "Info button clicked for item " + dataModel.getTitle());
                        }
                    });

                }
            });
        }
    }

    private ArrayList<Earthquake> parseData(String dataToParse)
    {
        Earthquake earthquake = null;
        ArrayList <Earthquake> alist = null;
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        alist  = new ArrayList<>();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        //Log.e("MyTag","Item Start Tag found");
                        earthquake = new Earthquake();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                       // Log.e("MyTag","Title is " + temp);
                        if(earthquake!=null){
                            earthquake.setTitle(temp);
                        }

                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            //Log.e("MyTag","Description is " + temp);
                            if (earthquake!=null) {
                                earthquake.setDescription(temp);
                            }
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("link"))
                            {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                               // Log.e("MyTag","Link is " + temp);
                                if (earthquake!=null) {
                                    earthquake.setLink(temp);
                                }
                            }
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("pubDate"))
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                       // Log.e("MyTag","pubDate is " + temp);
                        if (earthquake!=null) {
                            earthquake.setPubDate(temp);
                        }
                    }
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("category"))
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                       // Log.e("MyTag","Category is " + temp);
                        if (earthquake!=null) {
                            earthquake.setCategory(temp);
                        }
                    }
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("lat"))
                    {
                        // Now just get the associated text
                        float temp = Float.parseFloat(xpp.nextText());
                        // Do something with text
                        //Log.e("MyTag","Lat is " + temp);
                        if (earthquake!=null) {
                            earthquake.setLatitude(temp);
                        }
                    }
                    if (xpp.getName().equalsIgnoreCase("long"))
                    {
                        // Now just get the associated text
                        float temp = Float.parseFloat(xpp.nextText());
                        // Do something with text
                        //Log.e("MyTag","long is " + temp);
                        if (earthquake!=null) {
                            earthquake.setLongitude(temp);
                        }
                    }
                }
                else
                if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        assert earthquake != null;
                       // Log.e("MyTag","earthquake is " + earthquake.toString());
                        if(alist!=null){
                        alist.add(earthquake);
                         }
                    }

                    else
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        int size;
                        if(alist!=null) {
                            size = alist.size();
                            Log.e("MyTag", "earthquakelist size is " + size);
                        }
                    }
                }


                // Get the next event
                eventType = xpp.next();

            } // End of while

            //return alist;
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");

        return alist;

    }



}
