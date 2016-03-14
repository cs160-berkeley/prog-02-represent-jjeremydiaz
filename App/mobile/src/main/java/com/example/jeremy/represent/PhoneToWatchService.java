package com.example.jeremy.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which cat do we want to feed? Grab this info from INTENT
        // which was passed over when we called startService
        Bundle extras = intent.getExtras();
        final String zip = extras.getString("zip");
        String sen_1_name = extras.getString("sen_1_name");
        String sen_1_party = extras.getString("sen_1_party");
        String sen_1_bills = extras.getString("sen_1_bills");
        String sen_1_term = extras.getString("sen_1_term");
        String sen_1_committee = extras.getString("sen_1_committee");
        String sen_2_name = extras.getString("sen_2_name");
        String sen_2_party = extras.getString("sen_2_party");
        String sen_2_bills = extras.getString("sen_2_bills");
        String sen_2_term = extras.getString("sen_2_term");
        String sen_2_committee = extras.getString("sen_2_committee");
        String rep_1_name = extras.getString("rep_1_name");
        String rep_1_party = extras.getString("rep_1_party");
        String rep_1_bills = extras.getString("rep_1_bills");
        String rep_1_term = extras.getString("rep_1_term");
        String rep_1_committee = extras.getString("rep_1_committee");

        String sen_1_state = extras.getString("sen_1_state");
        String sen_1_county = extras.getString("sen_1_county");
        String sen_1_obama = extras.getString("sen_1_obama");
        String sen_1_romney = extras.getString("sen_1_romney");
        String sen_2_state = extras.getString("sen_2_state");
        String sen_2_county = extras.getString("sen_2_county");
        String sen_2_obama = extras.getString("sen_2_obama");
        String sen_2_romney = extras.getString("sen_2_romney");
        String rep_1_state = extras.getString("rep_1_state");
        String rep_1_county = extras.getString("rep_1_county");
        String rep_1_obama = extras.getString("rep_1_obama");
        String rep_1_romney = extras.getString("rep_1_romney");

        //Bioguide
        String sen_1_bioguide = extras.getString("sen_1_bioguide");
        String sen_2_bioguide = extras.getString("sen_2_bioguide");
        String rep_1_bioguide = extras.getString("rep_1_bioguide");

        //send string and remove ; delimiter
        final String send_string = zip + ";" + sen_1_name + ";" + sen_1_party + ";" +
                                    sen_2_name + ";" + sen_2_party + ";" + rep_1_name + ";"
                                    + rep_1_party + ";" + sen_1_bills + ";" + sen_1_term +
                                    ";" + sen_1_committee + ";" + sen_2_bills + ";" + sen_2_term
                                    + ";" + sen_2_committee + ";" + rep_1_bills + ";" + rep_1_term
                                    + ";" + rep_1_committee + ";" + sen_1_state + ";" + sen_1_county
                                    + ";" + sen_1_obama + ";" + sen_1_romney + ";" + sen_2_state + ";"
                                    + sen_2_county + ";" + sen_2_obama + ";" + sen_2_romney + ";" +
                                    rep_1_state + ";" + rep_1_county + ";" + rep_1_obama + ";" +
                                    rep_1_romney + ";" + sen_1_bioguide + ";" + sen_2_bioguide + ";" +
                                    rep_1_bioguide;
        // Send the message with the cat name
        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
                //now that you're connected, send a massage with the cat name
                sendMessage("/zip", send_string);
            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}

