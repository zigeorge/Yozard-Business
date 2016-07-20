package com.yozard.business;

import static com.yozard.business.pushNotification.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.yozard.business.pushNotification.CommonUtilities.EXTRA_MESSAGE;
import static com.yozard.business.pushNotification.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yozard.business.adapter.NavDrawerItem;
import com.yozard.business.adapter.NavDrawerListAdapter;
import com.yozard.business.fragments.ApproveCouponFragment;
import com.yozard.business.pushNotification.AlertDialogManager;
import com.yozard.business.pushNotification.ConnectionDetector;
import com.yozard.business.pushNotification.ServerUtilities;
import com.yozard.business.pushNotification.WakeLocker;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;
import com.yozard.business.utils.MyToast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class NotificationActivity extends ActionBarActivity {

	public static int which_fragment = 0;// starts with feed

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	// private ListView mDrawerList_right;

	private ActionBarDrawerToggle mDrawerToggle;
	private int count = 0;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	// private String[] navMenuTitles={"Business Feed",
	// "Transactions Log","Sign Out"};
	private String[] navMenuTitles = { "Pending Coupons", "Approved Coupons",
			"Sign Out" };
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	FrameLayout frame_container;

	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;

	static SharedPreferences registration_preference;
	static String currency = null, user_id = null;
	static String base_url;

	GoogleCloudMessaging gcm;

	CircularImageView imageView;
	TextView username_tv, email_tv;

	LinearLayout list_linear;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	String STORE_ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_layout);

		// mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		list_linear = (LinearLayout) findViewById(R.id.list_linear);
		// mDrawerList_right=(ListView) findViewById(R.id.listRight_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0],
				R.drawable.ic_approve));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],
				R.drawable.ic_done));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],
				R.drawable.ic_logout));

		/*
		 * // Communities, Will add a counter here navDrawerItems.add(new
		 * NavDrawerItem(navMenuTitles[3], navMenuIcons .getResourceId(3, -1),
		 * true, "22")); // Pages navDrawerItems.add(new
		 * NavDrawerItem(navMenuTitles[4], navMenuIcons .getResourceId(4, -1)));
		 */

		imageView = (CircularImageView) findViewById(R.id.userpic_IV);
		username_tv = (TextView) findViewById(R.id.userName);
		email_tv = (TextView) findViewById(R.id.emailAddress);

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.upload)
				.showImageOnLoading(R.drawable.upload)
				.showImageOnFail(R.drawable.upload)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				// .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		registration_preference = this.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String email_sp = registration_preference.getString(
				HashStatic.HASH_email, null);
		String currency_sp = registration_preference.getString(
				HashStatic.HASH_currency, null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);
		user_id = registration_preference.getString(HashStatic.CUSTOMER_ID,
				null);
		String image = registration_preference.getString(
				HashStatic.HASH_seller_image, null);
		String name = registration_preference.getString(
				HashStatic.HASH_seller_name, null);
		String email = registration_preference.getString(
				HashStatic.HASH_seller_email, null);

		imageLoader.displayImage(base_url + image, imageView, options);

		username_tv.setText(name);
		email_tv.setText(email);

		GCM_Setup(this, email_sp);

		frame_container = (FrameLayout) findViewById(R.id.frame_container);
		// loadFragment(0);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		if (which_fragment == 2) {
			loadFragment(1);
		} else {
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}
			try {
				unregisterReceiver(mHandleMessageReceiver);
				GCMRegistrar.onDestroy(this);
			} catch (Exception e) {
				Log.e("UnRegister Receiver Error", "> " + e.getMessage());
			}
			finish();
		}
	}

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;
					Log.i("GCM", msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// etRegId.setText(msg + "\n");
			}
		}.execute(null, null, null);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(list_linear);
		// menu.findItem(R.id.notification).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressLint("NewApi")
	private void displayView(int position) {
		// update the main content by replacing fragments
		/*
		 * Fragment fragment = null; switch (position) { case 0: fragment = new
		 * HomeFragment(); break; case 1: fragment = new FindPeopleFragment();
		 * break; case 2: fragment = new PhotosFragment(); break; case 3:
		 * fragment = new CommunityFragment(); break; case 4: fragment = new
		 * PagesFragment(); break; case 5: fragment = new WhatsHotFragment();
		 * break;
		 * 
		 * default: break; }
		 * 
		 * if (fragment != null) { FragmentManager fragmentManager =
		 * getFragmentManager(); fragmentManager.beginTransaction()
		 * .replace(R.id.frame_container, fragment).commit();
		 * 
		 * // update selected item and title, then close the drawer
		 * mDrawerList.setItemChecked(position, true);
		 * mDrawerList.setSelection(position);
		 * setTitle(navMenuTitles[position]);
		 * mDrawerLayout.closeDrawer(mDrawerList); } else { // error in creating
		 * fragment Log.e("MainActivity", "Error in creating fragment"); }
		 */

		if (position == 0) {
			mDrawerLayout.closeDrawer(Gravity.START);
			mDrawerList.setItemChecked(position, true);
			which_fragment = 0;
			loadFragment(0);

		} else if (position == 2) {
			mDrawerLayout.closeDrawer(Gravity.START);
			mDrawerList.setItemChecked(position, false);
			showALertSigh_out();

		} else if (position == 1) {
			mDrawerLayout.closeDrawer(Gravity.START);
			which_fragment = 1;
			loadFragment(1);

		}
	}

	String regId;

	public void GCM_Setup(Context ctx, final String email) {
		// Check if Internet present

		cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		regId = GCMRegistrar.getRegistrationId(this);
		System.out.println(regId + "");

		String checkSharepref_regId = registration_preference.getString(
				HashStatic.Hash_GCMID, null);

		if (!regId.equals("")) {
			if (registration_preference != null)
				registration_preference.edit().putString(HashStatic.Hash_GCMID,
						regId);
		}

		System.out.println("RegID: " + regId);
		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			// GCMRegistrar.register(this, SENDER_ID);
			// getRegId();
			final Context context = this;
			mRegisterTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					// Register on our server
					// On server creates a new user
					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging
									.getInstance(getApplicationContext());
						}
						regId = gcm.register(SENDER_ID);
						// msg = "Device registered, registration ID=" + regId;
						// Log.i("GCM", msg);

					} catch (IOException ex) {
						// msg = "Error :" + ex.getMessage();

					}
					System.out.println("onRegistration 2: " + regId);
					ServerUtilities.register(context, email, regId);

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					mRegisterTask = null;
				}

			};
			mRegisterTask.execute(null, null, null);
			System.out.println("onRegistration 1");
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				/*
				 * Toast.makeText(getApplicationContext(),
				 * "Already registered with GCM", Toast.LENGTH_LONG) .show();
				 */
				// this one is checked if the device was previously registered
				// and unregistered
				// so the next time if logged in it is able to register to the
				// server
				// the string represent the device is registered but the server
				// doesnt know it
				if (checkSharepref_regId == null) {
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							System.out.println("onRegistration 2: " + regId);
							ServerUtilities.register(context, email, regId);

							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}

			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						System.out.println("onRegistration 2: " + regId);
						ServerUtilities.register(context, email, regId);

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			String title = intent.getExtras().getString("title");
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			Bundle bundle = intent.getExtras();
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				Log.d("BR", String.format("%s %s (%s)", key, value.toString(),
						value.getClass().getName()));
			}

			System.out.println("On Broadcast1:" + newMessage);

			if (title != null) {
				doIncreaseCount_notification();
			}
			// Showing received message
			// lblMessage.append(newMessage + "\n");
			/*
			 * Toast.makeText(getApplicationContext(), "New Message: " +
			 * newMessage, Toast.LENGTH_LONG).show();
			 */
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// ////////////////////Options menu and counter

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_main, menu);

		// MenuItem menuItem = menu.findItem(R.id.notification);
		// menuItem.setIcon(buildCounterDrawable(count, R.drawable.bell));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// switch (id) {
		// case R.id.notification:
		/*
		 * if(mDrawerLayout.isDrawerOpen(Gravity.END))
		 * mDrawerLayout.closeDrawer(Gravity.END); else
		 * mDrawerLayout.openDrawer(Gravity.END);
		 */
		// doClearCount_notification();
		// invalidateOptionsMenu();
		//
		// if (which_fragment == 0) {
		// FragmentManager fm = getS
		// Feed_fragment fragment = (Feed_fragment) fm
		// .findFragmentByTag("counterFragment");
		//
		// fragment.callRefresh();

		// }else{
		// loadFragment(0);
		// }

		// break;
		//
		// default:
		// break;
		// }

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	private Drawable buildCounterDrawable(int count, int backgroundImageId) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
		view.setBackgroundResource(backgroundImageId);

		if (count == 0) {
			View counterTextPanel = view.findViewById(R.id.counterValuePanel);
			counterTextPanel.setVisibility(View.GONE);
		} else {
			TextView textView = (TextView) view.findViewById(R.id.count);
			textView.setText("" + count);
		}

		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);

		return new BitmapDrawable(getResources(), bitmap);
	}

	public void doIncreaseCount_notification() {
		count++;
		invalidateOptionsMenu();
	}

	public void doClearCount_notification() {
		count = 0;
		invalidateOptionsMenu();
	}

	public void setStoreId(String storeId) {
		STORE_ID = storeId;
	}

	public void loadFragment(int choose) {
		Fragment fragment = null;
		if (choose == 0) {
			getActionBar().setTitle("Pending Coupons");
			fragment = ApproveCouponFragment.newInstance(true);
			which_fragment = 0;
		} else if (choose == 1) {
			getActionBar().setTitle("Approved Coupons");
			fragment = ApproveCouponFragment.newInstance(false);
			which_fragment = 1;
		} else if (choose == 2) {
			// getActionBar().setTitle("Transactions Log");
			// fragment = Feed_transactions.getInstance(STORE_ID);
			// which_fragment=2;
		}
		getSupportFragmentManager();
		FragmentManager fragmentManager2 = getSupportFragmentManager();
		FragmentTransaction ft2 = fragmentManager2.beginTransaction();
		// fragmentManager.beginTransaction()
		ft2.addToBackStack(null);
		// ft2.setCustomAnimations(R.anim.enter_x_anim, R.anim.exit_x_anim);
		// frameContainer1.setVisibility(View.INVISIBLE);

		// getActionBar().setTitle("HOME");
		if (fragment != null) {
			ft2.replace(R.id.frame_container, fragment, "counterFragment")
					.commit();
		}
	}

	@SuppressWarnings("deprecation")
	public void showALertSigh_out() {

		final Dialog alertDialog = new Dialog(NotificationActivity.this,
				R.style.PauseDialog);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.sighout_alertdialog, null);
		alertDialog.setContentView(dialogView);
		// alertDialog.setTitle("Reset Password");

		Button yes_button = (Button) dialogView.findViewById(R.id.yes_button);
		Button no_button = (Button) dialogView.findViewById(R.id.no_button);

		final ImageButton dismiss_IM = (ImageButton) alertDialog
				.findViewById(R.id.crossDialog_IB_signout);

		dismiss_IM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});

		yes_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				new Async_signout_call().execute();
				alertDialog.dismiss();
			}
		});
		no_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	public void makeToast(final String str) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MyToast.makeToast(str, NotificationActivity.this);
			}
		});
	}

	// ///////SIGN OUT ALERT
	public class Async_signout_call extends AsyncTask<String, Void, Void> {

		ProgressDialog progressDialog;
		JSONParser_new jParser = null;
		HashMap<String, String> showmap = new HashMap<String, String>();
		boolean is_signed_out = false;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(NotificationActivity.this);
			progressDialog.setMessage("Logging out...");
			progressDialog.show();
			progressDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			try {
				if (ConnectionManagerPromo
						.getConnectivityStatus(NotificationActivity.this) != HashStatic.TYPE_NOT_CONNECTED) {
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					String ID = registration_preference.getString(
							HashStatic.CUSTOMER_ID, null);
					registration_preference = getSharedPreferences(
							HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
					String email_sp = registration_preference.getString(
							HashStatic.HASH_email, null);
					regId = registration_preference.getString(
							HashStatic.Hash_GCMID, null);
					base_url = registration_preference.getString(
							HashStatic.HASH_baseUrl, null);

					String url_select = null;
					// ServerUtilities.unregister(NotificationActivity.this, ID,
					// email_sp);
					try {
						url_select = base_url
								+ BaseURL.API
								+ "notifications.php?authtoken="
								+ URLEncoder
										.encode(BaseURL.AUTH_TOKEN, "utf-8")
								+ "&JSONParam="
								+ URLEncoder.encode("{\"reg_id\":\"" + regId
										+ "\", " + "\"email\":\"" + email_sp
										+ "\"," + "\"user_type\":\"" + "seller"
										+ "\"," + "\"user_id\":\"" + user_id
										+ "\"," + "\"type\":\"" + "unregister"
										+ "\"}", "utf-8");
					} catch (UnsupportedEncodingException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"POST", params);
					System.out.println(jobj.toString());

					String message = jobj.getString("message");
					System.out.println(message);
					if (message != null && message.contains("success")) {
						is_signed_out = true;
					}

					System.out.println(jobj.toString());

				} else {
					makeToast("Internet Connection not Available");
				}

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

			if (is_signed_out) {
				registration_preference.edit().clear().commit();
				finish();
				startActivity(new Intent(NotificationActivity.this,
						LoginActivity.class));
			} else {
				makeToast("Something went wrong. Please retry");
			}

		}

	}

}
