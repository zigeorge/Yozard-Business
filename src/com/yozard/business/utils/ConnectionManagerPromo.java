package com.yozard.business.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManagerPromo {

//////////////Inter net////////////////////////////////////
public static int TYPE_WIFI = 1;
public static int TYPE_MOBILE = 2;
public static int TYPE_NOT_CONNECTED = 0;

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == cm.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == cm.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}
	
}
