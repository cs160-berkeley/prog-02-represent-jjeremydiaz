package com.example.jeremy.represent;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class CongressViewActivity extends AppCompatActivity {
    ImageView imageView1;
    RoundImage roundedImage;
    RoundImage roundedImage2;
    RoundImage roundedImage3;
    String zip_code;
    String sen_1_name;
    String sen_1_party;
    String sen_1_website;
    String sen_1_bills;
    String sen_1_term;
    String sen_1_committee;
    String sen_1_tweetID;
    String sen_1_tweets;
    String sen_2_name;
    String sen_2_party;
    String sen_2_website;
    String sen_2_bills;
    String sen_2_term;
    String sen_2_committee;
    String sen_2_tweetID;
    String sen_2_tweets;
    String rep_1_name;
    String rep_1_party;
    String rep_1_website;
    String rep_1_bills;
    String rep_1_term;
    String rep_1_committee;
    String rep_1_tweetID;
    String rep_1_tweets;

    //Get Bio IDs for images
    //Bioguide for images
    String sen_1_bioguide;
    String sen_2_bioguide;
    String rep_1_bioguide;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nH7CbNFDxK2SeLxoV2SGTOFas";
    private static final String TWITTER_SECRET = "tE9OLTWLbLuq06riEpcbuhMdSa5bZAV37tXUi4CrtclXUH4rkD";

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
                twitterApiClient.getStatusesService().userTimeline(null, sen_1_tweetID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            final EditText sen_1_tweet = (EditText) findViewById(R.id.senator_1_tweet);
                            sen_1_tweet.setText(tweet.text);
                            Log.d("fabricstuff", "result: " + tweet.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });

                twitterApiClient.getStatusesService().userTimeline(null, sen_2_tweetID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            final EditText sen_2_tweet = (EditText) findViewById(R.id.senator_2_tweet);
                            sen_2_tweet.setText(tweet.text);
                            Log.d("fabricstuff", "result: " + tweet.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
                twitterApiClient.getStatusesService().userTimeline(null, rep_1_tweetID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            final EditText rep_1_tweet = (EditText) findViewById(R.id.senator_3_tweet);
                            rep_1_tweet.setText(tweet.text);
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

        //Log.d("result0:", sen_1_tweet);
        //Log.d("result1:", sen_2_tweet);
        //Log.d("result2:", rep_1_tweet);

        setContentView(R.layout.activity_congress_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //For demo use only
        final String demo_arr[] = {"94547", "46360"};

        //Get Zip code from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            zip_code = extras.getString("zip1");
            sen_1_name = extras.getString("sen_1_name");
            sen_1_party = extras.getString("sen_1_party");
            sen_1_website = extras.getString("sen_1_website");
            sen_1_bills = extras.getString("sen_1_bills");
            sen_1_term = extras.getString("sen_1_term");
            sen_1_committee = extras.getString("sen_1_committee");
            sen_1_tweetID = extras.getString("sen_1_tweetID");
            sen_2_name = extras.getString("sen_2_name");
            sen_2_party = extras.getString("sen_2_party");
            sen_2_website = extras.getString("sen_2_website");
            sen_2_bills = extras.getString("sen_2_bills");
            sen_2_term = extras.getString("sen_2_term");
            sen_2_committee = extras.getString("sen_2_committee");
            sen_2_tweetID = extras.getString("sen_2_tweetID");
            rep_1_name = extras.getString("rep_1_name");
            rep_1_party = extras.getString("rep_1_party");
            rep_1_website = extras.getString("rep_1_website");
            rep_1_bills = extras.getString("rep_1_bills");
            rep_1_term = extras.getString("rep_1_term");
            rep_1_committee = extras.getString("rep_1_committee");
            rep_1_tweetID = extras.getString("rep_1_tweetID");
            sen_1_bioguide = extras.getString("sen_1_bioguide");
            sen_2_bioguide = extras.getString("sen_2_bioguide");
            rep_1_bioguide = extras.getString("rep_1_bioguide");
        }

        //Only works on perfectly square images so it must rescale
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.test, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        //TextViews for 3 cardviews
        final EditText t_sen_1_name = (EditText) findViewById(R.id.senator_1_name);
        final EditText t_sen_1_party = (EditText) findViewById(R.id.senator_1_party);
        final EditText sen_1_website = (EditText) findViewById(R.id.senator_1_website);
        //final EditText sen_1_tweet = (EditText) findViewById(R.id.senator_1_tweet);

        final EditText t_sen_2_name = (EditText) findViewById(R.id.senator_2_name);
        final EditText t_sen_2_party = (EditText) findViewById(R.id.senator_2_party);
        final EditText sen_2_website = (EditText) findViewById(R.id.senator_2_website);
        //final EditText sen_2_tweet = (EditText) findViewById(R.id.senator_2_tweet);

        final EditText t_sen_3_name = (EditText) findViewById(R.id.senator_3_name);
        final EditText t_sen_3_party = (EditText) findViewById(R.id.senator_3_party);
        final EditText sen_3_website = (EditText) findViewById(R.id.senator_3_website);
        //final EditText sen_3_tweet = (EditText) findViewById(R.id.senator_3_tweet);
        //Convert new scaled down square image to round

        //demo String uri selection
        final String uri1;
        final String uri2;
        final String uri3;

        uri1 = "@drawable/sen_sample_1";
        t_sen_1_name.setText(this.sen_1_name);
        t_sen_1_party.setText(this.sen_1_party);
        sen_1_website.setText(this.sen_1_website);
        //sen_1_tweet.setText(sen_1_tweetID);

        uri2 = "@drawable/sen_sample_2";
        t_sen_2_name.setText(this.sen_2_name);
        t_sen_2_party.setText(this.sen_2_party);
        sen_2_website.setText(this.sen_2_website);
        //sen_2_tweet.setText(this.sen_2_tweetID);

        uri3 = "@drawable/rep_sample_1";
        t_sen_3_name.setText(this.rep_1_name);
        t_sen_3_party.setText(this.rep_1_party);
        sen_3_website.setText(this.rep_1_website);
        //sen_3_tweet.setText(this.rep_1_tweetID);

        //Round image and display
        roundedImage = new RoundImage(getResized(uri1));
        roundedImage2 = new RoundImage(getResized(uri2));
        roundedImage3 = new RoundImage(getResized(uri3));

        imageView1 = (ImageView) findViewById(R.id.senator_1_image_view);
        //imageView1.setImageDrawable(roundedImage);
        Picasso.with(this).load(image_base + sen_1_bioguide + jpg).resize(200, 200).transform(new CircleTransform()).into(imageView1);

        ImageView imageView2 = (ImageView) findViewById(R.id.senator_2_image_view);
        //imageView2.setImageDrawable(roundedImage2);
        Picasso.with(this).load(image_base + sen_2_bioguide + jpg).resize(200, 200).transform(new CircleTransform()).into(imageView2);

        ImageView imageView3 = (ImageView) findViewById(R.id.senator_3_image_view);
        //imageView3.setImageDrawable(roundedImage3);
        Picasso.with(this).load(image_base + rep_1_bioguide + jpg).resize(200, 200).transform(new CircleTransform()).into(imageView3);

        //New detailed activity view
        final Intent mDetailedActivity = new Intent(CongressViewActivity.this, DetailedViewActivity.class);

        Button mbutton1 = (Button) findViewById(R.id.senator_1_info_button);
        mbutton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri1);
                mDetailedActivity.putExtra("name", sen_1_name);
                mDetailedActivity.putExtra("party", sen_1_party);
                mDetailedActivity.putExtra("term_end", sen_1_term);
                mDetailedActivity.putExtra("committees", sen_1_committee);
                mDetailedActivity.putExtra("bills", sen_1_bills);
                mDetailedActivity.putExtra("bioguide", sen_1_bioguide);

                startActivity(mDetailedActivity);
            }
        });
        Button mbutton2 = (Button) findViewById(R.id.senator_2_info_button);
        mbutton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri2);
                mDetailedActivity.putExtra("name", sen_2_name);
                mDetailedActivity.putExtra("party", sen_2_party);
                mDetailedActivity.putExtra("term_end", sen_2_term);
                mDetailedActivity.putExtra("committees", sen_2_committee);
                mDetailedActivity.putExtra("bills", sen_2_bills);
                mDetailedActivity.putExtra("bioguide", sen_2_bioguide);

                startActivity(mDetailedActivity);
            }
        });
        Button mbutton3 = (Button) findViewById(R.id.representative_1_info_button);
        mbutton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri3);
                mDetailedActivity.putExtra("name", rep_1_name);
                mDetailedActivity.putExtra("party", rep_1_party);
                mDetailedActivity.putExtra("term_end", rep_1_term);
                mDetailedActivity.putExtra("committees", rep_1_committee);
                mDetailedActivity.putExtra("bills", rep_1_bills);
                mDetailedActivity.putExtra("bioguide", rep_1_bioguide);

                startActivity(mDetailedActivity);
            }
        });
    }

    //Calculate sample scale size of image
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap getResized(String location){
        int imageId = getResources().getIdentifier(location, null, getPackageName());
        //Gets memory effecient scaled down bitmap image
        Bitmap mBitMap = decodeSampledBitmapFromResource(getResources(), imageId, 200, 200);
        //Now must crop to the middle
        Bitmap resized = Bitmap.createScaledBitmap(mBitMap, 200, 200, true);
        return resized;
    }
}
