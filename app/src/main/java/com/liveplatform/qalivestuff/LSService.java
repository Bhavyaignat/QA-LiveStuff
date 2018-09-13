package com.liveplatform.qalivestuff;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by devanshu.kanik on 7/25/2016.
 */
public class LSService extends Service
{
    AppReceiver receiver;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            Log.e("Service","Sleep");
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        //CommonUI.scheduleTestAlarmReceiver(getApplicationContext());
        //countdown();

        /* receiver =new AppReceiver();
        IntentFilter filter=new IntentFilter("com.android.music.playstatechanged");
        filter.addAction("com.android.music.metachanged");
        filter.addAction("com.miui.player.metachanged");

        //filter.addAction("com.android.music.playbackcomplete");
        //filter.addAction("com.android.music.queuechanged");
        registerReceiver(receiver,filter);*/

        //  showNotification();


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
       /* Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);*/

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    public void countdown(){
        new CountDownTimer(100000,4000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                sendBroadcast(new Intent("fromservice"));

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        final Messenger myMessenger = new Messenger(mServiceHandler);
        return myMessenger.getBinder();
    }
    private void showNotification() {
        Intent notificationIntent = new Intent(this, Browser.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, LSService.class);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.alert);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("TutorialsFace Music Player")
                .setTicker("TutorialsFace Music Player")
                .setContentText("My song")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous",
                        ppreviousIntent)
                .build();
        startForeground(12345,
                notification);

    }
    @Override
    public void onDestroy() {
        // unregisterReceiver(receiver);
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Intent i=new Intent();
        i.setPackage(getPackageName());
        i.setClassName(getPackageName(),"LSService");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startService(i);
    }
    public void onTaskRemoved(Intent intent){
        super.onTaskRemoved(intent);
        Intent i=new Intent(this,this.getClass());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(i);
    }



    /* static Timer timer;
    static Handler h;
    static Boolean isAudioPLaying;
    static AudioManager am;
    static Context ct;
    public static int SERVICE_PERIOD = 10000;
    @Override
    public void onCreate() {
        ct=getApplicationContext();
        am=(AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if(IConstants.ILE)
            Log.e("Started","Active"+ am.isMusicActive());

      *//*  AppReceiver receiver =new AppReceiver();
        *//**//*IntentFilter filter=new IntentFilter(
                "com.android.music.musicservicecommand");*//**//*
        //  filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        //filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        IntentFilter filter=new IntentFilter("com.android.music.playstatechanged");
        //filter.addAction("com.android.music.playbackcomplete");
        //filter.addAction("com.android.music.queuechanged");
        registerReceiver(receiver,filter);*//*

     *//*    BroadcastReceiver receiver2=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
               Log.e("Ringer Change",intent.getAction());
            }
        };
        IntentFilter filter2=new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);



        registerReceiver(receiver2,filter2);*//*
        CommonUI.scheduleTestAlarmReceiver(ct);
      *//*  timer = new Timer();
        timer.schedule(new MonitoringTimerTask(), SERVICE_PERIOD);*//*
        super.onCreate();
    }
    @Override
    public void onTaskRemoved( Intent rootIntent ) {
        Intent intent = new Intent("com.liveplatform.livestuff.LSService");
        intent.setPackage(this.getPackageName());
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(IConstants.ILE)
            Log.e("onStart","yes");
        // Fires when a service is started up, do work here!
        // ...
        // Return "sticky" for services that are explicitly
        // started and stopped as needed by the app.
        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        //ct.startService(new Intent(LSService.this,LSService.class));
        //if(IConstants.ILE)
        Log.e("Service", "FirstService destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void check(final Context ct) {
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
    }


    private static class MonitoringTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Log.e("Timer",""+ am.isMusicActive());
           *//*     if(am.isMusicActive()){
                    isAudioPLaying=true;

                }else{
                    isAudioPLaying=false;
                }
            setWidget();*//*
        }
    }

    private static void setWidget(Context ct) {

        if(timer!=null){
            timer.cancel();
        }
        if(isAudioPLaying){
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
        timer = new Timer();
        timer.schedule(new MonitoringTimerTask(), SERVICE_PERIOD);
    }

    //////////////////
*/
}