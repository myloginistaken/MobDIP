package com.dip.mob.mobdip;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anton on 26.02.15.
 */

public class Ch2 extends Fragment {

    public Ch2(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ch2, container, false);

        return rootView;
    }
}