package com.yozard.business.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yozard.business.R;
import com.yozard.business.model.Coupon;
import com.yozard.business.model.Offer;
import com.yozard.business.utils.BaseURL;
import com.yozard.business.utils.ConnectionManagerPromo;
import com.yozard.business.utils.HashStatic;
import com.yozard.business.utils.JSONParser_new;
import com.yozard.business.utils.MyToast;
import com.yozard.business.utils.TypeFace_MY;

public class PendingLiveCouponAdapter extends ArrayAdapter<Coupon> {

	Context con;
	Vector<Coupon> coupons;
	int viewId;
	SharedPreferences registration_preference;
	String base_url, userId_sp, country;
	boolean isPending;

	Typeface tf_Roboto_Cond;

	// Offer offer;

	public PendingLiveCouponAdapter(Context context, int resource,
			Vector<Coupon> coupons, boolean isPending) {
		super(context, resource, coupons);
		this.con = context;
		this.viewId = resource;
		this.coupons = coupons;
		this.isPending = isPending;
		registration_preference = con.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		country = registration_preference.getString(HashStatic.HASH_country,
				null);
		base_url = registration_preference.getString(HashStatic.HASH_baseUrl,
				null);
		userId_sp = registration_preference.getString(HashStatic.CUSTOMER_ID,
				null);
		tf_Roboto_Cond = TypeFace_MY.getRoboto_condensed(con);
	}

	private class ViewHolder {
		TextView tvCouponType, tvTime, tvUsername, tvUserTrackingID,
				tvDiscount, tvOrderAmount, tvDatenTime, tvAccept;
		CircularImageView civUserImage;
		ImageView ivCompanyImage;
//		Button btnAccept; // , btnReject;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pending_coupon_row, null);
			holder = new ViewHolder();
			holder.civUserImage = (CircularImageView) convertView
					.findViewById(R.id.civUserImage);
			holder.ivCompanyImage = (ImageView) convertView
					.findViewById(R.id.ivCompannyImage);
			holder.tvCouponType = (TextView) convertView
					.findViewById(R.id.tvCouponType);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvUsername = (TextView) convertView
					.findViewById(R.id.tvUsername);
			holder.tvUserTrackingID = (TextView) convertView
					.findViewById(R.id.tvUserTrackingID);
			holder.tvDiscount = (TextView) convertView
					.findViewById(R.id.tvDiscount);
			holder.tvOrderAmount = (TextView) convertView
					.findViewById(R.id.tvOrderAmount);
			holder.tvDatenTime = (TextView) convertView
					.findViewById(R.id.tvDatenTime);
			holder.tvAccept = (TextView) convertView
					.findViewById(R.id.tvAccept);
			// holder.btnReject = (Button) convertView
			// .findViewById(R.id.btnReject);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Define Callback here if needed **
		holder.tvAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Coupon aCoupon = coupons.get(position);
				approveCouponRequest(aCoupon, position);
			}
		});

		if (position < coupons.size()) {
			setDataInRow(position, holder);
		}

		return convertView;
	}

	private void setDataInRow(int position, ViewHolder holder) {
		if(!isPending){
			holder.tvAccept.setVisibility(View.GONE);
		}else{
			holder.tvAccept.setVisibility(View.VISIBLE);
		}
		Coupon aCoupon = coupons.get(position);
		Offer anOffer;
		setTimeNdate(aCoupon, holder);
		if (!TextUtils.isEmpty(aCoupon.getCust_image())) {
			setImageFromUrl(holder.civUserImage, aCoupon.getCust_image(), false);
		}
		if (!TextUtils.isEmpty(aCoupon.getCompany_image())) {
			setImageFromUrl(holder.ivCompanyImage, aCoupon.getCompany_image(),
					true);
		}
		holder.tvCouponType.setText("Live Coupon");
		anOffer = aCoupon.getCoupon_offer();
		holder.tvUsername.setText(aCoupon.getCust_fname() + " "
				+ aCoupon.getCust_lname());
		holder.tvUserTrackingID.setText(aCoupon.getLc_unique_id());
		holder.tvOrderAmount.setText(BaseURL.selectCurrency(country) + " "
				+ aCoupon.getOrder_amount());
		if (anOffer.getOffer_type().equalsIgnoreCase("discounts")) {
			Log.e("OFFER!!", anOffer.getOffer_amount());
			holder.tvDiscount.setText(anOffer.getOffer_amount() + "% Discount");
		} else {
			Log.e("OFFER!!", anOffer.getOffer_title());
			holder.tvDiscount.setText(anOffer.getOffer_title());
		}
		holder.tvAccept.setTypeface(tf_Roboto_Cond);
		// holder.btnReject.setTypeface(tf_Roboto_Cond);
	}

	// img/companies/

	private void setImageFromUrl(ImageView imageView, String imgPath,
			boolean isCompanyImage) {
		SharedPreferences registration_preference = con.getSharedPreferences(
				HashStatic.PREF_NAME_REG, Context.MODE_PRIVATE);
		String base_url = registration_preference.getString(
				HashStatic.HASH_baseUrl, null);
		String imgUrl = "";
		if (isCompanyImage) {
			imgUrl = base_url + modifyImagePath(imgPath);
		} else {
			imgUrl = base_url + imgPath;
		}
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true)
				.resetViewBeforeLoading(true).showImageForEmptyUri(null)
				.showImageOnFail(null).showImageOnLoading(null).build();
		imageLoader.displayImage(imgUrl, imageView, options);
	}

	private String modifyImagePath(String imagePath) {
		int eol = imagePath.length() - 1;
		for (int i = eol; i > 0; i--) {
			if (imagePath.charAt(i) == '/') {
				imagePath = imagePath.substring(0, i) + "/thumb"
						+ imagePath.substring(i, imagePath.length());
				break;
			}
		}
		return imagePath;
	}

	private void setTimeNdate(Coupon coupon, ViewHolder holder) {
		// String endTime = hCoupon.getEnd_date() + " 23:59";
		String reqTime = coupon.getRequest_time();
		Calendar cal = Calendar.getInstance();
		long currentTimeMs = cal.getTimeInMillis();
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		SimpleDateFormat dFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm a");
		Date reqDate = formatDate(reqTime, dtFormat);
		holder.tvDatenTime.setText(dFormat.format(reqDate));
		long remainingTime = currentTimeMs - reqDate.getTime();
		int day = (int) ((((remainingTime / 1000) / 60) / 60) / 24);
		remainingTime = remainingTime - (long) day * 24 * 60 * 60 * 1000;
		int hour = (int) (((remainingTime / 1000) / 60) / 60);
		remainingTime = remainingTime - (long) hour * 60 * 60 * 1000;
		int minute = (int) ((remainingTime / 1000) / 60);
		String rTime = "";
		if (day > 0) {
			rTime = day + " days ago";
		} else {
			SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm a");
			rTime = tFormat.format(reqDate);
		}
		holder.tvTime.setText(rTime);
	}

	private Date formatDate(String inputDate, SimpleDateFormat sdFormat) {
		Date dt = null;
		try {
			dt = sdFormat.parse(inputDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	private void approveCouponRequest(Coupon aCoupon, int position) {
		HashMap<String, String> profileMap = new HashMap<String, String>();
		profileMap.put(HashStatic.HASH_Ref_Id, aCoupon.getClc_id());
		profileMap.put("position", position + "");
		new Async_ApproveCouponRequest().execute(profileMap);
	}

	public class Async_ApproveCouponRequest extends
			AsyncTask<HashMap<String, String>, Void, Void> {
		ProgressDialog progressDialog;
		JSONParser_new jparser = null;
		List<NameValuePair> paramiters = new ArrayList<NameValuePair>();
		String message = "";
		int position;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(con);
			progressDialog.setMessage("Approving Request...");
			progressDialog.show();
			progressDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(HashMap<String, String>... paramMap) {
			// TODO Auto-generated method stub
			if (ConnectionManagerPromo.getConnectivityStatus(con) != HashStatic.TYPE_NOT_CONNECTED) {
				HashMap<String, String> parametersList = paramMap[0];

				String ref_id = parametersList.get(HashStatic.HASH_Ref_Id);
				position = Integer.parseInt(parametersList.get("position"));

				try {

					String url_select3 = base_url
							+ BaseURL.API
							+ "pending-coupons.php?authtoken="
							+ URLEncoder.encode(BaseURL.AUTH_TOKEN, "utf-8")
							+ "&JSONParam="
							+ URLEncoder.encode("{\"seller_id\":\"" + userId_sp
									+ "\",\"clc_id\":\"" + ref_id
									+ "\"}", "utf-8");
					jparser = new JSONParser_new();
					JSONObject jobj = jparser.makeHttpRequest(url_select3,
							"POST", paramiters);
					
					System.out.println(url_select3);

					message = jobj.getString("message");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// Toast.makeText(con, "Network connection not available.",
				// Toast.LENGTH_SHORT).show();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			MyToast.makeToast(message, con);
			if (message.contains("success")) {
				coupons.remove(position);
				notifyDataSetChanged();
			}
		}

	}

}
