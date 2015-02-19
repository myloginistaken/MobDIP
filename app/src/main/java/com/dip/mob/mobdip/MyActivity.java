package com.dip.mob.mobdip;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MyActivity extends Activity {

    private MenuItem upload, camera, ch1, ch2, ch3, ch4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    // upload picture from gallery
    public void upload(MenuItem m){

    }

    // take photo and upload it to app
    public void takePhotoAndUpload(MenuItem m){

    }

    // show menu for chapter 1 functionality selection
    public void chapter1(MenuItem m){

    }

    public void chapter2(MenuItem m){

    }

    public void chapter3(MenuItem m){

    }

    public void chapter4(MenuItem m){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);

        upload = menu.findItem(R.id.upload);
        camera = menu.findItem(R.id.camera);
        ch1 = menu.findItem(R.id.ch1);
        ch2 = menu.findItem(R.id.ch2);
        ch3 = menu.findItem(R.id.ch3);
        ch4 = menu.findItem(R.id.ch4);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.upload:
                //upload();
                return true;
            case R.id.camera:
                //takePhotoAndUpload();
                return true;
            case R.id.ch1:
                //chapter1();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
