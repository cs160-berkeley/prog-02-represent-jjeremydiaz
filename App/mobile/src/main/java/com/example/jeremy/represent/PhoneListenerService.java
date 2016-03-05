package com.example.jeremy.represent;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TEST = "/zip";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(TEST) ) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String zip = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //Start a new watch activity with the updated values
            Intent mToWear = new Intent(this, PhoneToWatchService.class);

            mToWear.putExtra("zip", zip);

            //Dummy values
            if(zip.equals("46360")){
                mToWear.putExtra("sen_1_name", "Joe Donnelly");
                mToWear.putExtra("sen_1_party", "Democrat");
                mToWear.putExtra("sen_2_name", "Dan Coats");
                mToWear.putExtra("sen_2_party", "Republican");
                mToWear.putExtra("rep_1_name", "Peter J. Visclosky");
                mToWear.putExtra("rep_1_party", "Democrat");
            }
            else {
                mToWear.putExtra("sen_1_name", "Dianne Feinstein");
                mToWear.putExtra("sen_1_party", "Democrat");
                mToWear.putExtra("sen_2_name", "Barbara Boxer");
                mToWear.putExtra("sen_2_party", "Democrat");
                mToWear.putExtra("rep_1_name", "Mike Thompson");
                mToWear.putExtra("rep_1_party", "Democrat");
            }
            startService(mToWear);

            //Start new CongressViewActivity with new zip code obtained from the watch
            Intent mNewCongressIntent = new Intent(this, CongressViewActivity.class);
            mNewCongressIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mNewCongressIntent.putExtra("zip1", zip);
            startActivity(mNewCongressIntent);

            /*
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "test", duration);
            toast.show();
            */
            /*
            Context test = getBaseContext();
            Activity test2 = (Activity) test;
            test2.finish();
            */

            //((Activity) getBaseContext()).finish();

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
