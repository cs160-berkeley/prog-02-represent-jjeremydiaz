package com.example.jeremy.represent;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailedViewActivity extends AppCompatActivity {
    RoundImage roundedImage;
    String uri;
    String name;
    String party;
    String term;
    String committees;
    String bills;
    String bioguide;

    String image_base = "https://theunitedstates.io/images/congress/225x275/";
    String jpg = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get passed values from previous previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uri = extras.getString("uri");
            name = extras.getString("name");
            party = extras.getString("party");
            term = extras.getString("term_end");
            committees = extras.getString("committees");
            bills = extras.getString("bills");
            bioguide = extras.getString("bioguide");
        }

        //Convert new scaled down square image to round
        int imageId = getResources().getIdentifier(uri, null, getPackageName());
        //Gets memory effecient scaled down bitmap image
        Bitmap mBitMap = decodeSampledBitmapFromResource(getResources(), imageId, 350, 350);
        //Now must crop to the middle
        Bitmap resized = Bitmap.createScaledBitmap(mBitMap, 350, 350, true);

        //Round image and display
        roundedImage = new RoundImage(resized);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        //imageView.setImageDrawable(roundedImage);
        Picasso.with(this).load(image_base + bioguide + jpg).resize(350, 350).transform(new CircleTransform()).into(imageView);


        //Write to EditTexts
        EditText text_name = (EditText) findViewById(R.id.detailed_view_name);
        text_name.setText(name);

        EditText party_name = (EditText) findViewById(R.id.detailed_view_party);
        party_name.setText(party);

        EditText term_name = (EditText) findViewById(R.id.senator_term_date);
        term_name.setText(term);

        EditText committee_name = (EditText) findViewById(R.id.senator_commities_list);
        committee_name.setText(committees);

        EditText bill_name = (EditText) findViewById(R.id.senator_bills_list);
        bill_name.setText(bills);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}

