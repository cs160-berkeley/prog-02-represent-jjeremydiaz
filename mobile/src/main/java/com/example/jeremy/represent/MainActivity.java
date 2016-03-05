package com.example.jeremy.represent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    private Button mPopUpButton;
    private PopupWindow mPopUpWindow;
    private LayoutInflater mPopUpLayoutInflator;
    private ScrollView mScrollView;
    private int zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For demo use only
        final String demo_arr[] = {"46360", "94547"};

        //Handle popup window
        mPopUpButton = (Button) findViewById(R.id.button0);
        mScrollView = (ScrollView) findViewById(R.id.main_scroll);

        mPopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopUpLayoutInflator = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup mContainer = (ViewGroup) mPopUpLayoutInflator.inflate(R.layout.popup_layout, null);

                //Create popup window
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                mPopUpWindow = new PopupWindow(mContainer, metrics.widthPixels, 600, true);
                mPopUpWindow.showAtLocation(mScrollView, Gravity.CENTER, 0, 0);

                //Dim background
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams pp = (WindowManager.LayoutParams) mContainer.getLayoutParams();
                pp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                pp.dimAmount = 0.4f;
                wm.updateViewLayout(mContainer, pp);

                //Close if click on outside
                mContainer.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        mPopUpWindow.dismiss();
                        mPopUpWindow = null;
                        return true;
                    }
                });
                final EditText mZipEdit = (EditText) mContainer.findViewById(R.id.zip_edit);
                final EditText mWarningText = (EditText) mContainer.findViewById(R.id.warningText);

                //Pop up window submit listener, will open new activity and send ZIP data to next activity
                //Find button in popup view
                //View pview = mPopUpLayoutInflator.inflate(R.layout.popup_layout, (ViewGroup)findViewById(R.id.main_scroll));
                Button mZipSumbit = (Button) mContainer.findViewById(R.id.button2);
                mZipSumbit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //place location api stuff here

                        String testString = mZipEdit.getText().toString();
                        //check if input string is valid
                        if (android.text.TextUtils.isDigitsOnly(testString) && testString.length() == 5) {
                            //Service intent to communicate with wear
                            Intent mToWear = new Intent(getBaseContext(), PhoneToWatchService.class);
                            mToWear.putExtra("zip", testString);
                            //dummy variables, TODO: change to api results
                            mToWear.putExtra("sen_1_name", "Joe Donnelly");
                            mToWear.putExtra("sen_1_party", "Democrat");
                            mToWear.putExtra("sen_2_name", "Dan Coats");
                            mToWear.putExtra("sen_2_party", "Republican");
                            mToWear.putExtra("rep_1_name", "Peter J. Visclosky");
                            mToWear.putExtra("rep_1_party", "Democrat");
                            startService(mToWear);

                            Intent mToCongressView = new Intent(MainActivity.this, CongressViewActivity.class);
                            mToCongressView.putExtra("zip1", testString);
                            startActivity(mToCongressView);
                        } else {
                            mWarningText.setText("Invalid ZIP code!");
                        }
                    }
                });

                //Close window on cancel
                final Button mZipCancel = (Button) mContainer.findViewById(R.id.button3);
                mZipCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopUpWindow.dismiss();
                    }
                });
            }
        });

        //on current location click
        Button mCurrentLocation = (Button) findViewById(R.id.button1);
        mCurrentLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //place zip api stuff here

                //Service intent to communicate with wear
                Intent mToWear = new Intent(getBaseContext(), PhoneToWatchService.class);

                //dummy variables, TODO: change to api results
                mToWear.putExtra("zip", demo_arr[1]);
                mToWear.putExtra("sen_1_name", "Dianne Feinstein");
                mToWear.putExtra("sen_1_party", "Democrat");
                mToWear.putExtra("sen_2_name", "Barbara Boxer");
                mToWear.putExtra("sen_2_party", "Democrat");
                mToWear.putExtra("rep_1_name", "Mike Thompson");
                mToWear.putExtra("rep_1_party", "Democrat");
                startService(mToWear);

                Intent mToCongressView2 = new Intent(MainActivity.this, CongressViewActivity.class);
                mToCongressView2.putExtra("zip1", demo_arr[1]);
                startActivity(mToCongressView2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
