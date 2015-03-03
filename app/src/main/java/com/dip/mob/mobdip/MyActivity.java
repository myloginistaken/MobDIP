package com.dip.mob.mobdip;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.ext.*;

public class MyActivity extends Activity{

    private Button gallery, camera;
    private Drawable image;
    private ScaleDrawable sc;
    private ImageView theImage;
    private boolean imageSet = false;

    private Bitmap chosenImage = null;
    private static final int IMAGE_CHOSEN = 1;
    private TextView title, welcome, info;

    private static final int CAMERA_DATA = 2;
    private Uri outputFileUri;
    private String mCurrentPhotoPath;

    //Drawer Navigation Menu
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;


    private RelativeLayout relativeLayout;

    // Chapter 1 selected onCreate by default
    //NO!
    private int chapterSelected = 1;

    private int vd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mTitle = mDrawerTitle = getTitle();
        info = (TextView) findViewById(R.id.textView3);
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Chapter 1
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        // Chapter 2
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        // Chapter 3
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        // Chapter 4
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        // Chapter 5
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        // Chapter 6
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);

//		  Set from XML, possible to programmatically set
        float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
        menu.setSatelliteDistance((int) distance);
        menu.setExpandDuration(500);
        menu.setCloseItemsOnClick(true);
        menu.setTotalSpacingDegree(120);

        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(1, android.R.drawable.ic_menu_gallery));
        items.add(new SatelliteMenuItem(2, android.R.drawable.ic_menu_camera));
        items.add(new SatelliteMenuItem(3, android.R.drawable.ic_menu_info_details));
        menu.addItems(items);

        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {

            public void eventOccured(int id) {
                switch (id){
                    case 1:
                        upload();
                        break;
                    case 2:
                        dispatchTakePictureIntent();
                        break;
                    case 3:
                        info.setText("This app has been developed by a magic trio in order to illustrate different image processing techniques learnt in the course Digital Image Processing");
                }
                //Toast.makeText(MyActivity.this, "Clicked on " + id, Toast.LENGTH_LONG).show();
            }
        });

        theImage = (ImageView) findViewById(R.id.image);
        relativeLayout=(RelativeLayout) this.findViewById(R.id.relativeLayout);
        title = (TextView) findViewById(R.id.textView);
        welcome = (TextView) findViewById(R.id.textView2);

        if(savedInstanceState != null){
            chosenImage = savedInstanceState.getParcelable("image");
            image = new BitmapDrawable(chosenImage);
            sc = new ScaleDrawable(image,17,1,1);
            relativeLayout.setBackgroundDrawable(sc);

        }

        if(relativeLayout.getBackground() == null){ //theImage.getDrawable()

            title.setText("MobDIP");
            welcome.setText("Welcome to Digital Image Processing App");
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

        // take photo and upload it to app
        public void takePhotoAndUpload(MenuItem m) {
            dispatchTakePictureIntent();
        }

        // upload picture from gallery

        // Go to gallery
        public void upload() {
            Intent toGallery = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(toGallery, IMAGE_CHOSEN);
            imageSet = true;
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
                    imageSet = true;
                }
            }
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "DIP_" + timeStamp + "_";
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
    @Override
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
                    relativeLayout.setBackgroundDrawable(image);
                    title.setText("");
                    welcome.setText("");
                    info.setText("");
                }
            //TODO: Photo from gallery or from MobDIP? Is it the suitable way? Why do we do it differently than in IMAGE_CHOSEN case?
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
                        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
                        String imageLocation = cursor.getString(1);
                        File imageFile = new File(imageLocation);
                        if (imageFile.exists()) {
                            chosenImage = BitmapFactory.decodeFile(imageLocation);
                            chosenImage.setDensity(Bitmap.DENSITY_NONE);
                            image = new BitmapDrawable(chosenImage);
                            relativeLayout.setBackgroundDrawable(image);
                        }
                    }
                    title.setText("");
                    welcome.setText("");
                    info.setText("");
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
        relativeLayout.setBackgroundDrawable(image);

    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                break;
            case 1:
                fragment = new Ch1();
                break;
            case 2:
                fragment = new Ch2();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            //mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
