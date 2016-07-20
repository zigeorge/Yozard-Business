package com.yozard.business.pushNotification;

import static com.yozard.business.pushNotification.CommonUtilities.SENDER_ID;
import static com.yozard.business.pushNotification.CommonUtilities.TAG;
import static com.yozard.business.pushNotification.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.yozard.business.R;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	static SharedPreferences registration_preference;
	static String currency = null, user_id = null;
	static String base_url;

	/**
	 * Register this account/device pair within the server.
	 *
	 */
	public static void register(final Context context, String email,
			final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");

		registration_preference = context.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String email_sp = registration_preference.getString(
				HashStatic.HASH_email, null);
		String currency_sp = registration_preference.getString(
				HashStatic.HASH_currency, null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);
		String user_id = registration_preference.getString(
				HashStatic.CUSTOMER_ID, null);

		String url_select = null;
		try {
			url_select = base_url
					+ BaseURL.API
					+ "notifications.php?authtoken="
					+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
					+ "&JSONParam="
					+ URLEncoder.encode("{\"reg_id\":\"" + regId + "\", "
							+ "\"email\":\"" + email_sp + "\","
							+ "\"user_type\":\"" + "seller" + "\","
							+ "\"user_id\":\"" + user_id + "\","
							+ "\"type\":\"" + "register" + "\"}", "utf-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		System.out.println(url_select);

		// user_id = userId_sp;
		currency = currency_sp;

		String serverUrl = url_select;
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				// post(serverUrl, params);
				JSONParser_new jParser = new JSONParser_new();

				JSONObject jobj = jParser.makeHttpRequest(url_select, "POST",
						params);
				System.out.println(jobj.toString());

				GCMRegistrar.setRegisteredOnServer(context, true);
				registration_preference.edit()
						.putString(HashStatic.Hash_GCMID, regId).commit();

				if (jobj != null) {
					System.out.println(jobj.toString());
					String message = jobj.getString("message");

					if (message.contains("success"))
						;
				}

				// String message =
				// context.getString(R.string.server_registered);
				// CommonUtilities.displayMessage(context, message);
				return;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				// increase backoff exponentially
				backoff *= 2;
				e.printStackTrace();
			}
		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		CommonUtilities.displayMessage(context, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId,
			final String email) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		registration_preference = context.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String email_sp = registration_preference.getString(
				HashStatic.HASH_email, null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);
		user_id = registration_preference.getString(HashStatic.CUSTOMER_ID,
				null);
		String reg_Id = registration_preference.getString(
				HashStatic.Hash_GCMID, null);

		String url_select = null;
		try {
			url_select = base_url
					+ BaseURL.API
					+ "notifications.php?authtoken="
					+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
					+ "&JSONParam="
					+ URLEncoder.encode("{\"reg_id\":\"" + reg_Id + "\", "
							+ "\"email\":\"" + email_sp + "\","
							+ "\"user_type\":\"" + "seller"
							+ "\"user_type\":\"" + user_id + "\","
							+ "\"type\":\"" + "unregister" + "\"}", "utf-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		String serverUrl = url_select;
		Map<String, String> params = new HashMap<String, String>();
		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 *
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void post(String endpoint, Map<String, String> params)
			throws IOException {

		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			Log.e("URL", "> " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}
