package com.example.jeremy.represent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TEST = "/zip_gen";
    //test string for detailed view when watch tap
    private static final String TEST2 = "/detailed";

    private String mSunlightBase = "congress.api.sunlightfoundation.com/legislators/locate?";
    private String mSunlightKey = "&apikey=43e36454132a4063a1c497ed6b48e560";
    private String mGeoKey = "AIzaSyCHFlMt_7qfmaVDt4w5sDT7rjfFeNVx79U";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(TEST)) {
            //Get message from watch to generate new zip code on shake, then
            //do a process() here once the zip code has been converted to coordinates

            Log.d("inside", "inside phone listener");
            //Generate new zip
            String lat = getRandomValue(33, 41);
            String lon = String.valueOf(Double.parseDouble(getRandomValue(0, 35)) - 117);

            //Get JSON ready:
            String mQueryString = "http://" + mSunlightBase + "latitude=" + lat + "&" + "longitude=" + lon + mSunlightKey;
            String mCountyString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key=" + mGeoKey;

            String basic_info = "0";
            String county_info = "0";
            try {
                basic_info = new DownloadWebpageTask().execute(mQueryString).get();
                county_info = new DownloadWebpageTask().execute(mCountyString).get();
            }catch(Exception e){}
            Log.d("basic", basic_info);
            Log.d("county", county_info);

            process(basic_info, county_info);
        }
        else if(messageEvent.getPath().equalsIgnoreCase(TEST2)){
            Log.d("Test", "100");

            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String [] result = name.split(";");

            Log.d("Test", name);


            //Start a new watch activity with the updated values
            Intent detailed = new Intent(this, DetailedViewActivity.class);
            detailed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            detailed.putExtra("uri", "@drawable/sen_sample_1");
            detailed.putExtra("name", result[0]);
            detailed.putExtra("party", result[1]);
            detailed.putExtra("term_end", result[3]);
            detailed.putExtra("committees", result[4]);
            detailed.putExtra("bills", result[2]);
            detailed.putExtra("bioguide", result[5]);

            startActivity(detailed);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }

    //Modified from http://stackoverflow.com/questions/4143304/how-do-you-generate-a-random-number-with-decimal-places
    public static String getRandomValue(final int lowerBound, final int upperBound){
        final Random rand = new Random();
        if(lowerBound < 0 || upperBound <= lowerBound){
            return "Error";
        }

        final double dbl = ((rand == null ? new Random() : rand).nextDouble()
                            * (upperBound - lowerBound)) + lowerBound;
        return String.format("%." + 6 + "f", dbl);
    }

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

            Intent mToWear = new Intent(this, PhoneToWatchService.class);

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

            //dummy variables, TODO: change to api results
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

            Intent mToCongressView2 = new Intent(PhoneListenerService.this, CongressViewActivity.class);
            mToCongressView2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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
            //AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            //builder.setTitle("Incorrect Values");  // GPS not found
            //builder.setMessage("The values you have entered are invalid, please enter valid values"); // Want to enable?
            //builder.setNegativeButton("Ok", null);
            //builder.create().show();
        }
    }
}
