package com.example.jeremy.represent;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Jeremy on 3/5/2016.
 */
public class CustomCardFragment extends CardFragment{
    private View fragmentView;
    private View.OnClickListener listener;
    private int x;
    private int y;
    private String name;

    public static CustomCardFragment create(CharSequence title, CharSequence text, int x, int y) {
        CustomCardFragment fragment = new CustomCardFragment();
        Bundle args = new Bundle();
        if (title != null) args.putCharSequence("CardFragment_title", title);
        if (text != null) args.putCharSequence("CardFragment_text", text);
        fragment.setName(title.toString()); //TODO may be for testing purposes only until api is set
        fragment.setArguments(args);
        fragment.setPosition(x, y);
        return fragment;
    }

    @Override
    public View onCreateContentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.d("Test", "on create content");
        fragmentView = super.onCreateContentView(inflater, container, savedInstanceState);
        container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    Log.d("Test", "on click test onclick");
                    listener.onClick(view);
                }
            }

        });
        return fragmentView;
    }

    public void setOnClickListener(final View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getFragmentPosition(){
        if(this.x == 0 && this.y == 0){
            return 0;
        }
        else if(this.x == 0 && this.y == 1){
            return 1;
        }
        else{
            return 2;
        }
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}
