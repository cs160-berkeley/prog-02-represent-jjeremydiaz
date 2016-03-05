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
    //test string for detailed view when watch tap
    private static final String TEST2 = "/detailed";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase("/46360") ||  messageEvent.getPath().equalsIgnoreCase("/94547")) {
            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String zip = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //Start a new watch activity with the updated values
            Intent mToWear = new Intent(this, PhoneToWatchService.class);

            mToWear.putExtra("zip", zip);

            //Dummy values
            if (zip.equals("46360")) {
                mToWear.putExtra("sen_1_name", "Joe Donnelly");
                mToWear.putExtra("sen_1_party", "Democrat");
                mToWear.putExtra("sen_2_name", "Dan Coats");
                mToWear.putExtra("sen_2_party", "Republican");
                mToWear.putExtra("rep_1_name", "Peter J. Visclosky");
                mToWear.putExtra("rep_1_party", "Democrat");
            } else {
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

        }
        else if(messageEvent.getPath().equalsIgnoreCase(TEST2)){
            Log.d("Test", "100");

            String name = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("Test", name);


            //Start a new watch activity with the updated values
            Intent detailed = new Intent(this, DetailedViewActivity.class);
            detailed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //TODO Dummy values
            if (name.equals("Dianne Feinstein")) {
                detailed.putExtra("uri", "@drawable/sen_sample_1");
                detailed.putExtra("name", "Dianne Feinstein");
                detailed.putExtra("party", "Democrat");
                detailed.putExtra("term_end", "January 3, 2019");
                detailed.putExtra("committees", "Appropriations Committee, Select Committee on Intelligence, Judiciary Committee, Rules and Administration Committee");
                detailed.putExtra("bills", "Trade Act of 2015, Defending Public Safety Employee's Retirement Act, National Defense Authorization Act for Fiscal Year 2016");

            } else if (name.equals("Barbara Boxer")){
                detailed.putExtra("uri", "@drawable/sen_sample_2");
                detailed.putExtra("name", "Barbara Boxer");
                detailed.putExtra("party", "Democrat");
                detailed.putExtra("term_end", "January 3, 2017");
                detailed.putExtra("committees", "Select Committee on Ethics, Environment and Public Works Committee, Foreign Relations Committee");
                detailed.putExtra("bills", "Female Veteran Suicide Prevention Act, Pechanga Band of Luiseno Mission Indians Water RIghts Settlement Act, Tule Lake Nation HIstoric Site Establishment Act of 2015");

            }else if (name.equals("Mike Thompson")){
                detailed.putExtra("uri", "@drawable/rep_sample_1");
                detailed.putExtra("name", "Mike Thompson");
                detailed.putExtra("party", "Democrat");
                detailed.putExtra("term_end", "January 3, 2017");
                detailed.putExtra("committees", "Ways and Means Committee");
                detailed.putExtra("bills", "United States Fish and Wildlife Service Resource Protection Act");
            }else if (name.equals("Joe Donnelly")){
                detailed.putExtra("uri", "@drawable/sen_sample_1_in");
                detailed.putExtra("name", "Joe Donnelly");
                detailed.putExtra("party", "Democrat");
                detailed.putExtra("term_end", "January 3, 2019");
                detailed.putExtra("committees", "Special Committee on Aging, Agriculture, Nutrition, and Forestry Committee, Armed Services Committee, Banking, Housing, and Urban Affairs Committee");
                detailed.putExtra("bills", "FDA Regulatory Efficiency Act, Clean Air Act");
            }else if (name.equals("Dan Coats")){
                detailed.putExtra("uri", "@drawable/sen_sample_2_in");
                detailed.putExtra("name", "Dan Coats");
                detailed.putExtra("party", "Republican");
                detailed.putExtra("term_end", "January 3, 2017");
                detailed.putExtra("committees", "Finance Committee, Select Committee on Intelligence, Join Economic Committee");
                detailed.putExtra("bills", "Require Evaluation before Implementing Executive Wishlists Act of 2015, Control Unlawful Fugitive Felons Act of 2015, Access to Court challenges Act of 2015");
            }else{
                detailed.putExtra("uri", "@drawable/rep_sample_2");
                detailed.putExtra("name", "Democrat");
                detailed.putExtra("party", "Peter J. Visclosky");
                detailed.putExtra("term_end", "January 3, 2017");
                detailed.putExtra("committees", "Appropriations Committee");
                detailed.putExtra("bills", "American Steel First Act of 2015, Fighting for American Jobs Act of 2015, Fighting for American Jobs Act of 2013");
            }

            startActivity(detailed);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}
