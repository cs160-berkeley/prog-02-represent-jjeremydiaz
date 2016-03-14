package com.example.jeremy.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    //Use to detect shaking
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    String zip_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        zip_code = "";
        String sen_1_name = "test";
        String sen_1_party = "test";
        String sen_2_name = "test";
        String sen_2_party = "test";
        String rep_1_name = "test";
        String rep_1_party = "test";

        //For detailed view
        String sen_1_bills = "test";
        String sen_1_term = "test";
        String sen_1_committee = "test";
        String sen_2_bills = "test";
        String sen_2_term = "test";
        String sen_2_committee = "test";
        String rep_1_bills = "test";
        String rep_1_term = "test";
        String rep_1_committee = "test";

        //For 2012 Vote view
        String sen_1_state = "test";
        String sen_1_county = "test";
        String sen_1_obama = "test";
        String sen_1_romney = "test";
        String sen_2_state = "test";
        String sen_2_county = "test";
        String sen_2_obama = "test";
        String sen_2_romney = "test";
        String rep_1_state = "test";
        String rep_1_county = "test";
        String rep_1_obama = "test";
        String rep_1_romney = "test";

        //Bioguide for images
        String sen_1_bioguide = "test";
        String sen_2_bioguide = "test";
        String rep_1_bioguide = "test";

        //Get zip data sent from phone
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            zip_code = extras.getString("zip");
            sen_1_name = extras.getString("sen_1_name");
            sen_1_party = extras.getString("sen_1_party");
            sen_2_name = extras.getString("sen_2_name");
            sen_2_party = extras.getString("sen_2_party");
            rep_1_name = extras.getString("rep_1_name");
            rep_1_party = extras.getString("rep_1_party");

            //For detailed view
            sen_1_bills = extras.getString("sen_1_bills");
            sen_1_term = extras.getString("sen_1_term");
            sen_1_committee = extras.getString("sen_1_committee");
            sen_2_bills = extras.getString("sen_2_bills");
            sen_2_term = extras.getString("sen_2_term");
            sen_2_committee = extras.getString("sen_2_committee");
            rep_1_bills = extras.getString("rep_1_bills");
            rep_1_term = extras.getString("rep_1_term");
            rep_1_committee = extras.getString("rep_1_committee");

            //For Vote view
            sen_1_state = extras.getString("sen_1_state");
            sen_1_county = extras.getString("sen_1_county");
            sen_1_obama = extras.getString("sen_1_obama");
            sen_1_romney = extras.getString("sen_1_romney");
            sen_2_state = extras.getString("sen_2_state");
            sen_2_county = extras.getString("sen_2_county");
            sen_2_obama = extras.getString("sen_2_obama");
            sen_2_romney = extras.getString("sen_2_romney");
            rep_1_state = extras.getString("rep_1_state");
            rep_1_county = extras.getString("rep_1_county");
            rep_1_obama = extras.getString("rep_1_obama");
            rep_1_romney = extras.getString("rep_1_romney");

            //Image bioguide
            sen_1_bioguide = extras.getString("sen_1_bioguide");
            sen_2_bioguide = extras.getString("sen_2_bioguide");
            rep_1_bioguide = extras.getString("rep_1_bioguide");

        }

        //Package these in an array to send to SampleGridPagerAdapter to allow for UI
        //to update
        String [] ui_arr = {sen_1_name, sen_1_party, sen_2_name, sen_2_party, rep_1_name, rep_1_party, zip_code,
                            sen_1_bills, sen_1_term, sen_1_committee, sen_2_bills, sen_2_term, sen_2_committee,
                            rep_1_bills, rep_1_term, rep_1_committee, sen_1_state, sen_1_county, sen_1_obama,
                            sen_1_romney, sen_2_state, sen_2_county, sen_2_obama, sen_2_romney, rep_1_state,
                            rep_1_county, rep_1_obama, rep_1_romney, sen_1_bioguide, sen_2_bioguide, rep_1_bioguide};

        //Set up an OnShake listener
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            public void onShake() {
                //Toast to test shake
                Toast.makeText(MainActivity.this, "Sending new data...", Toast.LENGTH_SHORT).show();

                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);

                sendIntent.putExtra("path", "zip_gen");
                startService(sendIntent);

            }
        });

        //Sets up gridview to navigate pages
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });
        SampleGridPagerAdapter check = new SampleGridPagerAdapter(this, getFragmentManager(), ui_arr);
        pager.setAdapter(check);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);

        //test
        /*
        Fragment fragment = check.getFragment(1,1);
        View view = fragment.getView();

        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(MainActivity.this, "testing touch detection", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "view is null", Toast.LENGTH_SHORT).show();
        }
        */
    }

    //Deals with shake listener as well
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
