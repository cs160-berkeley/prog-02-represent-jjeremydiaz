package com.example.jeremy.represent;

/**
 * Created by google on 3/3/2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomFragment extends Fragment {
    //TextView txt;
    String state;
    String county;
    String obama;
    String romney;
    //Note to use only with custom xml
    public CustomFragment(String state, String county, String obama, String romney){
        super();
        this.state = state;
        this.county = county;
        this.obama = obama;
        this.romney = romney;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_fragment, container, false);
        TextView txt1 = (TextView) view.findViewById(R.id.text2);
        txt1.setText(county + ", " +state);

        TextView txt2 = (TextView) view.findViewById(R.id.text3);
        txt2.setText("Obama: " + obama + "%");

        TextView txt3 = (TextView) view.findViewById(R.id.text4);
        txt3.setText("Romney: " + romney + "%");
        return view;
    }
}
