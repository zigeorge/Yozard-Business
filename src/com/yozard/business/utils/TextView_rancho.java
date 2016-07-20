package com.yozard.business.utils;



import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView_rancho extends TextView {

	public TextView_rancho(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public TextView_rancho(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public TextView_rancho(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	
	private void init(Context ctx) {
		// TODO Auto-generated method stub
		Typeface tf=TypeFace_MY.getRancho(ctx);
		setTypeface(tf);
	}

}
