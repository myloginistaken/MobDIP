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
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


//Class to handle Chapter 1 image transformations
public class Ch1 extends Fragment {

    // the code is looking at you! 0_0
    private int[][] table = new int[9][256]; //for faster bitplane slicing

    private int menuID;
    private TextView title, seekBarLbl;
    private Bundle bundle; //need to collect data from MyActivity
    //private View seekBarView;
    private SeekBar seekBar;
    private DrawerLayout layout;
    private Action currentAction;
    private int imgCnt;

    private ImageView theImage;
    private Bitmap bitmap, resultBmp, origin;
    private Drawable drawable;
    private View rootView;

    private enum Action {
        NN, BIC, BIL, LANC, BIT
    }

    public Ch1(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        rootView = inflater.inflate(R.layout.fragment_ch1, container, false);
        bundle = getArguments();
        menuID = bundle.getInt("childPosition");
        title = (TextView) getActivity().findViewById(R.id.textView);
        title.setText(getResources().getStringArray(R.array.nav_drawer_kids1)[menuID]);
        title = (TextView) getActivity().findViewById(R.id.textView2);
        seekBarLbl = (TextView) getActivity().findViewById(R.id.seekBarLbl);
        title.setText("");
        origin = MyActivity.getOrigin();

        //creates the table for bitplane slicing
        int s;
        for (int j = 2; j<9;j++){
            s = (int)Math.pow(2,8-j);
            for (int i=0;i<256;i++)
                table[j][i]=(i/s)*s;
        }
        for (int i=0;i<128;i++)
            table[1][i]=0;
        for (int i=128;i<256;i++)
            table[1][i]=255;
        // for (int i=0;i<256;i++) //not necessary as it never gets to zero
        //   table[0][i]=0;
        //end of creating table


        return rootView;

    }
    public void onResume(){
        super.onResume();
        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);

        theImage = (ImageView) getActivity().findViewById(R.id.image);
        int width = ((BitmapDrawable) theImage.getDrawable()).getBitmap().getWidth();
        int height = ((BitmapDrawable) theImage.getDrawable()).getBitmap().getHeight();
        int max = 1;
        //TODO: Make correct ifs (use Bounds)
        if(width*8<=4096 || height*8<=4096){
            max = 8;
        } else if(width*5<=4096 || height*5<=4096){
            max = 5;
        } else if(width*2<=4096 || height*2<=4096){
            max = 2;
        } else {
            max = 0;
            Toast.makeText(getActivity(), "Image is too big to interpolate", Toast.LENGTH_SHORT).show();
        }
        switch (menuID){
            case 0:
                currentAction=Action.NN;
                seekBarInit(max, 1);
                break;
            case 1:
                currentAction=Action.BIC;
                seekBarInit(max, 1);
                break;
            case 2:
                currentAction=Action.BIL;
                seekBarInit(max, 1);
                break;
            case 3:
                currentAction=Action.LANC;
                seekBarInit(max, 1);
                break;
            case 4:
                currentAction=Action.BIT;
                seekBarInit(8, 8);
                break;
        }



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBarLbl.setText(""+progress);
                int seekLblPos = ((((seekBar.getRight() - seekBar.getLeft())*seekBar.getProgress())/seekBar.getMax())+seekBar.getLeft())-15;
                seekBarLbl.setX(seekLblPos);
                //Toast.makeText(getActivity(), "Progress: " + progress, Toast.LENGTH_SHORT).show();
                switch (currentAction){
                    case NN:
                        // Interpolate by the factor of progress
                        interpolate(progress, Imgproc.INTER_NEAREST);
                        //Toast.makeText(getActivity(), "Size: " + resultingImage.size(), Toast.LENGTH_SHORT).show();
                        break;
                    case BIC:
                        // Interpolate by the factor of progress
                        interpolate(progress, Imgproc.INTER_CUBIC);
                        break;
                    case BIL:
                        // Interpolate by the factor of progress
                        interpolate(progress, Imgproc.INTER_LINEAR);
                        break;
                    case LANC:
                        // Interpolate by the factor of progress
                        interpolate(progress, Imgproc.INTER_LANCZOS4);
                        break;
                    case BIT:
                        Mat oneBit = new Mat();
                        Utils.bitmapToMat(bitmap, oneBit);
                        resultBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Imgproc.cvtColor(oneBit, oneBit, Imgproc.COLOR_RGB2GRAY);
                        oneBit.convertTo(oneBit, CvType.CV_32S);
                        int size = (int) (oneBit.total()*oneBit.channels());

                        int[] temp = new int[size];

                        oneBit.get(0,0,temp);
                        // 1-8 bit representations
                        if (progress<2){
                            int[] result = bitPlaneSlice(temp, size, 1);
                            oneBit.put(0,0,result);
                        }else if (progress>=2 && progress<3){
                            int[] result = bitPlaneSlice(temp, size, 2);
                            oneBit.put(0,0,result);
                        }else if (progress<4){
                            int[] result = bitPlaneSlice(temp, size, 3);
                            oneBit.put(0,0,result);
                        }else if (progress<5){
                            int[] result = bitPlaneSlice(temp, size, 4);
                            oneBit.put(0,0,result);
                        }else if (progress<6){
                            int[] result = bitPlaneSlice(temp, size, 5);
                            oneBit.put(0,0,result);
                        }else if (progress<7){
                            int[] result = bitPlaneSlice(temp, size, 6);
                            oneBit.put(0,0,result);
                        }

                        oneBit.convertTo(oneBit, CvType.CV_8UC1);
                        Utils.matToBitmap(oneBit, resultBmp);
                        drawable = new BitmapDrawable(resultBmp);
                        theImage.setImageDrawable(drawable);
                        MyActivity.initialPos(theImage);

                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int currentImgCnt = MyActivity.getInstance().getImgCnt();
                if(imgCnt!=currentImgCnt){
                    bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
                    imgCnt = currentImgCnt;
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    private void seekBarInit(int max, int current){
        seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
        seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
        seekBar.setMax(max);
        seekBar.setProgress(current);
        bitmap = ((BitmapDrawable) theImage.getDrawable()).getBitmap();
        imgCnt = MyActivity.getInstance().getImgCnt();
    }

    private void interpolate(int seekBarProg, int interpolation){
        //restore the original image
        Mat origImage = new Mat();
        Utils.bitmapToMat(origin, origImage);
        theImage.setImageDrawable(new BitmapDrawable(origin));

        int zoomingFactor;
        if (seekBarProg>0) {
            zoomingFactor = seekBarProg;
        } else {
            zoomingFactor=1;
        }
        if(origImage.rows()*zoomingFactor * origImage.cols()*zoomingFactor>4096*4096){
            Toast.makeText(getActivity(), "Image is big", Toast.LENGTH_SHORT).show();
        } else {
            Mat resultingImage = new Mat(origImage.rows()*zoomingFactor, origImage.cols()*zoomingFactor, origImage.type());
            Imgproc.resize(origImage, resultingImage, resultingImage.size(), zoomingFactor, zoomingFactor, interpolation);
            resultBmp = Bitmap.createBitmap(resultingImage.width(), resultingImage.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(resultingImage, resultBmp);
            drawable = new BitmapDrawable(resultBmp);
            theImage.setImageDrawable(drawable);
            MyActivity.initialPos(theImage);
            Toast.makeText(getActivity(), "Image size is: " + resultingImage.size(), Toast.LENGTH_SHORT).show();
        }
    }


    private int[] bitPlaneSlice(int[] input, int size, int numOfPlanes){
        int[] outPut = new int[size];
        for (int i=0;i<size;i++){
                outPut[i]=table[numOfPlanes][input[i]];

        }

        return outPut;
    }

}
