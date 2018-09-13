package com.liveplatform.qalivestuff;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by devanshu.kanik on 7/25/2016.
 */
public class MediaPlayerServiceConnection extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}