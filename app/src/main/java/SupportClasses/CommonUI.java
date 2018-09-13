package SupportClasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import com.liveplatform.qalivestuff.Browser;
import com.liveplatform.qalivestuff.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Data.IConstants;

public class CommonUI {

	private Context ct;
	private static Dialog d;
	private static Dialog alertDialog;
	private TextView tvmsg, tvtitle;
	private Button ok;
	public static String audiopath;
	private SharedPreferences settings;

	public CommonUI(Context context) {
		this.ct = context;
		d = new Dialog(ct);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.simple_alert);
		tvmsg = (TextView) d.findViewById(R.id.tvmsg);
		tvtitle = (TextView) d.findViewById(R.id.tvtitle);
		ok = (Button) d.findViewById(R.id.btok);
		settings = context.getSharedPreferences(IConstants._PN, Context.MODE_PRIVATE);
	}

	public void showAlert(final String message, final String title, final String Btntxt) {
		/*
		 * if (d.isShowing()) { d.dismiss(); d=null; d = new Dialog(ct);
		 * d.requestWindowFeature(Window.FEATURE_NO_TITLE); }
		 */
		((Activity) ct).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!CommonUI.d.isShowing()) {
					tvmsg.setText(message);
					tvtitle.setText(title);
					ok.setText(Btntxt);
					ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CommonUI.d.dismiss();
						}

					});
					CommonUI.d.show();
				}

			}
		});

	}

	/*
	 */
	/* show the selected or clicked image in dialog wether to keep or not. *//*

	public void displayactivityResult(final MyActivity ma, final Bitmap bm, String title, final String ChoosenTye,
			final String uriInString) {
		logit("Gallery 3", title + " Bit" + bm.toString() + ChoosenTye);
		final Dialog nd = new Dialog(ma);
		nd.setTitle(title);
		nd.setContentView(R.layout.imageshow);
		ImageView iv = (ImageView) nd.findViewById(R.id.srcImg);
		iv.setImageBitmap(bm);
		Button save = (Button) nd.findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						MyActivity.showLoading();
					};

					@Override
					protected Void doInBackground(Void... voids) {
						ma.tempcallback.callback(menuResData(ChoosenTye, encodeTobase64(bm)));

						return null;
					}

					protected void onPostExecute(Void result) {
						MyActivity.stopshowLoading();
					};
				}.execute();

				// tempcallback.callback("Type: " + ChoosenTye + ", " + "Data: "
				// + uriInString);
				nd.dismiss();
			}

		});
		Button dis = (Button) nd.findViewById(R.id.dis);
		dis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nd.dismiss();
				ma.tempcallback.callback(errorMSG(true, "User Canceled : " + ChoosenTye));
				MyActivity.stopshowLoading();
			}
		});
		nd.show();
	}
*/
	/*

	 */
	/* opens the mobile device gallery to choose a image from. *//*

	public void goGallery(MyActivity ma) {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		ma.startActivityForResult(galleryIntent, IConstants.GALREQ);
	}

	*/
	/* displays file manager to choose any file from. */

	/*public void showfilechooser(Browser ma) {
		Intent intent = new Intent();
		intent.setType("file*//*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		ma.startActivityForResult(Intent.createChooser(intent, "Choose File"), IConstants.FILEREQ);
	}*/


	/* saving DRI in local prefrences of app. */
/*	public void saveDRIforfuture(String DRI) {
		Editor edit = settings.edit();
		if (null != DRI) {
			edit.putString(IConstants._DRI, DRI);
			edit.commit();
		}
	}*/

	public boolean appInstalledOrNot(String uri) {
		PackageManager pm = ct.getPackageManager();
		boolean app_installed;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	public void saveLastVisiedUrl(String URL) {
		Editor edit = settings.edit();
		if (null != URL) {
			edit.putString(IConstants._LURL, URL);
			edit.apply();
		}
	}

	public void saveCookie(@NonNull String cookie) {
		Editor edit = settings.edit();
		edit.putString(IConstants._CK, cookie);
		edit.apply();
	}

	/* request for android camera to take picture and return back. *//*
	public void showAndroidCamera(MyActivity ma) {
		File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IConstants._FOLDERNAME);
		if (!imageStorageDir.exists()) {
			imageStorageDir.mkdirs();
		}
		File file = new File(
				imageStorageDir + File.separator + "IMG" + String.valueOf(System.currentTimeMillis()) + ".jpg");
		Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		MyActivity.picUri = Uri.fromFile(file);
		ma.startActivityForResult(cameraIntent, IConstants.CAMREQ);
	}
*/
	public void logit(String TAG, String text) {
		try {
			if (IConstants.ILE)
				Log.e(TAG, text);
		} catch (NullPointerException ne) {
			Log.e(TAG, ne.getMessage());
		}

	}

	public String getMimeType(String url) {
		String type = null;
		//Improvde below logic with Regular Expression
		url = url.replaceAll(" ","").trim();
		url = url.replaceAll("!","");
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
		}
		if(type!=null)Log.i("getMimeType",type);
		return type;
	}


	/*
	 * for check of qr/bar code reader returns true if contains only numeric-
	 * data as bar code else its an qr code.
	 */
	/*public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}*/

	/* used to send response data in callback to JS. */
	public String menuResData(String type, String resp,String url) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss", Locale.ENGLISH);
		String currentDateandTime = sdf.format(new Date());
		JSONObject r = new JSONObject();
		try {
			//
			r.accumulate("filetype", type);
			if(type.equals("mp3"))
				r.accumulate("typevalue",resp);
			else if(type.equals("AddONtext"))
				r.accumulate("typevalue",resp);
			else if(type.equals(IConstants.BC)||type.equals(IConstants.QRC))
				r.accumulate("typevalue",resp);
			else
				r.accumulate("typevalue","data:image/"+type+";base64,"+resp);
			if(!(url.equals("")))
				//r.accumulate("_url", url);
				r.accumulate("Time_Stamp", currentDateandTime);
			return r.toString();
		} catch (JSONException e) {
			if (IConstants.ILE)
				e.printStackTrace();
			return "";
		}
	}

	/* used to send error in callback in predefined format to JS. */
	public String errorMSG(Boolean isError, String ErrorMsg) {
		JSONObject r = new JSONObject();
		try {
			r.accumulate("error", isError);
			r.accumulate("error_msg", ErrorMsg);
			return r.toString();
		} catch (JSONException e) {
			if (IConstants.ILE)
				e.printStackTrace();
			return "";
		}
	}

	/* used for conversion of image to base64 */
	public static String encodeTobase64(Bitmap image,Bitmap.CompressFormat f) {
		//Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		image.compress(f, 100, baos);
		byte[] b = baos.toByteArray();
		//String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		// imageEncoded="data:image/png;base64,"+imageEncoded;
		// imageEncoded="data:image/jpeg;base64,"+imageEncoded;
		/*if (IConstants.ISLOGENABLED)
			Log.e("LOOK IMG DATA", imageEncoded);
		*/
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/* used for conversion of base64 to image */
	/*public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static byte[] converttoBlob(Bitmap yourBitmap) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		yourBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] bArray = bos.toByteArray();
		return bArray;

	}*/

	/*
	 * public void showPrefrence() { final RadioGroup rgPref; final RadioButton
	 * rbQa, rbDev, rbLive; Button savePref; final Dialog d = new Dialog(ct);
	 * d.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * d.setContentView(R.layout.prefrence); rgPref = (RadioGroup)
	 * d.findViewById(R.id.rgWorkOn); rbQa = (RadioButton)
	 * d.findViewById(R.id.rbQa); rbDev = (RadioButton)
	 * d.findViewById(R.id.rbDev); rbLive = (RadioButton)
	 * d.findViewById(R.id.rbLive); savePref = (Button)
	 * d.findViewById(R.id.btSavePref); if
	 * (getFromSharedPref(IConstants._PREOFDOMAINNAMe).equals(IConstants._DEV))
	 * { rbDev.setSelected(true); } else if
	 * (getFromSharedPref(IConstants._PREOFDOMAINNAMe).equals(IConstants._QA)) {
	 * rbQa.setSelected(true); } else if
	 * (getFromSharedPref(IConstants._PREOFDOMAINNAMe).equals(IConstants._LIVE))
	 * { rbLive.setSelected(true); }
	 *
	 * savePref.setOnClickListener(new OnClickListener() {
	 *
	 * @Override public void onClick(View v) {
	 *
	 * switch (rgPref.getCheckedRadioButtonId()) { case R.id.rbDev:
	 * Toast.makeText(ct, "Dev", Toast.LENGTH_SHORT).show();
	 * saveToSharedPref(IConstants._DEFAULT, IConstants._DEV); Restart(); break;
	 * case R.id.rbQa: Toast.makeText(ct, "QA", Toast.LENGTH_SHORT).show();
	 * saveToSharedPref(IConstants._DEFAULT, IConstants._QA); Restart(); break;
	 * case R.id.rbLive: Toast.makeText(ct, "LIVE", Toast.LENGTH_SHORT).show();
	 * saveToSharedPref(IConstants._DEFAULT, IConstants._LIVE); Restart();
	 * break; default: Toast.makeText(ct, "Nothing To Save",
	 * Toast.LENGTH_SHORT).show(); saveToSharedPref(IConstants._DEFAULT,
	 * IConstants._DEV); break; } d.cancel(); } }); if (!d.isShowing())
	 * d.show();
	 *
	 * }
	 */

	public void saveToSharedPref(String Key, String data) {
		Editor edit = settings.edit();
		if (null != data) {
			edit.putString(Key, data);
			edit.apply();
		}
	}

	public String getFromSharedPref(String Key) {
		return settings.getString(Key, "");
	}

	/*public void Restart() {
		Activity act = (Activity) ct;
		Browser ma = (Browser) act;
		ma.recreate();
	}
*/
	/*public void RunAjaxCall(String _url, String _method, String _type) {
		AQuery q =new AQuery(ct);
	}*/
	/*
	 * public void showExitAlert(final MyActivity ma) { final Dialog d=new
	 * Dialog(ct); d.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * d.setContentView(R.layout.exit_alert); d.setCancelable(false); Button
	 * yes,no; yes=(Button)d.findViewById(R.id.btyes);
	 * no=(Button)d.findViewById(R.id.btno); yes.setOnClickListener(new
	 * OnClickListener() {
	 *
	 * @Override public void onClick(View v) { d.dismiss(); ma.closeApp(); } });
	 * no.setOnClickListener(new OnClickListener() {
	 *
	 * @Override public void onClick(View v) { d.dismiss(); } }); d.show(); }
	 */
//	public static void scheduleTestAlarmReceiver(Context context) {
//		Intent receiverIntent = new Intent(context, AppReceiver.class);
//		PendingIntent sender = PendingIntent.getBroadcast(context, 123456789,  receiverIntent, 0);
//
//		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000, 5000, sender);
//	}

	public static void filterByPackageName(Context context, Intent intent, String prefix) {
		List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
		for (ResolveInfo info : matches) {
			if (info.activityInfo.packageName.toLowerCase().startsWith(prefix)) {
				intent.setPackage(info.activityInfo.packageName);
				//return;
			}
		}
		context.startActivity(intent);
	}
	public static void showAlertDialogWithYesNoCallBack(Context context, String title,
														String message, Boolean isCancelable,Boolean showCancel,String positiveButtonTxt,
														String negativeButtonTxt, final DialogCallBackAlert callback) {
		if(alertDialog!=null) {
			alertDialog.dismiss();
		}
		TextView tvmsg,tvtitle;
		Button ok,cancel;
		alertDialog = new Dialog(context);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.alert_theme);

		tvmsg = (TextView) alertDialog.findViewById(R.id.tvmsg);
		tvtitle = (TextView) alertDialog.findViewById(R.id.tvtitle);
		ok = (Button) alertDialog.findViewById(R.id.btok);
		cancel = (Button) alertDialog.findViewById(R.id.btcan);
		Log.e("UI","inside");
		if(!showCancel)	cancel.setVisibility(View.GONE);

		alertDialog.setCancelable(isCancelable);
		alertDialog.setCanceledOnTouchOutside(isCancelable);
		// Setting Dialog Title
		tvtitle.setText(title);
		// Setting Dialog Message
		tvmsg.setText(message);
		ok.setText(positiveButtonTxt);
		cancel.setText(negativeButtonTxt);

		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.dialogCallBackPositive(alertDialog);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.dialogCallBackNagative(alertDialog);
			}
		});
		alertDialog.show();

	}

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			Log.e("URL Encode", "UTF-8 should always be supported", e);
			return "";
		}
	}
	public void showNotification(String Title,String message){
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ct)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle(Title)
						.setContentText(message);
// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(ct, Browser.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ct);
// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(Browser.class);
// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager)ct. getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}
}
