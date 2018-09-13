package Connection;


/**
 * Created by devanshu.kanik on 12/23/2015.
 */
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;



public class ConnectionDetector {

	private Context _context;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	/**
	 * Checking for all possible internet providers
	 **/


	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = connectivity.getAllNetworks();
			NetworkInfo networkInfo;
			for (Network mNetwork : networks) {
				networkInfo = connectivity.getNetworkInfo(mNetwork);
				if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
					return true;
				}
			}
		}else {
			if (connectivity != null) {
				//noinspection deprecation
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (NetworkInfo anInfo : info) {
						if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
