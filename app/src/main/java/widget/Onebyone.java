package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.liveplatform.qalivestuff.PopUpBrowser;
import com.liveplatform.qalivestuff.R;

import Data.IConstants;

public class Onebyone extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//final int N = appWidgetIds.length;
		for (int appWidgetId : appWidgetIds) {
			Intent intent = new Intent(context, Onebyone.class);
			/*
			 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 */
			// intent.putExtra(IConstants.WIDGETMATRIX, true);
			intent.setAction(IConstants.WM);
			// context.startActivity(i);

			// Create an Intent to launch ExampleActivity
			// Intent intent = new Intent(context, ExampleActivity.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			/*
			 * if (WebViewJavascriptBridge.isPageLoaded)
			 * WebViewJavascriptBridge.openMatrix = true;
			 */
			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.w_1x1);
			views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

			// Tell the AppWidgetManager to perform an update on the current app
			// w_1x1
			// pushWidgetUpdate(context,views);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

		/*
		 * RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
		 * R.layout.w_1x1);
		 * remoteViews.setOnClickPendingIntent(R.id.widget_button,
		 * buildButtonPendingIntent(context)); pushWidgetUpdate(context,
		 * remoteViews);
		 */
		super.onUpdate(context,appWidgetManager,appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent in) {
		//Log.e("onReceive", "yes");
		if (in.getAction().equals(IConstants.WM)) {
			//Log.e("onReceive launching", "yes");
			Intent intent = new Intent(context, PopUpBrowser.class);
			/*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(IConstants.WM, true);
			intent.putExtra(IConstants.TY,IConstants.AM);
			PopUpBrowser.type=IConstants.AM;
			PopUpBrowser.openMatrix = true;
			context.startActivity(intent);

		}
		super.onReceive(context, in);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

		//int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
		/*Log.e("Widget 1x1",minWidth+"");
		if(minWidth == 64 && maxWidth == 90 && minHeight == 58 && maxHeight == 84){
			updateViews = new RemoteViews(ctxt.getPackageName(), R.layout.widget1x1);
		}*/
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}



}
