package com.yozard.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.yozard.business.model.WhatsNew;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.PlayGifView;

public class SplashActivity extends Activity {

	public boolean isRegistered = false;

	Animation anim;
	// ImageView mImageView;
	SharedPreferences registration_preference;
	PlayGifView playGifView;
	boolean isAppUpdate, isFirstTimeLogin;
	WhatsNew whatsNew;
	String info = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		playGifView = (PlayGifView) findViewById(R.id.viewGif);
		playGifView.setImageResource(R.drawable.yozard_splash);
		
		registration_preference = getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);

		isAppUpdate = registration_preference.getBoolean(
				HashStatic.Hash_AppUpdate, false);
		info = registration_preference.getString(HashStatic.Hash_UpdateInfo,
				null);
		isFirstTimeLogin = registration_preference.getBoolean(
				HashStatic.Hash_FirstTimeLogin, true);
		if (isFirstTimeLogin) {
			registration_preference.edit()
					.putBoolean(HashStatic.Hash_FirstTimeLogin, false).commit();
			registration_preference.edit()
					.putString(HashStatic.Hash_AuthToken, BaseURL.AUTH_TOKEN)
					.commit();
		}

		checkAppVersion(info);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(playGifView.getWindowToken(), 0);

		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				String custId = registration_preference.getString(
						HashStatic.CUSTOMER_ID, null);
				if (isAppUpdate) {
					Intent intent = new Intent(SplashActivity.this,
							UpdateAppActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					finish();
				} else {
					if (custId == null) {
						Intent intent = new Intent(SplashActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(SplashActivity.this,
								NotificationActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}

		}, 1500);
	}

	private void checkAppVersion(String info) {
		try {
			String version = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			Log.e("App Version", version);
			Gson g = new Gson();
			if (info != null && isAppUpdate) {
				whatsNew = g.fromJson(info, WhatsNew.class);
				Log.e("App Version 2", whatsNew.getApp_version());
				if (version.equalsIgnoreCase(whatsNew.getApp_version())) {
					Editor editor = registration_preference.edit();
					editor.putBoolean(HashStatic.Hash_AppUpdate, false);
					isAppUpdate = false;
				}
			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
