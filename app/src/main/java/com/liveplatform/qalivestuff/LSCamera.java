package com.liveplatform.qalivestuff;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import BridgeJavaClass.WebViewJavascriptBridge;
import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;

import static Data.IConstants._CAM;

public class LSCamera extends AppCompatActivity {
    //Uri picUri;
    String uriInString;
    CommonUI ci;
    ImageView iv;
    Button save;
    boolean isFromBrowser = false;
    Button dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_lscamera);
        //BJ Commented:09/13/2017
        ci = new CommonUI(this);
     /*   iv = (ImageView) findViewById(R.id.srcImg);
        save = (Button) findViewById(R.id.save);
        dis = (Button) findViewById(R.id.dis);
        save.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DSA.RT = IConstants.IMG;
                DSA.RD = uriInString;
                DSA.AddOn = true;
                Intent i = null;
                ci.logit("isFromBrowser",isFromBrowser+"");

                if (isFromBrowser){
                    i = new Intent(LSCamera.this, Browser.class);
                    i.putExtra(IConstants._FROMOUTSIDE, true);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    i = new Intent(LSCamera.this, PopUpBrowser.class);
                    i.putExtra(IConstants._FROMOUTSIDE, true);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });
        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save.setEnabled(false);
                finish();
            }
        });*/
        String v = getIntent().getStringExtra("FromBrowser");
        if (v != null) {
            if (v.equals("Yes"))
                isFromBrowser = true;
            else
                isFromBrowser = false;
        }

        //openGalleryAudio();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LSCamera.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LSCamera.this,
                        android.Manifest.permission.CAMERA)) {

                    ci.showAlertDialogWithYesNoCallBack(LSCamera.this,"Camera Permission","LiveStuff needs permission to utilize the Camera.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(LSCamera.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            });

                } else {
                    ActivityCompat.requestPermissions(LSCamera.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else {
                showAndroidCamera();
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

            showAndroidCamera();
        }

        ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IConstants.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAndroidCamera();
                } else {
                    //BJ 09/15/2017: Added below check
                    if (Build.VERSION.SDK_INT < 23){
                        ci.showNotification("Camera", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Camera Access", "OK");
                    } else {
                        ci.showAlertDialogWithYesNoCallBack(LSCamera.this,"Camera Permission","LiveStuff needs permission to utilize the Camera.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(LSCamera.this,
                                                new String[]{Manifest.permission.CAMERA},
                                                IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                });
                    }
                }
                return;
            case IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Bitmap photo = BitmapFactory.decodeFile(uriInString.replace("file:///", ""));
                    //displayactivityResult(photo);
                    //BJ 07/25/2017: Commented above and updated below to now show save deny button .
                    uploadImage();

                } else {
                    if(Build.VERSION.SDK_INT < 23) {
                        ci.showNotification("Storage", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Read Storage", "OK");
                    } else {
                        ci.showAlertDialogWithYesNoCallBack(LSCamera.this,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(LSCamera.this,
                                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                }
                        );
                    }
                }
                return;
        }
    }

    public void showAndroidCamera() {
      /*  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IConstants.CAMREQ);*/
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IConstants._FN);
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }

        File file = new File(
                imageStorageDir + File.separator + "IMG" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        uriInString = Uri.fromFile(file).toString();
        startActivityForResult(cameraIntent, IConstants.CAMREQ);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case IConstants.CAMREQ:
                    ci.logit("CAM", resultCode + "");
                    ci.logit("CAM data", data + "");
                    //uriInString = picUri.toString().replace("file:///", "");
                    if (resultCode == Activity.RESULT_OK) {
                       /*try {
                           uriInString = data.getData().toString();
                       }catch(NullPointerException ne){
                           ci.logit("Null in Cam",ne.getMessage());
                       }

                        *///Bitmap photo = (Bitmap) data.getExtras().get("data");
                        ci.logit("Uri Img", uriInString.replace("file:///", ""));
                        if (Build.VERSION.SDK_INT >= 23) {
                            // Here, thisActivity is the current activity
                            if (ContextCompat.checkSelfPermission(LSCamera.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                if (ActivityCompat.shouldShowRequestPermissionRationale(LSCamera.this,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                                    ci.showAlertDialogWithYesNoCallBack(LSCamera.this,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                                            false,false,"Grant","",new DialogCallBackAlert() {
                                                @Override
                                                public void dialogCallBackPositive(Dialog alertDialog) {
                                                    alertDialog.dismiss();
                                                    ActivityCompat.requestPermissions(LSCamera.this,
                                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                            IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                                }
                                                @Override
                                                public void dialogCallBackNagative(Dialog alertDialog) {}
                                            }
                                    );

                                } else {
                                    ActivityCompat.requestPermissions(LSCamera.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);

                                }
                            } else {
                                uploadImage();
                              /*  ActivityCompat.requestPermissions(LSCamera.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);*/
                            }
                        } else {
                            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ci.showNotification("Storage", "No permission to access storage");
                                ci.showAlert("Please Grant permission to access Storage in settings", "Storage Access", "OK");
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }

                            //Bitmap photo = BitmapFactory.decodeFile(uriInString.replace("file:///", ""));
                            //displayactivityResult(photo);
                            //BJ 07/25/2017: Commented above and updated below to now show save deny button .
                            ci.logit("LScamera","Upload called");
                            uploadImage();

                        }


                       /* Bitmap photo = BitmapFactory.decodeFile(uriInString.replace("file:///", ""));
                        displayactivityResult(photo);*/
                    } else {
                        ci.logit(IConstants._LOGERROR, "No response in return");
                        ci.showAlert("Image Capture Canceled", "Canceled", "OK");
                        uriInString = null;
                    }
                    break;
            }


        } catch (Exception e) {
            ci.logit("Error in Img", e.getMessage());
        }
    }
    //BJ 07/25/2017: Added below to now show save deny button.
    private void uploadImage() {
        DSA.RT = IConstants.IMG;
        DSA.RD = uriInString;
        DSA.AddOn = true;
        Intent i = null;
        ci.logit("isFromBrowser",isFromBrowser+"");
        //TODO:above
        if (isFromBrowser){
            i = new Intent(LSCamera.this, Browser.class);
            i.putExtra(IConstants._FROMOUTSIDE, true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            i = new Intent(LSCamera.this, PopUpBrowser.class);
            i.putExtra(IConstants._FROMOUTSIDE, true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

    }

    private void displayactivityResult(Bitmap bm) {
        iv.setImageBitmap(bm);
        save.setEnabled(true);

    }
  /*  public void displayactivityResult(final MyActivity ma, final Bitmap bm, String title, final String ChoosenTye,
                                      final String uriInString) {
        logit("Gallery 3", title + " Bit" + bm.toString() + ChoosenTye);
        final Dialog nd = new Dialog(ma);
        nd.setTitle(title);
        nd.setContentView(R.layout.imageshow);
        ImageView iv = (ImageView) nd.findViewById(R.id.srcImg);
        iv.setImageBitmap(bm);
        Button save = (Button) nd.findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    protected void onPreExecute() {
                        MyActivity.showLoading();
                    };

                    @Override
                    protected Void doInBackground(Void... voids) {
                        ma.tempcallback.callback(menuResData(ChoosenTye, encodeTobase64(bm)));

                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        MyActivity.stopshowLoading();
                    };
                }.execute();

                // tempcallback.callback("Type: " + ChoosenTye + ", " + "Data: "
                // + uriInString);
                nd.dismiss();
            }

        });
        Button dis = (Button) nd.findViewById(R.id.dis);
        dis.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nd.dismiss();
                ma.tempcallback.callback(errorMSG(true, "User Canceled : " + ChoosenTye));
                MyActivity.stopshowLoading();
            }
        });
        nd.show();
    }*/

  /*  ///
    public void openGalleryAudio(){

        Intent intent = new Intent();
        intent.setType("audio*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), 123);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == 123)
            {

                Uri selectedImageUri = data.getData();
                Log.e("SELECT URi",selectedImageUri.toString());
                uriInString = getPath(selectedImageUri);
               Log.e("SELECT_AUDIO Path : ", uriInString);
               // doFileUpload();
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    ///*/
}
