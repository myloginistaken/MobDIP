package com.dip.mob.mobdip;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

/**
 * Created by anton on 26.02.15.
 */

//Class to handle Chapter 2 image transformations
public class Ch2 extends Fragment {

    private int menuID;
    private TextView title, seekBarLbl;
    private Bundle bundle; //need to collect data from MyActivity
    //private View seekBarView;
    private SeekBar seekBar;
    private DrawerLayout layout;
    private Action currentAction;
    private int imgCnt;

    private ImageView theImage;
    private Bitmap bitmap, resultBmp;
    private Drawable drawable;
    private View rootView;

    private enum Action {
        POW,ROOT, EXP, LOG, HIST, SVD
    }


    public Ch2() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        layout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        rootView = inflater.inflate(R.layout.fragment_ch2, container, false);
        bundle = getArguments();
        menuID = bundle.getInt("childPosition");
        title = (TextView) getActivity().findViewById(R.id.textView);
        title.setText(getResources().getStringArray(R.array.nav_drawer_kids2)[menuID]);
        title = (TextView) getActivity().findViewById(R.id.textView2);
        seekBarLbl = (TextView) getActivity().findViewById(R.id.seekBarLbl);
        title.setText("");


        // for (int i=0;i<256;i++) //not necessary as it never gets to zero
        //   table[0][i]=0;
        //end of creating table


        return rootView;

    }

    public void onResume() {
        super.onResume();
        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);

        theImage = (ImageView) getActivity().findViewById(R.id.image);

        switch (menuID) {
            case 0:
                currentAction = Action.POW;
                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
                seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                seekBar.setMax(200);
                seekBar.setProgress(100);
                bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                imgCnt = MyActivity.getInstance().getImgCnt();
                break;
            case 1:
                currentAction = Action.EXP;
                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
                seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                seekBar.setMax(600);
                seekBar.setProgress(272);
                bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                imgCnt = MyActivity.getInstance().getImgCnt();
                break;
            case 2:
                currentAction = Action.LOG;
                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
                seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                seekBar.setMax(200);
                seekBar.setProgress(100);
                bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                imgCnt = MyActivity.getInstance().getImgCnt();
                break;
            case 3:
                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
                seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                seekBar.setMax(200);
                seekBar.setProgress(100);

                currentAction = Action.HIST;
                bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                imgCnt = MyActivity.getInstance().getImgCnt();
                break;
            case 4:
                currentAction = Action.SVD;

                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
                seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                seekBar.setMax(200);
                seekBar.setProgress(100);
                bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                imgCnt = MyActivity.getInstance().getImgCnt();
                break;
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBarLbl.setText("" + progress/100.0);
                int seekLblPos = ((((seekBar.getRight() - seekBar.getLeft()) * seekBar.getProgress()) / seekBar.getMax()) + seekBar.getLeft()) - 15;
                seekBarLbl.setX(seekLblPos);
                //Toast.makeText(getActivity(), "Progress: " + progress, Toast.LENGTH_SHORT).show();

                switch (currentAction) {
                    case POW:
                        // Interpolate by the factor of progress
                        Mat illuPOW = new Mat();
                        Utils.bitmapToMat(bitmap, illuPOW);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(illuPOW, illuPOW, Imgproc.COLOR_RGB2GRAY);
                        illuPOW.convertTo(illuPOW, CvType.CV_32F);
                        //illuminate with progress = n

                        illuPOW = power(illuPOW,progress/100.0);
                        illuPOW.convertTo(illuPOW, CvType.CV_8U);
                        Utils.matToBitmap(illuPOW, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);
                        break;
                    case HIST:
                        Mat illuHIST = new Mat();
                        Utils.bitmapToMat(bitmap, illuHIST);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(illuHIST, illuHIST, Imgproc.COLOR_RGB2GRAY);
                        //illuminate with progress = n
                        // illuHIST.convertTo(illuHIST, CvType.CV_8UC1);
                        Imgproc.equalizeHist(illuHIST,illuHIST);
                        Utils.matToBitmap(illuHIST, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);
                        break;
                    case EXP:
                        // Interpolate by the factor of progress
                        Mat illuEXP = new Mat();
                        Utils.bitmapToMat(bitmap, illuEXP);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(illuEXP, illuEXP, Imgproc.COLOR_RGB2GRAY);
                        illuEXP.convertTo(illuEXP, CvType.CV_32F);
                        //illuminate with progress = n
                        illuEXP = exp(illuEXP,progress/100.0);
                        illuEXP.convertTo(illuEXP, CvType.CV_8U);
                        Utils.matToBitmap(illuEXP, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);
                        break;
                    case LOG:
                        // Interpolate by the factor of progress
                        Mat illuLOG = new Mat();
                        Utils.bitmapToMat(bitmap, illuLOG);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(illuLOG, illuLOG, Imgproc.COLOR_RGB2GRAY);
                        illuLOG.convertTo(illuLOG, CvType.CV_32F);
                        illuLOG = log(illuLOG,progress/100.0);
                        illuLOG.convertTo(illuLOG, CvType.CV_8U);
                        Utils.matToBitmap(illuLOG, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);
                        break;
                    case SVD:
                        //do some more
                        Mat illuSVD = new Mat();
                        Utils.bitmapToMat(bitmap, illuSVD);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(illuSVD, illuSVD, Imgproc.COLOR_RGB2GRAY);

                        //illuminate with progress = n


                        Utils.matToBitmap(illuSVD, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);

                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int currentImgCnt = MyActivity.getInstance().getImgCnt();
                if (imgCnt != currentImgCnt) {
                    bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                    imgCnt = currentImgCnt;
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public static Mat power(Mat image, double n){
        Mat result = new Mat(image.size(), image.type()) ;
        int m =(int) image.elemSize();

        double [] data = new double[m];
        Core.pow(image, n, result);
        return result;
    }


    public static Mat log(Mat image, double n){
        Mat result = new Mat(image.size(), image.type()) ;
        int m =(int) image.elemSize();
        n = 1/Math.log(n);
        Scalar n2 = new Scalar(n);
        Core.log(image, result);
        Core.multiply(result, n2, result);
        return result;
    }

    public static Mat exp(Mat image, double n){
        Mat result = new Mat(image.size(), image.type()) ;
        int m =(int) image.elemSize(); 
        n = Math.exp(n);
        for (int i = 0; i<image.rows();i++){
            for (int j = 0; j<image.cols();j++){
                result.put(i, j, Math.pow(n,image.get(i,j)[0]));
            }
        }
        return result;
    }


}

