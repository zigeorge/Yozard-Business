package com.yozard.business;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yozard.business.adapter.WhatsNewAdapter;
import com.yozard.business.model.WhatsNew;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.TypeFace_MY;

public class UpdateAppActivity extends ActionBarActivity {

	TextView tvTitle, tvSkip;
	Button btnUpdateApp;
	ListView lvWhatsNew;

	SharedPreferences registration_preference;
	Editor editor;
	WhatsNew whatsNew;
	WhatsNewAdapter whatsNewAdapter;
	
	Typeface tf_roboto;

	String title, custId, custEmail;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_app);

		context = this;

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		tf_roboto = TypeFace_MY.getRoboto_condensed(context);
		
		registration_preference = getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String infoJson = registration_preference.getString(
				HashStatic.Hash_UpdateInfo, null);
		title = registration_preference.getString(HashStatic.Hash_UpdateTitle,
				null);
		custId = registration_preference
				.getString(HashStatic.CUSTOMER_ID, null);
		custEmail = registration_preference.getString(
				HashStatic.HASH_email, "");
		Gson g = new Gson();
		whatsNew = g.fromJson(infoJson, WhatsNew.class);
		getSupportActionBar().setTitle(
				"Upgrade to Yozard Business " + whatsNew.getApp_version());
		initUI();

	}

	private void initUI() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(tf_roboto);
		tvSkip = (TextView) findViewById(R.id.tvSkip);
		tvSkip.setTypeface(tf_roboto);
		btnUpdateApp = (Button) findViewById(R.id.btnUpdateApp);
		btnUpdateApp.setTypeface(tf_roboto);
		lvWhatsNew = (ListView) findViewById(R.id.lvWhatsNew);

		tvTitle.setText(title);
		if (whatsNew.getMandatory().equals("true")) {
			tvSkip.setVisibility(View.INVISIBLE);
		} else {
			tvSkip.setVisibility(View.VISIBLE);
		}

		whatsNewAdapter = new WhatsNewAdapter(context, R.layout.whatsnew_row,
				whatsNew.getMessage());
		lvWhatsNew.setAdapter(whatsNewAdapter);

		btnUpdateApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.e("Update", "Go to Playstore App link moron!!!");
				final String appPackageName = getPackageName(); // getPackageName()
																// from Context
																// or Activity
																// object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/apps/details?id="
									+ appPackageName)));
				}
			}
		});

		tvSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeAppUpdateSettings();
				if (custId == null) {
					Intent intent = new Intent(UpdateAppActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
					// System.out.println("HIII");
				} else {
					Intent intent = new Intent(UpdateAppActivity.this,
							NotificationActivity.class);
					startActivity(intent);
					finish();
					// System.out.println("HIII2");
				}
			}
		});

	}
	
	private void changeAppUpdateSettings(){
		editor = registration_preference.edit();
		editor.putBoolean(HashStatic.Hash_AppUpdate, false).commit();
	}
}
