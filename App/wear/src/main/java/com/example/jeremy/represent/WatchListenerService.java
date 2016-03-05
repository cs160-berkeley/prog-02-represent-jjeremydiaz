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
    private static final String zip1 = "/94547";
    private static final String zip2 = "/46360";

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

        if( messageEvent.getPath().equalsIgnoreCase( zip1) ) {
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
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase( zip2 )) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("zip", "46360");
            intent.putExtra("sen_1_name", sen_1_name);
            intent.putExtra("sen_1_party", sen_1_party);
            intent.putExtra("sen_2_name", sen_2_name);
            intent.putExtra("sen_2_party", sen_2_party);
            intent.putExtra("rep_1_name", rep_1_name);
            intent.putExtra("rep_1_party", rep_1_party);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}