package com.liveplatform.qalivestuff;

import android.app.Application;


import android.os.StrictMode;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;



/**
 * Created by devanshu.kanik on 8/1/2016.
 *
 */
/*@ReportsCrashes(mailTo = "bjoshi@livetechnology.com", customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text, formKey = "")*/


public class LSApp extends Application {
    @Override
    public void onTerminate() {
        Log.e("Ends","yes");
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*//https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed/38925453
        //BJ 09272017:Added below code as error was Appearing in Nougat 7 because of file exposure
        //STACK_TRACE=java.lang.RuntimeException: Unable to start activity ComponentInfo{com.liveplatform.qalivestuff/com.liveplatform.qalivestuff.LSCamera}: android.os.FileUriExposedException: file:///storage/emulated/0/Pictures/LiveStuff/IMG1506508310307.jpg exposed beyond app through ClipData.Item.getUri()
        //TODO: BJ: Remove below code and find better way to run Camera in SDK 24 , Version 7*/
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        /*ACRA.init(this);*/
    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }
}
