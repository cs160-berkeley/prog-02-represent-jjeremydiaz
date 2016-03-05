package com.example.jeremy.represent;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CongressViewActivity extends AppCompatActivity {
    ImageView imageView1;
    RoundImage roundedImage;
    RoundImage roundedImage2;
    RoundImage roundedImage3;
    String zip_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }

        //Only works on perfectly square images so it must rescale
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.test, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        //TextViews for 3 cardviews
        final EditText sen_1_name = (EditText) findViewById(R.id.senator_1_name);
        final EditText sen_1_party = (EditText) findViewById(R.id.senator_1_party);
        final EditText sen_1_website = (EditText) findViewById(R.id.senator_1_website);
        final EditText sen_1_tweet = (EditText) findViewById(R.id.senator_1_tweet);

        final EditText sen_2_name = (EditText) findViewById(R.id.senator_2_name);
        final EditText sen_2_party = (EditText) findViewById(R.id.senator_2_party);
        final EditText sen_2_website = (EditText) findViewById(R.id.senator_2_website);
        final EditText sen_2_tweet = (EditText) findViewById(R.id.senator_2_tweet);

        final EditText sen_3_name = (EditText) findViewById(R.id.senator_3_name);
        final EditText sen_3_party = (EditText) findViewById(R.id.senator_3_party);
        final EditText sen_3_website = (EditText) findViewById(R.id.senator_3_website);
        final EditText sen_3_tweet = (EditText) findViewById(R.id.senator_3_tweet);
        //Convert new scaled down square image to round

        //demo String uri selection
        final String uri1;
        final String uri2;
        final String uri3;
        if(zip_code.equals(demo_arr[0])) {
            uri1 = "@drawable/sen_sample_1";
            sen_1_name.setText("Dianne Feinstein");
            sen_1_party.setText("Democrat");
            sen_1_website.setText("http://www.feinstein.senate.gov/public/");
            sen_1_tweet.setText("@SenFeinstein  2h2 hours ago Spoke on the Senate floor urging Republicans to consider president’s nominee to the Supreme Court. Watch: https://youtu.be/9KS53s-3tKA  #SCOTUS");

            uri2 = "@drawable/sen_sample_2";
            sen_2_name.setText("Barbara Boxer");
            sen_2_party.setText("Democrat");
            sen_2_website.setText("http://www.barbaraboxer.com/");
            sen_2_tweet.setText("@SenatorBoxer  Feb 25 @SenateDems stood united at the Supreme Court today to tell @Senate_GOPs: #DoYourJob ");

            uri3 = "@drawable/rep_sample_1";
            sen_3_name.setText("Mike Thompson");
            sen_3_party.setText("Democrat");
            sen_3_website.setText("http://mikethompson.house.gov/");
            sen_3_tweet.setText("@RepThompson  6h6 hours ago Proud to announce the 2016 Congressional Art Competition! Deadline to submit artwork is April 4. Contact my Napa District Office for details");
        }
        else {
            uri1 = "@drawable/sen_sample_1_in";
            sen_1_name.setText("Joe Donnelly");
            sen_1_party.setText("Democrat");
            sen_1_website.setText("http://www.donnelly.senate.gov/");
            sen_1_tweet.setText("@SenDonnelly  8h8 hours ago .@INDairport was named the best airport in North America for the 4th year in a row. http://bit.ly/1oKcvA1  #GoodNews");

            uri2 = "@drawable/sen_sample_2_in";
            sen_2_name.setText("Dan Coats");
            sen_2_party.setText("Republican");
            sen_2_website.setText("http://www.coats.senate.gov/");
            sen_2_tweet.setText("@SenatorBoxer  Feb 25 @SenateDems stood united at the Supreme Court today to tell @Senate_GOPs: #DoYourJob ");

            uri3 = "@drawable/rep_sample_2";
            sen_3_name.setText("Peter J. Visclosky");
            sen_3_party.setText("Democrat");
            sen_3_website.setText("https://visclosky.house.gov/");
            sen_3_tweet.setText("@RepVisclosky  Feb 29 Pleased to meet Elizabeth Gonzalez and hear about her inspiring anti-bullying campaign!  Keep up the great work!");
        }

        //Round image and display
        roundedImage = new RoundImage(getResized(uri1));
        roundedImage2 = new RoundImage(getResized(uri2));
        roundedImage3 = new RoundImage(getResized(uri3));

        imageView1 = (ImageView) findViewById(R.id.senator_1_image_view);
        imageView1.setImageDrawable(roundedImage);

        ImageView imageView2 = (ImageView) findViewById(R.id.senator_2_image_view);
        imageView2.setImageDrawable(roundedImage2);

        ImageView imageView3 = (ImageView) findViewById(R.id.senator_3_image_view);
        imageView3.setImageDrawable(roundedImage3);

        //New detailed activity view
        final Intent mDetailedActivity = new Intent(CongressViewActivity.this, DetailedViewActivity.class);

        Button mbutton1 = (Button) findViewById(R.id.senator_1_info_button);
        mbutton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri1);
                mDetailedActivity.putExtra("name", sen_1_name.getText().toString());
                mDetailedActivity.putExtra("party", sen_1_party.getText().toString());

                //Pass dummy values for term ends, current committees, and recent bills
                if(sen_1_name.getText().toString().equals("Dianne Feinstein")){
                    mDetailedActivity.putExtra("term_end", "January 3, 2019");
                    mDetailedActivity.putExtra("committees", "Appropriations Committee, Select Committee on Intelligence, Judiciary Committee, Rules and Administration Committee");
                    mDetailedActivity.putExtra("bills", "Trade Act of 2015, Defending Public Safety Employee's Retirement Act, National Defense Authorization Act for Fiscal Year 2016");
                }
                else{
                    mDetailedActivity.putExtra("term_end", "January 3, 2019");
                    mDetailedActivity.putExtra("committees", "Special Committee on Aging, Agriculture, Nutrition, and Forestry Committee, Armed Services Committee, Banking, Housing, and Urban Affairs Committee");
                    mDetailedActivity.putExtra("bills", "FDA Regulatory Efficiency Act, Clean Air Act");
                }

                startActivity(mDetailedActivity);
            }
        });
        Button mbutton2 = (Button) findViewById(R.id.senator_2_info_button);
        mbutton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri2);
                mDetailedActivity.putExtra("name", sen_2_name.getText().toString());
                mDetailedActivity.putExtra("party", sen_2_party.getText().toString());

                //Pass dummy values for term ends, current committees, and recent bills
                if(sen_2_name.getText().toString().equals("Barbara Boxer")){
                    mDetailedActivity.putExtra("term_end", "January 3, 2017");
                    mDetailedActivity.putExtra("committees", "Select Committee on Ethics, Environment and Public Works Committee, Foreign Relations Committee");
                    mDetailedActivity.putExtra("bills", "Female Veteran Suicide Prevention Act, Pechanga Band of Luiseno Mission Indians Water RIghts Settlement Act, Tule Lake Nation HIstoric Site Establishment Act of 2015");
                }
                else{
                    mDetailedActivity.putExtra("term_end", "January 3, 2017");
                    mDetailedActivity.putExtra("committees", "Finance Committee, Select Committee on Intelligence, Join Economic Committee");
                    mDetailedActivity.putExtra("bills", "Require Evaluation before Implementing Executive Wishlists Act of 2015, Control Unlawful Fugitive Felons Act of 2015, Access to Court challenges Act of 2015");
                }
                startActivity(mDetailedActivity);


            }
        });
        Button mbutton3 = (Button) findViewById(R.id.representative_1_info_button);
        mbutton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDetailedActivity.putExtra("uri", uri3);
                mDetailedActivity.putExtra("name", sen_3_name.getText().toString());
                mDetailedActivity.putExtra("party", sen_3_party.getText().toString());

                //Pass dummy values for term ends, current committees, and recent bills
                if(sen_3_name.getText().toString().equals("Mike Thompson")){
                    mDetailedActivity.putExtra("term_end", "January 3, 2017");
                    mDetailedActivity.putExtra("committees", "Ways and Means Committee");
                    mDetailedActivity.putExtra("bills", "United States Fish and Wildlife Service Resource Protection Act");
                }
                else{
                    mDetailedActivity.putExtra("term_end", "January 3, 2017");
                    mDetailedActivity.putExtra("committees", "Appropriations Committee");
                    mDetailedActivity.putExtra("bills", "American Steel First Act of 2015, Fighting for American Jobs Act of 2015, Fighting for American Jobs Act of 2013");
                }
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
