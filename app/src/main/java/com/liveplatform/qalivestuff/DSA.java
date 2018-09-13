package com.liveplatform.qalivestuff;

import Data.IConstants;
import SupportClasses.CommonUI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.PopupMenu;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DSA extends AppCompatActivity {
    CommonUI ci;
    public static String RT;
    public static String RD;
    public static Boolean AddOn = false;
    public static Bitmap BI;
    public String reUrl = null, domain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ci = new CommonUI(DSA.this);
        Intent i = getIntent();
        RT = i.getType();
        AddOn = true;
        if (RT.startsWith(IConstants.TXT)) {
            RT = IConstants.TXT;
            RD = i.getStringExtra(Intent.EXTRA_TEXT);
            try {
                ci.logit("Url Parsed",extractUrls(DSA.RD));
                DSA.RD = extractUrls(DSA.RD);
                domain = new URL(DSA.RD).getHost();
                reUrl = new URL(DSA.RD).getPath();
                new Title().execute();
            } catch (MalformedURLException e) {
                ci.logit("AddOn Error ",e.getMessage());
            }
        } else {
            Intent GoToMain = new Intent();
            if (RT.startsWith(IConstants.IMG)) {
                GoToMain.setClass(getApplicationContext(), PopUpBrowser.class);
                RT = IConstants.IMG;
                Uri receivedUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
                RD = receivedUri.toString();
                if(DSA.RD != null) BI = getRealPathFromURI(Uri.parse(DSA.RD));
                ci.logit("DSA.BI",DSA.BI.toString());
            }
            startbrowser(GoToMain);
        }
    }

    public void startbrowser(Intent GoToMain){
        GoToMain.putExtra(IConstants._FROMOUTSIDE, true);
        GoToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(GoToMain);
        finish();
    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect(DSA.RD).get();
                PopUpBrowser.extractTitle = document.title();
                ci.logit("ExtractTitle",PopUpBrowser.extractTitle);
            } catch (IOException e) {
                ci.logit("Error from JSOUP","DSA File");
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Intent GoToMain = new Intent();
            try {
                assert domain != null;
                if (domain.contains("livestuff")) {
                    GoToMain.setClass(getApplicationContext(), Browser.class);
                } else {
                    JSONObject res = null;
                    try {
                        res = new JSONObject(ci.menuResData("AddON" + DSA.RT, DSA.RD, ""));
                        GoToMain.setClass(getApplicationContext(), PopUpBrowser.class);
                        GoToMain.putExtra(IConstants.WM, true);
                        GoToMain.putExtra(IConstants.TY, IConstants.SHURL);
                        /*PopUpBrowser.type = IConstants.AU;*/
                        /*BJ 11/29/2017:Commented above and updated below*/
                        PopUpBrowser.type = IConstants.SHURL;
                        PopUpBrowser.typevalue = res.getString("typevalue");
                        PopUpBrowser.multiop = true;
                        DSA.AddOn = false;

                    } catch (JSONException e) {
                        ci.logit("E DSA", e.getMessage());
                    }
                }
            }catch(NullPointerException e){
                ci.logit("NPE Add",e.getMessage());
                JSONObject res = null;
                try {
                    res = new JSONObject(ci.menuResData("AddON" + DSA.RT, DSA.RD, ""));
                    GoToMain.setClass(getApplicationContext(), PopUpBrowser.class);
                    GoToMain.putExtra(IConstants.WM, true);
                    GoToMain.putExtra(IConstants.TY, IConstants.SHURL);
                    /*PopUpBrowser.type = IConstants.AU;*/
                    /*BJ 11/29/2017:Commented above and updated below*/
                    PopUpBrowser.type = IConstants.SHURL;
                    PopUpBrowser.typevalue = res.getString("typevalue");
                    PopUpBrowser.multiop = true;
                    DSA.AddOn = false;

                } catch (JSONException je) {
                    ci.logit("E DSA", je.getMessage());
                }
            }
            startbrowser(GoToMain);
        }
    }


    //BJ 11/14/2017:Created below function
    public Bitmap getRealPathFromURI(Uri contentUri){
        InputStream inputStream = null;
        try {
            ci.logit("contentURI",contentUri.toString());
            inputStream = getContentResolver().openInputStream(contentUri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractUrls(String text)
    {
        String containedUrls = text;
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls= (text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
}
