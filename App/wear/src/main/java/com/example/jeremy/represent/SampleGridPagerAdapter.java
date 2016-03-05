package com.example.jeremy.represent;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a different background is
 * provided.
 * <p>
 * Always avoid loading resources from the main thread. In this sample, the background images are
 * loaded from an background task and then updated using {@link #notifyRowBackgroundChanged(int)}
 * and {@link #notifyPageBackgroundChanged(int, int)}.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter{
    private static final int TRANSITION_DURATION_MILLIS = 50;

    private final Context mContext;
    private List<Row> mRows;
    private ColorDrawable mDefaultBg;

    private ColorDrawable mClearBg;

    //TODO dummy zip to choose background images
    private String zip_choose;

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm, String [] values) {
        super(fm);
        mContext = ctx;

        //TODO dummy code
        zip_choose = values[6];

        mRows = new ArrayList<SampleGridPagerAdapter.Row>();

        mRows.add(new Row(
                //cardFragment(CardFragment.create(), R.string.test),
                //CardFragment.create(values[0], values[1]),
                //CustomCardFragment.create(values[0], values[1]),
                cardFragment(values[0], values[1], 0, 0),
                new CustomFragment()));
        mRows.add(new Row(
                //CardFragment.create(values[2], values[3]),
                cardFragment(values[2], values[3], 0, 1),
                new CustomFragment()));
        mRows.add(new Row(
                //CardFragment.create(values[4], values[5]),
                cardFragment(values[4], values[5], 0, 2),
                new CustomFragment()));
        mDefaultBg = new ColorDrawable(R.color.dark_grey);
        mClearBg = new ColorDrawable(android.R.color.transparent);
    }

    LruCache<Integer, Drawable> mRowBackgrounds = new LruCache<Integer, Drawable>(3) {
        @Override
        protected Drawable create(final Integer row) {
            //TODO dummy static resid, has to be dynamic

            int resid = 0;
            if(zip_choose.equals("94547")) {
                resid = BG_IMAGES[row % BG_IMAGES.length];
            }
            else{
                resid = BG_IMAGES2[row % BG_IMAGES2.length];
            }

            new DrawableLoadingTask(mContext) {
                @Override
                protected void onPostExecute(Drawable result) {
                    TransitionDrawable background = new TransitionDrawable(new Drawable[] {
                            mDefaultBg,
                            result
                    });
                    mRowBackgrounds.put(row, background);
                    notifyRowBackgroundChanged(row);
                    background.startTransition(TRANSITION_DURATION_MILLIS);
                }
            }.execute(resid);
            return mDefaultBg;
        }
    };

    LruCache<Point, Drawable> mPageBackgrounds = new LruCache<Point, Drawable>(3) {
        @Override
        protected Drawable create(final Point page) {
            // place bugdroid as the background at row 2, column 1
            return GridPagerAdapter.BACKGROUND_NONE;
        }
    };


    /*
    private Fragment cardFragment(int titleRes, int textRes) {
        Resources res = mContext.getResources();
        CardFragment fragment =
                CardFragment.create(res.getText(titleRes), res.getText(textRes));
        // Add some extra bottom margin to leave room for the page indicator
        fragment.setCardMarginBottom(
                res.getDimensionPixelSize(R.dimen.card_margin_bottom));
        return fragment;
    }
    */

    /*
    private Fragment cardFragment(int titleRes, int textRes) {
        Resources res = mContext.getResources();
        CustomCardFragment fragment = CustomCardFragment.create(res.getText(titleRes), res.getText(textRes));
        fragment.setCardMarginBottom(res.getDimensionPixelSize(R.dimen.card_margin_bottom));
        fragment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                //do everything you want
                //here you've got access to clicked fragment
                Log.d("Test", "on click test2");
            }

        });
        return fragment;
    }
    */

    //test
    private Fragment cardFragment(String title, String text, int x, int y){
        Resources res = mContext.getResources();
        final CustomCardFragment fragment = CustomCardFragment.create(title, text, x, y);
        fragment.setCardMarginBottom(res.getDimensionPixelSize(R.dimen.card_margin_bottom));
        fragment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                //do everything you want
                //here you've got access to clicked fragment
                int position = fragment.getFragmentPosition();

                //New intent for detailed view
                Intent detailedIntent = new Intent(mContext, WatchToPhoneService.class);
                detailedIntent.putExtra("path", "detailed");
                detailedIntent.putExtra("message", fragment.getName());

                Log.d("Position", Integer.toString(position));
                if (position == 0) {
                    Log.d("Test", "0");
                }
                else if(position == 1){
                    Log.d("Test", "1");
                }
                else{
                    Log.d("Test", "2");
                }
                mContext.startService(detailedIntent);
            }
        });
        return fragment;
    }

    //TODO dummy, need to find a way to load images dynamically
    //Change this to draw bg images
    static final int[] BG_IMAGES = new int[] {
            R.drawable.sen_sample_1,
            R.drawable.sen_sample_2,
            R.drawable.rep_sample_1,
    };
    static final int[] BG_IMAGES2 = new int[] {
            R.drawable.sen_sample_1_in,
            R.drawable.sen_sample_2_in,
            R.drawable.rep_sample_2
    };

    /** A convenient container for a row of fragments. */
    private class Row {
        final List<Fragment> columns = new ArrayList<Fragment>();

        public Row(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            columns.add(f);
        }

        Fragment getColumn(int i) {
            return columns.get(i);
        }

        public int getColumnCount() {
            return columns.size();
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getColumn(col);
    }

    @Override
    public Drawable getBackgroundForRow(final int row) {
        return mRowBackgrounds.get(row);
    }

    @Override
    public Drawable getBackgroundForPage(final int row, final int column) {
        return mPageBackgrounds.get(new Point(column, row));
    }

    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mRows.get(rowNum).getColumnCount();
    }

    class DrawableLoadingTask extends AsyncTask<Integer, Void, Drawable> {
        private static final String TAG = "Loader";
        private Context context;

        DrawableLoadingTask(Context context) {
            this.context = context;
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            Log.d(TAG, "Loading asset 0x" + Integer.toHexString(params[0]));
            return context.getResources().getDrawable(params[0]);
        }
    }
}