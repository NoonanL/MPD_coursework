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

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private Button startButton;
    private Button searchButton;
    private Button filterButton;
    private EditText searchInput;
    public ProgressBar progressBar;
    private TextView txt_percentage;
    private ArrayList<Earthquake> originEarthquakeList;
    private ArrayList<Earthquake> earthquakeList;
    private ListView listView;
    private TextView listCount;
    private String result = "";
    private ListViewAdapter adapter;

    private String sortOption;
    private String searchParam;


    private Button mapsBackButton;
    private ViewFlipper flipper;
    private TextView mapsText;
    private Earthquake focusEarthquake;

    /**
     * On create method, initialises variables and attaches ui components where necessary
     * Also starts the main process.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the raw links to the graphical components
        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(this);

        listCount = findViewById(R.id.listCount);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchInput = findViewById(R.id.searchInput);
        searchInput.setWidth(120);
        searchInput.setFocusable(true);

        progressBar =  (ProgressBar) findViewById(R.id.progress);
        txt_percentage= (TextView) findViewById(R.id.txt_percentage);

        listView = findViewById(R.id.listView);

        mapsBackButton=(Button)findViewById(R.id.mapsBackButton);
        mapsBackButton.setOnClickListener(this);
        flipper=(ViewFlipper)findViewById(R.id.flipper);
        //when a view is displayed
        flipper.setInAnimation(this,android.R.anim.fade_in);
        //when a view disappears
        flipper.setOutAnimation(this, android.R.anim.fade_out);
        mapsText=findViewById(R.id.mapsText);

        startProgress();

    }



    /**
     * On click listener, handles button presses
     * @param aview
     */
    @SuppressLint("ResourceType")
    public void onClick(View aview)
    {
        if(aview == searchButton){
            System.out.println("Search button pressed!");
            searchParam = searchInput.getText().toString();
            searchFunc(searchParam);
        } else if (aview == filterButton){

            /**
             * Opens popup menu to display filters
             */
            //Create popup menu
            PopupMenu popup = new PopupMenu(MainActivity.this, filterButton);
            popup.getMenuInflater().inflate(R.layout.filter_menu, popup.getMenu());
            //Set onlick listener for popup menu
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                //on click handlers
                public boolean onMenuItemClick(MenuItem item) {
                    sortOption = item.getTitle().toString();
                    sorting(sortOption);
                    return true;
                }
            });

            popup.show();//showing popup menu
        }
        else if (aview == mapsBackButton){
            /**
             * If back is pressed on the maps page return to previous view.
             */
            flipper.showPrevious();
        }
    }

    /**
     * Starter method
     */
    public void startProgress()
    {
        //start main thread
        new Thread(new Task()).start();

    }

    /**
     * Main task which sets up the UI and async refresh thread.
     */
    private class Task implements Runnable
    {

        public Task(){}

        @Override
        public void run(){

            //Call fetch data once to populate the app with data. This will then be handled by ASYNC
            fetchData();

            //Set up UI thread
            MainActivity.this.runOnUiThread(new Runnable()
            {

                //Main function
                public void run() {
                   // Log.d("Position", "I am the UI thread");

                    //Call the search function to set the defaults.
                    searchFunc("");

                    //Create listener for each earthquake item onclick
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Earthquake dataModel= earthquakeList.get(position);
                            //On item click, so here you can redirect to the next page perhaps for more detail?
                            Log.e("UserEvent", "List Item clicked:" + dataModel.getTitle());
                            focusEarthquake = dataModel;
                            flipper.showNext();
                            mapsText.setText(dataModel.getTitle());
                        }
                    });

                    //Start a new thread to run in the background to Async refresh the data
                    new Thread(new BackgroundTask()).start();
                }

            });
        }
    }

    /**
     * Background task for async data refresh.
     * Sets up a timer for every 20 seconds and calls the async refresh method to re-fetch the data
     */
    private class BackgroundTask implements Runnable{

        public BackgroundTask(){}

        @Override
        public void run() {
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new FetchData().execute();
                }

            }, 0, 20000);

        }
    }

    /**
     * Provides search functionality
     * @param searchParam input string for searching
     */
    private void searchFunc(String searchParam) {

        //if the search is not empty
        if(searchParam.length() >0){

            if(earthquakeList == null){
                earthquakeList.addAll(originEarthquakeList);
            }
            //create new arraylist to contain search results
            ArrayList<Earthquake> searchResults = new ArrayList<>();

            //iterate through earthquakes, if earthquake title contains the search string add to
            //the arraylist of search results
            for (Earthquake e : earthquakeList) {
                if (e.getTitle().contains(searchParam)) {
                    searchResults.add(e);
                }
            }
            earthquakeList = searchResults;
            listCount.setText("(" + searchResults.size() + ")");
            //Reinstantiate the adapter with the search results
            adapter = new ListViewAdapter(earthquakeList, getApplicationContext());
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

        //else search is empty - return the full list of earthquakes
        else{
            listCount.setText("(" + originEarthquakeList.size() + ")");
            adapter = new ListViewAdapter(originEarthquakeList, getApplicationContext());
            //assign the new adapter to the listview
            listView.setAdapter(adapter);
            //update the adapter's dataset.
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * Provides sorting functionality using a switch statement
     * @param sortOption
     */
    private void sorting(String sortOption){
        switch(sortOption){
            case "Reset Filters":
                earthquakeList = new ArrayList<>();
                earthquakeList.addAll(originEarthquakeList);
                adapter = new ListViewAdapter(earthquakeList, getApplicationContext());
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "Filtered by Default", Toast.LENGTH_SHORT).show();
                break;
            case "Magnitude (Ascending)":
                Collections.sort(earthquakeList, Earthquake.magAscComparitor);
                adapter.sort(Earthquake.magAscComparitor);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Filtered by Magnitude (Ascending)", Toast.LENGTH_SHORT).show();
                break;
            case "Magnitude (Descending)":
                Collections.sort(earthquakeList, Earthquake.magDescComparitor);
                adapter.sort(Earthquake.magDescComparitor);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Filtered by Magnitude (Descending)", Toast.LENGTH_SHORT).show();
                break;
            case "Depth: (Shallow -> Deep)":
                Collections.sort(earthquakeList, Earthquake.depthAscComparator);
                adapter.sort(Earthquake.depthAscComparator);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Filtered by Depth: (Shallow -> Deep)", Toast.LENGTH_SHORT).show();
                break;
            case "Depth: (Deep -> Shallow)":
                Collections.sort(earthquakeList, Earthquake.depthDescComparator);
                adapter.sort(Earthquake.depthDescComparator);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Filtered by Depth: (Deep -> Shallow)", Toast.LENGTH_SHORT).show();
                break;


        }

    }

    /**
     * Async function to fetch data.
     */
    private class FetchData extends AsyncTask<Void, Integer, Void>{

        int progress_status;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progress_status = 0;
            progressBarVisibility(true);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            //Call fetch data function to refetch the earthquake data
           fetchData();
           //Since this is basically instant, set up a dummy timer for the UI and have it count down
                while(progress_status<100)
                {
                    progress_status += 10;
                    publishProgress(progress_status);
                    SystemClock.sleep(100); // or Thread.sleep(300);
                }
            callSort();

                return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            progressBarVisibility(false);


        }
    }

    /**
     * Creates URL connection to quakes.bgs.ac.uk and parses data
     */
    private void fetchData(){
        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";

        try
        {
           // Log.e("MyTag","in try");
            String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
            aurl = new URL(urlSource);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));


            while ((inputLine = in.readLine()) != null)
            {
                result = result + inputLine;
                //Log.e("MyTag",inputLine);

            }
            in.close();
        }
        catch (IOException ae)
        {
            Log.e("PARSING", "ioexception");
        }

        //establish the original dataset
        originEarthquakeList = parseData(result);
        //set up another list of earthquakes for filtering/searching
        earthquakeList = new ArrayList<>();
        earthquakeList.addAll(originEarthquakeList);
    }

    /**
     * Parse XML Feed into Earthquake objects.
     * @param dataToParse String data recieved from XML feed
     * @return an Arraylist of Earthquake objects
     */
    public ArrayList<Earthquake> parseData(String dataToParse)
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
                            //Log.e("MyTag", "earthquakelist size is " + size);

                        }
                        break;
                    }
                }

                // Get the next event
                eventType = xpp.next();


            } // End of while

            //return alist;
        }
        catch (XmlPullParserException e)
        {
            Log.e("MyTag","Pull Parser Exception");
            //e.printStackTrace();
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        // Log.e("MyTag","End document");

        return alist;

    }
    /**
     * Helper function to change the visibility of the progress bar and to Toast the update status,
     * this cannot be done on the same thread as the Async Task without additional code
     * @param val boolean value, true for visible, false for invisible
     */
    public void progressBarVisibility(final boolean val){

            runOnUiThread(new Runnable() {

                @Override
                public void run() {


                    if(val){

                    // Stuff that updates the UI
                    progressBar.setVisibility(View.VISIBLE);
                    txt_percentage.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this,"Auto Fetching data..", Toast.LENGTH_SHORT).show();

                }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        txt_percentage.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,"Updated.", Toast.LENGTH_SHORT).show();
                    }
            }
        });


    }

    /**
     * Resorts and re-searches data after a fetch operation as the earthquakelist is updated each fetch cycle
     * so must be resorted or fetched accordingly.
     */
    public void callSort(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(sortOption!=null){
                    sorting(sortOption);
                }
                if(searchParam != null && !searchParam.equals("")){
                    searchFunc(searchParam);
                }

            }
        });
    }

}
