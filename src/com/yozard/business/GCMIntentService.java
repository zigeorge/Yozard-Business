package com.yozard.business;

import static com.yozard.business.pushNotification.CommonUtilities.SENDER_ID;
import static com.yozard.business.pushNotification.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.yozard.business.model.WhatsNew;
import com.yozard.business.pushNotification.ServerUtilities;
import com.yozard.business.utils.HashStatic;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	static SharedPreferences registration_preference;
	static String currency = null, user_id = null;
	static String base_url;
	String email_sp = "";

	public GCMIntentService() {
		super(SENDER_ID);

		System.out.print("GCM IntentServ1");
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);

		System.out.print("GCM IntentServ2 ID: " + registrationId);

		registration_preference = context.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		email_sp = registration_preference.getString(HashStatic.HASH_email,
				null);

		// Log.d("NAME", MainActivity.name);
		ServerUtilities.register(context, email_sp, registrationId);
		displayMessage(context, "Your device registred with GCM");
	}

	/**
	 * Method called on device unregistred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		registration_preference = context.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		email_sp = registration_preference.getString(HashStatic.HASH_email,
				null);

		displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, email_sp, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");

		Bundle bundle = intent.getExtras();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value
					.getClass().getName()));
		}

		String message = intent.getExtras().getString("message");
		String title = intent.getExtras().getString("title");
		String type = intent.getExtras().getString("type");

		System.out.println(intent.toString());
		// System.out.printl

		displayMessage(context, message, title);

		if (TextUtils.isEmpty(type)) {
			if (title.equalsIgnoreCase("authtoken")) {
				registration_preference = context.getSharedPreferences(
						HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
				registration_preference.edit().putString(
						HashStatic.Hash_AuthToken, message).commit();
			} else {
				generateNotification(context, message, title);
			}
		} else {
			if (type.equalsIgnoreCase("update_app")) {
				String info = intent.getExtras().getString("info");
				// message = "Install new update";
				generateNotification(context, message, title, info);
				// if(mandatory.equalsIgnoreCase("true")){
				// // Log.e("Message", msg.get(0));
				// }
			}
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,
			String title_sent) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = title_sent; // context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context,
				NotificationActivity.class);
		// set intent so it does not start a new activity
		/*
		 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 */
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, Html.fromHtml(title),
				Html.fromHtml(message), intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}

	// /default
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context,
				NotificationActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, Html.fromHtml(title),
				Html.fromHtml(message), intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}
	
	private static void generateNotification(Context context, String message,
			String title, String info) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		Intent notificationIntent = new Intent(context, SplashActivity.class);
		saveNotificationData(info, title, context);
		// set intent so it does not start a new activity
		/*
		 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 */
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, Html.fromHtml(title),
				Html.fromHtml(message), intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
	
	private static void saveNotificationData(String info, String title,
			Context context) {
		Gson g = new Gson();
		SharedPreferences registration_preference = context
				.getSharedPreferences(HashStatic.PREF_NAME_REG,
						Context.MODE_PRIVATE);
		Editor editor = registration_preference.edit();
		try {
			String version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;

			WhatsNew whatsNew = g.fromJson(info, WhatsNew.class);
			if (version.equalsIgnoreCase(whatsNew.getApp_version())) {
				editor.putBoolean(HashStatic.Hash_AppUpdate, false);
			} else {
				editor.putBoolean(HashStatic.Hash_AppUpdate, true);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.putString(HashStatic.Hash_UpdateInfo, info);
		editor.putString(HashStatic.Hash_UpdateTitle, title);
		editor.commit();
	}

}
