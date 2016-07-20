package com.yozard.business.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.yozard.business.NotificationActivity;
import com.yozard.business.R;
import com.yozard.business.adapter.Transactions_xlistviewAdapter.ViewHolder;
import com.yozard.business.utils.Calculations;
import com.yozard.business.utils.HashStatic;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StoreAdapter extends BaseAdapter {

	/*public StoreAdapter(Context context,) {
		// TODO Auto-generated constructor stub
	}*/
	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();
	Context ctx;
	
	LayoutInflater mInflater;
	

	String currency = null, user_id = null, base_url = null;
	SharedPreferences registration_preference;
	
	
	public StoreAdapter(Context activity,
			ArrayList<HashMap<String, String>> totalset_list) {
		// TODO Auto-generated constructor stub
		Totalset_list=totalset_list;
		ctx=activity;
		
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		registration_preference = ctx.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String userId_sp = registration_preference.getString(
				HashStatic.CUSTOMER_ID, null);
		String currency_sp = registration_preference.getString(
				HashStatic.HASH_currency, null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);

		// editor=registration_preference.edit();

		user_id = userId_sp;
		currency = currency_sp;
	}

	public void addItem(ArrayList<HashMap<String, String>> total) {
		// TODO Auto-generated method stub
		Totalset_list = total;

		getViewTypeCount();
		notifyDataSetChanged();
	}
	
	public static class ViewHolder {
		// bill
		public TextView store_name_tv;
		public TextView store_wallet_tv;
		public TextView store_bill_tv;
		public TextView store_company_tv;

	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Totalset_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.store_list_item, null);

			holder.store_name_tv = (TextView) convertView
					.findViewById(R.id.store_name_tv);
			holder.store_wallet_tv = (TextView) convertView
					.findViewById(R.id.store_wallet_tv);
			holder.store_bill_tv = (TextView) convertView
					.findViewById(R.id.store_bill_tv);

			holder.store_company_tv = (TextView) convertView
					.findViewById(R.id.store_company_tv);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.store_name_tv.setText(Totalset_list.get(position).get(HashStatic.HASH_store_name1));
		holder.store_wallet_tv.setText("Wallet Id: "+Totalset_list.get(position).get(HashStatic.HASH_wallet_id ));
		holder.store_bill_tv.setText(currency+Calculations.formatString_ToString(
				Totalset_list.get(position).get(HashStatic.HASH_store_balance)));
		holder.store_company_tv.setText(Totalset_list.get(position).get(HashStatic.HASH_company_name));
		
		final String store_id=Totalset_list.get(position).get(HashStatic.HASH_store_id);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NotificationActivity ac=(NotificationActivity) ctx;
				ac.setStoreId(store_id);
				ac.loadFragment(2);
			}
		});
		
		
		return convertView;
	}

	

}
