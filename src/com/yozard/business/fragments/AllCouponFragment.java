package com.yozard.business.fragments;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.yozard.business.R;
import com.yozard.business.adapter.PendingLiveCouponAdapter;
import com.yozard.business.model.ApprovedCouponResponse;
import com.yozard.business.model.Coupon;
import com.yozard.business.model.PendingCouponResponse;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.MyToast;

public class AllCouponFragment extends Fragment {

	private static final String ARG_PARAM = "coupon_type";
	Context context;
	ListView lvLiveCoupons;
	// SwipeRefreshLayout swipeRefreshLayout;
	boolean isPending = true;

	Vector<Coupon> allCoupons = new Vector<Coupon>();
	PendingLiveCouponAdapter pendingCouponAdapter;

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
		View v = inflater.inflate(R.layout.fragment_all_coupon, null);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initUI(view);
		super.onViewCreated(view, savedInstanceState);
	}

	public static AllCouponFragment newInstance(boolean isPending) {
		AllCouponFragment fragment = new AllCouponFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_PARAM, isPending);
		fragment.setArguments(args);
		return fragment;
	}

	private void initUI(View v) {
		lvLiveCoupons = (ListView) v.findViewById(R.id.lvLiveCoupons);
		// swipeRefreshLayout = (SwipeRefreshLayout) v
		// .findViewById(R.id.swipe_refresh_layout);
		registration_preference = getActivity().getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String json = registration_preference.getString(
				HashStatic.Hash_Pending_Coupons, "");
		Gson g = new Gson();
		if (isPending) {
			PendingCouponResponse pendingCouponResponse = g.fromJson(json,
					PendingCouponResponse.class);
			allCoupons = pendingCouponResponse.getPending_coupon()
					.getLive_coupon();
		}else{
			ApprovedCouponResponse approvedCouponResponse = g.fromJson(json,
					ApprovedCouponResponse.class);
			allCoupons = approvedCouponResponse.getPending_coupon()
					.getLive_coupon();
		}
		pendingCouponAdapter = new PendingLiveCouponAdapter(context,
				R.layout.pending_coupon_row, allCoupons, isPending);
		if (allCoupons.size() > 0) {
			lvLiveCoupons.setAdapter(pendingCouponAdapter);
		} else {
			MyToast.makeToast(
					"You don't have any pending live coupons to approve!!",
					getActivity());
		}
	}

}
