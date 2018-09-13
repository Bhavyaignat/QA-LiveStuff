package com.liveplatform.qalivestuff;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import Data.IConstants;

/**
 * Created by devanshu.kanik on 7/28/2016.
 */
public class TriggerSer extends IntentService {

    public TriggerSer() {
        super("TriggerSer");

    }

    // ...
    @Override
    protected void onHandleIntent(Intent intent) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        for (int i=0; i<5; i++) {
            Log.e("SimpleWakefulReceiver", "Running service " + (i+1)
                    + "/5 @ " + SystemClock.elapsedRealtime());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        //  getApplicationContext().startService(new Intent(getApplicationContext(), LSService.class));
        Log.e("SimpleWakefulReceiver", "Completed service @ " + SystemClock.elapsedRealtime());
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
      /*  if(IConstants.ILE)
            Log.e("IntentService",intent.getAction());*/

    }
}
