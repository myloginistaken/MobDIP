package com.dip.mob.mobdip;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//Class to handle Chapter 1 image transformations
public class Ch1 extends Fragment {

    private int menuID;
    private TextView title;
    private Bundle bundle; //need to collect data from MyActivity
    private View seekBarView;
    private SeekBar seekBar;
    private DrawerLayout layout;
    private Action currentAction;

    private enum Action {
        NN, BIC, BIL, LANC, BIT
    }

    public Ch1(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        View rootView = inflater.inflate(R.layout.fragment_ch1, container, false);
        bundle = getArguments();
        menuID = bundle.getInt("childPosition");
        title = (TextView) getActivity().findViewById(R.id.textView);
        title.setText(getResources().getStringArray(R.array.nav_drawer_kids1)[menuID]);
        title = (TextView) getActivity().findViewById(R.id.textView2);
        title.setText("");

        switch (menuID){
            case 0:
                currentAction=Action.NN;
                break;
            case 1:
                currentAction=Action.BIC;
                break;
            case 2:
                currentAction=Action.BIL;
                break;
            case 3:
                currentAction=Action.LANC;
                break;
            case 4:
                currentAction=Action.BIT;
                break;
        }

        seekBarView = getActivity().getLayoutInflater().inflate(R.layout.seekbar, null);
        layout.addView(seekBarView);
        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChanged=progress;
                //Toast.makeText(getActivity(), "Progress: " + progress, Toast.LENGTH_SHORT).show();
                switch (currentAction){
                    case NN:
                        // Interpolate by the factor of progress
                        break;
                    case BIC:
                        // Interpolate by the factor of progress
                        break;
                    case BIL:
                        // Interpolate by the factor of progress
                        break;
                    case LANC:
                        // Interpolate by the factor of progress
                        break;
                    case BIT:
                        seekBar.setMax(8);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        return rootView;
    }


}
