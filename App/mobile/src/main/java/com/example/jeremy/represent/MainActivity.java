package com.example.jeremy.represent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nH7CbNFDxK2SeLxoV2SGTOFas";
    private static final String TWITTER_SECRET = "tE9OLTWLbLuq06riEpcbuhMdSa5bZAV37tXUi4CrtclXUH4rkD";

    private Button mPopUpButton;
    private PopupWindow mPopUpWindow;
    private LayoutInflater mPopUpLayoutInflator;
    private ScrollView mScrollView;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mSunlightBase = "congress.api.sunlightfoundation.com/legislators/locate?";
    private String mSunlightKey = "&apikey=43e36454132a4063a1c497ed6b48e560";
    private String mGeoKey = "AIzaSyCHFlMt_7qfmaVDt4w5sDT7rjfFeNVx79U";
    private ArrayList<String> mQueryResults = new ArrayList<>();
    private int zipCode;

    String image_base = "https://theunitedstates.io/images/congress/225x275/";
    String jpg = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //Guest login and authentication https://twittercommunity.com/t/test-run-with-fabric-android/60673
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, "elonmusk", 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            Log.d("fabricstuff", "result: " + tweet.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For demo use only
        final String demo_arr[] = {"46360", "94547"};

        //Current location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Handle popup window
        mPopUpButton = (Button) findViewById(R.id.button0);
        mScrollView = (ScrollView) findViewById(R.id.main_scroll);

        mPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopUpLayoutInflator = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup mContainer = (ViewGroup) mPopUpLayoutInflator.inflate(R.layout.popup_layout, null);

                //Create popup window
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                mPopUpWindow = new PopupWindow(mContainer, metrics.widthPixels, 600, true);
                mPopUpWindow.showAtLocation(mScrollView, Gravity.CENTER, 0, 0);

                //Dim background
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams pp = (WindowManager.LayoutParams) mContainer.getLayoutParams();
                pp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                pp.dimAmount = 0.4f;
                wm.updateViewLayout(mContainer, pp);

                //Close if click on outside
                mContainer.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        mPopUpWindow.dismiss();
                        mPopUpWindow = null;
                        return true;
                    }
                });
                final EditText mZipEdit = (EditText) mContainer.findViewById(R.id.zip_edit);
                final EditText mWarningText = (EditText) mContainer.findViewById(R.id.warningText);

                //Pop up window submit listener, will open new activity and send ZIP data to next activity
                //Find button in popup view
                //View pview = mPopUpLayoutInflator.inflate(R.layout.popup_layout, (ViewGroup)findViewById(R.id.main_scroll));
                Button mZipSumbit = (Button) mContainer.findViewById(R.id.button2);
                mZipSumbit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Converts zip code to coordinates so there is no ambiguity and only 3 will show up
                        String zipMap = "http://maps.googleapis.com/maps/api/geocode/json?address=";
                        String testString = mZipEdit.getText().toString();
                        String zipRegion = "&region=us";
                        String lat = "";
                        String lon = "";

                        String zipcall = zipMap + testString + zipRegion;
                        //get json
                        if(android.text.TextUtils.isDigitsOnly(testString) && testString.length() == 5) {
                            String zip_info = "0";
                            try {
                                zip_info = new DownloadWebpageTask().execute(zipcall).get();
                            } catch (Exception e) {
                                mWarningText.setText("No internet connection");
                            }

                            try {
                                JSONObject get_zip = new JSONObject(zip_info);
                                String status = get_zip.getString("status");
                                Log.d("test0", status);
                                if(!status.equals("OK")){
                                    throw new Exception();
                                }

                                JSONObject get_zip_results = get_zip.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                                                            .getJSONObject("location");
                                Log.d("test-1", "JSON object test");
                                lat = get_zip_results.getString("lat");
                                lon = get_zip_results.getString("lng");
                                Log.d("test1", lat);
                                Log.d("test2", lon);
                            } catch (Exception e) {mWarningText.setText("Invalid ZIP code!");}
                        }else{
                            mWarningText.setText("Invalid ZIP code!");
                        }

                        //make JSON calls using process
                        String mQueryString = "http://" + mSunlightBase + "latitude=" + lat + "&" + "longitude=" + lon + mSunlightKey;
                        String mCountyString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key=" + mGeoKey;

                        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                        String basic_info = "0";
                        String county_info = "0";
                        if (networkInfo != null && networkInfo.isConnected()) {
                            try {
                                basic_info = new DownloadWebpageTask().execute(mQueryString).get();
                                county_info = new DownloadWebpageTask().execute(mCountyString).get();
                            }catch(Exception e){}
                        }
                        Log.d("q string", county_info);

                        process(basic_info, county_info);

                    }
                });

                //Close window on cancel
                final Button mZipCancel = (Button) mContainer.findViewById(R.id.button3);
                mZipCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopUpWindow.dismiss();
                    }
                });
            }
        });

        //on current location click
        final Button mCurrentLocations = (Button) findViewById(R.id.button1);
        mCurrentLocations.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Check if Location is on
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mCurrentLocation == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("GPS Not Found");  // GPS not found
                    builder.setMessage("Do you want to enable GPS?"); // Want to enable?
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.create().show();
                    return;
                }

                //Get JSON ready:
                String mLat = String.valueOf(mCurrentLocation.getLatitude());
                String mLon = String.valueOf(mCurrentLocation.getLongitude());
                String mQueryString = "http://" + mSunlightBase + "latitude=" + mLat + "&" + "longitude=" + mLon + mSunlightKey;
                String mCountyString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mLat + "," + mLon + "&key=" + mGeoKey;

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                String basic_info = "0";
                String county_info = "0";
                if (networkInfo != null && networkInfo.isConnected()) {
                    try {
                        basic_info = new DownloadWebpageTask().execute(mQueryString).get();
                        county_info = new DownloadWebpageTask().execute(mCountyString).get();
                    }catch(Exception e){}
                }
                Log.d("q string", county_info);

                process(basic_info, county_info);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        //Note: only works in older versions of play services otherwise you must ask for permissions,
        //also this must not be the first service that invokes location otherwise it will not work
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //Debug
        if(mCurrentLocation == null){
            Log.d("Valid Location", "No");
        }
        else {
            Log.d("Location Test", mCurrentLocation.toString());
        }

    }
    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //From http://developer.android.com/intl/es/training/basics/network-ops/connecting.html#AsynTask
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 5000;
        Log.d("Download URL", "Connection failed-1");

        try {
            URL url = new URL(myurl);
            Log.d("Download URL", "Connection failed0");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d("Download URL", "Connection failed");
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            Log.d("Download URL", "Connection failed1");
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Download URL", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }
    // Reads an InputStream and converts it to a String. http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        java.util.Scanner s = new java.util.Scanner(stream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void process(String sunlightQuery, String countyQuery){
        //Have results, time to parse to json and send data to activities
        try {
            JSONObject myJObject = new JSONObject(sunlightQuery);
            Log.d("test", myJObject.getString("count"));
            if(myJObject.getString("count").equals("0")){
                throw new Exception();
            }

            String zip = "0";
            String sen_1_name = "0";
            String sen_1_party = "0";
            String sen_2_name = "0";
            String sen_2_party = "0";
            String rep_1_name = "0";
            String rep_1_party = "0";

            //Also send this to congress view and by extension, detailed view
            String sen_1_website = "0";
            String sen_2_website = "0";
            String rep_1_website = "0";

            String sen_1_tweetID = "0";
            String sen_2_tweetID = "0";
            String rep_1_tweetID = "0";

            String sen_1_term = "0";
            String sen_2_term = "0";
            String rep_1_term = "0";

            String sen_1_committee = "0";
            String sen_2_committee = "0";
            String rep_1_committee = "0";

            String sen_1_bills = "0";
            String sen_2_bills = "0";
            String rep_1_bills = "0";

            //For watch vote view
            String sen_1_state = "";
            String sen_1_county = "";
            String sen_1_romney = "";
            String sen_1_obama = "";
            String sen_2_state = "";
            String sen_2_county = "";
            String sen_2_romney = "";
            String sen_2_obama = "";
            String rep_1_state = "";
            String rep_1_county = "";
            String rep_1_romney = "";
            String rep_1_obama = "";

            //Bioguide for images
            String sen_1_bioguide = "";
            String sen_2_bioguide = "";
            String rep_1_bioguide = "";

            //Get state and county info
            String state = "";
            String sen_county = "";
            String county = "";
            String romney = "";
            String obama = "";
            JSONObject mCounty = new JSONObject(countyQuery);
            JSONArray mCountArr = mCounty.getJSONArray("results");
            //for(int i = 0; i < mCountArr.length(); i++){
            JSONArray addressArr = mCountArr.getJSONObject(0).getJSONArray("address_components");
            for(int j = 0; j < addressArr.length(); j++){
                JSONObject temp = addressArr.getJSONObject(j);
                JSONArray type = temp.getJSONArray("types");
                if(type.getString(0).equals("administrative_area_level_2")){
                    county = temp.getString("short_name");
                }
                if(type.getString(0).equals("administrative_area_level_1")){
                    state = temp.getString("short_name");
                    sen_county = temp.getString("long_name");
                }
                Log.d("type", type.getString(0));
            }
            //}

            //Parse voting % JSON
            InputStream stream = getAssets().open("newelectioncounty2012.json");
            int size = stream.available();
            byte [] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String JSONvoting = new String(buffer, "UTF-8");
            JSONObject mVotingPercent = new JSONObject(JSONvoting);

            JSONArray mJSONResults = myJObject.getJSONArray("results");

            Intent mToWear = new Intent(getBaseContext(), PhoneToWatchService.class);

            for(int i = 0; i < mJSONResults.length(); i++){
                JSONObject row = mJSONResults.getJSONObject(i);
                String bioguide = row.getString("bioguide_id");
                String term = row.getString("term_end");

                //Form api url and get json results for bills and committee
                String billUrl = "http://congress.api.sunlightfoundation.com/bills?" + "sponsor_id=" + bioguide + mSunlightKey;
                String billResult = "0";
                String billMessage = "";
                try {
                    billResult = new DownloadWebpageTask().execute(billUrl).get();
                }catch(Exception e){}
                JSONObject JSONBill = new JSONObject(billResult);
                JSONArray JSONBillArr = JSONBill.getJSONArray("results");
                for(int j = 0; j < JSONBillArr.length(); j++){
                    JSONObject billRow = JSONBillArr.getJSONObject(j);
                    if(billRow.isNull("short_title")) {
                        String temp = billRow.getString("official_title");
                        billMessage += temp.substring(0, temp.length()/2) + "..." + ", ";
                    }
                    else{
                        billMessage += billRow.getString("short_title") + ", ";
                    }
                }
                billMessage = billMessage.substring(0, billMessage.length() - 2);

                //Handle committees
                String committeeUrl = "http://congress.api.sunlightfoundation.com/committees?" + "member_ids=" + bioguide + mSunlightKey;
                String committeeResult = "0";
                String committeeMessage = "";
                try {
                    committeeResult = new DownloadWebpageTask().execute(committeeUrl).get();
                }catch(Exception e){}
                JSONObject JSONCommittee = new JSONObject(committeeResult);
                JSONArray JSONCommitteeArr = JSONCommittee.getJSONArray("results");
                for(int j = 0; j < JSONCommitteeArr.length(); j++){
                    JSONObject committeeRow = JSONCommitteeArr.getJSONObject(j);
                    if(!committeeRow.getBoolean("subcommittee")) {
                        String temp = committeeRow.getString("name");
                        committeeMessage += temp + ", ";
                    }
                }
                committeeMessage = committeeMessage.substring(0, committeeMessage.length() - 2);

                //For watch
                String name = row.getString("first_name") + " " + row.getString("last_name");
                String party = row.getString("party");
                String type = row.getString("chamber"); //senate or house

                //For twitter
                String twitterID = row.getString("twitter_id");

                //For congress view
                String website = row.getString("website");
                String tweet;

                //For detailed view

                if(type.equals("house")){
                    rep_1_name = name;
                    rep_1_website = website;
                    rep_1_bills = billMessage;
                    rep_1_term = term;
                    rep_1_committee = committeeMessage;
                    rep_1_tweetID = twitterID;

                    rep_1_state = state;
                    rep_1_county = county;

                    rep_1_bioguide = bioguide;

                    //voting values
                    rep_1_obama = mVotingPercent.getJSONObject(rep_1_county + ", " + rep_1_state).getString("obama");
                    rep_1_romney = mVotingPercent.getJSONObject(rep_1_county + ", " + rep_1_state).getString("romney");
                    if(party.equals("D")) {
                        rep_1_party = "Democrat";
                    }else{
                        rep_1_party = "Republican";
                    }
                    continue;
                }

                if(type.equals("senate") && sen_1_name.equals("0")){
                    sen_1_name = name;
                    sen_1_website = website;
                    sen_1_bills = billMessage;
                    sen_1_term = term;
                    sen_1_committee = committeeMessage;
                    sen_1_tweetID = twitterID;

                    sen_1_state = state;
                    sen_1_county = sen_county;

                    sen_1_bioguide = bioguide;

                    //Voting values
                    sen_1_obama = mVotingPercent.getJSONObject(sen_1_county + " County, " + sen_1_state).getString("obama");
                    sen_1_romney = mVotingPercent.getJSONObject(sen_1_county + " County, " + sen_1_state).getString("romney");
                    if(party.equals("D")) {
                        sen_1_party = "Democrat";
                    }else{
                        sen_1_party = "Republican";
                    }
                }else{
                    sen_2_name = name;
                    sen_2_website = website;
                    sen_2_bills = billMessage;
                    sen_2_term = term;
                    sen_2_committee = committeeMessage;
                    sen_2_tweetID = twitterID;

                    sen_2_state = state;
                    sen_2_county = sen_county;

                    sen_2_bioguide = bioguide;

                    //Voting values
                    sen_2_obama = mVotingPercent.getJSONObject(sen_2_county + " County, " + sen_2_state).getString("obama");
                    sen_2_romney = mVotingPercent.getJSONObject(sen_2_county + " County, " + sen_2_state).getString("romney");
                    if(party.equals("D")) {
                        sen_2_party = "Democrat";
                    }else{
                        sen_2_party = "Republican";
                    }
                    break;
                }
            }

            Log.d("billMessage1", sen_1_romney);
            Log.d("billMessage2", sen_2_romney);
            Log.d("billMessage3", rep_1_romney);

            //For watch detailed view click
            mToWear.putExtra("zip", zip);
            mToWear.putExtra("sen_1_name", sen_1_name);
            mToWear.putExtra("sen_1_party", sen_1_party);
            mToWear.putExtra("sen_1_bills", sen_1_bills);
            mToWear.putExtra("sen_1_term", sen_1_term);
            mToWear.putExtra("sen_1_committee", sen_1_committee);
            mToWear.putExtra("sen_2_name", sen_2_name);
            mToWear.putExtra("sen_2_party", sen_2_party);
            mToWear.putExtra("sen_2_bills", sen_2_bills);
            mToWear.putExtra("sen_2_term", sen_2_term);
            mToWear.putExtra("sen_2_committee", sen_2_committee);
            mToWear.putExtra("rep_1_name", rep_1_name);
            mToWear.putExtra("rep_1_party", rep_1_party);
            mToWear.putExtra("rep_1_bills", rep_1_bills);
            mToWear.putExtra("rep_1_term", rep_1_term);
            mToWear.putExtra("rep_1_committee", rep_1_committee);

            mToWear.putExtra("sen_1_state", sen_1_state);
            mToWear.putExtra("sen_1_county", sen_1_county);
            mToWear.putExtra("sen_1_obama", sen_1_obama);
            mToWear.putExtra("sen_1_romney", sen_1_romney);
            mToWear.putExtra("sen_2_state", sen_2_state);
            mToWear.putExtra("sen_2_county", sen_2_county);
            mToWear.putExtra("sen_2_obama", sen_2_obama);
            mToWear.putExtra("sen_2_romney", sen_2_romney);
            mToWear.putExtra("rep_1_state", rep_1_state);
            mToWear.putExtra("rep_1_county", rep_1_county);
            mToWear.putExtra("rep_1_obama", rep_1_obama);
            mToWear.putExtra("rep_1_romney", rep_1_romney);

            //Add bioID for images
            mToWear.putExtra("sen_1_bioguide", sen_1_bioguide);
            mToWear.putExtra("sen_2_bioguide", sen_2_bioguide);
            mToWear.putExtra("rep_1_bioguide", rep_1_bioguide);

            startService(mToWear);

            Intent mToCongressView2 = new Intent(MainActivity.this, CongressViewActivity.class);
            mToCongressView2.putExtra("zip1", zip);
            mToCongressView2.putExtra("sen_1_name", sen_1_name);
            mToCongressView2.putExtra("sen_1_party", sen_1_party);
            mToCongressView2.putExtra("sen_1_website", sen_1_website);
            mToCongressView2.putExtra("sen_1_bills", sen_1_bills);
            mToCongressView2.putExtra("sen_1_term", sen_1_term);
            mToCongressView2.putExtra("sen_1_committee", sen_1_committee);
            mToCongressView2.putExtra("sen_1_tweetID", sen_1_tweetID);
            mToCongressView2.putExtra("sen_2_name", sen_2_name);
            mToCongressView2.putExtra("sen_2_party", sen_2_party);
            mToCongressView2.putExtra("sen_2_website", sen_2_website);
            mToCongressView2.putExtra("sen_2_bills", sen_2_bills);
            mToCongressView2.putExtra("sen_2_term", sen_2_term);
            mToCongressView2.putExtra("sen_2_committee", sen_2_committee);
            mToCongressView2.putExtra("sen_2_tweetID", sen_2_tweetID);
            mToCongressView2.putExtra("rep_1_name", rep_1_name);
            mToCongressView2.putExtra("rep_1_party", rep_1_party);
            mToCongressView2.putExtra("rep_1_website", rep_1_website);
            mToCongressView2.putExtra("rep_1_bills", rep_1_bills);
            mToCongressView2.putExtra("rep_1_term", rep_1_term);
            mToCongressView2.putExtra("rep_1_committee", rep_1_committee);
            mToCongressView2.putExtra("rep_1_tweetID", rep_1_tweetID);

            //Add bioID for images
            mToCongressView2.putExtra("sen_1_bioguide", sen_1_bioguide);
            mToCongressView2.putExtra("sen_2_bioguide", sen_2_bioguide);
            mToCongressView2.putExtra("rep_1_bioguide", rep_1_bioguide);
            startActivity(mToCongressView2);
        }
        catch(Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Incorrect Values");  // GPS not found
            builder.setMessage("The values you have entered are invalid, please enter valid values"); // Want to enable?
            builder.setNegativeButton("Ok", null);
            builder.create().show();
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
