package com.liveplatform.qalivestuff;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import BridgeJavaClass.WebViewJavascriptBridge;
import Connection.ConnectionDetector;
import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.CustomSwipeToRefresh;
import SupportClasses.CustomWebView;
import SupportClasses.DialogCallBackAlert;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PopUpBrowser extends AppCompatActivity {
    /* all Views attached with java codes */
    @BindView(R.id.swipe_container)
    CustomSwipeToRefresh sw;
    @BindView(R.id.webView)
    CustomWebView wv;
    @BindView(R.id.progressBar)
    ProgressBar prg;
    /*  @Bind(R.id.container)
      RelativeLayout contentContainer;*/
    AQuery q;
    private Boolean scrolldisable = false;
    private Boolean isResuming = false;
    private Boolean LoggedIn = false;
    //  private String Base64Image = null;
    private String extension;
    private Boolean isUploadable = false;
    int scroll = 0;
    public static Boolean openMatrix = false;
    private static String sessionUser = "";
    public static String type = "",Music_url,typevalue;
    public static String extractTitle = "";
    public static String Barcode;
    private static String code="";
    /* object of connection class which detects internet connectivity */
    private ConnectionDetector cd;
    // private Boolean isUploadable = false;
    public static Bitmap bm;
    /* variables to be used by this class */
    private CommonUI ci;
    private ValueCallback<Uri[]> GfilePathCallback;
    /* callback temporary object to hold on the callback for multiple calls */
    public WebViewJavascriptBridge.WVJBResponseCallback tempcallback;
    /* object of bridge class for communicating with web page */
    private WebViewJavascriptBridge bridge;
    public static Boolean multiop=false;
    //Boolean check = true;
    private Bundle latlng;
    private  double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupbrowser);
        ButterKnife.bind(this);
        latlng = getIntent().getExtras();
        if((getIntent() != null) && (latlng != null)){
            latitude = latlng.getDouble(IConstants._LAT);
            longitude = latlng.getDouble(IConstants._LONG);
        }

        init();

    }

    private void init() {
        Log.v("init","");
        showLoading();
        cd = new ConnectionDetector(getApplicationContext());
        sw.setView(wv);
        wv.setClickable(true);
       /* wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }else{
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });*/
        //BJ 11/14/2017:Commented above
        enableSwipe(false);
        ci = new CommonUI(PopUpBrowser.this);
        //ci.logit("Density",getResources().getDisplayMetrics().density+"DPI");
        q = new AQuery(PopUpBrowser.this);
        // connect wv and bridge of android together with bridge object.
        bridge = new WebViewJavascriptBridge(PopUpBrowser.this, wv, new UserServerHandler(), ci);
        // this client will handle all JS alerts and progress.
        wv.setWebChromeClient(new MyWebChromeClient());
       /* wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_DOWN:
                        if(!v.hasFocus())
                            v.requestFocus();
                        break;
                }
                return true;
            }
        });*/
        //swipe refresh customize theme to match livestuff color and pages
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //showLoading();
                if (cd.isConnectingToInternet()) {
                    wv.reload();
                }
            }
        });
        // register all method that can be accessible from wv or web page.
        checkForAddon();
        registerallcallbacks();
        if(cd.isConnectingToInternet()){
            CheckSession();
        }
    }
    private void checkForAddon() {
        Intent i = getIntent();
        isUploadable = i.getBooleanExtra(IConstants._FROMOUTSIDE, false);
        // ci.logit("From Add on", isUploadable.toString());
    }
    public  void showLoading() {
        if(!sw.isRefreshing()) {
            sw.post(new Runnable() {
                @Override
                public void run() {
                    sw.setRefreshing(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sw.setRefreshing(false);
                        }
                    }, 2000);
                }
            });
        }
    }

    public void enableSwipe(final Boolean enable) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                sw.setEnabled(enable);

            }
        });
    }
    /* all the methods accessible directly from web page with callbacks. */
    private void registerallcallbacks() {

        bridge.registerHandler(IConstants._GSI, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                ci.logit("gsipopupbrowser", Browser.Base64Image);
                jsCallback.callback(Browser.Base64Image);
                Browser.Base64Image="";
            }
        });

        bridge.registerHandler(IConstants._TNB, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                // ci.logit("Back", data);
                //isNavigatingBack = true;
                jsCallback.callback(IConstants._SUCCESS);
                runOnUiThread(new Runnable() {
                    public void run() {
                        closeApp();
                        //customGoBack();
                    }
                });

            }
        });
        bridge.registerHandler(IConstants._SE, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (!scrolldisable) {
                    try {
                        scroll = Integer.parseInt(data);
                        if (scroll > 0) {
                            enableSwipe(false);
                        } else {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    enableSwipe(true);
                                }
                            }, 1000);
                        }
                    } catch (NumberFormatException nfe) {
                        ci.logit(IConstants._LOGERROR, nfe.getMessage());
                    }
                }

                jsCallback.callback(IConstants._SUCCESS);

            }
        });

        bridge.registerHandler(IConstants._DS, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                scrolldisable = Boolean.valueOf(data);
                enableSwipe(!scrolldisable);
                jsCallback.callback(IConstants._SUCCESS);
            }
        });

        bridge.registerHandler(IConstants._CS, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                jsCallback.callback(IConstants._SUCCESS);
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Iternet Connection", "Connectivity Error", "OK");
                } else {
                    // tempcallback = jsCallback;
                    ci.logit("Token in Native", data);
                    q.ajax(IConstants._DFT + IConstants._DT + IConstants._LSSC + data,
                            String.class,new AjaxCallback<String>(){
                                @Override
                                public void callback(String url, String object, AjaxStatus status) {
                                    ci.logit("Claim Re", object);
                                    super.callback(url, object, status);
                                }
                            });
                }
            }
        });

        bridge.registerHandler(IConstants._QR, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                //jsCallback.callback(IConstants._SUCCESS);
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Iternet Connection", "Connectivity Error", "OK");
                } else {
                    tempcallback = jsCallback;
                    ci.logit("Call for Scanner", data);
                 /*   q.ajax(IConstants._DFT + IConstants._DT + IConstants._LSSC + data,
                            String.class,new AjaxCallback<String>(){
                                @Override
                                public void callback(String url, String object, AjaxStatus status) {
                                    ci.logit("Claim Re", object);
                                    super.callback(url, object, status);
                                }
                            });*/
                    Intent i=new Intent(PopUpBrowser.this,Scanner.class);
                    i.putExtra(IConstants._QR,"Yes");
                    startActivityForResult(i,IConstants.SCANREQ);
                }

            }
        });
        //BJ 09/13/2017:Added below for Camera
        bridge.registerHandler(IConstants._CAM, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                //jsCallback.callback(IConstants._SUCCESS);
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Internet Connection", "Connectivity Error", "OK");
                } else {
                    tempcallback = jsCallback;
                    //finish();
                    finish();
                    if(wv!=null) wv=null;
                    //BJ 09/13/2017:To clear instance of this activity bcz bridge event was not getting connnected with Stuff Page.
                    Intent intent = new Intent(PopUpBrowser.this, LSCamera.class);
                    startActivity(intent);
                }

            }
        });
    }


    //BJ 10/06/2017:Added below class as Play store reported
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(PopUpBrowser.this);
            builder.setMessage("Unauthorized SSL");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView wv, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            GfilePathCallback=filePathCallback;
            startActivityForResult(fileChooserParams.createIntent(), IConstants.FILEREQ);
            return  true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            ci.logit(IConstants._LOGBRIDGE, cm.message() + " line:" + cm.lineNumber());
            return true;
        }

        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            setValue(newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            /*
             * if don't cancel the alert, wv after onJsAlert not responding
             * taps you can check this :
             * http://stackoverflow.com/questions/15892644/android-wv-after
             * -onjsalert-not-responding-taps
             */
            result.cancel();
            ci.showAlert(message, "Web Page Message", "OK");
            return true;
        }

    }
    /* set progress bar value. */
    public void setValue(int p) {
        prg.setProgress(p);
        if (p >= 100) // code to be added
        {
            prg.setVisibility(View.GONE);
            sw.setRefreshing(false);

        } else {
            prg.setVisibility(View.VISIBLE);

        }
    }

    private boolean isFromWidget() {
        Log.v("isfromWidget","test");
        Intent i = getIntent();
        try {
            type=i.getStringExtra(IConstants.TY);
            Log.v(type,IConstants.WM);
            return i.getBooleanExtra(IConstants.WM, false);
        } catch (NullPointerException ne) {
            ci.logit(IConstants._LOGERROR, ne.getMessage());

            return false;
        }

    }

    public void showloadonUI(Boolean show) {
        if (show) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showLoading();
                }
            });
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(sw.isRefreshing())
                        sw.setRefreshing(false);
                }

            });
        }
    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = 600;
            height = (int) (width / bitmapRatio);
        } else {
            height = 600;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void loadSpotifyAddUrl(String s){
        try {
            if(s==null){
                ci.showAlert("No results for selected music","No Music Found","OK");
                Browser.Base64Image=null;
                ci.logit("Base64null","tr");
                return;
            }
            if (!(s.equals(""))) {
                if (extension == null) {
                    Browser.Base64Image = ci.menuResData("mp3", s, "");

                }
            } else
                Browser.Base64Image = ci.errorMSG(true, "Unable to get Image Data !");
        } catch (NullPointerException n) {
            ci.logit("Error in Mu", n.getMessage());
            Browser.Base64Image = ci.errorMSG(true, "Error Occured !");
        }

        try {
            wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + IConstants.AU + IConstants._LSS2 + code +"&Url="+new JSONObject(Music_url).getString("Music_url")+"&Name="+new JSONObject(Music_url).getString("Name"));
        } catch (JSONException e) {
            wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + IConstants.AU + IConstants._LSS2 + code );
            ci.logit("JSon",e.getMessage());

        }
    }

    public void sendAddonData() throws Exception {
        ci.logit("sendAddonData","test");
        //isUploadable = false;
        DSA.AddOn = false;
        showloadonUI(true);
        Uri selectedimage;
        if(DSA.RT.equals(IConstants.LOC)){
        } else if (DSA.RT.equals(IConstants.IMG)) {
            /*extension = null;*/
            selectedimage = Uri.parse(DSA.RD);
            ci.logit("selectedimage",selectedimage.toString());
            try {
                if (selectedimage.toString().contains("content://com")) {
                    bm = DSA.BI;
                    ci.logit("1Popup", bm.toString());
                } else {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);
                    ci.logit("2Popup", bm.toString());
                    //bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);
                    //bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);
                    String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
                    ci.logit("Image share", selectedimage.toString());
                    Cursor cursor = getContentResolver().query(selectedimage, projection, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(projection[0]);
                            String filePath = cursor.getString(columnIndex);
                            ci.logit("OPath", filePath);
                            int fileNameIndex = cursor.getColumnIndex(projection[1]);
                            String fileName = cursor.getString(fileNameIndex);
                            // Here we get the extension you want
                            extension = fileName.replaceAll("^.*\\.", "");
                            ci.logit("Ext", extension);
                        }
                        cursor.close();
                    }
                }

                new AsyncTask<String,String,String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        // return  CommonUI.encodeTobase64( getResizedBitmap(bm,400));
                        if(extension==null)
                            return  CommonUI.encodeTobase64(getResizedBitmap(bm),Bitmap.CompressFormat.JPEG);
                        else
                            return  CommonUI.encodeTobase64(getResizedBitmap(bm),Bitmap.CompressFormat.PNG);

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            if (!(s.equals(""))){
                                if(extension==null){
                                    Browser.Base64Image = ci.menuResData("jpeg", s, "");
                                }else{
                                    Browser.Base64Image = ci.menuResData(extension, s, "");
                                }
                                ci.logit("Image: ",Browser.Base64Image);
                                //BJ
                                if(getIntent().getStringExtra("redirect")!=null && !getIntent().getStringExtra("redirect").equals("")){
                                    ci.logit("redirectPopup",Browser.Base64Image);
                                    wv.loadUrl(IConstants._DFT+IConstants._DT + getIntent().getStringExtra("redirect"));
                                    //BJ 12/12/2017:Added below as causing issue while redirecting
                                    getIntent().removeExtra("redirect");
                                    return;
                                }
                            }else
                                Browser.Base64Image = ci.errorMSG(true, "Unable to get Image Data !");
                        }catch(NullPointerException n){
                            ci.logit("Error in Img",n.getMessage());
                            Browser.Base64Image = ci.errorMSG(true, "Error Occured !");
                        }
                        wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + IConstants.AF + IConstants._LSS2 + code);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (IOException e) {
                showloadonUI(false);
                ci.logit("innAdd",e.getMessage());
            }
            //new ImageUploadTask(PopUpBrowser.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } if (DSA.RT.equals(IConstants.MUSIC)){
            //selectedimage = Uri.parse(DSA.RD);
            // return  CommonUI.encodeTobase64( getResizedBitmap(bm,400));
            if(DSA.RD.contains("spotify")){
                String spId[]= DSA.RD.split(":");
                q.ajax("https://api.spotify.com/v1/tracks/"+spId[2], JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject res, AjaxStatus status) {
                        try {
                            JSONObject Mdata=new JSONObject();
                            Mdata.accumulate("Music_url",String.valueOf(res.getJSONObject("external_urls").getString("spotify")));
                            Mdata.accumulate("Artist",res.getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name"));
                            Mdata.accumulate("Name",res.getString("name"));
                            Music_url = Mdata.toString();
                            // Music_url = String.valueOf(res.getJSONObject("external_urls").getString("spotify"));
                            loadSpotifyAddUrl(Music_url);
                            ci.logit("Music path",Music_url);
                        } catch (JSONException e) {
                            ci.logit("NullOccured Spotify", e.getMessage());
                            Music_url=DSA.RD;
                            loadSpotifyAddUrl(Music_url);
                            ci.logit("Music path",Music_url);
                        }

                    }
                });

            }else {

                ci.logit("value in sendAOD",DSA.RD);
                if(DSA.RD.contains("-")){
                    String part[]=DSA.RD.split("-");
                    DSA.RD=part[0];
                }
                String url="https://api.spotify.com/v1/search?q="+DSA.RD.trim()+"&type=track,artist&market=US&limit=1";
                ci.logit("Url for Spotify Search",url);
                //  try {
                q.ajax(url, String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String res, AjaxStatus status) {
                        try {
                            //ci.logit("Here 1",status.getMessage());
                            JSONObject ob=new JSONObject(res);
                            JSONObject Mdata=new JSONObject();
                            Mdata.accumulate("Music_url",String.valueOf(ob.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getJSONObject("external_urls").getString("spotify")));
                            Mdata.accumulate("Artist",ob.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name"));
                            Mdata.accumulate("Name",ob.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("name"));
                            Music_url = Mdata.toString();
                            //ci.logit("Here 2",res);
                            ci.logit("Music path S",Music_url);
                            //check =false;
                            loadSpotifyAddUrl(Music_url);

                        } catch (JSONException e) {
                            ci.logit("NullOccured Spotify", e.getMessage());
                            Music_url=DSA.RD;
                            // check =false;
                            ci.showAlert("No results for selected music","No Music Found","OK");
                            ci.logit("Music path S",Music_url);
                        }

                    }
                });
            /*    } catch (UnsupportedEncodingException e) {
                    ci.logit("UEncodingExcep",e.getMessage());
                    ci.showAlert("No results for selected music","No Music Found","OK");
                    Music_url=DSA.RD;
                    check=false;
                    loadSpotifyAddUrl(Music_url);
                }*/
               /* ci.logit("Music path",Music_url);
                if(check){
                    check=true;
                    ci.showAlert("No results for selected music","No Music Found","OK");
                }*/

            }

        } else{
            String reUrl = null, domain = null;
            try {
                domain = new URL(DSA.RD).getHost();
                reUrl = new URL(DSA.RD).getPath();
            } catch (MalformedURLException e) {
                ci.logit("AddOn Error ",e.getMessage());
            }
            showloadonUI(false);
            assert domain != null;
            if (domain.contains("livestuff")) {
                assert reUrl != null;
                if (reUrl.toLowerCase().endsWith("mhtml")) {
                    wv.loadUrl(IConstants._DFT + IConstants._DT + reUrl);
                } else if (reUrl.toLowerCase().endsWith("ihtml")) {
                    String url[] = reUrl.split("ihtml");
                    wv.loadUrl(IConstants._DFT + IConstants._DT + url[0] + "mhtml");
                } else if (reUrl.toLowerCase().endsWith("html")) {
                    String url[] = reUrl.split("html");
                    wv.loadUrl(IConstants._DFT + IConstants._DT + url[0] + "mhtml");
                } else {
                    wv.loadUrl(IConstants._DFT + IConstants._DT + reUrl + IConstants._LSEXT);
                }
            } else {
                bridge.callHandler(IConstants._AOC,
                        ci.menuResData("AddON" + DSA.RT, DSA.RD, ""));
            }
        }
    }

    private static class ImageUploadTask extends AsyncTask<Void, Void, String> {

        private WeakReference<PopUpBrowser> activityReference;

        // only retain a weak reference to the activity
        ImageUploadTask(PopUpBrowser context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... params) {
            if(activityReference.get() ==null)
                cancel(true);
            if (activityReference.get().extension == null)
                return CommonUI.encodeTobase64(activityReference.get().getResizedBitmap(bm), Bitmap.CompressFormat.JPEG);
            else
                return CommonUI.encodeTobase64(activityReference.get().getResizedBitmap(bm), Bitmap.CompressFormat.PNG);
        }

        @Override
        protected void onPostExecute(String s) {

            // get a reference to the activity if it is still there
            PopUpBrowser activity = activityReference.get();
            if (activity == null) return;
            try {
                if (!(s.equals(""))) {
                    if (activity.extension == null) {
                        Browser.Base64Image = activity.ci.menuResData("jpeg", s, "");
                    } else {
                        Browser.Base64Image = activity.ci.menuResData(activity.extension, s, "");
                    }
                    activity.ci.logit("Image: ", Browser.Base64Image);
                    //BJ
                    if (activity.getIntent().getStringExtra("redirect") != null && !activity.getIntent().getStringExtra("redirect").equals("")) {
                        activity.ci.logit("redirectPopup", Browser.Base64Image);
                        activity.wv.loadUrl(IConstants._DFT + IConstants._DT + activity.getIntent().getStringExtra("redirect"));
                        //BJ 12/12/2017:Added below as causing issue while redirecting
                        activity.getIntent().removeExtra("redirect");
                        return;
                    }
                } else
                    Browser.Base64Image = activity.ci.errorMSG(true, "Unable to get Image Data !");
            } catch (NullPointerException n) {
                activity.ci.logit("Error in Img", n.getMessage());
                Browser.Base64Image = activity.ci.errorMSG(true, "Error Occured !");
            }
            activity.wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + IConstants.AF + IConstants._LSS2 + code);

        }
    }

    /*
     * if any method called from JS which is not declared in native comes here.
     */
    class UserServerHandler implements WebViewJavascriptBridge.WVJBHandler {
        @Override
        public void handle(String id, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
            String msg = "Live Stuff: " + id;
            Toast.makeText(PopUpBrowser.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
    /* fire whenever user click back button of mobile device */
    @Override
    public void onBackPressed() {
        closeApp();
        //customGoBack();
    }

    /*private void customGoBack() {
        WebBackForwardList mWebBackForwardList = wv.copyBackForwardList();
        if (wv.canGoBack()) {
            // ci.logit("current index", mWebBackForwardList.getCurrentIndex() + ",Size " + mWebBackForwardList.getSize());
            String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
            //isNavigatingBack = true;
            if (historyUrl.toLowerCase().contains("login.html") || historyUrl.toLowerCase().contains("login.mhtml")) {
                if (mWebBackForwardList.getCurrentIndex() >= 2) {
                    *//*ci.logit("in if true",
                            mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 2).getUrl());*//*
                    wv.goBackOrForward(-2);
                } else {
                    closeApp();
                    // ci.logit("History Size can", mWebBackForwardList.getCurrentIndex() + "");
                }
            } else {
                wv.goBack();
            }
        } else {
            closeApp();
        }
    }
*/
    /* when activity resumes */
    @Override
    protected void onResume() {
        super.onResume();
        // super.onResumeFragments();

        if(wv!=null) wv.onResume();
        if (!cd.isConnectingToInternet()) {
            if(sw.isRefreshing())
                sw.setRefreshing(false);
            ci.showAlertDialogWithYesNoCallBack(PopUpBrowser.this, "Connectivity Error", "No Internet Connection", false,true, "Try Again", "Close App", new DialogCallBackAlert() {
                @Override
                public void dialogCallBackPositive(Dialog alertDialog) {
                    if(cd.isConnectingToInternet()){
                        if(wv.getUrl()==null){
                            CheckSession();
                        }else if(!WebViewJavascriptBridge.isPageLoaded){
                            wv.reload();
                        }
                        alertDialog.dismiss();
                    }
                }

                @Override
                public void dialogCallBackNagative(Dialog alertDialog) {
                    alertDialog.dismiss();
                    closeApp();
                }
            });
            // ci.showAlert("No Iternet Connection", "Connectivity Error", "OK");
        } else if(isResuming) {
            isResuming=false;
            wv.invalidate();
            if (WebViewJavascriptBridge.isPageLoaded ) {
                loadSuffIt();
            }
        }
    }

    private void loadSuffIt() {
        Log.v("loadStuffIt","test");
        if (openMatrix||multiop|| DSA.AddOn) {
            if(!LoggedIn) {
                showloadonUI(true);
                String url = IConstants._DFT + IConstants._DT + IConstants._LSGSI;
                // ci.logit("Session Service Call", url);
                q.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject res, AjaxStatus status) {
                        JSONObject sessionObject;
                        try {
                            sessionObject = res.getJSONArray("Results").getJSONObject(0).getJSONObject("session")
                                    .getJSONObject("login");
                            sessionUser = sessionObject.getString("dri");
                            code = sessionObject.getString("id");
                            //ci.logit(IConstants._LOGAPPNAME, sessionUser);
                            /// Load User Page
                            if (null != sessionUser && !(("").equals(sessionUser))) {
                                LoggedIn=true;
                                addonOrMatrix();
                            } else {
                                RedirectToLoginonResume();
                            }
                        } catch (JSONException e) {
                            ci.logit("NullOccuredRes", e.getMessage());
                            RedirectToLoginonResume();
                        }

                    }
                }.header("Cookie", ci.getFromSharedPref(IConstants._CK)));
            } else {
                ci.logit("AfterResume", "AddonMatrix");
                addonOrMatrix();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        Log.v("onNewIntent","test");
        if(wv!=null) {
            wv.invalidate();
            //wv.clearHistory();
            //wv=new CustomWebView(PopUpBrowser.this);
        }
        loadSuffIt();
    }
    private void addonOrMatrix(){
        Log.v("AddOn", "Inside addon");
        if (DSA.AddOn) {
            ci.logit("Addonormatrix","if case");
            DSA.AddOn = false;
            checkPermissionForStorage();
            /*try {
                sendAddonData();
            } catch (Exception e) {
                ci.logit("AddOn", e.getMessage());
            }*/
        } else {
            openMatrix = false;
            multiop = false;
            ci.logit("Type ",type);
            if(DSA.RT!=null && DSA.RT.equals(IConstants.LOC)){
                latitude = Location.ReturnLatLong.latitude;
                longitude = Location.ReturnLatLong.longitude;
                DSA.RT = "";
                wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser +IConstants._LSS1 +  IConstants.LOC + IConstants._LSS2 + code + IConstants._ALAT + latitude + IConstants._ALONG + longitude);
            } else if(DSA.RT!=null && DSA.RT.equals(IConstants.BC)) {
                Log.v("1","checkurl");
                DSA.RT = "";
                wv.loadUrl(IConstants._DFT+IConstants._DT+sessionUser+IConstants._LSS1+IConstants.BC+"&"+IConstants.BC+"="+Barcode+IConstants._LSS2+code);
            } else {
                Log.v("3",IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + IConstants._LSS2 + code + "&Url=" + typevalue);
                wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + IConstants._LSS2 + code + "&Url=" + typevalue + IConstants._ST + extractTitle);
            }
        }
    }

  /*  private void checkForAddon() {
        Intent i = getIntent();
        isUploadable = i.getBooleanExtra(IConstants._FROMOUTSIDE, false);
        if(i.getData()!=null)
        {
            try {
                deepLink = i.getData().toString();
                //  ci.logit("onCreate Deep Link", deepLink);
            } catch (NullPointerException ne) {
                ci.logit(IConstants._LOGAPPNAME,ne.getMessage());
            }
        }
        // ci.logit("From Add on", isUploadable.toString());
    }*/

    @Override
    protected void onPause() {
        isResuming=true;
        if(wv!=null)
            wv.onPause();
        super.onPause();
    }

    public void closeApp() {
        System.gc();
        q.ajaxCancel();
        if(sw.isRefreshing())
            sw.setRefreshing(false);
        if(wv!=null) {
            wv=null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
        finish();
    }

    public void CheckSession() {
        showloadonUI(true);
        String url = IConstants._DFT + IConstants._DT + IConstants._LSGSI;
        // ci.logit("Session Service Call", url);
        q.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject res, AjaxStatus status) {
                //  ci.logit("GetSession",res.toString());
                if (status.getMessage().equals(IConstants._OK)) {
                    try {
                      /*  ci.logit(IConstants._LOGAPPNAME,
                                res.getJSONArray("Results").getJSONObject(0).getJSONObject("session").toString());*/
                        JSONObject sessionObject = res.getJSONArray("Results").getJSONObject(0).getJSONObject("session")
                                .getJSONObject("login");
                        sessionUser = sessionObject.getString("dri");
                        code=sessionObject.getString("id");
                        //ci.logit(IConstants._LOGAPPNAME, sessionUser);
                        /// Load User Page
                        if (null != sessionUser && !(("").equals(sessionUser))) {
                            LoggedIn = true;
                            //showloadonUI(false);
                            CheckUrlToLoad(LoggedIn);

                        } else {
                            ReDirectToLogin();

                        }
                        ///
                    } catch (JSONException e) {
                        ci.logit(IConstants._LOGERROR, e.getMessage());
                        ReDirectToLogin();
                    }
                } else {
                    ReDirectToLogin();
                }
            }
        }.header("Cookie", ci.getFromSharedPref(IConstants._CK)));
    }

    public void clearCookiesAndCache(Context context) {
        ci.saveCookie("");
        LoggedIn=false;
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
        if(wv!=null) wv.clearCache(true);
    }

    public void RedirectToLoginonResume(){
        openMatrix=false;
        multiop=false;
        String logUrl=IConstants._DFT + IConstants._DT + "/"
                + IConstants._LSLP+IConstants._LPP+sessionUser+IConstants._LSSI1AND+type;
        if(WebViewJavascriptBridge.isPageLoaded) {
            if (!wv.getUrl().equalsIgnoreCase(logUrl))
                wv.loadUrl(logUrl);
        } else{
            wv.loadUrl(logUrl);
        }

    }

    public void ReDirectToLogin() {
        clearCookiesAndCache(getApplicationContext());
        // showloadonUI(false);
        sessionUser = "";
        code="";
        CheckUrlToLoad(false);
    }

    @Override
    protected void onDestroy() {
        if(sw.isRefreshing())
            sw.setRefreshing(false);
        q.ajaxCancel();
        if(wv!=null) {
            wv.freeMemory();
            wv.pauseTimers();
            wv.destroy();
            wv=null;
        }

        super.onDestroy();
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (wv != null) {
            wv.destroy();
        }
    }



    /*
     * calls when any activities return result like camera, gallery, audio
     * recording etc.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onAcitvityResult","test");
        //PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case IConstants.FILEREQ:
                    if (resultCode == RESULT_OK && data != null) {
                        // ci.logit("onActivity result", data.toString());
                        GfilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        GfilePathCallback.onReceiveValue(null);
                        GfilePathCallback=null;
                    } else {
                        ci.logit(IConstants._LOGERROR, "No response in return");
                        GfilePathCallback.onReceiveValue(null);
                        GfilePathCallback=null;
                    }
                    break;
                case IConstants.SCANREQ:
                    if (resultCode == RESULT_OK && data != null) {
                        ci.logit("onActivity result QR", data.toString());
                        String scandata= data.getStringExtra(IConstants._RES);
                        if(scandata.matches("^[0-9]*$") && scandata.length() > 2) {
                            tempcallback.callback(ci.menuResData(IConstants.BC,scandata,""));
                        }else {
                            tempcallback.callback(ci.menuResData(IConstants.QRC,scandata,""));
                        }
                        //  tempcallback.callback(data.getStringExtra(IConstants._RES));
                    } else {
                        ci.logit(IConstants._LOGERROR, "No response in return");
                        tempcallback.callback(IConstants._FAILURE);

                    }
                    break;
                default:

                    /*callbackManager.onActivityResult(requestCode, resultCode, data);*/
                    break;
            }
        } catch (Exception e) {
            ci.logit("onActivity result",e.getMessage());
        }
    }

    public void checkPermissionForStorage(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(PopUpBrowser.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PopUpBrowser.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ci.showAlertDialogWithYesNoCallBack(PopUpBrowser.this,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(PopUpBrowser.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            }
                    );
                } else {
                    ActivityCompat.requestPermissions(PopUpBrowser.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                }
            } else {
                try{
                    ci.logit("callsendaddon","Top");
                    sendAddonData();
                }catch(Exception e){
                    ci.logit("AddOnInCheckPermisssion", e.getMessage());
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ci.showNotification("Storage", "No permission to access storage");
                ci.showAlert("Please Grant permission to access Storage in settings", "Storage Access", "OK");
                return;
            }
            try{
                sendAddonData();
            }catch(Exception e){
                ci.logit("AddOn", e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try{
                        sendAddonData();
                    }catch(Exception e){
                        ci.logit("AddOn", e.getMessage());
                    }
                } else {
                    if(Build.VERSION.SDK_INT < 23) {
                        ci.showNotification("Storage", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Read Storage", "OK");
                    } else {
                        ci.showAlertDialogWithYesNoCallBack(PopUpBrowser.this,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(PopUpBrowser.this,
                                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                }
                        );
                    }
                }
                return;
        }
    }

    public void CheckUrlToLoad(Boolean isLogin) {
        Log.v("Inside CheckUrlToLoad",isLogin.toString());
        showloadonUI(false);
        // String LastUrl = ci.getFromSharedPref(IConstants._LURL);
        // String URL;
        if(!isLogin) {
            Log.v("Not ckurltoload","not logged in");
            openMatrix=false;
            multiop=false;
            Toast.makeText(getApplicationContext(),"Not Logged In",Toast.LENGTH_LONG).show();
            Browser.redirectStuff=true;
            Intent Browser=new Intent(PopUpBrowser.this, Browser.class);
            if((getIntent() != null) && (latlng != null)){
                Browser.putExtra(IConstants._LAT, Double.toString(latitude));
                Browser.putExtra(IConstants._LONG, Double.toString(longitude));
            }
            if(wv!=null) {
                wv.clearCache(true);
                wv.destroy();
                wv = null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
            startActivity(Browser);

        } else if (isFromWidget()||openMatrix||multiop) {
            Log.v("loggin","login");
            getIntent().putExtra(IConstants.WM,false);
            ci.logit("TypeinCHE",type);
            if(openMatrix) {
                ci.logit("open",type);
                openMatrix=false;
                wv.loadUrl(IConstants._DFT+IConstants._DT+sessionUser+IConstants._LSS1+type+IConstants._LSS2+code);
                //return;
            } else if(multiop) {
                ci.logit("Url is",IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + "&" + IConstants.BC + "=" + Barcode + IConstants._LSS2 + code);
                ci.logit("multiop",type);
                multiop=false;
                if(type.equals(IConstants.LOC)){
                    DSA.RT = "";
                    wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser +IConstants._LSS1 + type + IConstants._LSS2 + code + IConstants._ALAT + latitude + IConstants._ALONG + longitude);
                } else if(type.equals(IConstants.BC)) {
                    Log.v("1","checkurl");
                    DSA.RT = "";
                    wv.loadUrl(IConstants._DFT+IConstants._DT+sessionUser+IConstants._LSS1+type+"&"+IConstants.BC+"="+Barcode+IConstants._LSS2+code);
                }else if(TextUtils.isEmpty(typevalue)) {
                    Log.v("2","checkurl");
                    wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser +IConstants._LSS1 + type + IConstants._LSS2 + code);
                } else {
                    Log.v("3",IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + IConstants._LSS2 + code + "&Url=" + typevalue);
                    wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + IConstants._LSS2 + code + "&Url=" + typevalue + IConstants._ST + extractTitle);
                }
                //return;
            }
            //wvJavascriptBridge.openMatrix = true;
        } else  if (isUploadable) {
            Log.v("isUploadable","isuploadable");
            if(isLogin) {
                String getExtra = getIntent().getStringExtra("redirect");
                if(getExtra!=null && !getExtra.equals("") && !getExtra.contains(IConstants.AF)) {
                    wv.loadUrl(IConstants._DFT + IConstants._DT + getIntent().getStringExtra("redirect"));
                    //BJ 12/12/2017:Added below as causing issue while redirecting
                    getIntent().removeExtra("redirect");
                    return;
                } else {
                    checkPermissionForStorage();
                    /*try {
                        sendAddonData();
                        return;
                    } catch (Exception e) {
                        ci.logit("Add On2", e.getMessage());
                    }*/
                }
            } else {
                isUploadable=false;
                wv.loadUrl(IConstants._DFT + IConstants._DT + "/"
                        + IConstants._LSLP+IConstants._LPP+sessionUser+IConstants._LSSI1AND+type);
            }
        }

    }
}

