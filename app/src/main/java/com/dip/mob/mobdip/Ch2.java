package com.dip.mob.mobdip;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dip.mob.mobdip.R;

/**
 * Created by anton on 26.02.15.
 */

//Class to handle Chapter 2 image transformations
public class Ch2 extends Fragment {

    private int menuID;
    private TextView title;
    private Bundle bundle; //need to collect data from MyActivity

    public Ch2(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ch2, container, false);
        bundle = getArguments();
        menuID = bundle.getInt("childPosition");
        title = (TextView) getActivity().findViewById(R.id.textView);
        title.setText(getResources().getStringArray(R.array.nav_drawer_kids1)[menuID]);
        title = (TextView) getActivity().findViewById(R.id.textView2);
        title.setText("");

        return rootView;
    }
}