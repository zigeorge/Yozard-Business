package com.yozard.business.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;



import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yozard.business.R;
import com.yozard.business.adapter.Feed_xListview_Adapter.ViewHolder;
import com.yozard.business.utils.Calculations;
import com.yozard.business.utils.HashStatic;

public class Transactions_xlistviewAdapter extends ArrayAdapter<Object>{

	public  final String HASH_cust_fname = "cust_fname";
	public  final String HASH_cust_lname = "cust_lname";
	public  final String HASH_cust_email = "cust_email";
	public  final String HASH_cust_image = "cust_image";
	public  final String HASH_store_name = "store_name";
	public  final String HASH_receiver_wallet_id = "receiver_wallet_id";
	public  final String HASH_on_account_of= "on_account_of";
	public  final String HASH_amount = "amount";
	public  final String HASH_current_balance = "current_balance";
	public  final String HASH_effective_date = "effective_date";
	
	private static final int TYPE_transactions = 0;

	private static final int TYPE_MAX_COUNT = 1;
	
	private LayoutInflater mInflater;

	private TreeSet mSeparatorsSet = new TreeSet();

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	String currency = null, user_id = null, base_url = null;
	SharedPreferences registration_preference;
	
	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();
	
	public Transactions_xlistviewAdapter(Context ctx,
			/*
			 * ArrayList<HashMap<String,String>> bill_list,
			 * ArrayList<HashMap<String,String>>
			 * withdrawal_list,ArrayList<HashMap<String,String>> callrequst_list,
			 */
			ArrayList<HashMap<String, String>> total) {

				super(ctx, 0);

				mInflater = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
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

				Totalset_list = total;

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
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		
		return TYPE_transactions;
	}
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	@Override
	public int getCount() {
		return Totalset_list.size();
	}

	
	public void addItem(ArrayList<HashMap<String, String>> total) {
		// mData.add(item);
		Totalset_list = total;

		getViewTypeCount();
		notifyDataSetChanged();
	}
	
	
	
	
	public static class ViewHolder {
		// bill
		public TextView cust_fname_tv;
		public CircularImageView userId_IV;
		public TextView cust_email_tv;
		public TextView cust_image_tv;
		public TextView store_name_tv;
		public TextView receiver_wallet_id_tv;
		public TextView on_account_of_tv;
		public TextView amount_tv;
		public TextView current_balance_tv;
		public TextView effective_date_tv;
		public TextView just_time_tv;
	}
	
	private int lastPosition = -1 ;
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		HashMap<String, String> maps = Totalset_list.get(position);
		// int type_my = getItemViewType2(maps);

		int type = getItemViewType(position);

		System.out.println("getView " + position + " " + convertView
				+ " type = " + type);

		switch (type) {
		case TYPE_transactions:
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.transactions_listitem, null);

				holder.cust_fname_tv = (TextView) convertView
						.findViewById(R.id.userName_tv);
				holder.cust_email_tv = (TextView) convertView
						.findViewById(R.id.ID_tv);
				holder.store_name_tv = (TextView) convertView
						.findViewById(R.id.shopName_tv);

				holder.receiver_wallet_id_tv = (TextView) convertView
						.findViewById(R.id.walletID_tv);
				holder.amount_tv = (TextView) convertView
						.findViewById(R.id.priceTransactions_tv);
				holder.effective_date_tv = (TextView) convertView 
						.findViewById(R.id.date_tv);
				holder.just_time_tv = (TextView) convertView
						.findViewById(R.id.time_tv);
				holder.on_account_of_tv = (TextView) convertView
						.findViewById(R.id.onaccountTransactions_tv);
				
				holder.userId_IV=(CircularImageView) convertView.findViewById(R.id.transaction_user_IV);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.on_account_of_tv.setText(Totalset_list.get(position).get(HASH_on_account_of));
			
			holder.just_time_tv.setText(customDate(Totalset_list.get(position).get(
					HASH_effective_date)));
			
			holder.cust_fname_tv.setText(Totalset_list.get(position).get(
					HASH_cust_fname)
					+ " "
					+ Totalset_list.get(position).get(
							HASH_cust_lname));

			holder.cust_email_tv.setText("User Id: "+Totalset_list.get(position).get(
					HASH_cust_email));
			
			holder.store_name_tv.setText(Totalset_list.get(position).get(
					HASH_store_name));
			holder.receiver_wallet_id_tv.setText(Totalset_list.get(position).get(
					HASH_receiver_wallet_id));
			holder.amount_tv.setText(currency
					+ Calculations.formatString_ToString(Totalset_list.get(position).get(HASH_amount)));
			
			String date1=Totalset_list.get(
					position).get(HashStatic.HASH_effective_date);
			
			holder.effective_date_tv.setText(getDateBig(getDate(date1)) + " " + getDateSmallWith_year(getDate(date1))+
					"  "+getTime(Totalset_list.get(position)
							.get(HASH_effective_date)));
			
			

			imageLoader.displayImage(
					base_url + maps.get(HASH_cust_image),
					holder.userId_IV, options);
			break;
		}
		
		Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
		convertView.startAnimation(animation);
		lastPosition = position;
		return convertView;
	}
	
	
	// ///////////////////DATE RELATED WORKS//////////////////
		public String getDateBig(String date) {
			String number = "";
			String[] dates = { "" };
			if (date != null) {
				dates = date.split("-");
				if (dates.length > 0)
					number = dates[0];
			}

			return number;
		}

		public String getDateSmall(String date) {
			String Month = "";
			String[] dates = { "" };
			if (date != null) {
				dates = date.split("-");
				if (dates.length > 0)
					Month = dates[1];
			}

			if (Month != null && !Month.equals("")) {
				if (Month.equals("01"))
					Month = "Jan";
				else if (Month.equals("02"))
					Month = "Feb";
				else if (Month.equals("03"))
					Month = "Mar";
				else if (Month.equals("04"))
					Month = "Apr";
				else if (Month.equals("05"))
					Month = "May";
				else if (Month.equals("06"))
					Month = "Jun";
				else if (Month.equals("07"))
					Month = "Jul";
				else if (Month.equals("08"))
					Month = "Aug";
				else if (Month.equals("09"))
					Month = "Sep";
				else if (Month.equals("10"))
					Month = "Oct";
				else if (Month.equals("11"))
					Month = "Nov";
				else if (Month.equals("12"))
					Month = "Dec";
			}

			return Month;
		}
		public String getDateSmallWith_year(String date) {
			String Month = "";
			String year = "";
			String[] dates = { "" };
			if (date != null) {
				dates = date.split("-");
				if (dates.length > 0){
					Month = dates[1];
					year=dates[2];	
				}
			}

			if (Month != null && !Month.equals("")) {
				if (Month.equals("01"))
					Month = "Jan";
				else if (Month.equals("02"))
					Month = "Feb";
				else if (Month.equals("03"))
					Month = "Mar";
				else if (Month.equals("04"))
					Month = "Apr";
				else if (Month.equals("05"))
					Month = "May";
				else if (Month.equals("06"))
					Month = "Jun";
				else if (Month.equals("07"))
					Month = "Jul";
				else if (Month.equals("08"))
					Month = "Aug";
				else if (Month.equals("09"))
					Month = "Sep";
				else if (Month.equals("10"))
					Month = "Oct";
				else if (Month.equals("11"))
					Month = "Nov";
				else if (Month.equals("12"))
					Month = "Dec";
			}

			return Month+" " +year;
		}
		

		private String getDate(String myDate) {
			String[] dates;
			Calendar c = Calendar.getInstance();
			System.out.println("Current time => " + c.getTime());
			try {
				if (myDate != null) {
					dates = myDate.split("\\s+");
					myDate = dates[0];
				}
				System.out.println(myDate);

				// String formattedDate = df.format(myDate);
				DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = null;
				try {
					date = inputFormat.parse(myDate);
					String outputDateStr = outputFormat.format(date);
					myDate = outputDateStr;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return myDate;
		}

		private String getTime(String myDate) {
			String[] dates;
			Calendar c = Calendar.getInstance();
			System.out.println("Current time => " + c.getTime());
			try {
				if (myDate != null) {
					dates = myDate.split("\\s+");
					myDate = dates[1];
				}
				System.out.println(myDate);

				// String formattedDate = df.format(myDate);
				DateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
				DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
				Date date = null;
				try {
					date = inputFormat.parse(myDate);
					String outputDateStr = outputFormat.format(date);
					myDate = outputDateStr;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Current time => " + myDate);

			return myDate;
		}

		public String customDate(String date) {
			String new_date = null;
			Calendar c = Calendar.getInstance();
			System.out.println("Current time => " + c.getTime());

			long millisStart = Calendar.getInstance().getTimeInMillis();
			long millisEnd = 0;
			// String formattedDate = df.format(myDate);
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dates = null;
			try {
				dates = inputFormat.parse(date);
				millisEnd = dates.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (dates != null) {
				Calendar c1 = Calendar.getInstance(); // today

				Calendar cYesterDay = Calendar.getInstance();
				cYesterDay.add(Calendar.DAY_OF_YEAR, -1); // yesterday

				Calendar cWeek = Calendar.getInstance();
				cWeek.add(Calendar.DAY_OF_YEAR, -7);

				Calendar cMydate = Calendar.getInstance();
				cMydate.setTime(dates); // my date

			if(c.get(Calendar.YEAR) == cMydate.get(Calendar.YEAR)
						&& c.get(Calendar.DAY_OF_YEAR) == cMydate
						.get(Calendar.DAY_OF_YEAR))
				new_date=getTime(date);
				
				// checking yesterday
			else if (cYesterDay.get(Calendar.YEAR) == cMydate.get(Calendar.YEAR)
						&& cYesterDay.get(Calendar.DAY_OF_YEAR) == cMydate
								.get(Calendar.DAY_OF_YEAR)) {
					new_date = "yesterday";
				}
				// within one week
				else if (cWeek.get(Calendar.YEAR) == cMydate.get(Calendar.YEAR)
						&& (c.get(Calendar.DAY_OF_YEAR) - cMydate
								.get(Calendar.DAY_OF_YEAR)) <= 7) {
					String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dates);
					new_date = dayOfWeek +", "+getTime(date);
				}

				// more than one week
				else if (c.get(Calendar.YEAR) == cMydate.get(Calendar.YEAR)
						&& (c.get(Calendar.DAY_OF_YEAR) - cMydate
								.get(Calendar.DAY_OF_YEAR)) > 7) {

					new_date = getDateBig(getDate(date)) + " " + getDateSmall(getDate(date))
							+ ", " + getTime(date);
				}

			}
			return new_date;
		}
	
	
}
