package com.liveplatform.qalivestuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import Data.IConstants;

/**
 * Created by devanshu.kanik on 7/28/2016.
 */
public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        Intent startServiceIntent = new Intent(context, TriggerSer.class);
        startWakefulService(context, startServiceIntent);
        // setResultCode(Activity.RESULT_OK);
        if(IConstants.ILE)
            Log.e("Boot",intent.getAction());
    }
}