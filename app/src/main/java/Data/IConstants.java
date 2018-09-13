package Data;

import BridgeJavaClass.WebViewJavascriptBridge;

/**
 * Created by devanshu.kanik on 12/23/2015. this class contains all the
 * variables used in any classes directly for avoiding misspelling the words
 */
public class IConstants {
	// Person Infor.
	public static final String _PN = "LiveStuffAndroidAppPrefrenceNEW";
	//public static final String _DRI = "DriName";
	public static final String _CK = "cookie";
	public static final String _LURL = "lastUrl";
	//public static final String _LASTDEEPLINK = "lastDeepLink";
	public static final String _UN = "Username";
	public static final String _PWD = "Password";

	// Calling Activity Request Code
	public static final int FILEREQ = 13;
	public static final int CAMREQ = 11;
	public static final int SCANREQ = 14;
	public static final int LOCREQ = 16;
	public static final int MY_PERMISSIONS_REQUEST_CAMERA = 17;
	public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 18;
	public static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 19;
	public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE  = 20;


	// Bridge CallType
	public static final String _AOC = "NativeAddOn";
	public static final String _TNB = "triggerNativeBack";
	public static final String _GSI ="GetSelectedImage";
	public static final String _CS = "callSession";
	public static final String _LO = "logoutinNative";
	public static final String _SE ="swipeRefreshEnable";
	public static final String _DS ="disableScrollCheck";
	public static final String _SHI ="shareit";
	public static final String _QR = "qrbarcode";
	public static final String _CAM = "Camera";

	public static final String _DT = "livestuff.com";

	// LiveStuff Folder Name
	public static final String _FN = "LiveStuff";

	// LiveStuff Domain Prefix
/*	public static final String _QA = "http://qa.";
	public static final String _DEV = "http://dev.";
	public static final String _LIVE = "http://";*/
	public static final String _DFT = "https://qa.";
	public static final String _ALAT = "&latitude=";
	public static final String _ALONG = "&longitude=";
	//BJ 08/01/2017:Original http://qa.
	// URL
	//public static final String _LOCALASSETS = "file:///android_asset/";
	public static final String _LSEXT = "/index.mhtml";
	public static final String _LSS1 = "/StuffIt.mhtml?Action=";
	public static final String _LSSI1AND = "/StuffIt.mhtml&Action=";
	public static final String _LSS2 = "&Destination=";
	public static final String _LSLP = "Login.mhtml";
	public static final String _LPP = "?Redirect=";
	//public static final String _LIVESTUFFTWOLOGINPAGEWITHREDIRECT = "Login.mhtml?Redirect=";
	public static final String _LSGSI = "/GetSessionInfo.json";
	public static final String _LSSC = "/Sessionclaim.do?Token=";

	//URL
	//public static final String _LIVESTUFFTWOPAGEEXTMOB = "mhtml";
	//public static final String _LIVESTUFFTWOPAGEEXTTAB = "thtml";

	// Audio File Name
	public static final String _AFN = "/liveStuffAudio";// 3gpp
	public static final String _AFNEXT = ".3gpp";

	// Log Tag
	public static final String _LOGBRIDGE = "Bridge message";
	public static final String _LOGAPPNAME = "Live Stuff";
	public static final String _LOGERROR = "Error";

	// User Agent String for WebView
	public static final String _USERAGENT = "LiveStuff Android App";//OLD: AndroidAppNativeLiveStuff

	// Responses
	public static final String _SUCCESS = "Success";
	public static final String _FAILURE = "Failed";
	public static final String _OK = "OK";
	public static final String _FROMOUTSIDE = "from outside";
	//public static final String _DATATOADD = "data from outside";

	// Response from other activities
	public static final String _RES = "Done";
	public static final String _GOTERROR = "erroroccured";

	// Widget
	public static final String WM = "OpenMatrixPage";
	public static final String TY = "type";
	public static final String AML = "AddItem";
	public static final String AQK = "AddCopy";
	//public static final String AF = "AddFile";
	//05/05/2018:Comented above and updated below
	public static final String AF = "StuffImage";
	public static final String ANC = "AddCloud";
	public static final String ACAM = "Camera";
	public static final String AQR = "qrbar";
	public static final String ALOC = "Loc";
	public static final String AM = "GetStuffItOptions";
	public static final String SHURL = "StuffUrl";
	public static final String BC = "barcode";
	public static final String QRC = "qrcode";
	public static final String AU = "AddUrl";
	//BJ 07/28/2017:For Location
	public static final String LOC = "StuffLocation";
	public static final String _LAT = "&latitude=";
	public static final String _LONG = "&longitude=";

	public static final String _ST = "&sitetitle=";

	//Default Share Activity
	public static final String IMG = "image";
	public static final String MUSIC = "music";
	public static final String TXT = "text";

	//social apps
	public static final String Facebook = "com.facebook.katana";
	public static final String Twitter = "com.twitter.android";
	public static final String Instagram = "com.instagram.android";
	public static final String Pinterest = "com.pinterest";
	public static final String _FS = "showHideFullScreen";
	public static final String _HF ="sendHaptickFeedback";

	// Enable Console Logging
	public static final Boolean ILE = true;
}
