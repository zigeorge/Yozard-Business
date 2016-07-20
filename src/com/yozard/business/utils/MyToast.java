package com.yozard.business.utils;

import com.yozard.business.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class MyToast {

	public static void makeToast(String str,Context ctx) {
		final String strr = str;


				LayoutInflater inflater = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.toast_layout, null);

				ImageView image = (ImageView) layout
						.findViewById(R.id.Toastimage);
				//image.setImageResource(R.drawable.favorite_1_sel);
				//image.setVisibility(View.GONE);
				TextView text = (TextView) layout.findViewById(R.id.Toasttext);
				text.setText(strr);

				int actionBarHeight = 0;
				// /////////set the toasts below
				// actionbar///////////////////////
				TypedValue tv = new TypedValue();
				if (ctx.getTheme().resolveAttribute(
						android.R.attr.actionBarSize, tv, true)) {
					actionBarHeight = TypedValue.complexToDimensionPixelSize(
							tv.data, ctx.getResources().getDisplayMetrics());
				}
				Toast tt = new Toast(ctx);
				tt.setView(layout);
				tt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
						actionBarHeight + 5);
				tt.show();
			}

	}
	

