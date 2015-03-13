package com.dip.mob.mobdip;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends Activity implements View.OnTouchListener {

    private Button gallery, camera;
    private Drawable colorImage, image;
    private ScaleDrawable sc;
    private ImageView theImage;
    private boolean imageSet = false;
    private boolean grayscaled = false;
    private boolean fromCamera = false;
    private String filePath;
    private String imageLocation;

    private Bitmap chosenImage = null;
    private Bitmap chosenImageColor = null;
    private static final int IMAGE_CHOSEN = 1;
    private TextView title, welcome, info;

    private static final int CAMERA_DATA = 2;
    private Uri outputFileUri;
    private String mCurrentPhotoPath;

    // For zoom
    private Matrix m;
    private Matrix initm;
    private Action act;
    private PointF start;
    private PointF middle;
    private float xMid, yMid;
    private float initDist;

    //Drawer Navigation Menu
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private SeekBar seekBar;

    private ArrayList<ArrayList<String>> groups;
    private ArrayList<String> groupNames, chapter1, chapter2, chapter3, chapter4;

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

        m = new Matrix();
        initm = new Matrix();
        act = Action.NONE;
        start = new PointF();
        middle = new PointF();

        mTitle = mDrawerTitle = getTitle();
        info = (TextView) findViewById(R.id.textView3);

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_groups);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.list_slidermenu);

        //Make data for NavDrawerListAdapter - groups and buttons in sliding menu
        //TODO:Fill arrays with loops
        groups = new ArrayList<ArrayList<String>>();
        groupNames = new ArrayList<String>();
        groupNames.add(getResources().getStringArray(R.array.nav_drawer_groups)[0]);
        groupNames.add(getResources().getStringArray(R.array.nav_drawer_groups)[1]);
        groupNames.add(getResources().getStringArray(R.array.nav_drawer_groups)[2]);
        chapter1 = new ArrayList<String>();
        chapter2 = new ArrayList<String>();
        chapter3 = new ArrayList<String>();
        chapter4 = new ArrayList<String>();
        chapter1.add(getResources().getStringArray(R.array.nav_drawer_kids1)[0]);
        chapter1.add(getResources().getStringArray(R.array.nav_drawer_kids1)[1]);
        chapter1.add(getResources().getStringArray(R.array.nav_drawer_kids1)[2]);
        chapter1.add(getResources().getStringArray(R.array.nav_drawer_kids1)[3]);
        chapter1.add(getResources().getStringArray(R.array.nav_drawer_kids1)[4]);
        groups.add(chapter1);
        chapter2.add(getResources().getStringArray(R.array.nav_drawer_kids2)[0]);
        chapter2.add(getResources().getStringArray(R.array.nav_drawer_kids2)[1]);
        chapter2.add(getResources().getStringArray(R.array.nav_drawer_kids2)[2]);
        chapter2.add(getResources().getStringArray(R.array.nav_drawer_kids2)[3]);
        chapter2.add(getResources().getStringArray(R.array.nav_drawer_kids2)[4]);
        groups.add(chapter2);
        chapter3.add(getResources().getStringArray(R.array.nav_drawer_kids3)[0]);
        chapter3.add(getResources().getStringArray(R.array.nav_drawer_kids3)[1]);
        chapter3.add(getResources().getStringArray(R.array.nav_drawer_kids3)[2]);
        groups.add(chapter3);

        //Make adapter and give it the list of data
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), groups, groupNames);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnChildClickListener(new SlideMenuClickListener());

        SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);

//		  Set from XML, possible to programmatically set
        float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
        menu.setSatelliteDistance((int) distance);
        menu.setExpandDuration(500);
        menu.setCloseItemsOnClick(true);
        menu.setTotalSpacingDegree(90);

        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(7, android.R.drawable.ic_menu_info_details));
        items.add(new SatelliteMenuItem(6, R.drawable.histogram));
        items.add(new SatelliteMenuItem(5, android.R.drawable.ic_menu_save));
        items.add(new SatelliteMenuItem(4, android.R.drawable.ic_menu_camera));
        items.add(new SatelliteMenuItem(3, android.R.drawable.ic_menu_gallery));
        items.add(new SatelliteMenuItem(2, R.drawable.gray));
        items.add(new SatelliteMenuItem(1, R.drawable.color));
        menu.addItems(items);

        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {

            public void eventOccured(int id) {
                switch (id){
                    case 1:
                        // Back to color image
                        colorImage = new BitmapDrawable(chosenImageColor);
                        theImage.setImageDrawable(colorImage);
                        initialPos(theImage);
                        grayscaled=false;
                        break;
                    case 2:
                        // Grayscale image
                        grayscale(theImage);
                        grayscaled=true;
                        break;
                    case 3:
                        upload();
                        break;
                    case 4:
                        dispatchTakePictureIntent();
                        break;
                    case 7:
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

        if(theImage.getDrawable() == null){

            title.setText("MobDIP");
            welcome.setText("Welcome to Digital Image Processing App");
        }

        theImage.setOnTouchListener(this);
    }


        // FUNCTION that makes image middle and nice
        public void initialPos(ImageView iv){
            Matrix mat = new Matrix();
            mat.setRectToRect(new RectF(iv.getDrawable().copyBounds()),new RectF(0,0,iv.getMeasuredWidth(),iv.getMeasuredHeight()),Matrix.ScaleToFit.CENTER);
            iv.setImageMatrix(mat);
            m = mat;
        }

        // take photo and upload it to app
        public void grayscale(ImageView iv) {
            if (chosenImage!=null) {
                Mat gray = new Mat();
                Utils.bitmapToMat(chosenImage, gray);
                Imgproc.cvtColor(gray, gray, Imgproc.COLOR_RGB2GRAY);
                Utils.matToBitmap(gray, chosenImage);
                image = new BitmapDrawable(chosenImage);
                iv.setImageDrawable(image);

            }
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
                    filePath = cursor.getString(colInd);
                    cursor.close();

                    chosenImage = BitmapFactory.decodeFile(filePath);

                    chosenImageColor = BitmapFactory.decodeFile(filePath);
                    colorImage = new BitmapDrawable(chosenImageColor);

                    image = new BitmapDrawable(chosenImage);
                    //relativeLayout.setBackgroundDrawable(image);
                    theImage.setImageDrawable(image);
                    initialPos(theImage);
                    fromCamera = false;
                    title.setText("");
                    welcome.setText("");
                    info.setText("");
                }
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
                        imageLocation = cursor.getString(1);
                        File imageFile = new File(imageLocation);
                        if (imageFile.exists()) {
                            chosenImage = BitmapFactory.decodeFile(imageLocation);

                            chosenImageColor = BitmapFactory.decodeFile(imageLocation);
                            colorImage = new BitmapDrawable(chosenImageColor);

                            image = new BitmapDrawable(chosenImage);
                            theImage.setImageDrawable(image);
                            fromCamera = true;
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
        if ((BitmapDrawable)theImage.getDrawable()!=null) {
            outState.putParcelable("image", ((BitmapDrawable) theImage.getDrawable()).getBitmap());
            outState.putBoolean("ifGrayscaled", grayscaled);
            outState.putBoolean("ifFromCamera", fromCamera);
            outState.putString("pathToFileGallery", filePath);
            outState.putString("pathToFileCamera", imageLocation);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        chosenImage = savedInstanceState.getParcelable("image");
        grayscaled = savedInstanceState.getBoolean("ifGrayscaled");
        fromCamera = savedInstanceState.getBoolean("ifFromCamera");
        filePath = savedInstanceState.getString("pathToFileGallery");
        imageLocation = savedInstanceState.getString("pathToFileCamera");

        if (fromCamera){
            chosenImageColor = BitmapFactory.decodeFile(imageLocation);
        }else {
            chosenImageColor = BitmapFactory.decodeFile(filePath);
        }

        if (grayscaled) {
            image = new BitmapDrawable(chosenImage);
        } else {
            image = new BitmapDrawable(chosenImageColor);
        }

        theImage.setImageDrawable(image);

    }


    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition,
                                long id) {
            // display view for selected nav drawer item
            displayView(groupPosition, childPosition);
            return true;
        }
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    private void displayView(int groupPosition, int childPosition) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (groupPosition) {
            case 0:
                fragment = new Ch1();
                break;
            case 1:
                fragment = new Ch2();
                break;
            case 2:
                fragment = new Ch3();
                break;
            default:
                break;
        }

        if (fragment != null) {
            Bundle bund = new Bundle();
            bund.putInt("childPosition", childPosition);
            fragment.setArguments(bund);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(groupPosition, true);
            mDrawerList.setSelection(groupPosition);
            //setTitle(navMenuTitles[groupPosition]);
            mDrawerLayout.closeDrawer(mDrawerList);
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

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("load", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    private float distBetweenFingers(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float z = (float) Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 0.5);
        return z;
    }

    private enum Action {
        DRAG, ZOOM, NONE
    }

    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {

            // Drag
            case MotionEvent.ACTION_DOWN:
                initm.set(m);
                start.set(e.getX(), e.getY());
                act = Action.DRAG;
                break;

            // Zoom in/out
            case MotionEvent.ACTION_POINTER_DOWN:
                initDist = distBetweenFingers(e);
                initm.set(m);
                xMid = (e.getX(0) + e.getX(1)) / 2;
                yMid = (e.getY(0) + e.getY(1)) / 2;
                middle.set(xMid, yMid);
                act = Action.ZOOM;
                break;

            case MotionEvent.ACTION_MOVE:
                if (act == Action.DRAG) {
                    m.set(initm);
                    m.postTranslate(e.getX() - start.x, e.getY() - start.y);
                } else if (act == Action.ZOOM) {
                    float newDist = distBetweenFingers(e);
                    m.set(initm);
                    float scale = newDist / initDist;
                    m.postScale(scale, scale, middle.x, middle.y);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                act = Action.NONE;
                break;
        }

        theImage.setImageMatrix(m);

        return true;
    }
}
