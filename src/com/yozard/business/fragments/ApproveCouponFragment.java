package com.yozard.business.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yozard.business.R;
import com.yozard.business.model.ApprovedCouponResponse;
import com.yozard.business.model.PendingCouponResponse;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;
import com.yozard.business.utils.MyToast;
import com.yozard.business.utils.TypeFace_MY;

public class ApproveCouponFragment extends Fragment {

	private static final String ARG_PARAM = "coupon_type";
	SharedPreferences registration_preference;
	String baseUrl, userId = "";

	boolean isPendingCoupon = true;

	MyPagerAdapter adapterViewPager;

	Typeface tf_roboto_condensed, tf_roboto, tf_roboto_thin;

	TextView tvLiveCoupon, tvHappyHour;
	View vUnderLineLiveCoupon, vUnderLineHappyHour;
	ViewPager vpPager;

	View viewInstance;

	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.approve_coupon_fragment, null);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();

		registration_preference = context.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		userId = registration_preference
				.getString(HashStatic.CUSTOMER_ID, null);
		baseUrl = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);

		if (getArguments() != null) {
			isPendingCoupon = getArguments().getBoolean(ARG_PARAM, true);
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		viewInstance = view;
		new Async_GetPendingCoupon().execute();
		super.onViewCreated(view, savedInstanceState);
	}

	public static ApproveCouponFragment newInstance(boolean isPendingCoupon) {
		ApproveCouponFragment fragment = new ApproveCouponFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_PARAM, isPendingCoupon);
		fragment.setArguments(args);
		return fragment;
	}

	private void initUI(View v) {
		tf_roboto_condensed = TypeFace_MY.getRoboto_condensed(context);
		tf_roboto = TypeFace_MY.getRoboto(context);
		tf_roboto_thin = TypeFace_MY.getRobotoThin(context);
		tvLiveCoupon = (TextView) v.findViewById(R.id.tvLiveCoupon);
		tvLiveCoupon.setTextColor(Color.WHITE);
		tvLiveCoupon.setTypeface(tf_roboto_condensed);
		vUnderLineLiveCoupon = (View) v.findViewById(R.id.vUnderLineLivecoupon);
		tvLiveCoupon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				vpPager.setCurrentItem(0);
			}
		});
		tvHappyHour = (TextView) v.findViewById(R.id.tvHappyHour);
		tvHappyHour.setTypeface(tf_roboto_condensed);
		vUnderLineHappyHour = (View) v.findViewById(R.id.vUnderLineHappyHour);
		tvHappyHour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				vpPager.setCurrentItem(1);
			}
		});
		vpPager = (ViewPager) v.findViewById(R.id.pagerPendingCoupon);
		vpPager.setOffscreenPageLimit(2);
		adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
		vpPager.setAdapter(adapterViewPager);
		vpPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				changeButtonTabBG(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});

	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		private int NUM_ITEMS = 2;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show FirstFragment
				return AllCouponFragment.newInstance(isPendingCoupon);
			case 1: // Fragment # 0 - This will show FirstFragment different
				return HappyHourFragment.newInstance(isPendingCoupon);
			default:
				return null;
			}
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

	}

	private void changeButtonTabBG(int position) {
		switch (position) {
		case 0:
			HashStatic.HASH_SEARCH_LIVE = true;
			tvLiveCoupon.setSelected(true);
			tvHappyHour.setSelected(false);
			vUnderLineLiveCoupon.setVisibility(View.VISIBLE);
			vUnderLineHappyHour.setVisibility(View.GONE);
			break;
		case 1:
			HashStatic.HASH_SEARCH_LIVE = false;
			tvHappyHour.setSelected(true);
			tvLiveCoupon.setSelected(false);
			vUnderLineHappyHour.setVisibility(View.VISIBLE);
			vUnderLineLiveCoupon.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public class Async_GetPendingCoupon extends AsyncTask<String, Void, Void> {

		JSONParser_new jParser = null;
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog = new ProgressDialog(context);
			if (isPendingCoupon) {
				progressDialog.setMessage("Loading Pending Coupons...");
			}else{
				progressDialog.setMessage("Loading Approved Coupons...");
			}
			progressDialog.show();
			progressDialog.setCancelable(false);
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			try {
				if (ConnectionManagerPromo.getConnectivityStatus(getActivity()) != HashStatic.TYPE_NOT_CONNECTED) {
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					registration_preference = getActivity()
							.getSharedPreferences(HashStatic.PREF_NAME_REG,
									Context.MODE_PRIVATE);
					Editor editor = registration_preference.edit();

					@SuppressWarnings("deprecation")
					String url_select = "";
					if(isPendingCoupon){
						url_select = baseUrl
								+ BaseURL.API
								+ "pending-coupons.php?authtoken="
								+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
								+ "&JSONParam="
								+ URLEncoder.encode("{\"seller_id\":\"" + userId
										+ "\"}", "utf-8");
					}else{
						url_select = baseUrl
								+ BaseURL.API
								+ "approved-coupons.php?authtoken="
								+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
								+ "&JSONParam="
								+ URLEncoder.encode("{\"seller_id\":\"" + userId
										+ "\"}", "utf-8");
					}
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"GET", params);
					if (jobj != null)
						System.out.println(jobj.toString());

					String messaage = jobj.getString("message");
					Gson g = new Gson();
					if (messaage.contains("success")) {
						// pendingCouponResponse = g.fromJson(jobj.toString(),
						// Pending)
						editor.putString(HashStatic.Hash_Pending_Coupons,
								jobj.toString()).commit();

					}

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
			progressDialog.dismiss();
			initUI(viewInstance);
			super.onPostExecute(result);
		}

	}

	public void makeToast(final String str) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MyToast.makeToast(str, getActivity());
			}
		});
	}
}
