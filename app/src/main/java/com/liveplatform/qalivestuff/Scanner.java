package com.liveplatform.qalivestuff;

import Data.IConstants;
import SupportClasses.CameraPreview;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

import static Data.IConstants._QR;

@SuppressWarnings("deprecation")
public class Scanner extends Activity {
    /* all Views attached with java codes */
    @BindView(R.id.scanText)
    TextView scanText;
    @BindView(R.id.scan)
    Button scanBtn;
    @BindView(R.id.scanPreview)
    FrameLayout sp;
    /* variables to be used by this class */
    private Intent returnresp;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    View animatedImage;
    //ObjectAnimator mAnimation;
    String CB = "No";
    static boolean support = true;
    CommonUI ci;
    private Dialog alertDialog1;

    /*
     * this block will gets executed before any other methods and loads the NDK
     * for Scanner Library
     */
    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_scanner_url);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        returnresp = this.getIntent();
        CB = returnresp.getStringExtra(_QR);
        try {
            if (CB.equals(null))
                CB = "No";
        } catch (NullPointerException ne) {
            Log.e("NPE Sc", ne.getMessage());
            CB = "No";
        }

        ButterKnife.bind(this);
        ci = new CommonUI(this);
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Scanner.this,
                        android.Manifest.permission.CAMERA)) {
                    ci.showAlertDialogWithYesNoCallBack(Scanner.this,"Camera Permission","LiveStuff needs permission to utilize the Camera.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    /*alertDialog1 = alertDialog;*/
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(Scanner.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            });
                } else {
                    ActivityCompat.requestPermissions(Scanner.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else {
                init();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ci.showNotification("Camera", "No permission to access camera");
                ci.showAlert("Please Grant permission to access Camera in settings", "Camera Access", "OK");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            init();
        }
    }

    private void init() {
       /* if(alertDialog1 != null) alertDialog1.dismiss();*/
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        try {
            Log.e("inside init","Scanner");
        /* Instance barcode scanner */
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(mPreview);
		/*	mPreview.setX(sp.getX());
			mPreview.setY(sp.getY());*/
		/*
		Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
		animatedImage.startAnimation(animation);*/
            //setAnimation();
        } catch (Exception e) {
            ci.logit("Exception in lib", e.getMessage());
            Toast.makeText(getApplicationContext(), "Not Supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IConstants.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    if (Build.VERSION.SDK_INT < 23) {
                        ci.showNotification("Camera", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Camera Access", "OK");
                    }else {
                        ci.showAlertDialogWithYesNoCallBack(Scanner.this,"Camera Permission","LiveStuff needs permission to utilize the Camera.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                    /*alertDialog1 = alertDialog;*/
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(Scanner.this,
                                                new String[]{Manifest.permission.CAMERA},
                                                IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                });
                    }
                }
                return;


        }
    }
    /*public void setAnimation() {
        animatedImage =  findViewById(R.id.imageView3);
        mAnimation = ObjectAnimator.ofFloat(animatedImage,"y",500,1350);//TranslateAnimation(0, 0, 0, 900);

        mAnimation.setDuration(10000);
        //mAnimation.setFillAfter(true);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(ValueAnimator.REVERSE);
        //mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.start();
        //animatedImage.setAnimation(mAnimation);


    }*/

    /* method will restart the scanning process again */
    @OnClick(R.id.scan)
    public void reset() {
        if (barcodeScanned) {
            try {
                barcodeScanned = false;
                scanText.setText("Scanning...");
                mCamera.setPreviewCallback(previewCb);
                //setAnimation();
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(mAnimation.isPaused())
                        mAnimation.resume();

                }else*/
                //mAnimation.start();
                mCamera.startPreview();
                previewing = true;
                mCamera.autoFocus(autoFocusCB);
            } catch (NullPointerException n) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        releaseCamera();
        super.onBackPressed();

    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    scanText.setText(sym.getData());
                    barcodeScanned = true;
                    scanText.setText(sym.getData());
                    //animatedImage.clearAnimation();
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mAnimation.pause();
                    }else
                        mAnimation.cancel();
                    */
                    if (CB.equals("Yes"))
                        returnresultToIntent(sym.getData());
                    else {
                        finish();
                        DSA.RT = IConstants.BC;
                        Intent intent = new Intent(Scanner.this, PopUpBrowser.class);
                        /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(IConstants.WM, true);
                        intent.putExtra(IConstants.TY, IConstants.BC);
                        //PopUpBrowser.type = IConstants.BC;
                        PopUpBrowser.Barcode = sym.getData();
                        PopUpBrowser.multiop = true;
                        startActivity(intent);
                    }
				/*	if(sym.getData().matches("^[0-9]*$") && sym.getData().length() > 2 ) {
						showAlert(sym.getData(), "Continue with saving", "Stuff It");
					}else{
						scanText.setText(sym.getData()+" Coming Soon for QR code.");
					}*/
                    //showAlert(sym.getData(), "Continue with saving", "Save");
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /*
     * this method will used to put the response data back to the calling class
     */
    private void returnresultToIntent(String data) {
        if (IConstants.ILE)
            Log.e(IConstants._LOGAPPNAME, data);
        returnresp.putExtra(IConstants._RES, data);
        this.setResult(Activity.RESULT_OK, returnresp);
        releaseCamera();
        finish();
    }

    /* used to show alert dialog with the message passed to it */
    private void showAlert(final String message, String title, String Btntxt) {
        final Dialog d = new Dialog(Scanner.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.simple_alert);
        TextView tvmsg, tvtitle;
        Button ok;
        tvmsg = (TextView) d.findViewById(R.id.tvmsg);
        tvtitle = (TextView) d.findViewById(R.id.tvtitle);
        tvmsg.setText(message);
        tvtitle.setText(title);
        ok = (Button) d.findViewById(R.id.btok);
        ok.setText(Btntxt);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CB.equals("Yes"))
                    returnresultToIntent(message);
                else {
                    finish();
                    Intent intent = new Intent(Scanner.this, PopUpBrowser.class);
			/*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(IConstants.WM, true);
                    intent.putExtra(IConstants.TY, IConstants.BC);
                    //PopUpBrowser.type = IConstants.BC;
                    PopUpBrowser.Barcode = message;
                    PopUpBrowser.multiop = true;
                    startActivity(intent);
                }

                d.dismiss();
            }
        });
        d.show();


    }

}
