package com.example.jeremy.represent;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String zip_check = "/zip";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        String getStringData = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        String [] result = getStringData.split(";");
        String zip = result[0];
        String sen_1_name = result[1];
        String sen_1_party = result[2];
        String sen_2_name = result[3];
        String sen_2_party = result[4];
        String rep_1_name = result[5];
        String rep_1_party = result[6];
        String sen_1_bills = result[7];
        String sen_1_term = result[8];
        String sen_1_committee = result[9];
        String sen_2_bills = result[10];
        String sen_2_term = result[11];
        String sen_2_committee = result[12];
        String rep_1_bills = result[13];
        String rep_1_term = result[14];
        String rep_1_committee = result[15];

        String sen_1_state = result[16];
        String sen_1_county = result[17];
        String sen_1_obama = result[18];
        String sen_1_romney = result[19];
        String sen_2_state = result[20];
        String sen_2_county = result[21];
        String sen_2_obama = result[22];
        String sen_2_romney = result[23];
        String rep_1_state = result[24];
        String rep_1_county = result[25];
        String rep_1_obama = result[26];
        String rep_1_romney = result[27];

        String sen_1_bioguide = result[28];
        String sen_2_bioguide = result[29];
        String rep_1_bioguide = result[30];


        if( messageEvent.getPath().equalsIgnoreCase( zip_check) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("zip", "94547"); //TODO maybe change this
            intent.putExtra("sen_1_name", sen_1_name);
            intent.putExtra("sen_1_party", sen_1_party);
            intent.putExtra("sen_2_name", sen_2_name);
            intent.putExtra("sen_2_party", sen_2_party);
            intent.putExtra("rep_1_name", rep_1_name);
            intent.putExtra("rep_1_party", rep_1_party);

            //For detailed view
            intent.putExtra("sen_1_bills", sen_1_bills);
            intent.putExtra("sen_1_term", sen_1_term);
            intent.putExtra("sen_1_committee", sen_1_committee);
            intent.putExtra("sen_2_bills", sen_2_bills);
            intent.putExtra("sen_2_term", sen_2_term);
            intent.putExtra("sen_2_committee", sen_2_committee);
            intent.putExtra("rep_1_bills", rep_1_bills);
            intent.putExtra("rep_1_term", rep_1_term);
            intent.putExtra("rep_1_committee", rep_1_committee);

            //For 2012 Vote view
            intent.putExtra("sen_1_state", sen_1_state);
            intent.putExtra("sen_1_county", sen_1_county);
            intent.putExtra("sen_1_obama", sen_1_obama);
            intent.putExtra("sen_1_romney", sen_1_romney);
            intent.putExtra("sen_2_state", sen_2_state);
            intent.putExtra("sen_2_county", sen_2_county);
            intent.putExtra("sen_2_obama", sen_2_obama);
            intent.putExtra("sen_2_romney", sen_2_romney);
            intent.putExtra("rep_1_state", rep_1_state);
            intent.putExtra("rep_1_county", rep_1_county);
            intent.putExtra("rep_1_obama", rep_1_obama);
            intent.putExtra("rep_1_romney", rep_1_romney);

            //Bioguide for images
            intent.putExtra("sen_1_bioguide", sen_1_bioguide);
            intent.putExtra("sen_2_bioguide", sen_2_bioguide);
            intent.putExtra("rep_1_bioguide", rep_1_bioguide);

            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}