package widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.liveplatform.qalivestuff.PopUpBrowser;
import com.liveplatform.qalivestuff.R;

import Data.IConstants;

public class Twobytwo extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {



            Intent mi = new Intent(context, Twobytwo.class);
            mi.setAction(IConstants.AML);
            Intent qi = new Intent(context, Twobytwo.class);
            qi.setAction(IConstants.AQK);
            Intent fi = new Intent(context, Twobytwo.class);
            fi.setAction(IConstants.AF);
            Intent nci = new Intent(context, Twobytwo.class);
            nci.setAction(IConstants.ANC);

            PendingIntent mpi = PendingIntent.getBroadcast(context, 0, mi, 0);
            PendingIntent qpi = PendingIntent.getBroadcast(context, 0, qi, 0);
            PendingIntent fpi = PendingIntent.getBroadcast(context, 0, fi, 0);
            PendingIntent ncpi = PendingIntent.getBroadcast(context, 0, nci, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.w_2x2);
            views.setOnClickPendingIntent(R.id.bt_ma, mpi);
            views.setOnClickPendingIntent(R.id.bt_qa, qpi);
            views.setOnClickPendingIntent(R.id.bt_f, fpi);
            views.setOnClickPendingIntent(R.id.bt_nc, ncpi);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent in) {
        Log.e("Action in 2","See "+in.getAction());
        Intent intent = new Intent(context, PopUpBrowser.class);
			/*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.WM, true);
        //Log.e("onReceive", "yes");
        try {
            switch (in.getAction()) {
                case IConstants.AML:
                    intent.putExtra(IConstants.TY, IConstants.AML);
                    PopUpBrowser.type = IConstants.AML;
                    // if (WebViewJavascriptBridge.isPageLoaded)
                    PopUpBrowser.multiop = true;
                    context.startActivity(intent);
                    break;
                case IConstants.AQK:
                    intent.putExtra(IConstants.TY, IConstants.AQK);
                    PopUpBrowser.type = IConstants.AQK;
                    PopUpBrowser.multiop = true;
                    context.startActivity(intent);
                    break;
                case IConstants.AF:
                    intent.putExtra(IConstants.TY, IConstants.AF);
                    PopUpBrowser.type = IConstants.AF;
                    PopUpBrowser.multiop = true;
                    context.startActivity(intent);
                    break;
                case IConstants.ANC:
                    intent.putExtra(IConstants.TY, IConstants.ANC);
                    PopUpBrowser.type = IConstants.ANC;
                    PopUpBrowser.multiop = true;
                    context.startActivity(intent);
                    break;

            }
        }catch (NullPointerException ne){
            Log.e("Null",ne.getMessage());
            intent.putExtra(IConstants.TY,IConstants.AM);
            PopUpBrowser.type=IConstants.AM;
            PopUpBrowser.openMatrix = true;
            context.startActivity(intent);
        }

        super.onReceive(context, in);
    }


}
