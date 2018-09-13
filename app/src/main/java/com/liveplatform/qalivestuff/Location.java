package com.liveplatform.qalivestuff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import Data.IConstants;
import SupportClasses.CommonUI;
import SupportClasses.DialogCallBackAlert;
import SupportClasses.Loader;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Location extends Activity implements OnMapReadyCallback, LocationListener, OnMapClickListener, OnInfoWindowClickListener, OnMarkerDragListener {
	/* all Views attached with java codes */
	@BindView(R.id.imbtndoneLoc)
	ImageButton imbtndoneLoc;
	/* variables to be used by this class */
	private CommonUI ci;
	private Loader load;
	private GoogleMap map = null;
	private getAdd ga;
	private Boolean isGetAdd = false;
	private Boolean isAddEXC = false;
	private LocationManager locationManager;
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;
	private Intent returnresp;
	private String ReturnResult = null;
	public static LatLng ReturnLatLong = null;
	private static final int GPS_ENABLE_REQUEST = 21;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		returnresp = this.getIntent();
		ButterKnife.bind(this);
		ci = new CommonUI(Location.this);
		load = new Loader(Location.this);
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


		if (Build.VERSION.SDK_INT >= 23) {
			// Here, thisActivity is the current activity
			if (ContextCompat.checkSelfPermission(Location.this,
					Manifest.permission.ACCESS_FINE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED) {

				// Should we show an explanation?
				if (ActivityCompat.shouldShowRequestPermissionRationale(Location.this,
						Manifest.permission.ACCESS_FINE_LOCATION)) {

				} else {

					ActivityCompat.requestPermissions(Location.this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							IConstants.MY_PERMISSIONS_REQUEST_READ_LOCATION);

				}
			} else {
				ActivityCompat.requestPermissions(Location.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						IConstants.MY_PERMISSIONS_REQUEST_READ_LOCATION);
			}
		} else {
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ci.showNotification("Location", "No permission to access Location");
				ci.showAlert("Please Grant permission to access Location in settings", "Location Access", "OK");

				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); // You

		}


		// can
		// also
		// use
		// LocationManager.GPS_PROVIDER
		// and
		// LocationManager.PASSIVE_PROVIDER


	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case IConstants.MY_PERMISSIONS_REQUEST_READ_LOCATION:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					//BJ
					int locationMode = 0;
					try {
						locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
						Log.i("BhavyaLocation_mode: ",":"+locationMode);
					} catch (Settings.SettingNotFoundException e) {
						e.printStackTrace();
					}
					if(Build.VERSION.SDK_INT >=24 && (locationMode < 2)){
						ci.showAlertDialogWithYesNoCallBack(Location.this, "GPS Disabled",
								"GPS is disabled,in order to use the application properly you need to enable GPS of your device",
								true, true, "Enable GPS", "Search Manually", new DialogCallBackAlert() {
									@Override
									public void dialogCallBackPositive(Dialog alertDialog) {
										startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
										alertDialog.dismiss();
									}
									@Override
									public void dialogCallBackNagative(Dialog alertDialog) {
										alertDialog.dismiss();
									}
								});
					} else{
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); // You
					}
				} else {
					ci.showNotification("Location", "Permission Denied");
					ci.showAlert("Permission Denied by the User", "Location Access", "OK");
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GPS_ENABLE_REQUEST){
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); // You
		}
	}

	/* this method will automatically gets called when google map loaded */
	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		// map.setMyLocationEnabled(true);
		map.setOnMapClickListener(this);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerDragListener(this);
		map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				LinearLayout info = new LinearLayout(getApplicationContext());
				info.setOrientation(LinearLayout.VERTICAL);
				info.setBackgroundColor(Color.WHITE);
				TextView title = new TextView(getApplicationContext());
				title.setTextColor(Color.parseColor("#004785"));
				title.setGravity(Gravity.CENTER);
				title.setTypeface(null, Typeface.BOLD);
				title.setText(marker.getTitle());

				TextView snippet = new TextView(getApplicationContext());
				snippet.setTextColor(Color.parseColor("#004785"));
				snippet.setTextSize(10);
				snippet.setPadding(4, 2, 4, 2);
				snippet.setText(marker.getSnippet());

				info.addView(title);
				info.addView(snippet);

				return info;
			}
		});
	}

	/* this method will update the current location if location is changed */
	@Override
	public void onLocationChanged(android.location.Location location) {
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		if (latLng.latitude == 0) {
			Toast.makeText(getApplicationContext(), "Searching", Toast.LENGTH_SHORT).show();
		} else {
			// CameraUpdate cameraUpdate =
			// CameraUpdateFactory.newLatLngZoom(latLng, 13);
			if (map != null) {
				setMarker(latLng);
			}
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	/* this method will gets called when ever a click event occured on map */
	@Override
	public void onMapClick(LatLng latLng) {
		setMarker(latLng);
	}

	private void setMarker(LatLng latLng) {
		// TODO Auto-generated method stub
		MarkerOptions markerOptions = new MarkerOptions();
		// Setting the position for the marker
		markerOptions.position(latLng);
		ReturnLatLong = latLng;
		markerOptions.title(formatUptoTwoPlace(latLng.latitude) + " : " + formatUptoTwoPlace(latLng.longitude));
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint));
		markerOptions.snippet("Searching Location...");
		// Clears the previously touched position
		map.clear();
		// Animating to the touched position
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
		Marker mark = map.addMarker(markerOptions);
		mark.setDraggable(true);
		if (ga != null) {
			ga.cancel(true);
			ga = null;
		}
		ga = new getAdd(latLng, mark);
		ga.execute();
	}

	/*
	 * this method will return the lat long value with only upto two decimal
	 * place
	 */
	private String formatUptoTwoPlace(Double d) {
		DecimalFormat sf = new DecimalFormat("00.00");
		return sf.format(d);
	}

	/* method will returns the data from the lat long passed to it */
	public static String getStringFromLocation(double lat, double lng)
			throws ClientProtocolException, IOException, JSONException {
		String ret = null;
		String address = String.format(Locale.ENGLISH,
				"https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyA2bmrFXp0ag3q_voAwQLXhpWaV--U8MyE&latlng=%1$f,%2$f&sensor=true&language="
						+ Locale.getDefault().getCountry(),
				lat, lng);
		//IN CASE ABOVE DOES NOT WORK.
		/*String address = String.format(Locale.ENGLISH,
				"https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyA2bmrFXp0ag3q_voAwQLXhpWaV--U8MyE&latlng=%1$f,%2$f&sensor=true&language="
						+ Locale.getDefault().getCountry(),
				lat, lng);*/
		HttpGet httpGet = new HttpGet(address);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		int b;
		while ((b = stream.read()) != -1) {
			stringBuilder.append((char) b);
		}

		JSONObject jsonObject = new JSONObject(stringBuilder.toString());

		if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
			JSONArray results = jsonObject.getJSONArray("results");
			JSONObject first = results.getJSONObject(0);
			ret = first.getString("formatted_address");
			if (IConstants.ILE)
				Log.e("first", first.toString());

		}

		return ret;
	}

	/*
	 * this is a sub class which runs the getAddressFromLocation method in
	 * background
	 */
	public class getAdd extends AsyncTask<String, String, String> {
		LatLng latLng;
		Marker m;

		public getAdd(LatLng ll, Marker mark) {
			this.latLng = ll;
			this.m = mark;
		}

		@Override
		protected void onPreExecute() {
			imbtndoneLoc.setVisibility(View.GONE);
			load.showLoader(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			if (IConstants.ILE)
				Log.e("lat long", latLng.latitude + " " + latLng.longitude);
			String result = null;
			try {
				result = getStringFromLocation(latLng.latitude, latLng.longitude);
			} catch (ClientProtocolException e) {
				if (IConstants.ILE)
					e.printStackTrace();
			} catch (IOException e) {
				if (IConstants.ILE)
					e.printStackTrace();
			} catch (JSONException e) {
				if (IConstants.ILE)
					e.printStackTrace();
			} // searchAdd(latLng);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			load.showLoader(false);
			if (null == result) {
				ci.showAlert("Can't get Address !!!", "Connection Error", "OK");
				m.hideInfoWindow();
				m.setSnippet("No Address");
				isGetAdd = false;
				ReturnResult = null;
				imbtndoneLoc.setVisibility(View.GONE);
			} else {
				if (isAddEXC) {
					isGetAdd = false;
					isAddEXC = false;
					ReturnResult = null;
					m.setSnippet("Unable To get Address\n Close and Try Again");
					m.showInfoWindow();
					imbtndoneLoc.setVisibility(View.GONE);
					return;

				}
				ci.logit("Loc", result);
				isGetAdd = true;
				ReturnResult = result;
				String t = result;
				String[] snt = t.split(",");

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < snt.length; i++) {
					sb.append(snt[i]);
					if (!(i == snt.length - 1))
						sb.append("\n");
					else
						sb.append(".");
				}

				m.setSnippet(sb.toString());
				m.showInfoWindow();
				imbtndoneLoc.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}

	}

	/*	*//*
	 * this method will used to put the response data back to the calling class
	 *//*
	private void returnresultToIntent(String data) {
		if (isGetAdd) {
			if (IConstants.ILE)
				Log.e(IConstants._LOGAPPNAME, data);
			returnresp.putExtra(IConstants._RES,
					data + " " + ReturnLatLong.latitude + ", " + ReturnLatLong.longitude);
			this.setResult(Activity.RESULT_OK, returnresp);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Please Select a valid location", Toast.LENGTH_SHORT).show();
		}
	}*/

	/*
	 * this method will used to trigger another method which will put response
	 * data in returning class
	 */
	@OnClick(R.id.imbtndoneLoc)
	public void onClick(View v) {
		/*ci.showAlert("Coming Soon...","Location","OK");*/
		//BJ 07/28/2017: Commented above and updated below
		DSA.RT = IConstants.LOC;
		DSA.AddOn = true;
		Intent intentlocation = new Intent(this, PopUpBrowser.class);
		intentlocation.putExtra(IConstants.WM, true);
		intentlocation.putExtra(IConstants.TY, IConstants.LOC);
		if(ReturnLatLong != null) {
			intentlocation.putExtra(IConstants._LAT, ReturnLatLong.latitude);
			intentlocation.putExtra(IConstants._LONG, ReturnLatLong.longitude);
			PopUpBrowser.multiop = true;
			intentlocation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentlocation);
			finish();
		}
		/*returnresultToIntent(ReturnResult);*/
	}




	@Override
	public void onInfoWindowClick(Marker arg0) {
		arg0.hideInfoWindow();
	}

	@Override
	public void onMarkerDrag(final Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		LatLng tl = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
		map.animateCamera(CameraUpdateFactory.newLatLng(tl));
		if (ga != null) {
			ga.cancel(true);
			ga = null;
		}
		ga = new getAdd(tl, arg0);
		ga.execute();
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		imbtndoneLoc.setVisibility(View.GONE);
	}


}