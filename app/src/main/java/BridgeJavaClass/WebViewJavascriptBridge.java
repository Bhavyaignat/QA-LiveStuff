package BridgeJavaClass;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.liveplatform.qalivestuff.Browser;
import com.liveplatform.qalivestuff.PopUpBrowser;
import com.liveplatform.qalivestuff.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;

@SuppressWarnings("deprecation")
@SuppressLint("SetJavaScriptEnabled")
public class WebViewJavascriptBridge {
	private Dialog d;

	private TextView tvmsg, tvtitle;
	private Button ok;

	private WVJBHandler handler;
	private WVJBResponseCallback responseCallback = null;

	private WebView mWebView;

	//private static Boolean HideBackArrow = false;
	private static Boolean AskToSaveCred = false;
	private String credUrl;
	public static Boolean isPageLoaded = false;
	/*private String hidebackarrow = "var backarrow = document.getElementsByClassName('Icon-Left-Standard left-arrow-icon ')[0];"
			+ " if(backarrow) { backarrow.parentElement.classList +=' -collapse'; }";*/
	private Activity mContext;
	private WVJBHandler _messageHandler;
	private Map<String, WVJBHandler> _messageHandlers;
	private Map<String, WVJBResponseCallback> _responseCallbacks;
	//SwipeRefreshLayout swipeLayout;
	private long _uniqueId;
	private CommonUI ci;

	@SuppressLint("AddJavascriptInterface")
	public WebViewJavascriptBridge(Activity context, WebView webview, WVJBHandler handler, CommonUI ci) {
		this.mContext = context;
		this.mWebView = webview;
		//this.swipeLayout = swipeLayout;
		this.ci = ci;
		this._messageHandler = handler;
		_messageHandlers = new HashMap<>();
		_responseCallbacks = new HashMap<>();
		_uniqueId = 0;
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(this, "_WebViewJavascriptBridge");
		mWebView.setWebViewClient(new MyWebViewClient());

	}

	@JavascriptInterface
	private void loadWebViewJavascriptBridgeJs(WebView webView) {
		InputStream is = mContext.getResources().openRawResource(R.raw.webviewjavascriptbridge);
		String script = convertStreamToString(is);
		// after adding target version in manifest below commented line wont
		// work.
		// webView.loadUrl("javascript:" + script);
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			webView.evaluateJavascript(script, null);
		} else {
			webView.loadUrl("javascript: " + script);
		}
	}

	@JavascriptInterface
	private static String convertStreamToString(InputStream is) {
		String s = "";
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
			if (scanner.hasNext())
				s = scanner.next();
			is.close();
		} catch (IOException e) {
			if (IConstants.ILE)
				e.printStackTrace();
		}
		return s;
	}

	private class MyWebViewClient extends WebViewClient {
		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			//ci.logit("Request Type",request.getUrl().toString());
			if (request.getUrl().toString().contains("500Cloud.png") || request.getUrl().toString().contains("404Cloud.png")) {
				//  mWebView.stopLoading();
				ci.saveToSharedPref(IConstants._GOTERROR, "Y");

			}
			if(request.getUrl().toString().contains("login.do?Username")){
				try {
					credUrl=URLDecoder.decode(String.valueOf(request.getUrl()), "UTF-8");
					AskToSaveCred=true;
				} catch (UnsupportedEncodingException e) {
					ci.logit("interceptRequest",e.getMessage());
				}
			}
			/*if(request.getUrl().toString().contains("https://www.facebook.com/connect/ping?")){
				//ci.logit("Request Type",request.getUrl().toString());
				String part[]= request.getUrl().toString().split("redirect_uri");
				ci.logit("final Url",part[1].substring(1));
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, view.getUrl());
				mContext.startActivity(Intent.createChooser(sharingIntent,"Choose App"));

			}*/
			return super.shouldInterceptRequest(view, request);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			loadWebViewJavascriptBridgeJs(view);
			/*	if (!url.toLowerCase().startsWith(IConstants._DEFAULT + IConstants._DT)) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				view.getContext().startActivity(intent);
				mWebView.stopLoading();
				return;
			} */
			//Browser.showLoading();
			isPageLoaded = false;
			//view.setVisibility(View.INVISIBLE);
			//String CookieV = ci.getFromSharedPref(IConstants._COOKIE);
			//ci.logit("COOKIES Stored", CookieV);
			ci.logit("URL onPageStart",url+"-Start");
			if ((url.toLowerCase().startsWith(IConstants._DFT + IConstants._DT))) {
				ci.logit("COOKIES if", "if");
				try {
					String cookies = CookieManager.getInstance().getCookie(url);
					ci.logit("COOKIES from Server", cookies+" -CK Value");
					if (cookies.length() > 1) {
						ci.saveCookie(cookies);
					}
					//CookieSyncManager.getInstance().sync();
					ci.logit("URL in webView", url);

				} catch (NullPointerException ne) {
					ci.logit("Null Occured", ne.getMessage());
				}
			}else{
				ci.logit("COOKIES if", "else");
			}

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			// Otherwise, the link is not for a page on my site, so launch
			// another Activity that handles URLs

			if (url.startsWith("http:") || url.startsWith("https:")) {
				return super.shouldOverrideUrlLoading(view, url);
				//return false;
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			view.getContext().startActivity(intent);
			return true;

			/*
			 * if(url.startsWith("mailto:")){ MailTo mt = MailTo.parse(url);
			 * Intent i = new Intent(Intent.ACTION_SEND);
			 * i.setType("text/plain"); i.putExtra(Intent.EXTRA_EMAIL, new
			 * String[]{mt.getTo()}); i.putExtra(Intent.EXTRA_SUBJECT,
			 * mt.getSubject()); i.putExtra(Intent.EXTRA_CC, mt.getCc());
			 * i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
			 * mContext.startActivity(i); view.reload(); return true; }
			 */

			/*
			 * view.loadUrl(url); return true;
			 */

		}

		@Override
		public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
			/*Browser browserContext = null;
			PopUpBrowser popupBrowserContext= null;
			AlertDialog.Builder builder = null ;
			try{
				browserContext = (Browser) mContext;
				builder = new AlertDialog.Builder(browserContext);
			}catch(ClassCastException cce){
				popupBrowserContext = (PopUpBrowser) mContext;
				builder = new AlertDialog.Builder(popupBrowserContext);
			}
			builder.setMessage(R.string.notification_error_ssl_cert_invalid);
			builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.proceed();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.cancel();
				}
			});
			AlertDialog ssldialog = builder.create();*//*
			ssldialog.show();*/
			CommonUI.showAlertDialogWithYesNoCallBack(mContext, "SSL certificate error", error.getUrl() + " ("+mContext.getResources().getString(R.string.notification_error_ssl_cert_invalid)+")", false, true, "Cancel", "Continue", new DialogCallBackAlert() {
				@Override
				public void dialogCallBackPositive(Dialog alertDialog) {
					alertDialog.dismiss();
					handler.cancel();
				}

				@Override
				public void dialogCallBackNagative(Dialog alertDialog) {
					alertDialog.dismiss();
					handler.proceed();
				}
			});
		}


		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
			super.onReceivedHttpError(view, request, errorResponse);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				ci.logit("Http Err",errorResponse.getReasonPhrase());
			}
		}

		@Override
		public void onPageFinished(final WebView webView, String url) {
			loadWebViewJavascriptBridgeJs(webView);
			ci.logit("Finished","Yes");
			//Browser.stopshowLoading();
			//webView.invalidate();
			//if (url.toLowerCase().startsWith(IConstants._DEFAULT + IConstants._DT))
			//	animate(webView);
			isPageLoaded = true;
			if (ci.getFromSharedPref(IConstants._GOTERROR).equals("Y")) {
				CommonUI.showAlertDialogWithYesNoCallBack(mContext, "WebPage Error", "There is a Error on this page. Do you want to go back?", false, true, "Yes", "Reload", new DialogCallBackAlert() {
					@Override
					public void dialogCallBackPositive(Dialog alertDialog) {

						if (mWebView.canGoBack())
							mWebView.goBack();
						else
							mWebView.loadUrl(IConstants._DFT + IConstants._DT + "/"
									+ IConstants._LSLP);
						ci.saveToSharedPref(IConstants._GOTERROR, "N");
						alertDialog.dismiss();
					}

					@Override
					public void dialogCallBackNagative(Dialog alertDialog) {
						mWebView.reload();
						alertDialog.dismiss();
					}
				});
			}
			//Browser.Logo.setVisibility(View.INVISIBLE);
			//webView.setVisibility(View.VISIBLE);
			//ci.logit(IConstants._LOGBRIDGE, "onPageFinished");
			Browser m = null;
			PopUpBrowser m2= null;
			try{
				m = (Browser) mContext;
			}catch(ClassCastException cce){
				ci.logit("CCE Error",cce.getMessage());
				m2 = (PopUpBrowser) mContext;
			}
			ci.logit("URL",url);
			if (!(url.toLowerCase().contains("login"))) {
				ci.logit("URL login",url);
				if (!(url.toLowerCase().contains("stuffit"))) {
					ci.logit("URL not stuffit", url);
					/*try {
						if (m != null)
							m.enableSwipe(false);
						else
							m2.enableSwipe(false);
					} catch (NullPointerException npe) {
						ci.logit("NPE Error", npe.getMessage());
					}*/
					//04282018:Commented above
					if ((url.toLowerCase().startsWith(IConstants._DFT + IConstants._DT))) {
						//ci.logit("URL To Save", url);
						ci.saveLastVisiedUrl(url);
					}
					if (AskToSaveCred) {
						AskToSaveCred = false;
						saveCredentials(credUrl);
					}
					/*if (openMatrix) {
						openMatrix = false;
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								webViewLoadJS("document.getElementsByClassName('plus-button')[0].click();");
								// webView.loadUrl("javascript:document.getElementsByClassName('plus-button')[0].click();");
							}
						}, 1000);

					}*/
				/*	if (WebViewJavascriptBridge.HideBackArrow) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								webViewLoadJS(hidebackarrow);
								// webView.evaluateJavascript(hidebackarrow,null);
								WebViewJavascriptBridge.HideBackArrow = false;
							}
						}, 500);
					}*/
				}
			} else if ((url.toLowerCase().startsWith(IConstants._DFT + IConstants._DT))) {
				webViewLoadJS("javascript:document.getElementsByName('Username')[0].value = '"+ci.getFromSharedPref(IConstants._UN)+"'; javascript:document.getElementsByName('Password')[0].value = '"+ci.getFromSharedPref(IConstants._PWD)+"';");
				//WebViewJavascriptBridge.HideBackArrow = true;
				try{
					if(m!=null)
						m.enableSwipe(false);
					else
						m2.enableSwipe(false);
				}catch (NullPointerException npe){
					ci.logit("NPE Error",npe.getMessage());
				}
			}


		}

		@Override
		public void onReceivedError(WebView view, final int errorCode, final String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			view.loadUrl("file:///android_asset/error.html");
			//Browser.stopshowLoading();
			loadWebViewJavascriptBridgeJs(view);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					WebViewJavascriptBridge.this.callHandler("Error",description+", Error Code is : "+errorCode);
					ci.logit("Recieved Error",description);
				}
			},1000);

			//ci.showAlert(description + " Error Code is : " + errorCode, "Connection Error", "OK");
			isPageLoaded = false;

		}

	}

	private void saveCredentials(String url) {
		//String url=requesturl;
		// http://livestuff.com/login.do?Username=jkhatri%40livestuff.com&Password=livetech1&Redirect=%2Fjkhatri%2Findex.mhtml
		try {
			final String u[] = url.split(String.valueOf("Username="));
			final String p[] = u[1].split(String.valueOf("&Password="));
			final String r[] = p[1].split(String.valueOf("&Redirect="));
			if(!(ci.getFromSharedPref(IConstants._UN).equalsIgnoreCase(p[0])&&ci.getFromSharedPref(IConstants._PWD).equalsIgnoreCase(r[0]))) {
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						d = new Dialog(mContext);
						d.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								ci.saveToSharedPref(IConstants._UN, "");
								ci.saveToSharedPref(IConstants._PWD, "");
							}
						});
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setContentView(R.layout.simple_alert);
						tvmsg = (TextView) d.findViewById(R.id.tvmsg);
						tvtitle = (TextView) d.findViewById(R.id.tvtitle);
						ok = (Button) d.findViewById(R.id.btok);
						tvmsg.setText(String.format("Save Password for %s" , p[0]));
						tvtitle.setText(R.string.RMP);
						ok.setText(R.string.YES);
						ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								ci.saveToSharedPref(IConstants._UN, p[0]);
								ci.saveToSharedPref(IConstants._PWD, r[0]);
								d.dismiss();
							}

						});
						d.show();
					}
				});
			}

		}catch (Exception e){
			ci.logit("Error",e.getMessage());
		}
	}

	/*private void animate(final WebView view) {
		if (Browser.isNavigatingBack) {
			Browser.isNavigatingBack = false;
			Animation anim = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
			view.startAnimation(anim);

		} else {
			Animation anim = AnimationUtils.loadAnimation(mContext, R.animator.slideinright);
			view.startAnimation(anim);

		}
	}*/

	public interface WVJBHandler {
		void handle(String data, WVJBResponseCallback jsCallback);
	}

	public interface WVJBResponseCallback {
		void callback(String data);
	}

	@JavascriptInterface
	public void registerHandler(String handlerName, WVJBHandler handler) {
		_messageHandlers.put(handlerName, handler);
	}

	private class CallbackJs implements WVJBResponseCallback {
		private final String callbackIdJs;

		private CallbackJs(String callbackIdJs) {
			this.callbackIdJs = callbackIdJs;
		}

		@Override
		public void callback(String data) {
			_callbackJs(callbackIdJs, data);
		}
	}

	@JavascriptInterface
	private void _callbackJs(String callbackIdJs, String data) {
		Map<String, String> message = new HashMap<>();
		message.put("responseId", callbackIdJs);
		message.put("responseData", data);
		_dispatchMessage(message);
	}

	@JavascriptInterface
	public void _handleMessageFromJs(final String data, String responseId, String responseData, String callbackId,
									 String handlerName) {
		if (null != responseId) {

			WVJBResponseCallback responseCallback = _responseCallbacks.get(responseId);
			responseCallback.callback(responseData);
			_responseCallbacks.remove(responseId);
		} else {

			if (null != callbackId) {
				responseCallback = new CallbackJs(callbackId);
			}

			if (null != handlerName) {
				handler = _messageHandlers.get(handlerName);
				if (null == handler) {
					ci.logit(IConstants._LOGBRIDGE, "WVJB Warning: No handler for " + handlerName);

					return;
				}
			} else {
				handler = _messageHandler;
			}
			try {
				mContext.runOnUiThread(new Runnable(){

					@Override public void run() {
						handler.handle(data, responseCallback);

					} });

			} catch (Exception exception) {
				ci.logit(IConstants._LOGBRIDGE, "WebViewJavascriptBridge: WARNING: java handler threw. " + exception);
			}
		}
	}

	@JavascriptInterface public void send(String data) {
		send(data, null);
	}

	@JavascriptInterface
	public void send(String data, WVJBResponseCallback responseCallback) {
		_sendData(data, responseCallback, null);
	}

	@JavascriptInterface
	private void _sendData(String data, WVJBResponseCallback responseCallback, String handlerName) {
		Map<String, String> message = new HashMap<>();
		message.put("data", data);
		if (null != responseCallback) {
			String callbackId = "java_cb_" + (++_uniqueId);
			_responseCallbacks.put(callbackId, responseCallback);
			message.put("callbackId", callbackId);
		}
		if (null != handlerName) {
			message.put("handlerName", handlerName);
		}
		_dispatchMessage(message);
	}

	@JavascriptInterface
	private void _dispatchMessage(Map<String, String> message) {
		final String messageJSON = new JSONObject(message).toString();
		ci.logit(IConstants._LOGBRIDGE, "sending:" + messageJSON);
		new DisPatchMessageTask(this,messageJSON).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		/*new AsyncTask<Void,Void,String>(){
			@Override
			protected String doInBackground(Void... params) {
				final String javascriptCommand = String.format(
						"javascript:WebViewJavascriptBridge._handleMessageFromJava('%s');", doubleEscapeString(messageJSON));
				return javascriptCommand;
			}

			@Override
			protected void onPostExecute(final String s) {
				super.onPostExecute(s);
				mWebView.loadUrl(s);
				*//*	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});*//*
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
		/*
		final String javascriptCommand = String.format(
				"javascript:WebViewJavascriptBridge._handleMessageFromJava('%s');", doubleEscapeString(messageJSON));
		mContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mWebView.loadUrl(javascriptCommand);
			}
		});*/
	}

	private static class DisPatchMessageTask extends AsyncTask<Void, Void, String> {

		private WeakReference<WebViewJavascriptBridge> activityReference;
		private String messageJSON;

		// only retain a weak reference to the activity
		DisPatchMessageTask(WebViewJavascriptBridge context,String msg) {
			activityReference = new WeakReference<>(context);
			messageJSON = msg;
		}

		@Override
		protected String doInBackground(Void... params) {
			if(activityReference.get() ==null)
			{ cancel(true);
				return "";
			}
			return String.format(
					"javascript:WebViewJavascriptBridge._handleMessageFromJava('%s');", activityReference.get().doubleEscapeString(messageJSON));
		}

		@Override
		protected void onPostExecute(String s) {
			WebViewJavascriptBridge activity = activityReference.get();
			if (activity == null) return;
			activity.mWebView.loadUrl(s);
		}
	}

	@JavascriptInterface
	public void callHandler(String handlerName) {
		callHandler(handlerName, null, null);
	}

	@JavascriptInterface
	public void callHandler(String handlerName, String data) {
		callHandler(handlerName, data, null);
	}

	@JavascriptInterface
	public void callHandler(String handlerName, String data, WVJBResponseCallback responseCallback) {
		_sendData(data, responseCallback, handlerName);
	}

	/*
	 * you must escape the char \ and char ", or you will not recevie a correct
	 * json object in your javascript which will cause a exception in chrome.
	 *
	 * please check this and you will know why.
	 * http://stackoverflow.com/questions/5569794/escape-nsstring-for-javascript
	 * -input http://www.json.org/
	 */
	@JavascriptInterface
	private String doubleEscapeString(String javascript) {
		String result;
		result = javascript.replace("\\", "\\\\");
		result = result.replace("\"", "\\\"");
		result = result.replace("\'", "\\\'");
		result = result.replace("\n", "\\n");
		result = result.replace("\r", "\\r");
		result = result.replace("\f", "\\f");
		return result;
	}

	private void webViewLoadJS(String js) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			mWebView.evaluateJavascript(js, null);
		} else {
			mWebView.loadUrl("javascript: " + js);
		}
	}
}
