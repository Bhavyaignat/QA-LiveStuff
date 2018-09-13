package com.liveplatform.qalivestuff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;
import pl.droidsonroids.gif.GifImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ortiz.touchview.TouchImageView;


public class GlideModule extends AppCompatActivity {

    CommonUI ci;
    String url;
    WebView gwv;
    Downloader glideDownloader;
    GifImageView gifLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_module);
        try{
            Intent glideIntent = getIntent();
            /*Temporary hack,Do not publish in App Store with this*/
                /*StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());*/
            gwv = (WebView) findViewById(R.id.glidewebview);
            ci = new CommonUI(GlideModule.this);

            TouchImageView glideImageView = (TouchImageView) findViewById(R.id.glideImageView);
            gifLoader = (GifImageView)findViewById(R.id.gifloader);

            glideDownloader = new Downloader(this,gwv,ci,false);

            gwv.setDownloadListener(glideDownloader);
            Browser.showDownloadpopup = false;
            url = glideIntent.getStringExtra("URL");
            if(url!=null) Log.i("URL: ",url);
            String mime = ci.getMimeType(url);
            if(mime !=null) Log.i("MimeType: ",mime);

            gifLoader.setVisibility(View.GONE);

            if((mime!=null) && mime.contains("image")){
                Browser.showDownloadpopup = true;
                glideImageView.setVisibility(View.VISIBLE);
                gwv.setVisibility(View.GONE);
                Glide.with(this)
                        .load(url)
                        .thumbnail(Glide.with(this).load(R.drawable.gifloaderu100x100))
                        .into(glideImageView);

            } else if((mime!=null) && mime.contains("video")){
                gwv.loadUrl(url);
                if(mime.contains("mp4")){
                    gwv.setVisibility(View.VISIBLE);
                    Browser.showDownloadpopup = true;
                } else {
                    gifLoader.setVisibility(View.VISIBLE);
                }
                glideImageView.setVisibility(View.GONE);
            } else if(mime!= null){
                gifLoader.setVisibility(View.VISIBLE);
                glideDownloader.d_url=url;
                glideDownloader.askForStoragePermission();
                //glideDownloader.startDownload();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                this.startActivity(intent);
                finish();
            }
        }catch(Exception e){
            Log.i("GlideModule",e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode){
            case IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("uploadVideo: ","ReadPermission");
                    glideDownloader.askForWritePermission();
                } else {
                    if(Build.VERSION.SDK_INT >= 23){
                        ci.showAlertDialogWithYesNoCallBack(this,"Storage Permission","JD WOW needs permission to write the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(GlideModule.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                }
                        );
                    } else {
                        Log.i("Storage permission","No Permission Granted");
                    }
                }
                return;
            case IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("download: ","WritePermission");
                    glideDownloader.startDownload();
                    glideDownloader = null;
                } else {
                    if(Build.VERSION.SDK_INT >= 23){
                        ci.showAlertDialogWithYesNoCallBack(this,"Storage Permission","JD WOW needs permission to write the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(GlideModule.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                }
                        );
                    } else {
                        Log.i("Storage permission","No Permission Granted");
                    }
                }

        }
    }

    @Override
    protected void onDestroy() {
        if (gwv != null) {
            gwv.destroy();
        }
        super.onDestroy();
    }


}
