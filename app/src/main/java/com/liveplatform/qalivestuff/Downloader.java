package com.liveplatform.qalivestuff;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;
import pl.droidsonroids.gif.GifImageView;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by bhavya.joshi on 4/4/2018.
 */

public class Downloader implements DownloadListener {
    private Activity ct;
    private WebView wv;
    private CommonUI ci;
    private RelativeLayout downloadpopup;
    private Button opendownload;
    public String d_url="",d_contentDisposition="",d_mimetype="";
    private Boolean notify;
    private Long enque;

    Downloader(Activity context, WebView webview, CommonUI ci,Boolean showNotifications){
        ct=context;
        wv = webview;
        this.ci = ci;
        notify = showNotifications;

        if(notify && Browser.showDownloadpopup){
            downloadpopup= ct.findViewById(R.id.downloadpopup);
            opendownload = ct.findViewById(R.id.opendownloads);
            opendownload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                    //pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        //ct.startActivity(pageView);
                        openDownloadedFile();
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Cannot find Downloads app", e);
                    }
                }
            });
        }
        ct.registerReceiver(onComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void onDownloadStart(String url, String userAgent,String contentDisposition,String mimetype,long contentLength){
        Log.i("Download URL: ",url);
        d_contentDisposition = contentDisposition;
        d_url = url;
        d_mimetype = mimetype;
        try {
            askForStoragePermission();
        }catch(Exception e){
            Log.i("OnDownloadStart",e.getMessage());
        }
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(notify && Browser.showDownloadpopup) downloadpopup.setVisibility(View.VISIBLE);
            if(!notify && !Browser.showDownloadpopup) openDownloadedFile(intent);
            new Handler().postDelayed(
                    new Runnable(){
                        public void run(){
                            if(notify && Browser.showDownloadpopup) downloadpopup.setVisibility(View.GONE);
                        }
                    },5000);
        }
    };

    public void openDownloadedFile(){
        if(enque == null) return;
        DownloadManager.Query query = new DownloadManager.Query();
        try{
            query.setFilterById(enque);
            DownloadManager downloadManager = (DownloadManager)ct.getSystemService(DOWNLOAD_SERVICE);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Log.i("downloadedfile: ",uriString);
                    if(uriString !=null) {
                        String mime = ci.getMimeType(uriString);
                        if (uriString.substring(0, 7).matches("file://")) {
                            uriString =  uriString.substring(7);
                        }
                        File file = new File(uriString);
                        Uri uriFile = FileProvider.getUriForFile(ct, BuildConfig.APPLICATION_ID + ".download.fileprovider", file);
                        Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                        downloadIntent.setDataAndType(uriFile, mime);
                        downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            ct.startActivity(downloadIntent);
                        }catch(Exception e){
                            Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                            pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ct.startActivity(pageView);

                        }
                    }
                }
            }
        }catch(Exception e){
            Log.i("DownloadeComplete:",e.getMessage());
        }
    };

    public void openDownloadedFile(Intent intent){
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            if(enque == null) return;
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            try{
                GifImageView gifLoader = (GifImageView) ct.findViewById(R.id.gifloader);
                if(gifLoader!=null) gifLoader.setVisibility(View.GONE);
                query.setFilterById(enque);
                DownloadManager downloadManager = (DownloadManager)ct.getSystemService(DOWNLOAD_SERVICE);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Log.i("downloadedfile: ",uriString);
                        if(uriString !=null) {
                            String mime = ci.getMimeType(uriString);

                            if (uriString.substring(0, 7).matches("file://")) {
                                uriString =  uriString.substring(7);
                            }
                            File file = new File(uriString);
                            Uri uriFile = FileProvider.getUriForFile(ct, BuildConfig.APPLICATION_ID + ".download.fileprovider", file);
                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                            downloadIntent.setDataAndType(uriFile, mime);
                            downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            ct.startActivity(downloadIntent);
                            ct.finish();
                            Browser.showDownloadpopup = true;
                        }
                    }
                }
            }catch(Exception e){
                Log.i("DownloadeComplete:",e.getMessage());
            }
        }
    };

    public void startDownload(){
        Log.i("startdownload","up");
        if(d_url == null) return;
        Log.i("startdownload","inside");
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(d_url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(d_url,d_contentDisposition,d_mimetype));
        DownloadManager dm=(DownloadManager) ct.getSystemService(Context.DOWNLOAD_SERVICE);
        try{
            enque = dm.enqueue(request);
        }catch(NullPointerException e){
            Log.i("error in download",e.getMessage());
        }
        d_contentDisposition = "";
        d_url = "";
        d_mimetype = "";
        if(notify && Browser.showDownloadpopup) Toast.makeText(ct,"Downloading File", Toast.LENGTH_SHORT).show();

    };


    public void askForStoragePermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(ct, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(ct,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    ci.showAlertDialogWithYesNoCallBack(ct,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(ct,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            }
                    );
                } else{
                    ActivityCompat.requestPermissions(ct,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                }
            } else {
                Log.i("uploadVideo: ","StorageTop");
                //startDownload();
                askForWritePermission();
            }
        } else{
            if(ActivityCompat.checkSelfPermission(ct, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Log.i("No storage pernmission","");
                return;
            }
            Log.i("uploadVideo: ","StorageBelow");
            askForWritePermission();
            //startDownload();
        }
    }

    public void askForWritePermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(ct, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(ct,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ci.showAlertDialogWithYesNoCallBack(ct,"Storage Permission","JD WOW needs permission to write the Storage.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(ct,
                                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            }
                    );
                } else{
                    ActivityCompat.requestPermissions(ct,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            } else {
                Log.i("Download External else ","StorageTop");
                startDownload();
            }
        } else{
            if(ActivityCompat.checkSelfPermission(ct, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Log.i("Download External no","");
                return;
            }
            Log.i("Download External else","StorageBelow");
            startDownload();
        }
    }
}
