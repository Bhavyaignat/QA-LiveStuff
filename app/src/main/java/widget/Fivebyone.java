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
import android.widget.Toast;

import com.liveplatform.qalivestuff.DSA;
import com.liveplatform.qalivestuff.PopUpBrowser;
import com.liveplatform.qalivestuff.LSCamera;
import com.liveplatform.qalivestuff.Location;
import com.liveplatform.qalivestuff.R;
import com.liveplatform.qalivestuff.Scanner;

import java.io.File;

import Data.IConstants;
import SupportClasses.CommonUI;

/**
 * Created by devanshu.kanik on 7/7/2016.
 */
public class Fivebyone extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        for (int appWidgetId : appWidgetIds) {

            Intent qi = new Intent(context, Fivebyone.class);
            qi.setAction(IConstants.AQK);
            Intent loi = new Intent(context, Fivebyone.class);
            loi.setAction(IConstants.ALOC);
            Intent sti = new Intent(context, Fivebyone.class);
            sti.setAction(IConstants.WM);
            Intent qri = new Intent(context, Fivebyone.class);
            qri.setAction(IConstants.AQR);
            Intent cami = new Intent(context, Fivebyone.class);
            cami.setAction(IConstants.ACAM);

            PendingIntent q_pi = PendingIntent.getBroadcast(context, 0, qi, 0);
            PendingIntent l_pi = PendingIntent.getBroadcast(context, 0, loi, 0);
            PendingIntent sti_pi = PendingIntent.getBroadcast(context, 0, sti, 0);
            PendingIntent qr_pi = PendingIntent.getBroadcast(context, 0, qri, 0);
            PendingIntent cam_pi = PendingIntent.getBroadcast(context, 0, cami, 0);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.w_5x1);
            view.setOnClickPendingIntent(R.id.bt_qa, q_pi);
            view.setOnClickPendingIntent(R.id.bt_loc, l_pi);
            view.setOnClickPendingIntent(R.id.bt_sti, sti_pi);
            view.setOnClickPendingIntent(R.id.bt_qr, qr_pi);
            view.setOnClickPendingIntent(R.id.bt_ca, cam_pi);
            // context.startService(new Intent(context, LSService.class));
            appWidgetManager.updateAppWidget(appWidgetId, view);

        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent in) {
        if(IConstants.ILE)
            Log.e("Action ",in.getAction());
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
                    if(IConstants.ILE)
                        Log.e("5x1","active");
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
                            Log.e("5x1","Battery Saver or Auto run");
                        intent.putExtra(IConstants.TY,IConstants.AM);
                        PopUpBrowser.type=IConstants.AM;
                        PopUpBrowser.openMatrix = true;
                        context.startActivity(intent);
                    }
                }
                else{
                    if(IConstants.ILE)
                        Log.e("5x1","de active");
                    intent.putExtra(IConstants.TY,IConstants.AM);
                    PopUpBrowser.type=IConstants.AM;
                    PopUpBrowser.openMatrix = true;
                    context.startActivity(intent);
                }

                break;
            case IConstants.AQK:
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
                Intent i2 = new Intent(context, LSCamera.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // context.startService(new Intent(context, LSService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}
