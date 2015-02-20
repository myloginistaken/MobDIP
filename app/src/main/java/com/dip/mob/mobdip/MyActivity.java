package com.dip.mob.mobdip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MyActivity extends Activity{

    private MenuItem upload, camera, ch1, ch2, ch3, ch4;
    private Drawable image;
    private ImageView theImage;
    private Bitmap chosenImage = null;
    private static final int IMAGE_CHOSEN = 1;
    private TextView title, welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        theImage = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.textView);
        welcome = (TextView) findViewById(R.id.textView2);

        if(savedInstanceState != null){
            chosenImage = savedInstanceState.getParcelable("image");
            image = new BitmapDrawable(chosenImage);
            theImage.setImageDrawable(image);
        }

        if(theImage.getDrawable()==null){
            title.setText("MobDIP");
            welcome.setText("Welcome to Digital Image Processing App");
        }
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

    // upload picture from gallery

    // Go to gallery
    public void upload(MenuItem mi) {
        Intent toGallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(toGallery, IMAGE_CHOSEN);
    }

    // Get selected photo
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CHOSEN:
                if (requestCode == IMAGE_CHOSEN) {
                    Uri uri = data.getData();
                    String[] projection = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(uri, projection,
                            null, null, null);
                    cursor.moveToFirst();

                    int colInd = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(colInd);
                    cursor.close();

                    chosenImage = BitmapFactory.decodeFile(filePath);
                    image = new BitmapDrawable(chosenImage);
                    theImage.setImageDrawable(image);
                    title.setText("");
                    welcome.setText("");
                }
        }
    }

    // Handle data save on activity destruction
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", chosenImage);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        chosenImage = savedInstanceState.getParcelable("image");
        image = new BitmapDrawable(chosenImage);
        theImage.setImageDrawable(image);


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
                upload(item);
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
