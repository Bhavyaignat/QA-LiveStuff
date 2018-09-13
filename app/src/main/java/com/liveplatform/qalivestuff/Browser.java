package com.liveplatform.qalivestuff;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
//import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import BridgeJavaClass.WebViewJavascriptBridge;
import BridgeJavaClass.WebViewJavascriptBridge.WVJBResponseCallback;
import Connection.ConnectionDetector;
import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.CustomSwipeToRefresh;
import SupportClasses.CustomWebView;
import SupportClasses.DialogCallBackAlert;
import butterknife.BindView;
import butterknife.ButterKnife;

import static Data.IConstants.ALOC;
import static Data.IConstants._CAM;
import static Data.IConstants._LSS2;
import static Data.IConstants._QR;


public class Browser extends AppCompatActivity {
    /* all Views attached with java codes */
    @BindView(R.id.swipe_container)
    CustomSwipeToRefresh sw;
    @BindView(R.id.webView)
    CustomWebView wv;
    @BindView(R.id.progressBar)
    ProgressBar prg;
    @BindView(R.id.progressBar2)
    ProgressBar prg2;
    @BindView(R.id.splashlo)
    RelativeLayout splashlo;
    @BindView(R.id.tvMsg)
    TextView tvMsg;
    private Bitmap bm;
    private String extension;
    Boolean check = false;
    AQuery q;
    private Boolean scrolldisable = false;
    private Boolean isResuming = false;
    public static Boolean showDownloadpopup = true;
    private Boolean LoggedIn = false;
    public static String Base64Image = null;
    // private String extension;
    int scroll = 0;
    public static Boolean openMatrix = false;
    private static String sessionUser = "";
    public static String type = "";
    private static String code = "";
    //BJAdded Gloabal Variable
    public static Boolean redirectStuff = false;
    /* object of connection class which detects internet connectivity */
    private ConnectionDetector cd;
    //private Boolean isUploadable = false;
    //  private Bitmap bm;
    /* variables to be used by this class */
    private CommonUI ci;
    private ValueCallback<Uri[]> GfilePathCallback;
    /* callback temporary object to hold on the callback for multiple calls */
    public WVJBResponseCallback tempcallback;
    /* object of bridge class for communicating with web page */
    private WebViewJavascriptBridge bridge;
    public static String deepLink = "";
    public static Boolean multiop = false;
    GoogleApiClient mGoogleApiClient;
    ShareDialog shareDialog;
    public static String tokenLoaded = "";
    public Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);
        // if (savedInstanceState != null) { wv.restoreState(savedInstanceState); }
        // Build GoogleApiClient with AppInvite API for receiving deep links
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        ci.showAlert("Could Not Make Internet Connection!", "Error", "OK");
                    }
                })
                .addApi(AppInvite.API).build();
        init();

    }

    private void init() {
        showLoading();
        showsplash();
        cd = new ConnectionDetector(getApplicationContext());
        sw.setView(wv);
        ci = new CommonUI(Browser.this);
        //ci.logit("Density",getResources().getDisplayMetrics().density+"DPI");
        q = new AQuery(Browser.this);
        downloader = new Downloader(this,wv,ci,true);
        wv.setDownloadListener(downloader);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*/
        // connect wv and bridge of android together with bridge object.
        bridge = new WebViewJavascriptBridge(Browser.this, wv, new UserServerHandler(), ci);
        // this client will handle all JS alerts and progress.
        wv.setWebChromeClient(new MyWebChromeClient());
        //wv.setWebViewClient(new MyWebViewClient());
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
        CookieSyncManager.createInstance(this);
        checkForAddon();
        // register all method that can be accessible from wv or web page.
        registerallcallbacks();
        if (cd.isConnectingToInternet()) {
            CheckSession();
            FacebookSdk.sdkInitialize(getApplicationContext());
            //   callbackManager =CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);
        }
    }

    private void showsplash() {
        if (!check) {
            check = true;
            splashlo.setVisibility(View.VISIBLE);
        } else {
            splashlo.setVisibility(View.GONE);
        }
    }

    private void checkForAddon() {
        Intent i = getIntent();
        // isUploadable = i.getBooleanExtra(IConstants._FROMOUTSIDE, false);
        if (i.getData() != null) {
            try {
                deepLink = i.getData().toString();
                //  ci.logit("onCreate Deep Link", deepLink);
            } catch (NullPointerException ne) {
                ci.logit(IConstants._LOGAPPNAME, ne.getMessage());
            }
        }
        // ci.logit("From Add on", isUploadable.toString());
    }

    /* all the methods accessible directly from web page with callbacks. */
    private void registerallcallbacks() {
        bridge.registerHandler(IConstants._SHI, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                shareSocial(data);

            }
        });
        bridge.registerHandler("redirectToPopup", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                tokenLoaded = data;
                ci.logit("redirectToPopup",tokenLoaded);
            }
        });
        bridge.registerHandler(IConstants._GSI, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                jsCallback.callback(Base64Image);
                Base64Image = "";
            }
        });

        /*bridge.registerHandler(IConstants._FS, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                Boolean value=false;
                if(data != null) value = Boolean.valueOf(data);
                View decorView = getWindow().getDecorView();
                if (Build.VERSION.SDK_INT >= 16) {
                    try{
                        if(value){
                            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        } else {
                            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                    } catch(Exception e){
                        ci.logit("fullscreen","error while going fullscreen");
                    }
                }
            }
        });*/


        bridge.registerHandler(IConstants._TNB, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                // ci.logit("Back", data);
                //isNavigatingBack = true;
                jsCallback.callback(IConstants._SUCCESS);
                runOnUiThread(new Runnable() {
                    public void run() {
                        customGoBack();
                    }
                });

            }
        });
        bridge.registerHandler(IConstants._SE, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                //if (!scrolldisable) {
                //Commented for now as causing issue
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
                //}

                jsCallback.callback(IConstants._SUCCESS);

            }
        });

        bridge.registerHandler(IConstants._DS, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                scrolldisable = Boolean.valueOf(data);
                enableSwipe(!scrolldisable);
                jsCallback.callback(IConstants._SUCCESS);
            }
        });

        bridge.registerHandler(IConstants._LO, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        clearCookiesAndCache(getApplicationContext());
                        ReDirectToLogin();
                    }
                });
                jsCallback.callback(IConstants._SUCCESS);
            }
        });

        bridge.registerHandler(IConstants._CS, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, final WVJBResponseCallback jsCallback) {
                jsCallback.callback(IConstants._SUCCESS);
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Internet Connection", "Connectivity Error", "OK");
                } else {
                    // tempcallback = jsCallback;
                    ci.logit("Token in Native", data);
                    q.ajax(IConstants._DFT + IConstants._DT + IConstants._LSSC + data,
                            String.class, new AjaxCallback<String>() {
                                @Override
                                public void callback(String url, String object, AjaxStatus status) {
                                    ci.logit("Claim Re", object+" redirect = "+tokenLoaded);
                                    super.callback(url, object, status);
                                    //TODO BJ:Think for alternative of global variable
                                    if(!tokenLoaded.equals("") && tokenLoaded!=null) {
                                        Intent i = new Intent(Browser.this, PopUpBrowser.class);
                                        i.putExtra(IConstants._FROMOUTSIDE, true);
                                        i.putExtra("redirect", tokenLoaded);
                                        ci.logit("RedirectToBar",tokenLoaded);
                                        tokenLoaded="";
                                        if (wv != null) {
                                            wv = null;
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            finishAndRemoveTask();
                                        } else {
                                            finish();
                                        }
                                        startActivity(i);

                                    }
                                }
                            });
                }

            }
        });
        bridge.registerHandler(IConstants._FS, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WVJBResponseCallback jsCallback) {
                if(data !=null){
                    Intent glideIntent = new Intent(Browser.this, GlideModule.class);
                    glideIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    glideIntent.putExtra("URL",data);
                    startActivity(glideIntent);
                }
            }
        });

        bridge.registerHandler(_QR, new WebViewJavascriptBridge.WVJBHandler() {

            @Override
            public void handle(String data, final WVJBResponseCallback jsCallback) {
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Internet Connection", "Connectivity Error", "OK");
                } else {
                    tempcallback = jsCallback;
                    ci.logit("Scanner call", data);
                    Intent i = new Intent(Browser.this, Scanner.class);
                    i.putExtra(_QR, "Yes");
                    startActivityForResult(i, IConstants.SCANREQ);
                }

            }
        });
        bridge.registerHandler(IConstants._HF, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {

                try{
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                }catch(NullPointerException e){
                    Log.i("Haptick error",e.getMessage());
                }
            }
        });

        bridge.registerHandler(_CAM, new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(String data, final WVJBResponseCallback jsCallback) {
                //jsCallback.callback(IConstants._SUCCESS);
                if (!cd.isConnectingToInternet()) {
                    ci.showAlert("No Internet Connection", "Connectivity Error", "OK");
                } else {
                    tempcallback = jsCallback;
                    //BJ 07/25/2017: created below function to give permission of camera when opened through browser
                    checkAndOpenCam();
                    //jsCallback.callback(IConstants._SUCCESS);
                    // Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // startActivityForResult(cameraIntent, IConstants.CAMREQ);

                 /*   Intent i2 = new Intent(Browser.this, LSCamera.class);
                    i2.putExtra("FromBrowser","Yes");
                    i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i2);*/
                }

            }
        });

    }

    //BJ 07/25/2017:Added below function
    public void checkAndOpenCam(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(Browser.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ci.logit("UI","above shouldshowrationalepermission");
                if (ActivityCompat.shouldShowRequestPermissionRationale(Browser.this,android.Manifest.permission.CAMERA)) {
                    ci.showAlertDialogWithYesNoCallBack(Browser.this,"Camera Permission","LiveStuff needs permission to utilize the Camera.\n To grant permission, click here",
                            false,false,"Grant","",new DialogCallBackAlert() {
                                @Override
                                public void dialogCallBackPositive(Dialog alertDialog) {
                                    alertDialog.dismiss();
                                    ActivityCompat.requestPermissions(Browser.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                                @Override
                                public void dialogCallBackNagative(Dialog alertDialog) {}
                            });
                    ci.logit("UI","shouldshowrationalepermission");

                } else {
                    ActivityCompat.requestPermissions(Browser.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            IConstants.MY_PERMISSIONS_REQUEST_CAMERA);

                }
            }else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, IConstants.CAMREQ);

            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ci.showNotification("Camera", "No permission to access camera");
                ci.showAlert("Please Grant permission to access Camera in settings", "Camera Access", "OK");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, IConstants.CAMREQ);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IConstants.MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, IConstants.CAMREQ);
                } else {
                    if (Build.VERSION.SDK_INT < 23){
                        ci.showNotification("Camera", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Camera Access", "OK");
                    }
                }
                return;
            case IConstants.MY_PERMISSIONS_REQUEST_READ_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*downloader.startDownload();
                    downloader = null;*/
                    downloader.askForWritePermission();
                } else {
                    if(Build.VERSION.SDK_INT < 23) {
                        ci.showNotification("Storage", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Read Storage", "OK");
                    } else {
                        ci.showAlertDialogWithYesNoCallBack(this,"Storage Permission","LiveStuff needs permission to utilize the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(Browser.this,
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
            case IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ci.logit("download: ","WritePermission");
                    downloader.startDownload();
                    downloader = null;
                } else {
                    if(Build.VERSION.SDK_INT >= 23){
                        ci.showAlertDialogWithYesNoCallBack(this,"Storage Permission",
                                "LiveStuff needs permission to write to the Storage.\n To grant permission, click here",
                                false,false,"Grant","",new DialogCallBackAlert() {
                                    @Override
                                    public void dialogCallBackPositive(Dialog alertDialog) {
                                        alertDialog.dismiss();
                                        ActivityCompat.requestPermissions(Browser.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                IConstants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    }
                                    @Override
                                    public void dialogCallBackNagative(Dialog alertDialog) {}
                                }
                        );
                    } else {
                        ci.logit("Storage permission","No Permission Granted");
                        ci.showNotification("Storage", "Permission Denied");
                        ci.showAlert("Permission Denied by the User", "Write Storage", "OK");
                    }
                }
        }
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
                        ci.logit(IConstants._LOGAPPNAME,
                                res.getJSONArray("Results").getJSONObject(0).getJSONObject("session").toString());
                        JSONObject sessionObject = res.getJSONArray("Results").getJSONObject(0).getJSONObject("session")
                                .getJSONObject("login");
                        sessionUser = sessionObject.getString("dri");
                        code = sessionObject.getString("id");
                        //ci.logit(IConstants._LOGAPPNAME, sessionUser);
                        /// Load User Page
                        if (null != sessionUser && !(("").equals(sessionUser))) {
                            LoggedIn = true;
                            tvMsg.setText("Loading your clouds...");
                            //showloadonUI(false);
                            CheckUrlToLoad(true);
                        } else {
                            //BJAdded
                            ci.logit("checkSession","elseloading");
                            tvMsg.setText("Loading...");
                            ReDirectToLogin();

                        }
                        ///
                    } catch (JSONException e) {
                        //BJAdded
                        ci.logit("checkSession","JSONException");
                        tvMsg.setText("Loading...");
                        ci.logit(IConstants._LOGERROR, e.getMessage());
                        ReDirectToLogin();
                    }
                } else {
                    ReDirectToLogin();
                }
            }
        }.header("Cookie", ci.getFromSharedPref(IConstants._CK)));
    }

    public void ReDirectToLogin() {
        clearCookiesAndCache(getApplicationContext());
        // showloadonUI(false);
        sessionUser = "";
        code = "";
        //BJAdded
        ci.logit("RedirectToLogin","first");
        CheckUrlToLoad(false);
    }

    public void CheckUrlToLoad(Boolean isLogin) {
        showloadonUI(false);
        String LastUrl = ci.getFromSharedPref(IConstants._LURL);
        String URL;
        ci.logit("CheckURLTOLoad","outside");
        if (!(LastUrl.equalsIgnoreCase("") || LastUrl.equalsIgnoreCase(null))) {
            if (isLogin) {
                URL = LastUrl;
                ci.logit("CheckURLTOLoad 1",URL);
            } else {
                /*String reUrl;
                try {
                    reUrl = new URL(LastUrl).getPath();
                    ci.logit("path without Domain", reUrl);
                } catch (MalformedURLException e) {
                    String redirectPath[] = LastUrl.split("com");
                    reUrl = redirectPath[redirectPath.length - 1];
                    ci.logit("path without Domain", reUrl);
                    ci.logit(IConstants._LOGERROR, e.getMessage());
                }*/
                if(redirectStuff){
                    if(DSA.RT!=null && DSA.RT.equals(IConstants.IMG)){
                        URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.AF + IConstants._LSS2 + code;
                    } else if(DSA.RT!=null && DSA.RT.equals(IConstants.BC)){
                        URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.BC +"&"+
                                IConstants.BC + "=" + PopUpBrowser.Barcode + IConstants._LSS2 + code;
                    } else if(DSA.RT!=null && DSA.RT.equals(IConstants.LOC)){
                        URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.LOC +
                                IConstants._ALAT + getIntent().getStringExtra(IConstants._LAT) + IConstants._ALONG + getIntent().getStringExtra(IConstants._LONG) + IConstants._LSS2 + code;
                    } else if(PopUpBrowser.type !=null) {
                        /*URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + PopUpBrowser.type +
                                IConstants._LSS2 + code;*/
                        URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + PopUpBrowser.type +
                                "&Url=" + DSA.RD + IConstants._ST + PopUpBrowser.extractTitle + IConstants._LSS2 + code;
                    }else {
                        URL = IConstants._DFT + IConstants._DT + "/"
                                + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.SHURL + "&Url=" + DSA.RD + IConstants._ST + PopUpBrowser.extractTitle; //+IConstants._LPP + reUrl;
                    }
                    /*URL = IConstants._DFT + IConstants._DT + "/"
                            + IConstants._LSLP +"?Redirect=/StuffIt.html&Action=StuffUrl&Url=" + DSA.RD; //+IConstants._LPP + reUrl;*/
                    redirectStuff = false;
                    ci.logit("CheckURLTOLoad 2",URL);
                } else{
                    URL = IConstants._DFT + IConstants._DT + "/"
                            + IConstants._LSLP;//+IConstants._LPP + reUrl;
                    ci.logit("CheckURLTOLoad 3",URL);
                }

            }
        } else {
            if (isLogin) {
                URL = IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSEXT;
                ci.logit("CheckURLTOLoad 4",URL);
            } else {
                if(DSA.RT!=null && DSA.RT.equals(IConstants.IMG)){
                    ci.logit("IN","2");
                    URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.AF + IConstants._LSS2 + code;
                } else if(DSA.RT!=null && DSA.RT.equals(IConstants.BC)){
                    ci.logit("IN","3");
                    URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.BC +"&"+
                            IConstants.BC + "=" + PopUpBrowser.Barcode + IConstants._LSS2 + code;
                } else if(DSA.RT!=null && DSA.RT.equals(IConstants.LOC)){
                    ci.logit("IN","4");
                    URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + IConstants.LOC +
                            IConstants._ALAT + getIntent().getStringExtra(IConstants._LAT) + IConstants._ALONG + getIntent().getStringExtra(IConstants._LONG) + IConstants._LSS2 + code;
                } else if(PopUpBrowser.type !=null && !PopUpBrowser.type.equals("")) {
                    ci.logit("IN","5");
                    URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP + IConstants._LPP +  IConstants._LSSI1AND + PopUpBrowser.type +
                            "&Url=" + DSA.RD + IConstants._ST + PopUpBrowser.extractTitle + IConstants._LSS2 + code;
                } else {
                    ci.logit("IN","6");
                    URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP;
                }
                /*URL = IConstants._DFT + IConstants._DT + "/" + IConstants._LSLP;*/
                ci.logit("CheckURLTOLoad 5",URL);
            }
        }
       /* if (isFromWidget()||openMatrix||multiop) {
            getIntent().putExtra(IConstants.WM,false);
            ci.logit("TypeinCHE",type);
            if(!isLogin) {
                openMatrix=false;
                multiop=false;
                //wv.loadUrl(URL); +IConstants._LSSTUFFIT2+code
                wv.loadUrl(IConstants._DFT + IConstants._DT + "/"
                        + IConstants._LSLP+IConstants._LPP+sessionUser+IConstants._LSSI1AND+type);
                return;
            }

            if(openMatrix) {
                openMatrix=false;
                wv.loadUrl(IConstants._DFT+IConstants._DT+sessionUser+IConstants._LSS1+type+IConstants._LSS2+code);
                return;
            }
            else if(multiop) {
                multiop=false;
                wv.loadUrl(IConstants._DFT+IConstants._DT+sessionUser+IConstants._LSS1+type+IConstants._LSS2+code);
                return;
            }
            //wvJavascriptBridge.openMatrix = true;
        }*/
        try {
            if (!deepLink.equals("")) {
                wv.loadUrl(deepLink);
            } else {
             /*   if (isUploadable) {
                    if(isLogin) {
                        try {
                            sendAddonData();
                            return;
                        } catch (Exception e) {
                            ci.logit("Add On2", e.getMessage());
                        }
                    } else {
                        isUploadable=false;
                        wv.loadUrl(IConstants._DFT + IConstants._DT + "/"
                                + IConstants._LSLP+IConstants._LPP+sessionUser+IConstants._LSSI1AND+type);
                    }
                }*/
                wv.loadUrl(URL);
            }
        } catch (NullPointerException n) {
            ci.logit("CheckUrlToLoad", n.getMessage());
         /*   if (isUploadable) {
                if(isLogin) {
                    try {
                        sendAddonData();
                        return;
                    } catch (Exception e) {
                        ci.logit("Add On2", e.getMessage());
                    }
                } else {
                    isUploadable=false;
                    wv.loadUrl(IConstants._DFT + IConstants._DT + "/"
                            + IConstants._LSLP+IConstants._LPP+sessionUser+IConstants._LSSI1AND+type);
                }
            }*/
            wv.loadUrl(URL);
        }
    }

    private void shareSocial(String data) {
        try {
            JSONObject res = new JSONObject(data);
            switch (res.getString("app")) {
                case "facebook":
                    if (TextUtils.isEmpty(res.getString("imageurl"))) {
                        ci.logit("Image fb", "empty");
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(res.getString("title"))
                                    .setContentDescription("LiveStuff")
                                    .setContentUrl(Uri.parse("https://" + URLDecoder.decode(res.getString("url"), "UTF-8")))
                                    .build();
                            shareDialog.show(linkContent);
                        }
                    } else {
                        ci.logit("Image fb", res.getString("imageurl"));

                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(res.getString("title"))
                                    .setContentDescription("LiveStuff")
                                    .setImageUrl(Uri.parse(res.getString("imageurl")))
                                    .setContentUrl(Uri.parse("https://" + URLDecoder.decode(res.getString("url"), "UTF-8")))
                                    .build();

                            shareDialog.show(linkContent);
                        }
                    }
                    break;
                case "pinterest":
                    String url;
                    String pinteresturl = URLDecoder.decode(res.getString("url"),"UTF-8");
                    if(pinteresturl != null && !(pinteresturl.contains("https://") || pinteresturl.contains("http://"))){
                        pinteresturl = "https://" + pinteresturl;
                    }
                    if (TextUtils.isEmpty(res.getString("imageurl"))) {
                        url = String.format(
                                "https://www.pinterest.com/pin/create/button/?url=%s&media=%s&description=%s",
                                pinteresturl, URLDecoder.decode("http://livestuff.com/Icons/LiveStuff_Logo/Icon256x256.png", "UTF-8"), "LiveStuff");
                    } else {
                        url = String.format(
                                "https://www.pinterest.com/pin/create/button/?url=%s&media=%s&description=%s",
                                pinteresturl, URLDecoder.decode(res.getString("imageurl"), "UTF-8"), "LiveStuff");
                    }

                    if (ci.appInstalledOrNot(IConstants.Pinterest)) {
                        ci.logit("pin", "Installed");
                        // String description = "Pinterest sharing";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        CommonUI.filterByPackageName(Browser.this, intent, IConstants.Pinterest);
                    } else {
                        ci.logit("pin", "Not Installed");
                        Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(sharingIntent);
                    }
                    break;
                case "twitter":
                    String twitterurl = URLDecoder.decode(res.getString("url"),"UTF-8");
                    if(twitterurl != null && !(twitterurl.contains("https://") || twitterurl.contains("http://"))){
                        twitterurl = "https://" + twitterurl;
                    }
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?url=%s",
                            twitterurl);
                    if (ci.appInstalledOrNot(IConstants.Twitter)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                        CommonUI.filterByPackageName(Browser.this, intent, IConstants.Twitter);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                        startActivity(intent);
                    }
                    break;
                default:
                    String defaulturl = URLDecoder.decode(res.getString("url"),"UTF-8");
                    if(defaulturl != null && !(defaulturl.contains("https://") || defaulturl.contains("http://"))){
                        defaulturl = "https://" + defaulturl;
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, defaulturl);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Share To..."));
                    /*ArrayList<Uri> Uris = new ArrayList<Uri>();
                    Uris.add(Uri.parse(res.getString("imageurl"))); // Add your image URIs here
                    Uris.add(Uri.parse("https://" + URLDecoder.decode(res.getString("url"), "UTF-8")));
*/               /*   ci.logit("image Url",res.getString("imageurl"));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://" + URLDecoder.decode(res.getString("url"), "UTF-8"));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(res.getString("imageurl"))));
                    shareIntent.setType("**//*");
                    /*startActivity(Intent.createChooser(shareIntent, "Share to.."));*/
                    break;
            }
        } catch (JSONException e) {
            ci.logit("JSon Error", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            ci.logit("Encoding Error", e.getMessage());
        }
    }
    //BJ 10/06/2017:Added below class as Play store reported
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Browser.this);
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
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView wv, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            GfilePathCallback = filePathCallback;
            startActivityForResult(fileChooserParams.createIntent(), IConstants.FILEREQ);
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            ci.logit(IConstants._LOGBRIDGE, cm.message() + " line:" + cm.lineNumber());
            return true;
        }

        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            ci.logit("Progress", newProgress + "");
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
        prg2.setProgress(p);

        /*if (p >= 100) // code to be added
        {
            prg.setVisibility(View.GONE);
            showsplash();
            sw.setRefreshing(false);

        } else {
            prg.setVisibility(View.VISIBLE);
        }*/
        //BJ 11/14/2017:Commented above and updated below
        if (p >= 100) // code to be added
        {
            prg.setVisibility(View.GONE);
            sw.setRefreshing(false);
            showsplash();
        } else if (p >= 90) {
            check = true;
            showsplash();
        } else {
            prg.setVisibility(View.VISIBLE);

        }
    }

   /* private boolean isFromWidget() {
        Intent i = getIntent();
        try {
            type=i.getStringExtra(IConstants.TY);
            return i.getBooleanExtra(IConstants.WM, false);
        } catch (NullPointerException ne) {
            ci.logit(IConstants._LOGERROR, ne.getMessage());
            return false;
        }

    }*/

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
                    if (sw.isRefreshing())
                        sw.setRefreshing(false);
                }

            });
        }
    }

    public void sendAddonData() throws Exception {
        DSA.AddOn = false;
        showloadonUI(true);
        Uri selectedimage;
        if (DSA.RT.equals(IConstants.IMG)) {
            selectedimage = Uri.parse(DSA.RD);
            try {
                if(selectedimage.toString().contains("com.whatsapp")){
                    bm = DSA.BI;
                    ci.logit("1",bm.toString());
                } else {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);
                    ci.logit("2",bm.toString());
                    /*bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimage);*/
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

                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        // return  CommonUI.encodeTobase64( getResizedBitmap(bm,400));
                        if (extension == null)
                            return CommonUI.encodeTobase64(getResizedBitmap(bm, 600), Bitmap.CompressFormat.JPEG);
                        else
                            return CommonUI.encodeTobase64(getResizedBitmap(bm, 600), Bitmap.CompressFormat.PNG);

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            if (!(s.equals(""))) {
                                if (extension == null) {
                                    Base64Image = ci.menuResData("jpeg", s, "");
                                } else {
                                    Base64Image = ci.menuResData(extension, s, "");
                                }
                            } else
                                Base64Image = ci.errorMSG(true, "Unable to get Image Data !");
                        } catch (NullPointerException n) {
                            ci.logit("Error in Img", n.getMessage());
                            Base64Image = ci.errorMSG(true, "Error Occured !");
                        }
                        // wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + IConstants.AF + IConstants._LSS2 + code);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (IOException e) {
                showloadonUI(false);
                ci.logit("innAdd", e.getMessage());
            }
        }
        String reUrl = null, domain = null;
        try {
            domain = new URL(DSA.RD).getHost();
            reUrl = new URL(DSA.RD).getPath();
        } catch (MalformedURLException e) {
            ci.logit("AddOn Error ", e.getMessage());
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
               /* bridge.callHandler(IConstants._AOC,
                        ci.menuResData("AddON" + DSA.RT, DSA.RD, ""));*/
            JSONObject res = new JSONObject(ci.menuResData("AddON" + DSA.RT, DSA.RD, ""));
            Intent intent = new Intent(this, PopUpBrowser.class);
            intent.putExtra(IConstants.WM, true);
            intent.putExtra(IConstants.TY, IConstants.SHURL);
            PopUpBrowser.type = IConstants.AU;
            PopUpBrowser.typevalue = res.getString("typevalue");
            PopUpBrowser.multiop = true;
            startActivity(intent);
        }
        // }
    }

    /*
     * if any method called from JS which is not declared in native comes here.
     */
    class UserServerHandler implements WebViewJavascriptBridge.WVJBHandler {
        @Override
        public void handle(String id, WVJBResponseCallback jsCallback) {
            String msg = "Live Stuff: " + id;
            Toast.makeText(Browser.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /* fire whenever user click back button of mobile device */
    @Override
    public void onBackPressed() {
        customGoBack();
    }


    private void customGoBack() {


        bridge.callHandler("popup", "", new WVJBResponseCallback() {
            @Override
            public void callback(String data) {
                ci.logit("popup", data);
                if (data.equals("Y")) {
                    // check=true;
                } else {
                    // check=false;
                    ci.logit("popup", "else ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noPopup();
                        }
                    });

                }
            }
        });

    }

    public void noPopup() {
        ci.logit("popup", "else 2");
        WebBackForwardList mWebBackForwardList = wv.copyBackForwardList();
        if (wv.canGoBack()) {

            ci.logit("current index", mWebBackForwardList.getCurrentIndex() + ",Size " + mWebBackForwardList.getSize());
            String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
            //isNavigatingBack = true;
            if (historyUrl.toLowerCase().contains("login.html") || historyUrl.toLowerCase().contains("login.mhtml")) {
                if (mWebBackForwardList.getCurrentIndex() >= 2) {
                    ci.logit("in if true",
                            mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 2).getUrl());
                    wv.goBackOrForward(-2);
                } else {
                    closeApp();
                    ci.logit("History Size can", mWebBackForwardList.getCurrentIndex() + "");
                }
            } else {
                wv.goBack();
                ci.logit("Go Back", "");
            }
        } else {
            closeApp();
            ci.logit("Close", "");
        }
    }

    /* when activity resumes */
    @Override
    protected void onResume() {
        super.onResume();
        // super.onResumeFragments();
        if(wv!=null) wv.onResume();
        if (!cd.isConnectingToInternet()) {
            if (sw.isRefreshing())
                sw.setRefreshing(false);
            ci.showAlertDialogWithYesNoCallBack(Browser.this, "Connectivity Error", "No Internet Connection", false,true, "Try Again", "Close App", new DialogCallBackAlert() {
                @Override
                public void dialogCallBackPositive(Dialog alertDialog) {
                    if (cd.isConnectingToInternet()) {
                        if (wv.getUrl() == null) {
                            CheckSession();
                        } else if (!WebViewJavascriptBridge.isPageLoaded) {
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
            // ci.showAlert("No Internet Connection", "Connectivity Error", "OK");
        } else if(redirectStuff){
            //BJ 10/06/2017:Added else if
        } else if (isResuming) {
            CookieSyncManager.getInstance().sync();
            ci.logit("onResume()","else if");
            isResuming = false;
            wv.invalidate();
            if (WebViewJavascriptBridge.isPageLoaded) {
                loadSuffIt();
            }
        }
    }

    public void loadSuffIt() {
        if (openMatrix || multiop || DSA.AddOn) {
            if (!LoggedIn) {
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
                                LoggedIn = true;
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

    private void addonOrMatrix() {
        if (DSA.AddOn) {
            DSA.AddOn = false;
            try {
                sendAddonData();
            } catch (Exception e) {
                ci.logit("AddOn", e.getMessage());
            }
        } else {
            openMatrix = false;
            multiop = false;
            wv.loadUrl(IConstants._DFT + IConstants._DT + sessionUser + IConstants._LSS1 + type + _LSS2 + code);
        }
    }

    @Override
    protected void onPause() {
        isResuming = true;
        if (wv != null) wv.onPause();
        super.onPause();
    }

    public void closeApp() {
        System.gc();
        q.ajaxCancel();
        if (sw.isRefreshing())
            sw.setRefreshing(false);
        if (wv != null)
            wv = null;
        finish();
    }

    public void showLoading() {
        if (!sw.isRefreshing()) {
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

    public void clearCookiesAndCache(Context context) {
        ci.saveCookie("");
        LoggedIn = false;
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }

    public void RedirectToLoginonResume() {
        openMatrix = false;
        multiop = false;
        String logUrl = IConstants._DFT + IConstants._DT + "/"
                + IConstants._LSLP + IConstants._LPP + sessionUser + IConstants._LSSI1AND + type;
        ci.logit("RedirectToLoginonResume",logUrl);
        if (WebViewJavascriptBridge.isPageLoaded) {
            if (!wv.getUrl().equalsIgnoreCase(logUrl))
                wv.loadUrl(logUrl);
        } else {
            wv.loadUrl(logUrl);
        }

    }

    @Override
    protected void onDestroy() {
        if (sw.isRefreshing())
            sw.setRefreshing(false);
        q.ajaxCancel();
        if (wv != null) {
            wv.freeMemory();
            wv.pauseTimers();
            wv.destroy();
        }
        super.onDestroy();
    }

    /*
     * calls when any activities return result like camera, gallery, audio
     * recording etc.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case IConstants.FILEREQ:
                    if (resultCode == RESULT_OK && data != null) {
                        // ci.logit("onActivity result", data.toString());
                        GfilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        GfilePathCallback.onReceiveValue(null);
                        GfilePathCallback = null;
                    } else {
                        ci.logit(IConstants._LOGERROR, "No response in return");
                        GfilePathCallback.onReceiveValue(null);
                        GfilePathCallback = null;
                    }
                    break;
                case IConstants.SCANREQ:
                    if (resultCode == RESULT_OK && data != null) {
                        ci.logit("onActivity result QR", data.toString());
                        String scandata = data.getStringExtra(IConstants._RES);
                        if (scandata.matches("^[0-9]*$") && scandata.length() > 2) {
                            tempcallback.callback(ci.menuResData(IConstants.BC, scandata, ""));
                        } else {
                            tempcallback.callback(ci.menuResData(IConstants.QRC, scandata, ""));
                        }
                    } else {
                        ci.logit(IConstants._LOGERROR, "No response in return");
                        tempcallback.callback(IConstants._FAILURE);

                    }
                    break;
                case IConstants.CAMREQ:
                    if (resultCode == RESULT_OK && data != null) {
                        ci.logit("onActivity result cam", data.toString());
                        final Bitmap photo = (Bitmap) data.getExtras().get("data");
                        new AsyncTask<String, String, String>() {

                            @Override
                            protected String doInBackground(String... params) {
                                // return  CommonUI.encodeTobase64( getResizedBitmap(bm,400));
                                if (extension == null)
                                    return CommonUI.encodeTobase64(getResizedBitmap(photo, 600), Bitmap.CompressFormat.JPEG);
                                else
                                    return CommonUI.encodeTobase64(getResizedBitmap(photo, 600), Bitmap.CompressFormat.PNG);

                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                try {
                                    if (!(s.equals(""))) {
                                        if (extension == null) {
                                            Base64Image = ci.menuResData("jpeg", s, "");
                                        } else {
                                            Base64Image = ci.menuResData(extension, s, "");
                                        }
                                    } else
                                        Base64Image = ci.errorMSG(true, "Unable to get Image Data !");
                                    ci.logit("redirectPost",Base64Image);
                                    tempcallback.callback(Base64Image);
                                    tempcallback = null;
                                } catch (NullPointerException n) {
                                    ci.logit("Error in Img", n.getMessage());
                                    Base64Image = ci.errorMSG(true, "Error Occured !");
                                    tempcallback.callback(Base64Image);
                                    tempcallback = null;
                                }
                            }


                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    break;
                default:

                    /*callbackManager.onActivityResult(requestCode, resultCode, data);*/
                    break;
            }
        } catch (Exception e) {
            ci.logit("onActivity result", e.getMessage());
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        deepLink = intent.getDataString();
        ci.logit("onNew intent", deepLink + " <");
        if (!(deepLink == null) || (!(TextUtils.isEmpty(deepLink)))) {
            wv.loadUrl(deepLink);
        } else if(redirectStuff){
            //BJ 10/06/2017:Added above else if
            ci.logit("inside","onNewIntent");
            CheckUrlToLoad(false);
        } else {
            loadSuffIt();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
