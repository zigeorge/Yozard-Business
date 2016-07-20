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

import com.yozard.business.NotificationActivity;
import com.yozard.business.R;
import com.yozard.business.adapter.Feed_xListview_Adapter;
import com.yozard.business.adapter.Transactions_xlistviewAdapter;
import com.yozard.business.fragments.Feed_fragment.Async_fetchFeed;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Feed_transactions extends Fragment implements
		com.yozard.business.listview.XListView.IXListViewListener {

	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	
	public  final String HASH_cust_fname = "cust_fname";
	public  final String HASH_cust_lname = "cust_lname";
	public  final String HASH_cust_email = "cust_email";
	public  final String HASH_cust_image = "cust_image";
	public  final String HASH_store_name = "store_name";
	public  final String HASH_receiver_wallet_id = "receiver_wallet_id";
	public  final String HASH_on_account_of= "on_account_of";
	public  final String amount = "amount";
	public  final String current_balance = "current_balance";
	public  final String effective_date = "effective_date";
	
	static XListView listview_X;
	SharedPreferences registration_preference;
	String currency = null, user_id = null;
	String base_url;
	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();
	Transactions_xlistviewAdapter adapter;
	boolean firstTime = true;
	ProgressDialog progressDialog;
	int offset = 0;
	String storeId="";

	boolean refressAsyncCall = false;

	boolean loadAsyncCall = false;
	
	LinearLayout trransactions_Iv;

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
	
	public static Feed_transactions getInstance(String storeId){
		Feed_transactions frag=new Feed_transactions();
		
		Bundle bdl = new Bundle();
	    bdl.putString(EXTRA_MESSAGE, storeId);
	
	    frag.setArguments(bdl);
		
		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle bundle=getArguments(); 	
		storeId=bundle.getString(EXTRA_MESSAGE);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.transactions_fragment_layout, null);

		listview_X = (XListView) v.findViewById(R.id.xListView_transaction);
		listview_X.setPullLoadEnable(true);
		
		trransactions_Iv=(LinearLayout) v.findViewById(R.id.trransactionshidden_Iv);
		
		
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
							+ "seller-transactions.php?authtoken="
							+ URLEncoder.encode(
									"b26a83a4bfd05a6b68383a2df887012d18e45a81",
									"utf-8") + "&JSONParam="
							+ URLEncoder.encode("{\"seller_id\":\"" + ID +"\", " +
									"\"store_id\":\""+storeId+"\", "+
									"\"offset\":\""+offset

							+ "\"}", "utf-8");
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"GET", params);
					if(jobj!=null)
					System.out.println(jobj.toString());
					
					try{
					JSONArray jArray = jobj.getJSONArray("transactions");
					
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

						map.put(HASH_cust_fname ,
								object.getString(HASH_cust_fname));
						map.put(HASH_cust_lname ,
								object.getString(HASH_cust_lname));
						map.put( HASH_cust_email,
								object.getString( HASH_cust_email));
						map.put(HASH_cust_image ,
								object.getString(HASH_cust_image ));
						map.put(HASH_store_name ,
								object.getString(HASH_store_name ));
						
						map.put(HASH_receiver_wallet_id ,
								object.getString(HASH_receiver_wallet_id ));
						
						map.put(HASH_on_account_of,
								object.getString(HASH_on_account_of));
						map.put(amount,
								object.getString(amount));
						map.put(current_balance,
								object.getString(current_balance));
						map.put(effective_date ,
								object.getString(effective_date));
						

						Totalset_list.add(map);
					}
					
					} catch (JSONException ee) {
						// TODO Auto-generated catch block
						ee.printStackTrace();
					}

					/*
					 * String message = jobj.getString("message");
					 * System.out.println(message); if (message != null &&
					 * message.contains("success")) {
					 * //showReset_SuccessfulAlert(); }
					 */

					System.out.println(jobj.toString());
					
					String messaage=jobj.getString("message");
					if(messaage.contains("No transaction to show"))
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
				if(loadAsyncCall){
					loadAsyncCall=false;
					if(adapter!=null)
					adapter.addItem(Totalset_list);
				}
				else{
				adapter=new Transactions_xlistviewAdapter(getActivity(), Totalset_list);
				listview_X.setAdapter(adapter);
				}
				//the progress dialog shall be dismissed
				if(firstTime){
				listview_X.setXListViewListener(Feed_transactions.this);
				
				//listview_X.setPullLoadEnable(false);
				
				firstTime=false;
				progressDialog.dismiss();
				}
				
				
			}else{
				if(firstTime){
					listview_X.setVisibility(View.GONE);
					trransactions_Iv.setVisibility(View.VISIBLE);
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
	
	
	
	
	public void callRefresh(){
		/*if(listview_X!=null)
		listview_X.refreshCall_custom();*/		
//		offset=0;
//		refressAsyncCall=true;
//		NotificationActivity activity=(NotificationActivity) getActivity();
//		activity.doClearCount_notification();
//		new Async_fetchFeed().execute(); 
	}

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
//		listview_X.setPullLoadEnable(true);
//		new Async_fetchFeed().execute(); 

	}
	
	private void onLoad() {
		listview_X.stopRefresh();
		listview_X.stopLoadMore();
	}

}