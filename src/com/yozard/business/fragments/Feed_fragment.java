package com.yozard.business.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yozard.business.LoginActivity;
import com.yozard.business.NotificationActivity;
import com.yozard.business.R;
import com.yozard.business.adapter.Feed_xListview_Adapter;
import com.yozard.business.listview.XListView;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;
import com.yozard.business.utils.MyToast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Feed_fragment extends Fragment implements
		com.yozard.business.listview.XListView.IXListViewListener {

	public static final String COMAPNY_NAME = "company_name";
	public static final String ISSUED = "prized_date";// issued

	public static final String USED = "coupon_used";
	public static final String TOTALTIMES = "redeemable_times";

	public static final String DISCCOUNT = "amount";
	public static final String Instructions = "instruction";

	public static final String DAYS_TO_REDEEM = "days_to_redeem";

	public static final String COUPON_ID = "cp_id";
	public static final String DISCOUNT_INFO = "discount_info";
	
	
	
	static XListView listview_X;
	SharedPreferences registration_preference;
	String currency = null, user_id = null;
	String base_url;
	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();
    Feed_xListview_Adapter adapter;
    boolean firstTime=true;
    ProgressDialog progressDialog;
    int offset=0;
    
    boolean refressAsyncCall=false;
    
    boolean loadAsyncCall=false;
    LinearLayout feedHidden_Iv;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}
	
	public int modifyOffset(int offsetNow){
		int offsetNext=offset;
		
		offsetNext+=10;
		
		return offsetNext;
	}

	// making the toast
	public void makeToast(final String str) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MyToast.makeToast(str, getActivity());
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.feed_fragment_layout, null);

		listview_X = (XListView) v.findViewById(R.id.xListView);
		listview_X.setPullLoadEnable(true);
		
		feedHidden_Iv=(LinearLayout) v.findViewById(R.id.feedHidden_ln);
		
		
		registration_preference = getActivity().getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String userId_sp = registration_preference.getString(
				HashStatic.CUSTOMER_ID, null);
		String currency_sp = registration_preference.getString(
				HashStatic.HASH_currency, null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);

		user_id = userId_sp;
		currency = currency_sp;
		
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		progressDialog.setCancelable(false);

		new Async_fetchFeed().execute();
		return v;
	}

	public class Async_fetchFeed extends AsyncTask<String, Void, Void> {

		JSONParser_new jParser = null;
		HashMap<String, String> showmap = new HashMap<String, String>();
		boolean noNotification=false;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
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
					String ID = registration_preference.getString(
							HashStatic.CUSTOMER_ID, null);
					
					@SuppressWarnings("deprecation")
					String url_select = base_url
							+ BaseURL.API
							+ "feeds.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN,
									"utf-8") + "&JSONParam="
							+ URLEncoder.encode("{\"seller_id\":\"" + ID +"\", " + "\"offset\":\""+offset

							+ "\"}", "utf-8");
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"POST", params);
					if(jobj!=null)
					System.out.println(jobj.toString());

					JSONArray jArray = jobj.getJSONArray("feed");
					
					if(jArray.length()>0){
						//modifying offset
						offset=modifyOffset(offset);
						
						if(refressAsyncCall){
							Totalset_list.clear();
							refressAsyncCall=false;
						}
					}
					
					for (int i = 0; i < jArray.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject object = jArray.getJSONObject(i);
						
						String type=object.getString("type");
						
						if(type.equals("discount")){
							String str = object.getString("message");
							JSONObject jobj_temp = new JSONObject(str);
							
							map.put(HashStatic.HASH_cust_fname_m6,
									jobj_temp.getString(HashStatic.HASH_cust_fname_m6));
							map.put(HashStatic.HASH_cust_lname_m6,
									jobj_temp.getString(HashStatic.HASH_cust_lname_m6));
							map.put(HashStatic.HASH_cust_email_m6,
									jobj_temp.getString(HashStatic.HASH_cust_email_m6));
							/*map.put(HashStatic.HASH_store_name,
									jobj_temp.getString(HashStatic.HASH_store_name));*/
							map.put(HashStatic.HASH_cust_image_m6,
									jobj_temp.getString(HashStatic.HASH_cust_image_m6));
							map.put(HashStatic.HASH_effective_date, jobj_temp
									.getString(ISSUED));

							map.put(USED, jobj_temp.getString(USED));
							map.put(TOTALTIMES,
									jobj_temp.getString(TOTALTIMES));
							map.put(DISCCOUNT, jobj_temp.getString(DISCCOUNT));
							map.put(Instructions,
									jobj_temp.getString(Instructions));
							map.put(DAYS_TO_REDEEM,
									jobj_temp.getString(DAYS_TO_REDEEM));
							map.put(COMAPNY_NAME,
									jobj_temp.getString(COMAPNY_NAME));
							/*map.put(COUPON_ID, jobj_temp.getString(COUPON_ID));*/
							map.put(ISSUED, jobj_temp.getString(ISSUED));
							
							//puting the type value
							map.put(HashStatic.HASH_transaction_type, type);
							
						}
					///////////***BILL PAYMENT***************/////
						if(type.equals("bill payment")){
							String str = object.getString("message");
							JSONObject jobj_temp = new JSONObject(str);
							
							map.put(HashStatic.HASH_cust_fname_m6,
									jobj_temp.getString(HashStatic.HASH_cust_fname_m6));
							map.put(HashStatic.HASH_cust_lname_m6,
									jobj_temp.getString(HashStatic.HASH_cust_lname_m6));
							map.put(HashStatic.HASH_cust_email_m6,
									jobj_temp.getString(HashStatic.HASH_cust_email_m6));
							map.put(HashStatic.HASH_store_name,
									jobj_temp.getString(HashStatic.HASH_store_name));
							map.put(HashStatic.HASH_cust_image_m6,
									jobj_temp.getString(HashStatic.HASH_cust_image_m6));
							map.put(HashStatic.HASH_effective_date, jobj_temp
									.getString(HashStatic.HASH_effective_date));

							
							map.put(HashStatic.HASH_store_name,
									jobj_temp.getString(HashStatic.HASH_store_name));
							map.put(HashStatic.HASH_receiver_wallet_id,
									jobj_temp.getString(HashStatic.HASH_receiver_wallet_id));
							map.put(HashStatic.HASH_amount, jobj_temp
									.getString(HashStatic.HASH_amount));
							
							map.put(HashStatic.HASH_on_account_of,"on acc"); //jobj_temp
									//.getString(HashStatic.HASH_on_account_of));//fahmidul
							//puting the type value
							map.put(HashStatic.HASH_transaction_type, type);
							
						}
						
						/////////////////Withdrawal****////////////////////////////
						if(type.equals("withdrawal")){
							String str = object.getString("message");
							JSONObject jobj_temp = new JSONObject(str);
							
				
							map.put(HashStatic.HASH_store_name,
									jobj_temp.getString(HashStatic.HASH_store_name));
							
							map.put(HashStatic.HASH_effective_date, jobj_temp
									.getString(HashStatic.HASH_effective_date));
							map.put(HashStatic.HASH_transaction_date, jobj_temp
									.getString(HashStatic.HASH_transaction_date));

							
							map.put(HashStatic.HASH_store_name,
									jobj_temp.getString(HashStatic.HASH_store_name));
							map.put(HashStatic.HASH_store_mobile,
									jobj_temp.getString(HashStatic.HASH_store_mobile));

							map.put(HashStatic.HASH_amount, jobj_temp
									.getString(HashStatic.HASH_amount));
							
							map.put(HashStatic.HASH_company_image, jobj_temp
									.getString(HashStatic.HASH_company_image));
							
							
							map.put(HashStatic.HASH_bt_reference_id,
									jobj_temp.getString(HashStatic.HASH_bt_reference_id));
							map.put(HashStatic.HASH_method_name,
									jobj_temp.getString(HashStatic.HASH_method_name));
							map.put(HashStatic.HASH_bt_status,
									jobj_temp.getString(HashStatic.HASH_transaction_status));//fahmidul its new key
							
							//puting the type value
							map.put(HashStatic.HASH_transaction_type, type);
							
						}
					/*	map.put(HashStatic.HASH_cust_fname_m6,
								object.getString(HashStatic.HASH_cust_fname_m6));
						map.put(HashStatic.HASH_cust_lname_m6,
								object.getString(HashStatic.HASH_cust_lname_m6));
						map.put(HashStatic.HASH_cust_email_m6,
								object.getString(HashStatic.HASH_cust_email_m6));
						map.put(HashStatic.HASH_store_name,
								object.getString(HashStatic.HASH_store_name));
						map.put(HashStatic.HASH_cust_image_m6,
								object.getString(HashStatic.HASH_cust_image_m6));
						
						map.put(HashStatic.HASH_receiver_wallet_id,
								object.getString(HashStatic.HASH_receiver_wallet_id));
						map.put(HashStatic.HASH_on_account_of,
								object.getString(HashStatic.HASH_on_account_of));
						map.put(HashStatic.HASH_effective_date, object
								.getString(HashStatic.HASH_effective_date));
						map.put(HashStatic.HASH_transaction_type, object
								.getString(HashStatic.HASH_transaction_type));
						
						map.put(HashStatic.HASH_amount, object
								.getString(HashStatic.HASH_amount));
						map.put(HashStatic.HASH_store_mobile, object
								.getString(HashStatic.HASH_store_mobile));
						
						
						System.out.println(object
								.getString(HashStatic.HASH_transaction_type));
						
						map.put(HashStatic.HASH_bt_status,
								object.getString(HashStatic.HASH_bt_status));
						
						map.put(HashStatic.HASH_bt_reference_id,
								object.getString(HashStatic.HASH_bt_reference_id));
						map.put(HashStatic.HASH_method_name,
								object.getString(HashStatic.HASH_method_name));*/
						

						Totalset_list.add(map);
					}

					/*
					 * String message = jobj.getString("message");
					 * System.out.println(message); if (message != null &&
					 * message.contains("success")) {
					 * //showReset_SuccessfulAlert(); }
					 */

					System.out.println(jobj.toString());
					
					String messaage=jobj.getString("message");
					if(messaage.contains("No Notification"))
						noNotification=true;
					

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
			if(!Totalset_list.isEmpty()){
				
				feedHidden_Iv.setVisibility(View.GONE);
				listview_X.setVisibility(View.VISIBLE);
				
				if(loadAsyncCall){
					loadAsyncCall=false;
					if(adapter!=null)
					adapter.addItem(Totalset_list);
				}
				else{
				adapter=new Feed_xListview_Adapter(getActivity(), Totalset_list);
				listview_X.setAdapter(adapter);
				}
				//the progress dialog shall be dismissed
				if(firstTime){
				listview_X.setXListViewListener(Feed_fragment.this);
				
				
				//listview_X.setPullLoadEnable(false);
				
				firstTime=false;
				progressDialog.dismiss();
				}
				
				
			}else{
				if(firstTime){
					listview_X.setVisibility(View.GONE);
					feedHidden_Iv.setVisibility(View.VISIBLE);
				}
			}
			
			// if the feed is empty
			if(firstTime){
				firstTime=false;
				progressDialog.dismiss();
			}
			
			if(noNotification)
				listview_X.setPullLoadEnable(false);
			//finish loading	
			onLoad();
		}

	}
	
//	public void callRefresh(){
//		/*if(listview_X!=null)
//		listview_X.refreshCall_custom();*/
//		
//		offset=0;
//		refressAsyncCall=true;
//		NotificationActivity activity=(NotificationActivity) getActivity();
//		activity.doClearCount_notification();
//		new Async_fetchFeed().execute(); 
//	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadAsyncCall=true;
		new Async_fetchFeed().execute(); 
	}
	


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
//		offset=0;
//		refressAsyncCall=true;
//		NotificationActivity activity=(NotificationActivity) getActivity();
//		activity.doClearCount_notification();
//		
//		listview_X.setPullLoadEnable(true);
//		
//		new Async_fetchFeed().execute(); 
		
	}
	
	private void onLoad() {
		listview_X.stopRefresh();
		listview_X.stopLoadMore();
		
	}

}
