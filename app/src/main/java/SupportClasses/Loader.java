package SupportClasses;

import com.liveplatform.qalivestuff.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.view.Window;
import android.webkit.WebView;

public class Loader {
	Context context;
	Dialog d;
	WebView loadinggif;
	CommonUI ci;

	public Loader(Context ct) {
		this.context = ct;
		d = new Dialog(ct, R.style.transparentdialog);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.live_stuff_loader);
		// d.setCancelable(false);
		d.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				showLoader(false);
			}
		});
		// d.getWindow().setBackgroundDrawable(new
		// ColorDrawable(android.graphics.Color.TRANSPARENT));
		loadinggif = (WebView) d.findViewById(R.id.loadingViewoverlay);
		loadinggif.loadDataWithBaseURL("file:///android_asset/", "<img src='loading_animation.gif' />",
				"text/html", "utf-8", null);
		loadinggif.setBackgroundColor(Color.TRANSPARENT);
	}

	public void showLoader(Boolean show) {
		if (show) {
			if (d.isShowing())
				d.cancel();
			d.show();
		} else {
			if (d.isShowing())
				d.cancel();
			d.dismiss();
		}
	}
}
