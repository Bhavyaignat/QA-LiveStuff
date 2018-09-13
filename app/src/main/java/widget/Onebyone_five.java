package widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.liveplatform.qalivestuff.DSA;
import com.liveplatform.qalivestuff.LSCamera;
import com.liveplatform.qalivestuff.Location;
import com.liveplatform.qalivestuff.PopUpBrowser;
import com.liveplatform.qalivestuff.R;
import com.liveplatform.qalivestuff.Scanner;

import Data.IConstants;
import SupportClasses.CommonUI;

/**
 * Created by devanshu.kanik on 7/7/2016.
 */
public class Onebyone_five extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent qi = new Intent(context, Onebyone_five.class);
            qi.setAction(IConstants.AQK);
            Intent loi = new Intent(context, Onebyone_five.class);
            loi.setAction(IConstants.ALOC);
            Intent sti = new Intent(context, Onebyone_five.class);
            sti.setAction(IConstants.WM);
            Intent qri = new Intent(context, Onebyone_five.class);
            qri.setAction(IConstants.AQR);
            Intent cami = new Intent(context, Onebyone_five.class);
            cami.setAction(IConstants.ACAM);

            PendingIntent q_pi = PendingIntent.getBroadcast(context, 0, qi, 0);
            PendingIntent l_pi = PendingIntent.getBroadcast(context, 0, loi, 0);
            PendingIntent sti_pi = PendingIntent.getBroadcast(context, 0, sti, 0);
            PendingIntent qr_pi = PendingIntent.getBroadcast(context, 0, qri, 0);
            PendingIntent cam_pi = PendingIntent.getBroadcast(context, 0, cami, 0);
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);
            view.setOnClickPendingIntent(R.id.qa, q_pi);
            view.setOnClickPendingIntent(R.id.loc, l_pi);
            view.setOnClickPendingIntent(R.id.sti, sti_pi);
            view.setOnClickPendingIntent(R.id.qr, qr_pi);
            view.setOnClickPendingIntent(R.id.cam, cam_pi);
            // CommonUI.scheduleTestAlarmReceiver(context);
            //context.startService(new Intent(context, LSService.class));
            appWidgetManager.updateAppWidget(appWidgetId, view);

        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent in) {
        if(IConstants.ILE)
            Log.e("Widget Size",in.getAction()+"");
        Intent intent = new Intent(context, PopUpBrowser.class);
			/*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.WM, true);
        //Log.e("onReceive", "yes");
        switch(in.getAction())
        {
            case IConstants.WM:

                AudioManager am;
                am=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if(am.isMusicActive()){
                    if(IConstants.ILE) {
                        Log.e("1x1_5", "active");
                        Log.e("Audio Path 2", CommonUI.audiopath + " ye hai");
                    }
                    // Toast.makeText(context,"Stuff Music Coming Soon...",Toast.LENGTH_SHORT).show();
                    if(CommonUI.audiopath!=null){
                        // if(IConstants.ILE)
                        //Log.e("Real path ",getRealPathFromURI(Uri.parse(CommonUI.audiopath)));
                        // getRealPathFromURI(Uri.parse(CommonUI.audiopath));

                        DSA.RT = IConstants.MUSIC;
                        DSA.RD = CommonUI.audiopath;
                        DSA.AddOn=true;
                        Intent i=new Intent(context,PopUpBrowser.class);
                        i.putExtra(IConstants._FROMOUTSIDE, true);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }else{
                        if(IConstants.ILE)
                            Log.e("1x1_5","Battery Saver or Auto run");

                        intent.putExtra(IConstants.TY,IConstants.AM);
                        PopUpBrowser.type=IConstants.AM;
                        PopUpBrowser.openMatrix = true;
                        context.startActivity(intent);
                    }

                }
                else{
                    if(IConstants.ILE)
                        Log.e("1x1_5","de active");

                    intent.putExtra(IConstants.TY,IConstants.AM);
                    PopUpBrowser.type=IConstants.AM;
                    PopUpBrowser.openMatrix = true;
                    context.startActivity(intent);
                }
                break;
            case IConstants.AQK:
                //context.startService(new Intent(context, ChatHeadService.class));
                intent.putExtra(IConstants.TY,IConstants.AQK);
                PopUpBrowser.type=IConstants.AQK;
                PopUpBrowser.multiop= true;
                context.startActivity(intent);
                break;
            case IConstants.AQR:
                Intent i = new Intent(context, Scanner.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
            case IConstants.ACAM:
                if(IConstants.ILE)
                    Log.e("Action CAM",in.getAction()+"");
                Intent i2 = new Intent(context, LSCamera.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i2);
                break;
            case IConstants.ALOC:
                Intent i3 = new Intent(context, Location.class);
                i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i3);
                break;

        }
        super.onReceive(context, in);
    }

 /*   // Setup a recurring alarm every half hour
    public void scheduleAlarm(Context context) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, AppReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, 123,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1000, pIntent);
    }*/

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // context.startService(new Intent(context, LSService.class));
       /* Intent intent = new Intent(context, LSService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2000, pintent);*/
        //updateViews=   new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);
        //scheduleAlarm(context);
       /* AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName component =new ComponentName(context,AppReceiver.class);
        am.registerMediaButtonEventReceiver(component);*/
        if(IConstants.ILE)
            Log.e("Widget Size 2",newOptions.size()+"");

/*
        Log.e("Density",context.getResources().getDisplayMetrics().density+"DPI");
        switch(context.getResources().getDisplayMetrics().densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                resize(context,appWidgetManager,newOptions,appWidgetId);
                Log.e("DPI","LDPI");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                resize(context,appWidgetManager,newOptions,appWidgetId);
                Log.e("DPI","MDPI");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                resize(context,appWidgetManager,newOptions,appWidgetId);
                Log.e("DPI","HDPI");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                resize(context,appWidgetManager,newOptions,appWidgetId);
                Log.e("DPI","XDPI");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
               // resizeXX(context,appWidgetManager,newOptions,appWidgetId);
                Log.e("DPI","XXDPI");
                break;

        }

*/

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
    /* public void resize(Context context, AppWidgetManager appWidgetManager,Bundle newOptions,int appWidgetId){
         RemoteViews updateViews ;
         int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
         int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
         Log.e("Widget Size h",minHeight+"");
         Log.e("Widget Size w",minWidth+"");
         if(minWidth>69&&minHeight>69){
              updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_withfive_ic_re);
           *//*  updateViews = new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);
            updateViews.setInt(R.id.bt_qa, "setWidth", 25);
            updateViews.setInt(R.id.bt_qa, "setHeight", 25);*//*
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        }else{
            updateViews = new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);

            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        }
    }
    public void resizeXX(Context context, AppWidgetManager appWidgetManager,Bundle newOptions,int appWidgetId){
        RemoteViews updateViews ;
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        Log.e("Widget Size h",minHeight+"");
        Log.e("Widget Size w",minWidth+"");
        if(minWidth>75&&minHeight>85){
            //updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_withfive_ic_re);
            updateViews = new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);
            updateViews.setInt(R.id.bt_qa, "setWidth", LinearLayout.LayoutParams.MATCH_PARENT);
            updateViews.setInt(R.id.bt_qa, "setHeight", LinearLayout.LayoutParams.MATCH_PARENT);

            updateViews.setInt(R.id.bt_loc, "setWidth", LinearLayout.LayoutParams.MATCH_PARENT);
            updateViews.setInt(R.id.bt_loc, "setHeight", LinearLayout.LayoutParams.MATCH_PARENT);

            updateViews.setInt(R.id.bt_sti, "setWidth", LinearLayout.LayoutParams.MATCH_PARENT);
            updateViews.setInt(R.id.bt_sti, "setHeight", LinearLayout.LayoutParams.MATCH_PARENT);

            updateViews.setInt(R.id.bt_qr, "setWidth", LinearLayout.LayoutParams.MATCH_PARENT);
            updateViews.setInt(R.id.bt_qr, "setHeight", LinearLayout.LayoutParams.MATCH_PARENT);

            updateViews.setInt(R.id.bt_ca, "setWidth", LinearLayout.LayoutParams.MATCH_PARENT);
            updateViews.setInt(R.id.bt_ca, "setHeight", LinearLayout.LayoutParams.MATCH_PARENT);

            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        }else{
            updateViews = new RemoteViews(context.getPackageName(), R.layout.w_1x1_5);

            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        }
    }*/
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        if(IConstants.ILE)
            Log.e("Widget Size 2","Restored");
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

}