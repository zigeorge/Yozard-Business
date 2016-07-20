package com.yozard.business;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.Country_manager;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;

public class LoginActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, ResultCallback<LoadPeopleResult> {

	private static final int PROFILE_PIC_SIZE = 400;

	public static final String HASH_firstNAME = "firstname";
	public static final String HASH_email = "email";
	public static final String HASH_date = "birthday";
	public static final String HASH_gender = "gender";
	public static final String HASH_lastname = "lastname";
	public static final String HASH_password = "password";
	public static final String HASH_country = "country";
	public static final String HASH_baseUrl = "baseUrl";
	public static final String HASH_auth = "auth_type";

	public static final String HASH_GOOGLE_pp = "image_link";
	public static final String HASH_GOOGLE_userLink = "user_link";
	public static final String HASH_GOOGLE_authUrl = "auth_url";
	public static final String HASH_GOOGLE_id = "google_id";

	Button fb_button, btngplogin;
	SharedPreferences fpref;
	// Instance of Facebook Class
	private boolean mIntentInProgress;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;
	boolean isGoogle_buttonPressed = false;

	private String TAG = this.getClass().getSimpleName();

	private static String APP_ID = "578277912272525";// "771442562903897"; //
														// "277681635750065";//"648082168606567";

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;
	String access_token = null;

	private boolean mSignInClicked;
	TextView title_tv;
	private ConnectionResult mConnectionResult;
	private static final int RC_SIGN_IN = 0;
	// ImageButton backTitle_bt;
	LinearLayout login_bt;
	LinearLayout join_bt;
	SharedPreferences registration_preference;
	Editor editor;
	EditText email_et, passWord_et;
	HashMap<String, String> regMapGoogle;
	HashMap<String, String> regMap;
	TextView forgotMypassword;

	ProgressDialog progressDialog;
	TextView dontHv_account_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		 * overridePendingTransition(R.anim.activity_open_translate,
		 * R.anim.activity_close_scale);
		 */
		setContentView(R.layout.login_page);

		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// getActionBar().setCustomView(R.layout.custom_title);
		forgotMypassword = (TextView) findViewById(R.id.forgotMypassword);
		forgotMypassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				
				*/
				// TextView tv=(TextView) v;
				String email = email_et.getText().toString();
				showALertResetPassword(email);
			}
		});

		dontHv_account_tv = (TextView) findViewById(R.id.dontHv_account_tv);
		dontHv_account_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDontHv_AccountAlertDialog();
			}
		});

		email_et = (EditText) findViewById(R.id.email_login_et);
		passWord_et = (EditText) findViewById(R.id.password_login_et);

		login_bt = (LinearLayout) findViewById(R.id.login_login_bt);
		login_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String emai_str = email_et.getText().toString();
				String password = passWord_et.getText().toString();
				int i = validation(emai_str, password);

				if (i == 0) {

					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String countryIso = tm.getSimCountryIso();
					System.out.println("countryIso: " + countryIso);
					String country = Country_manager.getCountry(countryIso
							.toUpperCase());
					String baseurl = BaseURL.selectBaseUrl(country);
					System.out.println("baseurl: " + baseurl);

					HashMap<String, String> Regmap = new HashMap<String, String>();
					Regmap.put(HASH_email, emai_str);

					Regmap.put(HASH_country, country);
					Regmap.put(HASH_baseUrl, baseurl);
					Regmap.put(HASH_password, password);

					new SubmitAsync().execute(Regmap);
				} else {
					makeToast("Please fill in the form correctly");
				}
				/*
				 * Intent in = new Intent(LoginActivity.this,
				 * TabActivity.class); startActivity(in);
				 */
			}
		});

		fb_button = (Button) findViewById(R.id.login_facebook_login_bt);
		fb_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginToFacebook();
			}
		});
		btngplogin = (Button) findViewById(R.id.login_gplus_login_bt);
		btngplogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isGoogle_buttonPressed = true;
				signInWithGplus();

			}
		});

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		mAsyncRunner = new AsyncFacebookRunner(facebook);
	}

	@SuppressWarnings("deprecation")
	public void showALertResetPassword(String emails) {

		final Dialog alertDialog = new Dialog(LoginActivity.this,
				R.style.PauseDialog);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_dialog, null);
		alertDialog.setContentView(dialogView);
		// alertDialog.setTitle("Reset Password");

		Button send_button = (Button) dialogView.findViewById(R.id.send_button);

		final ImageButton dismiss_IM = (ImageButton) alertDialog
				.findViewById(R.id.crossDialog_IB);

		dismiss_IM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});

		final EditText email_et = (EditText) alertDialog
				.findViewById(R.id.alertEmail);
		email_et.setText(emails);
		send_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// here you can add functions

				// Toast.makeText(getApplicationContext(),
				// "done"+email_et.getText().toString(),
				// Toast.LENGTH_LONG).show();
				String email = email_et.getText().toString();
				if (email != null && !email.equals("")) {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String countryIso = tm.getSimCountryIso();
					System.out.println("countryIso: " + countryIso);
					String country = Country_manager.getCountry(countryIso
							.toUpperCase());
					String baseurl = BaseURL.selectBaseUrl(country);

					new Async_forgotPassword_call().execute(baseurl, email);
				} else {
					makeToast("Please fill the email address field");
				}
				// new Async_.execute();
			}
		});

		alertDialog.show();
	}

	public void makeToast(String str) {
		final String strr = str;

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.toast_layout, null);

				ImageView image = (ImageView) layout
						.findViewById(R.id.Toastimage);
				// image.setImageResource(R.drawable.favorite_1_sel);
				TextView text = (TextView) layout.findViewById(R.id.Toasttext);
				text.setText(strr);

				int actionBarHeight = 0;
				// /////////set the toasts below
				// actionbar///////////////////////
				TypedValue tv = new TypedValue();
				if (getTheme().resolveAttribute(android.R.attr.actionBarSize,
						tv, true)) {
					actionBarHeight = TypedValue.complexToDimensionPixelSize(
							tv.data, getResources().getDisplayMetrics());
				}
				Toast tt = new Toast(LoginActivity.this);
				tt.setView(layout);
				tt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
						actionBarHeight + 5);
				tt.show();
			}
		});
	}

	public class Async_forgotPassword_call extends
			AsyncTask<String, Void, Void> {

		ProgressDialog progressDialog;
		JSONParser_new jParser = null;
		HashMap<String, String> showmap = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage("Submitting forgot password request...");
			progressDialog.show();
			progressDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(String... arg) {
			// TODO Auto-generated method stub
			String base_url = arg[0];
			String emailtext = arg[1];
			String country;
			try {
				if (ConnectionManagerPromo
						.getConnectivityStatus(LoginActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					if (base_url == null || base_url.equals("")) {

						String url = "http://ip-api.com/json";
						JSONParser_new jp = new JSONParser_new();
						JSONObject jo = jp.makeHttpRequest(url, "GET", params);

						try {
							country = jo.getString(HashStatic.hash_jsonCountry);
						} catch (Exception e) {
							e.printStackTrace();
							country = "Bangladesh";
						}
						base_url = BaseURL.selectBaseUrl(country);
					}

					@SuppressWarnings("deprecation")
					String url_select = base_url
							+ BaseURL.API
							+ "password-reset.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
							+ "&JSONParam="
							+ URLEncoder.encode("{\"email\":\"" + emailtext
									+ "\"," + "\"user_type\":\"" + "seller"

									+ "\"}", "utf-8");
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"POST", params);

					String message = jobj.getString("message");
					if (message != null && message.contains("success")) {
						makeToast("An email with reset password instructions is sent to your email address.");
					}

					System.out.println(jobj.toString());

				} else {
					makeToast("Internet Connection not Available");
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();

		}

	}

	public int validation(String email, String password) {
		int result = 0;

		if (email == null || email.equals("")) {
			result = 1;
		}
		if (password == null || password.equals("")) {
			result = 1;
		}

		return result;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);

	}

	@Override
	public void onActivityResult(int requestCode, int responseCode, Intent data) {
		super.onActivityResult(requestCode, responseCode, data);
		facebook.authorizeCallback(requestCode, responseCode, data);

		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}

		}

		if (requestCode == 10)
			new GetAuthTokenFromGoogle().execute();

	}

	@SuppressWarnings("deprecation")
	public void getProfileInformationFB() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				System.out.println("Token: "
						+ mPrefs.getString("access_token", null));
				Log.d("Token", mPrefs.getString("access_token", "asd"));

				String json = response;
				try {
					final HashMap<String, String> regMap = new HashMap<String, String>();
					JSONObject profile = new JSONObject(json);
					System.out.println("JSON:: " + profile.toString());
					// getting name of the user
					final String accesstokenLink = "https://graph.facebook.com/me?access_token="
							+ mPrefs.getString("access_token", "")
							+ "&format=json";
					final String name = profile.getString("name");
					// getting email of the user
					final String email = profile.getString("email");
					final String first_name = profile.getString("first_name");
					final String last_name = profile.getString("last_name");
					final String id = profile.getString("id");
					final String imageurl = "http://graph.facebook.com/" + id
							+ "/picture?type=large";
					// final String birthday = profile.getString("birthday");
					final String gender = profile.getString("gender");

					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String countryIso = tm.getSimCountryIso();
					System.out.println("countryIso: " + countryIso);
					String country = Country_manager.getCountry(countryIso
							.toUpperCase());
					System.out.println("country: " + country);
					String baseurl = BaseURL.selectBaseUrl(country);
					System.out.println("baseurl: " + baseurl);

					regMap.put(HashStatic.ID, id);
					regMap.put(HashStatic.FACEBOOK_auth_LINK, accesstokenLink);
					regMap.put(HashStatic.FACEBOOK_PRO_LINK, imageurl);

					regMap.put(HASH_email, email);
					regMap.put(HASH_firstNAME, first_name);
					regMap.put(HASH_lastname, last_name);
					regMap.put(HASH_country, country);
					regMap.put(HASH_baseUrl, baseurl);
					regMap.put(HASH_gender, gender);
					regMap.put(HASH_auth, "facebook");

					// Looper.prepare();

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							new SubmitSocialAsync().execute(regMap);

						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void loginToFacebook() {
		fpref = getSharedPreferences("PromoRegToken", 0);

		mPrefs = getPreferences(MODE_PRIVATE);
		access_token = fpref.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		/*
		 * if (access_token != null) { facebook.setAccessToken(access_token);
		 * fpref.edit().putString("token", access_token).commit(); // Intent
		 * in=new Intent(LoginActivity.this,MainmenuActivity.class); //
		 * startActivity(in); makeToast("Already Logged In");
		 * 
		 * Log.d("FB Sessions", "" + facebook.isSessionValid()); }
		 */

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "email" }, // "email",
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
						}

						@SuppressWarnings("deprecation")
						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
							fpref.edit()
									.putString("token",
											facebook.getAccessToken()).commit();
							System.out.println("Token: "
									+ facebook.getAccessToken());
							progressDialog = new ProgressDialog(
									LoginActivity.this);
							progressDialog.setMessage("Logging in...");
							progressDialog.show();
							progressDialog.setCancelable(false);
							getProfileInformationFB();

						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors
						}

					});
		}
	}

	// //////////////////////////google log in//////////////

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult != null) {
			if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					mConnectionResult
							.startResolutionForResult(this, RC_SIGN_IN);
				} catch (SendIntentException e) {
					mIntentInProgress = false;
					mGoogleApiClient.connect();
				}
			}
		}
	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}

		if (isGoogle_buttonPressed && mGoogleApiClient.isConnected())
			new GetAuthTokenFromGoogle().execute();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void getProfileInformation(String accessToken) {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String personGoogleId = currentPerson.getId();
//				String accessTokenUrl = "https://www.googleapis.com/plus/v1/people/me?access_token="
//						+ accessToken;
				// String personFname = currentPerson.getName().

				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				int gender = currentPerson.getGender();
				String gender_str = "";
				if (gender == 0)
					gender_str = "male";
				else if (gender == 1)
					gender_str = "female";

				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				regMapGoogle = new HashMap<String, String>();

				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String countryIso = tm.getSimCountryIso();
				String country = Country_manager.getCountry(countryIso
						.toUpperCase());
				String baseurl = BaseURL.selectBaseUrl(country);

				regMapGoogle.put(HASH_email, email);
				regMapGoogle.put(HASH_firstNAME, personName);
				regMapGoogle.put(HASH_gender, gender_str);
				regMapGoogle.put(HASH_country, country);
				regMapGoogle.put(HASH_baseUrl, baseurl);
				regMapGoogle.put(HASH_auth, "google");
				regMapGoogle.put(HASH_GOOGLE_pp, personPhotoUrl);
				regMapGoogle.put(HASH_GOOGLE_userLink, personGooglePlusProfile);
				regMapGoogle.put(HASH_GOOGLE_authUrl, accessToken);
				regMapGoogle.put(HASH_GOOGLE_id, personGoogleId);

				this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new SubmitGoogleAsync().execute(regMapGoogle);
					}
				});

				/*
				 * Log.e(TAG, "Name: " + personName + ", plusProfile: " +
				 * personGooglePlusProfile + ", email: " + email + ", Image: " +
				 * personPhotoUrl);
				 * 
				 * txtName.setText(personName); txtEmail.setText(email);
				 */

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				/*
				 * personPhotoUrl = personPhotoUrl.substring(0,
				 * personPhotoUrl.length() - 2) + PROFILE_PIC_SIZE;
				 */

				// new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} else {
				makeToast("Could not access the google account. Please try again");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		// updateUI(false);
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		// Plus.PeopleApi.loadVisible(mGoogleApiClient,
		// null).setResultCallback(this);
		if (isGoogle_buttonPressed) {
			new GetAuthTokenFromGoogle().execute();
			// isGoogle_buttonPressed=false;
		}

	}

	private class GetAuthTokenFromGoogle extends AsyncTask<Void, Integer, Void> {
		String access_token = null;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (ConnectionManagerPromo
					.getConnectivityStatus(LoginActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
				String accountname = Plus.AccountApi
						.getAccountName(mGoogleApiClient);
				String scope = "oauth2:"
						+ Scopes.PLUS_LOGIN
						+ " "
						+ "https://www.googleapis.com/auth/userinfo.email"
						+ " https://www.googleapis.com/auth/plus.profile.agerange.read";
				try {
					access_token = GoogleAuthUtil
							.getToken(
									LoginActivity.this,
									accountname + "",
									"oauth2:"
											+ Scopes.PROFILE
											+ " "
											+ "https://www.googleapis.com/auth/plus.login"
											+ " "
											+ "https://www.googleapis.com/auth/plus.profile.emails.read");

					String accessToken1 = GoogleAuthUtil.getToken(
							LoginActivity.this, accountname, scope);
				} catch (UserRecoverableAuthException er) {
					// TODO Auto-generated catch block
					startActivityForResult(er.getIntent(), 10);
					er.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (GoogleAuthException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				makeToast("Internet Connection not Available");
			}
			System.out.println("AccessToken Google: " + access_token);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (access_token != null){
				getProfileInformation(access_token);
			}else{
				makeToast("Google Login Failed, Restart the Application and try again!");
			}
		}
	}

	public class SubmitGoogleAsync extends
			AsyncTask<HashMap<String, String>, Void, Void> {
		ProgressDialog progressDialog;
		int cust_Id;
		String accessToken1 = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage("Logging in...");
			progressDialog.show();
			progressDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(HashMap<String, String>... paramMap) {
			// TODO Auto-generated method stub
			if (ConnectionManagerPromo
					.getConnectivityStatus(LoginActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
				HashMap<String, String> parametersList = paramMap[0];

				registration_preference = getSharedPreferences(
						HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
				editor = registration_preference.edit();

				String email = parametersList.get(HASH_email);
				String fname = parametersList.get(HASH_firstNAME);
				String gender = parametersList.get(HASH_gender);
				// String lastName = parametersList.get(HASH_lastname);
				String baseUrl = parametersList.get(HASH_baseUrl);
				String auth_type = parametersList.get(HASH_auth);
				String country = parametersList.get(HASH_country);
				String id = parametersList.get(HASH_GOOGLE_id);
				String authUrl = parametersList.get(HASH_GOOGLE_authUrl);
				String imageUrl = parametersList.get(HASH_GOOGLE_pp);

				Log.e("Auth URL", authUrl);

				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("link", authUrl));

					if (country == null || country.equals("")) {
						String url = "http://ip-api.com/json";
						JSONParser_new jp = new JSONParser_new();
						JSONObject jo = jp.makeHttpRequest(url, "GET", params);

						try {
							country = jo.getString(HashStatic.hash_jsonCountry);
						} catch (Exception e) {
							e.printStackTrace();
							country = "Bangladesh";
						}
						baseUrl = BaseURL.selectBaseUrl(country);
						Log.e("Auth URL", authUrl);
					}

					String url_select = baseUrl
							+ BaseURL.API
							+ "authenticate.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
							+ "&JSONParam="
							+ URLEncoder.encode("{\"image_url\":\"" + imageUrl
									+ "\"" + ",\"auth_type\":\"" + auth_type
									+ "\"" + ",\"firstname\":\"" + fname + "\""
									+ ",\"lastname\":\"" + "\""
									+ ",\"country\":\"" + country + "\""
									+ ",\"gender\":\"" + gender + "\""
									+ ",\"user_type\":\"" + "seller" + "\""
									+ ",\"id\":\"" + id + "\""
									+ ",\"email\":\"" + email + "\"" + "}",
									"utf-8");

					System.out.println(url_select);

					JSONParser_new jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"POST", nameValuePairs);
					JSONObject jCust = jobj.getJSONObject("seller");

					if (jobj != null) {
						String message = jobj.getString("message");
						if (message != null
								&& message
										.contains("invalid username or password"))
							runOnUiThread(new Runnable() {
								public void run() {
									showDontHv_AccountAlertDialog();
								}
							});
					}
					cust_Id = jCust.getInt("seller_id");

					String sellerEmail = jCust
							.getString(HashStatic.HASH_seller_email);
					String sellerImage = jCust
							.getString(HashStatic.HASH_seller_image);
					String sellerName = jCust
							.getString(HashStatic.HASH_seller_name);

					if (cust_Id > 0) {
						editor.putString(HashStatic.CUSTOMER_ID,
								String.valueOf(cust_Id)).commit();
						editor.putString(HashStatic.HASH_email, email).commit();
						editor.putString(HashStatic.HASH_firstNAME,
								String.valueOf(fname)).commit();
						// editor.putString(HashStatic.HASH_lastname,
						// String.valueOf(lastName)).commit();
						editor.putString(HashStatic.HASH_baseUrl,
								String.valueOf(baseUrl)).commit();
						editor.putString(HashStatic.HASH_auth,
								String.valueOf(auth_type)).commit();
						// editor.putString(HashStatic.FACEBOOK_PRO_LINK,
						// imageUrl)// /this
						// is
						// used
						// for
						// both
						// google
						// &
						// fb

						editor.putString(HashStatic.HASH_seller_email,
								sellerEmail).commit();
						editor.putString(HashStatic.HASH_seller_image,
								sellerImage).commit();
						editor.putString(HashStatic.HASH_seller_name,
								sellerName).commit();

						editor.putString(HashStatic.HASH_country,
								String.valueOf(country)).commit();
						String currency = BaseURL.selectCurrency(country);
						editor.putString(HashStatic.HASH_currency,
								String.valueOf(currency)).commit();
					}

					System.out.println("CustomerID" + cust_Id);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				makeToast("Network connection not available.");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (cust_Id > 0) {
				signOutFromGplus();
				makeToast("Welcome to PromoPayout");
				HashStatic.firstTimeLogin = true;
				Intent intent = new Intent(LoginActivity.this,
						NotificationActivity.class);
				startActivity(intent);
				finish();
				// editor.putString(HashStatic.CUSTOMER_ID,
				// String.valueOf(cust_Id)).commit();
			}
		}

	}

	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();

		}
	}

	public class SubmitSocialAsync extends
			AsyncTask<HashMap<String, String>, Void, Void> {

		int cust_Id;

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		JSONParser_new jParser;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(HashMap<String, String>... paramMap) {
			// TODO Auto-generated method stub
			if (ConnectionManagerPromo
					.getConnectivityStatus(LoginActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
				HashMap<String, String> parametersList = paramMap[0];

				registration_preference = getSharedPreferences(
						HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
				editor = registration_preference.edit();

				String email = parametersList.get(HASH_email);
				String fname = parametersList.get(HASH_firstNAME);
				String lastName = parametersList.get(HASH_lastname);
				String baseUrl = parametersList.get(HASH_baseUrl);
				String auth_type = parametersList.get(HASH_auth);
				String gender = parametersList.get(HASH_gender);

				String country = parametersList.get(HASH_country);
				String id = parametersList.get(HashStatic.ID);
				String authUrl = parametersList
						.get(HashStatic.FACEBOOK_auth_LINK);
				String imageUrl = parametersList
						.get(HashStatic.FACEBOOK_PRO_LINK);

				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("link", authUrl));

					// params.add(new UrlEncodedFormEntity(nameValuePairs));
					if (country == null || country.equals("")) {
						String url = "http://ip-api.com/json";
						JSONParser_new jp = new JSONParser_new();
						JSONObject jo = jp.makeHttpRequest(url, "GET", params);

						try {
							country = jo.getString(HashStatic.hash_jsonCountry);
						} catch (Exception e) {
							e.printStackTrace();
							country = "Bangladesh";
						}
						baseUrl = BaseURL.selectBaseUrl(country);

					}

					String url_select = baseUrl
							+ BaseURL.API
							+ "authenticate.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
							+ "&JSONParam="
							+ URLEncoder.encode("{\"firstname\":\"" + fname
									+ "\"" + ",\"lastname\":\"" + lastName
									+ "\"" + ",\"auth_type\":\"" + auth_type
									+ "\"" + ",\"country\":\"" + country + "\""
									+ ",\"id\":\"" + id + "\""
									+ ",\"gender\":\"" + gender
									+ "\""
									// + ",\"link\":\"" + authUrl + "\""
									+ ",\"image_url\":\"" + imageUrl + "\""
									+ ",\"user_type\":\"" + "seller" + "\""
									+ ",\"email\":\"" + email + "\"" + "}",
									"utf-8");
					/*
					 * System.out.println("{\"firstname\":\"" + fname + "\"" +
					 * ",\"lastname\":\"" + lastName + "\"" +
					 * ",\"auth_type\":\"" + auth_type+ "\"" + ",\"country\":\""
					 * + country + "\"" + ",\"id\":\"" + id + "\"" +
					 * ",\"link\":\"" + authUrl + "\"" + ",\"image_url\":\"" +
					 * imageUrl + "\""
					 * 
					 * + ",\"email\":\"" + email + "\"" + "}");
					 */
					System.out.println(url_select);

					jParser = new JSONParser_new();
					// JSONObject jobj =jParser.makeHttpRequest(url_select,
					// "POST", nameValuePairs);

					/*
					 * DefaultHttpClient httpClient = new DefaultHttpClient();
					 * HttpPost httpPost = new HttpPost(url_select);
					 * httpPost.setEntity(new
					 * UrlEncodedFormEntity(nameValuePairs));
					 * 
					 * HttpResponse httpResponse = httpClient.execute(httpPost);
					 * HttpEntity httpEntity = httpResponse.getEntity(); is =
					 * httpEntity.getContent();
					 * 
					 * try { BufferedReader reader = new BufferedReader( new
					 * InputStreamReader(is, "iso-8859-1"), 8); StringBuilder sb
					 * = new StringBuilder(); String line = null; while ((line =
					 * reader.readLine()) != null) { sb.append(line + "\n"); }
					 * is.close(); json = sb.toString(); Log.d("String jason",
					 * sb.toString()); } catch (Exception e) {
					 * Log.e("Buffer Error", "Error converting result " +
					 * e.toString()); }
					 */

					jObj = jParser.makeHttpRequest(url_select, "POST",
							nameValuePairs);

					if (jObj != null) {
						String message = jObj.getString("message");
						if (message != null
								&& message
										.contains("invalid username or password"))
							runOnUiThread(new Runnable() {
								public void run() {
									showDontHv_AccountAlertDialog();
								}
							});
					}

					JSONObject jCust = jObj.getJSONObject("seller");
					cust_Id = jCust.getInt("seller_id");

					String sellerEmail = jCust
							.getString(HashStatic.HASH_seller_email);
					String sellerImage = jCust
							.getString(HashStatic.HASH_seller_image);
					String sellerName = jCust
							.getString(HashStatic.HASH_seller_name);

					if (cust_Id > 0) {
						editor.putString(HashStatic.CUSTOMER_ID,
								String.valueOf(cust_Id)).commit();
						editor.putString(HashStatic.HASH_email, email).commit();
						editor.putString(HashStatic.HASH_firstNAME,
								String.valueOf(fname)).commit();
						editor.putString(HashStatic.HASH_lastname,
								String.valueOf(lastName)).commit();
						editor.putString(HashStatic.HASH_baseUrl,
								String.valueOf(baseUrl)).commit();
						editor.putString(HashStatic.HASH_auth,
								String.valueOf(auth_type)).commit();

						editor.putString(HashStatic.HASH_seller_email,
								sellerEmail).commit();
						editor.putString(HashStatic.HASH_seller_image,
								sellerImage).commit();
						editor.putString(HashStatic.HASH_seller_name,
								sellerName).commit();
						/*
						 * editor.putString(HashStatic.FACEBOOK_PRO_LINK,
						 * imageUrl) .commit();
						 */

						editor.putString(HashStatic.HASH_country,
								String.valueOf(country)).commit();
						String currency = BaseURL.selectCurrency(country);
						editor.putString(HashStatic.HASH_currency,
								String.valueOf(currency)).commit();
					}

					System.out.println("CustomerID" + cust_Id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				makeToast("Network connection not available");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (cust_Id > 0) {
				makeToast("Welcome to PromoPayout");
				HashStatic.firstTimeLogin = true;
				Intent intent = new Intent(LoginActivity.this,
						NotificationActivity.class);
				startActivity(intent);
				finish();
				// editor.putString(HashStatic.CUSTOMER_ID,
				// String.valueOf(cust_Id)).commit();
			}
		}

	}

	// //////////////////////

	public class SubmitAsync extends
			AsyncTask<HashMap<String, String>, Void, Void> {
		int cust_Id;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage("Logging in...");
			progressDialog.show();
			progressDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(HashMap<String, String>... paramMap) {
			// TODO Auto-generated method stub
			if (ConnectionManagerPromo
					.getConnectivityStatus(LoginActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
				HashMap<String, String> parametersList = paramMap[0];

				String email = parametersList.get(HASH_email);
				String password = parametersList.get(HASH_password);
				String baseUrl = parametersList.get(HASH_baseUrl);
				String country = parametersList.get(HASH_country);

				byte[] data;
				try {

					data = password.getBytes("UTF-8");

					password = Base64.encodeToString(data, Base64.DEFAULT);
					password = password.trim();

					// Log.i("Base 64 ", base64);
					System.out.println(password);
					System.out.println(URLEncoder.encode(password, "utf-8"));

				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();

				}

				registration_preference = getSharedPreferences(
						HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
				editor = registration_preference.edit();

				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					if (country == null || country.equals("")) {
						String url = "http://ip-api.com/json";
						JSONParser_new jp = new JSONParser_new();
						JSONObject jo = jp.makeHttpRequest(url, "GET", params);

						try {
							country = jo.getString(HashStatic.hash_jsonCountry);
						} catch (Exception e) {
							e.printStackTrace();
							country = "Bangladesh";
						}
						baseUrl = BaseURL.selectBaseUrl(country);

					}

					String url_select = baseUrl
							+ BaseURL.API
							+ "authenticate.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
							+ "&JSONParam="
							+ URLEncoder.encode("{\"email\":\"" + email + "\""
									+ ",\"auth_type\":\"" + "normal" + "\""
									+ ",\"user_type\":\"" + "seller" + "\""
									+ ",\"password\":\"" + password + "\""
									+ "}", "utf-8");

					// System.out.println(url_select);

					JSONParser_new jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"POST", params);
					System.out.println(jobj.toString());

					JSONObject jCust = jobj.getJSONObject("seller");
					cust_Id = jCust.getInt("seller_id");

					String sellerEmail = jCust
							.getString(HashStatic.HASH_seller_email);
					String sellerImage = jCust
							.getString(HashStatic.HASH_seller_image);
					String sellerName = jCust
							.getString(HashStatic.HASH_seller_name);

					// System.out.println("SelID" + cust_Id);

					System.out.println("mir: " + jobj.toString());
					if (cust_Id > 0) {
						editor.putString(HashStatic.CUSTOMER_ID,
								String.valueOf(cust_Id)).commit();
						editor.putString(HashStatic.HASH_email, email).commit();
						editor.putString(HashStatic.HASH_baseUrl,
								String.valueOf(baseUrl)).commit();

						editor.putString(HashStatic.HASH_country,
								String.valueOf(country)).commit();

						editor.putString(HashStatic.HASH_seller_email,
								sellerEmail).commit();
						editor.putString(HashStatic.HASH_seller_image,
								sellerImage).commit();
						editor.putString(HashStatic.HASH_seller_name,
								sellerName).commit();

						String currency = BaseURL.selectCurrency(country);
						editor.putString(HashStatic.HASH_currency,
								String.valueOf(currency)).commit();
					}

				} catch (Exception e) {
					e.printStackTrace();
					// makeToast("Invalid user name or password.");
				}
			} else {
				makeToast("Network connection not available.");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (cust_Id > 0) {
				makeToast("Welcome to PromoPayout");
				HashStatic.firstTimeLogin = true;
				Intent intent = new Intent(LoginActivity.this,
						NotificationActivity.class);
				startActivity(intent);
				finish();
			} else {
				makeToast("Invalid username or password.");
			}
		}

	}

	// //dont have account alertDialog
	public void showDontHv_AccountAlertDialog() {

		final Dialog dialog = new Dialog(this, R.style.PauseDialog);
		dialog.setContentView(R.layout.account_dialog_layout);

		final ImageButton dismiss_IM = (ImageButton) dialog
				.findViewById(R.id.dismiss_IM);
		final Button ok_button = (Button) dialog.findViewById(R.id.ok_button);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String countryIso = tm.getSimCountryIso();
		System.out.println("countryIso: " + countryIso);
		String country = Country_manager.getCountry(countryIso.toUpperCase());
		System.out.println("country: " + country);
		String baseurl = BaseURL.selectBaseUrl(country);

		final TextView text_web = (TextView) dialog
				.findViewById(R.id.webText_tv);

		String url = "";
		if (baseurl == null || baseurl.equals("")) {
			baseurl = "www.promopayout.com";
			url = baseurl;
		} else
			url = baseurl;

		text_web.setText(url);

		dismiss_IM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		ok_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
		isGoogle_buttonPressed = false;
	}

	@Override
	public void onResult(LoadPeopleResult arg0) {
		// TODO Auto-generated method stub

	}

}
