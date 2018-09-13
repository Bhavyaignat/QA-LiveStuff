package com.liveplatform.qalivestuff;

/**
 * Created by devanshu.kanik on 7/22/2016.
 */
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import Data.IConstants;
import SupportClasses.CommonUI;
import widget.Fivebyone;
import widget.Onebyone_five;

public class AppReceiver extends BroadcastReceiver   {
    AudioManager am;
    static Handler h;
    static String Actionname;
    //static Boolean Check=true;
    static String track ,album,artist,trackId;
    static Boolean isAudioPLaying;
    // CursorLoader cursorLoader;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Reciever","Recieved "+intent.getAction());
      /*  if(!isServiceRunning("LSService",context))
            context.startService(new Intent(context, LSService.class));*/
        Actionname=intent.getAction();
      /*  if(intent.getAction().contains("com.miui.player.playstatechanged"))
        {
            if(!Check)
           Check=true;
            else
            Check=false;
            Log.e("Yes same action",intent.getAction() + Check);
        }*/
        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
        {
            isAudioPLaying=false;
            setWidget(context);
        }else {
            //am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            isAudioPLaying=intent.getBooleanExtra("playing", false);
            // check(context);
            Log.e("intent URI", intent.toUri(0));
            artist = intent.getStringExtra("artist");
            album = intent.getStringExtra("album");
            //String track = intent.getStringExtra("track");
            String URL = intent.getStringExtra("base_uri");
            trackId = intent.getStringExtra("id");
            //if(intent.getStringExtra("track")!=null);
            track = intent.getStringExtra("track");
            try {
                if (!trackId.equals(null)) {
                    CommonUI.audiopath = trackId;
                    Log.e("Trackid else", trackId + " ye hai");
                }
            }catch (NullPointerException ne){
                Log.e("NPE",ne.getMessage());
                CommonUI.audiopath=track;
                /*if (URL != null) {
                    CommonUI.audiopath = URL;
                    //Toast.makeText(context,URL,Toast.LENGTH_LONG).show();
                } else {
                    URL = intent.getStringExtra("track_path");
                    Log.e("Audio Path 2", URL + " ye hai");
                    //Toast.makeText(context,URL,Toast.LENGTH_LONG).show();
                    CommonUI.audiopath = URL;
                }*/
            }
            /* *//* try {
                trackId = intent.getStringExtra("id");
            }catch(ClassCastException cce){
                Log.e("CCE AR",cce.getMessage());


            }*//*
            String URL = intent.getStringExtra("base_uri");
            Log.e("Status",intent.getBooleanExtra("playing", false)+"-value");
            Log.e("Audio Track", track+ " ye hai");


            if(!(track==null)){
                Log.e("TrackId if", track+ " ye hai");
                CommonUI.audiopath=track;

            }else if(trackId!=null||trackId!="null"){
                CommonUI.audiopath = trackId;
                Log.e("Trackid else", trackId+ " ye hai");*/
                /*if (URL != null) {
                CommonUI.audiopath = URL;
                //Toast.makeText(context,URL,Toast.LENGTH_LONG).show();
            } else {
                URL = intent.getStringExtra("track_path");
                Log.e("Audio Path 2", URL + " ye hai");
                //Toast.makeText(context,URL,Toast.LENGTH_LONG).show();
                CommonUI.audiopath = URL;
            }*/


            setWidget(context);
        }
        //Log.e("Audio Path 2",URL+" ye hai");


        /*  String[] proj = { MediaStore.Images.Media.DATA };
       CursorLoader cursorLoader= new CursorLoader(context, Uri.parse(URL+"/cte"), null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));*/
        // Log.e("result CP",path);
        // LSService.check(context);

       /* try {
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(URL));
        } catch (FileNotFoundException e) {
            Log.e("Error in File",e.getMessage());
        }*/
      /*  try {
            Log.e("LoC",getFilePath(context, Uri.parse(URL)));
            File file = new File(getFilePath(context, Uri.parse(URL)));
            Log.e("File",file.getName());
            Log.e("FileSize",file.length()+"");
        } catch (URISyntaxException e) {
            Log.e("Error in Path",e.getMessage());
        }*/
        // context.getContentUriForPath(URL);
      /*  Bundle b = intent.getExtras();
        Set<String> set = b.keySet();
        Iterator it = set.iterator();
        while(it.hasNext()==true){
            Log.e("Extras",it.next().toString());
        }*/
        //  Log.e("Music",artist+":"+album+":"+track);

     /*   Bundle b = intent.getExtras();
        Set<String> set = b.keySet();
        Iterator it = set.iterator();
        while(it.hasNext()==true){
            Toast.makeText(getApplicationContext(),""+it.next(), Toast.LENGTH_SHORT).show();
        }*/



        // Log.e("Started","Active"+ am.isMusicActive());
            /*String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w("MY_DEBUG_TAG", state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras
                        .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.w("MY_DEBUG_TAG", phoneNumber);
            }*/

    }
   /* public static boolean isServiceRunning(String serviceClassName,Context c){
        final ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                Log.e("Running","yes");
                return true;
            }
        }
        Log.e("Running","No");
        return false;
    }*/

    /* public String getRealPathFromURI(Context ct,Uri contentUri)
     {

         Cursor cursor = ct.getContentResolver().query(contentUri, null, null, null, null);

         int idx;
         if(contentUri.getPath().startsWith("/external/image") || contentUri.getPath().startsWith("/internal/image")) {
             idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
         }
         else if(contentUri.getPath().startsWith("/external/video") || contentUri.getPath().startsWith("/internal/video")) {
             idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
         }
         else if(contentUri.getPath().startsWith("/external/audio") || contentUri.getPath().startsWith("/internal/audio")) {
             idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
         }
         else{
             return contentUri.getPath();
         }
         if(cursor != null && cursor.moveToFirst()) {
             return cursor.getString(idx);
         }
         return null;


         *//*String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = ct.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);*//*
    }*/
  /* @SuppressLint("NewApi")
   public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
       String selection = null;
       String[] selectionArgs = null;
       // Uri is different in versions after KITKAT (Android 4.4), we need to
       if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
           if (isExternalStorageDocument(uri)) {
               final String docId = DocumentsContract.getDocumentId(uri);
               final String[] split = docId.split(":");
               return Environment.getExternalStorageDirectory() + "/" + split[1];
           } else if (isDownloadsDocument(uri)) {
               final String id = DocumentsContract.getDocumentId(uri);
               uri = ContentUris.withAppendedId(
                       Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
           } else if (isMediaDocument(uri)) {
               final String docId = DocumentsContract.getDocumentId(uri);
               final String[] split = docId.split(":");
               final String type = split[0];
               if ("image".equals(type)) {
                   uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
               } else if ("video".equals(type)) {
                   uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
               } else if ("audio".equals(type)) {
                   uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
               }
               selection = "_id=?";
               selectionArgs = new String[]{
                       split[1]
               };
           }
       }
       if ("content".equalsIgnoreCase(uri.getScheme())) {
           String[] projection = {
                   MediaStore.Images.Media.DATA
           };
           Cursor cursor = null;
           try {
               cursor = context.getContentResolver()
                       .query(uri, projection, selection, selectionArgs, null);
               int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
               if (cursor.moveToFirst()) {
                   return cursor.getString(column_index);
               }
           } catch (Exception e) {
           }
       } else if ("file".equalsIgnoreCase(uri.getScheme())) {
           return uri.getPath();
       }
       return null;
   }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }*/
 /*  public void check(final Context ct) {
       try {
           am = (AudioManager) ct.getSystemService(Context.AUDIO_SERVICE);
           h = new Handler();
           h.postDelayed(new Runnable() {
               @Override
               public void run() {
                   if (IConstants.ILE)
                       Log.e("Check", am.isMusicActive() + "");
                   isAudioPLaying = am.isMusicActive();
                   setWidget(ct);
               }
           }, 1000);
       }catch(NullPointerException ne){
           if(IConstants.ILE)
               Log.e("NullCheck",ne.getMessage());
       }
   }*/
    private static void setWidget(Context ct) {
        /*if(Actionname!=null&&Actionname.contains("com.miui.player.playstatechanged")&&isAudioPLaying&&Check){

            isAudioPLaying=!isAudioPLaying;
            Log.e("Stopped forcely",Check+"");
        }*/
        if(isAudioPLaying){
            StringBuilder sb=new StringBuilder();
            sb.append("track name:"+track);
            sb.append(" album:"+album);
            sb.append(" artist:"+artist);
            sb.append(" trackId:"+trackId);
            sb.append(" path"+CommonUI.audiopath);
            Log.e("Info",sb.toString());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ct);
            RemoteViews remoteViews = new RemoteViews(ct.getPackageName(), R.layout.w_1x1_5);
            ComponentName widget = new ComponentName(ct, Onebyone_five.class);
            remoteViews.setImageViewResource(R.id.sti, R.drawable.st_music);
            appWidgetManager.updateAppWidget(widget, remoteViews);

            RemoteViews remoteViews2 = new RemoteViews(ct.getPackageName(), R.layout.w_5x1);
            ComponentName widget2 = new ComponentName(ct, Fivebyone.class);
            remoteViews2.setImageViewResource(R.id.bt_sti, R.drawable.st_music);
            appWidgetManager.updateAppWidget(widget2, remoteViews2);
        } else {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ct);

            RemoteViews remoteViews = new RemoteViews(ct.getPackageName(), R.layout.w_1x1_5);
            ComponentName widget = new ComponentName(ct, Onebyone_five.class);
            remoteViews.setImageViewResource(R.id.sti, R.drawable.stuff_it_plus);
            appWidgetManager.updateAppWidget(widget, remoteViews);

            RemoteViews remoteViews2 = new RemoteViews(ct.getPackageName(), R.layout.w_5x1);
            ComponentName widget2 = new ComponentName(ct, Fivebyone.class);
            remoteViews2.setImageViewResource(R.id.bt_sti, R.drawable.stuff_it_plus);
            appWidgetManager.updateAppWidget(widget2, remoteViews2);
        }
    }

/*    public String getPath(Context ct,Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = ct.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/
}