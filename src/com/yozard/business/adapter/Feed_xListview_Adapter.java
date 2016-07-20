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

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yozard.business.R;
import com.yozard.business.utils.Calculations;
import com.yozard.business.utils.HashStatic;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Feed_xListview_Adapter extends ArrayAdapter<Object> {

	public static final String COMAPNY_NAME = "company_name";
	public static final String ISSUED = "prized_date";// issued

	public static final String USED = "coupon_used";
	public static final String TOTALTIMES = "redeemable_times";

	public static final String DISCCOUNT = "amount";
	public static final String Instructions = "instruction";

	public static final String DAYS_TO_REDEEM = "days_to_redeem";

	public static final String COUPON_ID = "cp_id";
	public static final String DISCOUNT_INFO = "discount_info";
	
	
	private static final String BILL_STRING = "bill payment";
	private static final String WITHDRAWAL_STRING = "withdrawal";
	private static final String CALLBACK_STRING = "discount";

	private static final int TYPE_SEPARATOR_bill = 0;
	private static final int TYPE_SEPARATOR_withdraw = 1;
	private static final int TYPE_SEPARATOR_discount = 2;

	private static final int TYPE_MAX_COUNT = 3;

	private LayoutInflater mInflater;

	private TreeSet mSeparatorsSet = new TreeSet();

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	String currency = null, user_id = null, base_url = null;
	SharedPreferences registration_preference;

	/*
	 * ArrayList<HashMap<String,String>> billset_list=new
	 * ArrayList<HashMap<String,String>>(); ArrayList<HashMap<String,String>>
	 * withdrawalset_list=new ArrayList<HashMap<String,String>>();
	 * ArrayList<HashMap<String,String>> callRequestset_list=new
	 * ArrayList<HashMap<String,String>>();
	 */

	ArrayList<HashMap<String, String>> Totalset_list = new ArrayList<HashMap<String, String>>();

	public Feed_xListview_Adapter(Context ctx,
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

	public void addItem(ArrayList<HashMap<String, String>> total) {
		// mData.add(item);
		Totalset_list = total;

		getViewTypeCount();
		notifyDataSetChanged();
	}

	/*
	 * public void addSeparatorItem(final String item) { // mData.add(item); //
	 * save separator position mSeparatorsSet.add(mData.size() - 1);
	 * notifyDataSetChanged(); }
	 */

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		HashMap<String, String> maps = Totalset_list.get(position);
		String selector = maps.get(HashStatic.HASH_transaction_type);

		if (selector.equals(BILL_STRING))
			return TYPE_SEPARATOR_bill;

		else if (selector.equals(WITHDRAWAL_STRING))
			return TYPE_SEPARATOR_withdraw;

		else if (selector.equals(CALLBACK_STRING))
			return TYPE_SEPARATOR_discount;
		return position;
	}

	public int getItemViewType2(HashMap<String, String> maps) {

		// HashMap<String, String> maps = new HashMap<String, String>();

		String selector = maps.get(HashStatic.HASH_transaction_type);

		if (selector.equals(BILL_STRING))
			return TYPE_SEPARATOR_bill;

		else if (selector.equals(WITHDRAWAL_STRING))
			return TYPE_SEPARATOR_withdraw;

		else if (selector.equals(CALLBACK_STRING))
			return TYPE_SEPARATOR_discount;

		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return Totalset_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
		case TYPE_SEPARATOR_bill:
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.feed_listitem_1, null);

				holder.time_tv = (TextView) convertView
						.findViewById(R.id.time_tv);
				holder.username_tv = (TextView) convertView
						.findViewById(R.id.username_tv);
				holder.userEmail_tv = (TextView) convertView
						.findViewById(R.id.userEmail_tv);

				holder.Itemname_tv = (TextView) convertView
						.findViewById(R.id.Itemname_tv);
				holder.codenumber_tv = (TextView) convertView
						.findViewById(R.id.codenumber_tv);
				holder.amount_bill_tv = (TextView) convertView
						.findViewById(R.id.amount_bill_tv);
				holder.date_tv = (TextView) convertView
						.findViewById(R.id.date_tv);
				holder.time2_bill_tv = (TextView) convertView
						.findViewById(R.id.time2_bill_tv);
				holder.acknowledgement_bill_tv = (TextView) convertView
						.findViewById(R.id.acknowledgement_bill_tv);

				holder.userId_IV = (CircularImageView) convertView
						.findViewById(R.id.userId_IV);
				
				holder.userId_IV.setBorderWidth(0);
				
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.time_tv.setText(customDate(Totalset_list.get(position).get(
					HashStatic.HASH_effective_date)));
			
			holder.username_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_cust_fname_m6)
					+ " "
					+ Totalset_list.get(position).get(
							HashStatic.HASH_cust_lname_m6));

			holder.userEmail_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_cust_email_m6));
			holder.Itemname_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_store_name));
			holder.codenumber_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_receiver_wallet_id));
			holder.amount_bill_tv.setText(currency
					+ Calculations.formatString_ToString(Totalset_list.get(position).get(HashStatic.HASH_amount)));
			
			String date1=Totalset_list.get(
					position).get(HashStatic.HASH_effective_date);
			
			holder.date_tv.setText(getDateBig(getDate(date1)) + " " + getDateSmallWith_year(getDate(date1)));
			
			holder.time2_bill_tv.setText(getTime(Totalset_list.get(position)
					.get(HashStatic.HASH_effective_date)));
			holder.acknowledgement_bill_tv.setText(Totalset_list.get(position)
					.get(HashStatic.HASH_on_account_of));

			imageLoader.displayImage(
					base_url + maps.get(HashStatic.HASH_cust_image_m6),
					holder.userId_IV, options);
			break;
			//////////////////////************************//////////////
		case TYPE_SEPARATOR_discount:
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.feed_listitem_4, null);
				holder = new ViewHolder();
				//use it when call button is needed right now i have stopped it
				/*holder.timeCallback_tv = (TextView) convertView
						.findViewById(R.id.timeCallback_tv);
				holder.usernameCallback_tv = (TextView) convertView
						.findViewById(R.id.usernameCallback_tv);
				holder.userEmailcallback_tv = (TextView) convertView
						.findViewById(R.id.userEmailcallback_tv);
				holder.ItemnameCallback_tv = (TextView) convertView
						.findViewById(R.id.ItemnameCallback_tv);
				holder.priceCallback_tv = (TextView) convertView
						.findViewById(R.id.priceCallback_tv);
				holder.acknowledgementCallback_bill_tv = (TextView) convertView
						.findViewById(R.id.acknowledgementCallback_bill_tv);

				holder.callback_background_IV = (ImageView) convertView
						.findViewById(R.id.callback_background_IV);
				holder.userId_callback_IV = (CircularImageView) convertView
						.findViewById(R.id.userId_callback_IV);
				holder.callButton = (ImageButton) convertView
						.findViewById(R.id.callButton);*/
				holder.time_tv = (TextView) convertView
						.findViewById(R.id.time_tv4);
				holder.username_tv = (TextView) convertView
						.findViewById(R.id.username_tv4);
				holder.userEmail_tv = (TextView) convertView
						.findViewById(R.id.userEmail_tv4);
				holder.userId_IV = (CircularImageView) convertView
						.findViewById(R.id.userId_IV4);
				
				holder.company_name_tv = (TextView) convertView
						.findViewById(R.id.companyName);
				holder.issued_tv = (TextView) convertView
						.findViewById(R.id.issued_tv);
				holder.validity_tv = (TextView) convertView
						.findViewById(R.id.validity_tv);
				holder.used_tv = (TextView) convertView.findViewById(R.id.used_tv);
				holder.discount_tv1 = (TextView) convertView
						.findViewById(R.id.discount_tv1);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.time_tv.setText(customDate(Totalset_list.get(position).get(
					HashStatic.HASH_effective_date)));
			
			holder.username_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_cust_fname_m6)
					+ " "
					+ Totalset_list.get(position).get(
							HashStatic.HASH_cust_lname_m6));

			holder.userEmail_tv.setText(Totalset_list.get(position).get(
					HashStatic.HASH_cust_email_m6));
			
			holder.company_name_tv.setText(Totalset_list.get(position).get(
					COMAPNY_NAME));
			String date=getDate(Totalset_list.get(position).get(ISSUED));
			
			holder.issued_tv.setText(getDateBig(date)+" "+getDateSmallWith_year(date));

			holder.validity_tv.setText("Expiry Date: "+ customDate(
					Totalset_list.get(position).get(ISSUED),
					Totalset_list.get(position).get(DAYS_TO_REDEEM)));

			holder.used_tv.setText(Totalset_list.get(position).get(USED) + "/"
					+ Totalset_list.get(position).get(TOTALTIMES));
			
			holder.discount_tv1.setText(Totalset_list.get(position).get(DISCCOUNT)+"%");

			/*
			 * holder.ItemnameCallback_tv.setText(Totalset_list.get(position).get
			 * (HashStatic.HASH_store_name));
			 * holder.codenumber_tv.setText(Totalset_list
			 * .get(position).get(HashStatic.HASH_receiver_wallet_id));
			 * holder.amount_bill_tv
			 * .setText(Totalset_list.get(position).get(HashStatic
			 * .HASH_amount));
			 * holder.date_tv.setText(Totalset_list.get(position)
			 * .get(HashStatic.HASH_effective_date));
			 * holder.time2_bill_tv.setText
			 * (Totalset_list.get(position).get(HashStatic
			 * .HASH_transaction_type));
			 * holder.acknowledgement_bill_tv.setText(Totalset_list
			 * .get(position).get(HashStatic.HASH_on_account_of));
			 */
			holder.userId_IV.setBorderWidth(0);
			imageLoader.displayImage(
					base_url + maps.get(HashStatic.HASH_cust_image_m6),
					holder.userId_IV, options);

			break;

		case TYPE_SEPARATOR_withdraw:
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.feed_listitem_withdrawal, null);
				holder = new ViewHolder();

				holder.time_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.time_withdrawal_tv);
				holder.username_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.username_withdrawal_tv);
				holder.userEmail_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.userEmail_withdrawal_tv);
				holder.Itemname_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.Itemname_withdrawal_tv);
				holder.codenumber_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.codenumber_withdrawal_tv);
				holder.amount_bill_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.amount_bill_withdrawal_tv);
				holder.date_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.date_withdrawal_tv);
				holder.time2_bill_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.time2_bill_withdrawal_tv);
				holder.status_withdrawal_tv = (TextView) convertView
						.findViewById(R.id.status_withdrawal_tv);

				holder.userId_withdrawal_IV = (CircularImageView) convertView
						.findViewById(R.id.userId_withdrawal_IV);
				holder.userId_withdrawal_IV.setBorderWidth(0);
				
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.time_withdrawal_tv.setText(customDate(Totalset_list.get(position).get(
					HashStatic.HASH_effective_date)));
			holder.username_withdrawal_tv.setText(Totalset_list.get(position).get(
							HashStatic.HASH_store_name));

			holder.userEmail_withdrawal_tv.setText(Totalset_list.get(position)
					.get(HashStatic.HASH_store_mobile));
			holder.Itemname_withdrawal_tv.setText(Totalset_list.get(position)
					.get(HashStatic.HASH_method_name));
			holder.codenumber_withdrawal_tv.setText(Totalset_list.get(position)
					.get(HashStatic.HASH_bt_reference_id));
			holder.amount_bill_withdrawal_tv.setText(currency
					+ Calculations.formatString_ToString(Totalset_list.get(position).get(HashStatic.HASH_amount)));
			
			String dates=Totalset_list.get(
					position).get(HashStatic.HASH_effective_date);
			holder.date_withdrawal_tv.setText(getDateBig(getDate(dates)) + " " + getDateSmallWith_year(getDate(dates)));
			
			
			holder.time2_bill_withdrawal_tv.setText(getTime(Totalset_list.get(position)
					.get(HashStatic.HASH_effective_date)));
			holder.status_withdrawal_tv.setText(Totalset_list.get(position)
					.get(HashStatic.HASH_bt_status));
			
			
			

			imageLoader.displayImage(
					base_url + maps.get(HashStatic.HASH_company_image),
					holder.userId_withdrawal_IV, options);
			break;

		}
		// convertView.setTag(holder);
		/*
		 * } else { holder = (ViewHolder) convertView.getTag(); }
		 */
		
		Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
		convertView.startAnimation(animation);
		lastPosition = position;

		return convertView;
	}

	public static class ViewHolder {
		// bill
		public TextView time_tv;
		public CircularImageView userId_IV;
		public TextView username_tv;
		public TextView userEmail_tv;
		public TextView Itemname_tv;
		public TextView codenumber_tv;
		public TextView amount_bill_tv;
		public TextView date_tv;
		public TextView time2_bill_tv;
		public TextView acknowledgement_bill_tv;

		// callback
		public TextView timeCallback_tv;
		public CircularImageView userId_callback_IV;
		public TextView usernameCallback_tv;
		public TextView userEmailcallback_tv;
		public ImageButton callButton;
		public TextView ItemnameCallback_tv;
		public ImageView callback_background_IV;

		public TextView priceCallback_tv;
		public TextView acknowledgementCallback_bill_tv;

		// withdrawal
		public TextView time_withdrawal_tv;
		public CircularImageView userId_withdrawal_IV;
		public TextView username_withdrawal_tv;
		public TextView userEmail_withdrawal_tv;
		public TextView Itemname_withdrawal_tv;
		public TextView codenumber_withdrawal_tv;
		public TextView amount_bill_withdrawal_tv;
		public TextView date_withdrawal_tv;
		public TextView time2_bill_withdrawal_tv;
		public TextView status_withdrawal_tv;
		
		////discount coupon
		public TextView time_tv4;
		public CircularImageView userId_IV4;
		public TextView username_tv4;
		public TextView userEmail_tv4;
		public TextView company_name_tv;
		public TextView issued_tv;
		public TextView validity_tv;
		public TextView used_tv;
		public TextView discount_tv1;

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
	
	////////////////this one is for Discount*********************
	public String customDate(String date, String daystoRedeem) {
		String new_date = null;
		  String dt = date;  // Start date
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    Calendar c = Calendar.getInstance();
		    try {
		        c.setTime(sdf.parse(dt));
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		    
		    int days = Integer.parseInt(daystoRedeem);
		    
		    c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
		    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		    String output = sdf1.format(c.getTime()); 
		return getDateBig(output)+" "+getDateSmallWith_year(output);
	}

}
