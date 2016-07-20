package com.yozard.business.fragments;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.yozard.business.R;
import com.yozard.business.adapter.PendingHappyCouponAdapter;
import com.yozard.business.model.ApprovedCouponResponse;
import com.yozard.business.model.Coupon;
import com.yozard.business.model.PendingCouponResponse;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.MyToast;

public class HappyHourFragment extends Fragment {

	private static final String ARG_PARAM = "coupon_type";
	Context context;
	ListView lvHappyCoupon;
	boolean isPending = true;
	// SwipeRefreshLayout swipeRefreshLayout;

	Vector<Coupon> happyCoupons = new Vector<Coupon>();
	PendingHappyCouponAdapter pendingHappyCouponAdapter;

	SharedPreferences registration_preference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();
		if (getArguments() != null) {
			isPending = getArguments().getBoolean(ARG_PARAM, true);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_happy_hour, null);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initUI(view);
		super.onViewCreated(view, savedInstanceState);
	}

	public static HappyHourFragment newInstance(boolean isPending) {
		HappyHourFragment fragment = new HappyHourFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_PARAM, isPending);
		fragment.setArguments(args);
		return fragment;
	}

	private void initUI(View v) {
		Log.e("Happy Hour", "Is ON!!");
		lvHappyCoupon = (ListView) v.findViewById(R.id.lvHappyCoupon);
		// swipeRefreshLayout = (SwipeRefreshLayout)
		// v.findViewById(R.id.swipe_refresh_layout);
		registration_preference = getActivity().getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String json = registration_preference.getString(
				HashStatic.Hash_Pending_Coupons, "");
		Gson g = new Gson();
		if (isPending) {
			PendingCouponResponse pendingCouponResponse = g.fromJson(json,
					PendingCouponResponse.class);
			happyCoupons = pendingCouponResponse.getPending_coupon()
					.getHappy_hour();
		}else{
			ApprovedCouponResponse approvedCouponResponse = g.fromJson(json,
					ApprovedCouponResponse.class);
			happyCoupons = approvedCouponResponse.getPending_coupon()
					.getHappy_hour();
		}
		pendingHappyCouponAdapter = new PendingHappyCouponAdapter(context,
				R.layout.pending_coupon_row, happyCoupons, isPending);
		if (happyCoupons.size() > 0) {
			lvHappyCoupon.setAdapter(pendingHappyCouponAdapter);
		} else {
			MyToast.makeToast(
					"You don't have any pending happy hour coupons to approve!!",
					getActivity());
		}
	}

}
