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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yozard.business.NotificationActivity;
import com.yozard.business.R;
import com.yozard.business.adapter.StoreAdapter;
import com.yozard.business.adapter.Transactions_xlistviewAdapter;
import com.yozard.business.fragments.Feed_transactions.Async_fetchFeed;
import com.yozard.business.listview.XListView;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;
import com.yozard.business.utils.MyToast;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Storelist_fragment extends Fragment implements
com.yozard.business.listview.XListView.IXListViewListener{

	XListView listview_X;
	
	String currency = null, user_id = null, base_url = null;
	SharedPreferences registration_preference;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
	private LayoutInflater mInflater;
	
	boolean refressAsyncCall = false;

	boolean loadAsyncCall = false;
	int offset = 0;
	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();
	
	StoreAdapter adapter;
	boolean firstTime = true;
	ProgressDialog progressDialog;
	
	LinearLayout storeHidden_Ln;

	public int modifyOffset(int offsetNow){
		int offsetNext=offset;
		
		offsetNext+=10;
		
		return offsetNext;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.upload)
				.showImageOnLoading(R.drawable.upload)
				.showImageOnFail(R.drawable.upload)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				// .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		/*
		 * billset_list=bill_list; withdrawalset_list=withdrawal_list;
		 * callRequestset_list=callrequst_list;
		 */

		registration_preference = getActivity().getSharedPreferences(
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.store_fragment, null);
		
		listview_X=(XListView) v.findViewById(R.id.xListView_storeList);
		
		storeHidden_Ln=(LinearLayout) v.findViewById(R.id.storeHidden_Ln);
		
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
							+ "stores.php?authtoken="
							+ URLEncoder.encode(
									BaseURL.AUTH_TOKEN,
									"utf-8") + "&JSONParam="
							+ URLEncoder.encode("{\"seller_id\":\"" + ID +"\", " + "\"offset\":\""+offset

							+ "\"}", "utf-8");
					System.out.println(url_select);

					jParser = new JSONParser_new();
					JSONObject jobj = jParser.makeHttpRequest(url_select,
							"GET", params);
					if(jobj!=null)
					System.out.println(jobj.toString());
					
					try{
					JSONArray jArray = jobj.getJSONArray("stores");
					
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

						map.put(HashStatic.HASH_store_id ,
								object.getString(HashStatic.HASH_store_id));
						map.put(HashStatic.HASH_store_name1 ,
								object.getString(HashStatic.HASH_store_name1));
						map.put( HashStatic.HASH_store_balance,
								object.getString( HashStatic.HASH_store_balance));
						map.put(HashStatic.HASH_wallet_id ,
								object.getString(HashStatic.HASH_wallet_id ));
						map.put(HashStatic.HASH_company_name ,
								object.getString(HashStatic.HASH_company_name ));
						
		
						

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
				adapter=new StoreAdapter(getActivity(), Totalset_list);
				listview_X.setAdapter(adapter);
				}
				//the progress dialog shall be dismissed
				if(firstTime){
				listview_X.setXListViewListener(Storelist_fragment.this);
				
				//listview_X.setPullLoadEnable(false);
				
				firstTime=false;
				progressDialog.dismiss();
				}
				
				
			}else{
				if(firstTime){
					listview_X.setVisibility(View.GONE);
					storeHidden_Ln.setVisibility(View.VISIBLE);
				}
			}
			// if the feed is empty
			if(firstTime){
				firstTime=false;
				progressDialog.dismiss();
			}
			
			if(noNotification)
				listview_X.setPullLoadEnable(false);
			
			listview_X.setPullLoadEnable(false);
			//finish loading	
			onLoad();
		}

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


	public void callRefresh(){
		/*if(listview_X!=null)
		listview_X.refreshCall_custom();*/		
		offset=0;
		refressAsyncCall=true;
		NotificationActivity activity=(NotificationActivity) getActivity();
		activity.doClearCount_notification();
		new Async_fetchFeed().execute(); 
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
		offset=0;
		refressAsyncCall=true;
		NotificationActivity activity=(NotificationActivity) getActivity();
		activity.doClearCount_notification();	
		listview_X.setPullLoadEnable(true);
		new Async_fetchFeed().execute(); 

	}
	
	private void onLoad() {
		listview_X.stopRefresh();
		listview_X.stopLoadMore();
	}
	
}
