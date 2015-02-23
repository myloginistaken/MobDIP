package com.dip.mob.mobdip;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yvelabs.satellitemenu.AbstractAnimation;
import com.yvelabs.satellitemenu.DefaultAnimation;
import com.yvelabs.satellitemenu.SatelliteItemModel;
import com.yvelabs.satellitemenu.SatelliteMenu;
import com.yvelabs.satellitemenu.SettingPara;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyActivity extends Activity{

    private MenuItem upload, camera, ch1, ch2, ch3, ch4;
    private Drawable image;
    private ImageView theImage;

    private Bitmap chosenImage = null;
    private static final int IMAGE_CHOSEN = 1;
    private TextView title, welcome;

    private static final int CAMERA_DATA = 2;
    private Uri outputFileUri;
    private String mCurrentPhotoPath;

    private SatelliteMenu satelliteMenu;
    private ArrayList<SatelliteItemModel> satllites;

    // Chapter 1 selected onCreate by default
    private int chapterSelected = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        satelliteMenu = (SatelliteMenu) this.findViewById(R.id.satellite_menu);

        satllites = new ArrayList<SatelliteItemModel>();
        satllites.add(new SatelliteItemModel(1, R.drawable.satellite_1));
        satllites.add(new SatelliteItemModel(2, R.drawable.satellite_1));
        satllites.add(new SatelliteItemModel(3, R.drawable.satellite_1));
        satllites.add(new SatelliteItemModel(4, R.drawable.satellite_1));
        satllites.add(new SatelliteItemModel(5, R.drawable.satellite_1));

        SettingPara para = new SettingPara(0, 90, 300, R.drawable.planet_menu, satllites);

        AbstractAnimation anim = new DefaultAnimation();
        para.setMenuAnimation(anim);
        para.setPlanetPosition(SettingPara.POSITION_BOTTOM_LEFT);

        try {
            satelliteMenu.setting(para);
        } catch (Exception e) {
            e.printStackTrace();
        }

        satelliteMenu.setOnSatelliteClickedListener(new SatelliteMenu.OnSatelliteClickedListener() {
            @Override
            public void onClick(View v) {
                String a = "getLeft: " + v.getLeft() + ", getTop: "
                        + v.getTop() + ", getRight: " + v.getRight()
                        + ", getBottom: " + v.getBottom();
                Toast.makeText(MyActivity.this, a, Toast.LENGTH_SHORT).show();
            }
        });

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
    public void takePhotoAndUpload(MenuItem m) {
        dispatchTakePictureIntent();
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                outputFileUri = Uri.fromFile(photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_DATA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MobDIP");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d("Directory name", "failed to create directory");
                return null;
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // Get selected photo
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CHOSEN:
                if (requestCode == IMAGE_CHOSEN && data!=null){
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
                /**
                 * NEXT CASE DOES NOT WORK PROPERLY. PICTURE IS NOT RETURNED TO THE APP FROM CAMERA!!!
                 * Perhaps, just automatically open it from gallery
                 */
            case CAMERA_DATA :
                if (requestCode == CAMERA_DATA){
                    // Find the last picture
                    String[] projection = new String[]{
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.DATE_TAKEN,
                            MediaStore.Images.ImageColumns.MIME_TYPE
                    };
                    final Cursor cursor = getContentResolver()
                            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                    null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

                    // Put it in the image view
                    if (cursor.moveToFirst()) {
                        final ImageView imageView = (ImageView) findViewById(R.id.image);
                        String imageLocation = cursor.getString(1);
                        File imageFile = new File(imageLocation);
                        if (imageFile.exists()) {   // TODO: is there a better way to do this?
                            Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                            imageView.setImageBitmap(bm);
                        }
                    }
                    title.setText("");
                    welcome.setText("");
                }


            /*case CAMERA_DATA :
                if (requestCode == CAMERA_DATA){
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
                */
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
                takePhotoAndUpload(camera);
                return true;
            case R.id.ch1:
                //chapter1();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
