package SupportClasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import Data.IConstants;

public class CustomWebView extends WebView {

	Context ct;
	WebSettings ws;

	public CustomWebView(Context context) {
		super(context);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		ct = context;
		ws = getSettings();
		setBackgroundColor(Color.TRANSPARENT);
		ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
		if (Build.VERSION.SDK_INT >= 19) {
			// chromium, enable hardware acceleration
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {
			// older android version, disable hardware acceleration
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		// CookieSyncManager.createInstance(webView.getContext()).sync();
		// CookieManager.getInstance().acceptCookie();
		// webviewSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		ws.setAppCacheEnabled(true);
		ws.setAppCachePath(ct.getCacheDir().getPath());
		ws.setAppCacheMaxSize( 10 * 1024 * 1024 ); // 10MB
		ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		//BJ 02/22/2018:Added below to hide Scroll bar
		setVerticalScrollBarEnabled(false);
		// below settings are for accelarating the webview speed and smooth
		// transitions
		requestFocus(View.FOCUS_DOWN | View.FOCUS_UP);
		//webviewSettings.setLightTouchEnabled(true);
		ws.setTextSize(WebSettings.TextSize.NORMAL);
		ws.setDomStorageEnabled(true);
		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		ws.setUseWideViewPort(true);
		ws.setSavePassword(true);
		ws.setSaveFormData(true);
		ws.setSupportMultipleWindows(true);
		ws.setEnableSmoothTransition(true);
		ws.setDatabaseEnabled(true);
		ws.setPluginState(WebSettings.PluginState.ON_DEMAND);
		ws.setAllowFileAccess(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ws.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}
		ws.setAllowUniversalAccessFromFileURLs(true);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);

		// webView.setOnLongClickListener(this);
		String databasePath = ct.getDir("databases", Context.MODE_PRIVATE).getPath();
		// "/data/data/" + webView.getContext().getPackageName() + "/databases/"
		// ws.setDatabasePath(databasePath);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			ws.setDatabasePath(databasePath);
		}
		ws.setUserAgentString(ws.getUserAgentString() + IConstants._USERAGENT);

	}



	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	public void onResume() {

		super.onResume();
	}
}
