package com.yozard.business.model;

import java.util.Vector;

import com.google.gson.annotations.SerializedName;

public class PendingCoupons {
	@SerializedName("live_coupon")
	private Vector<Coupon> live_coupon;
	@SerializedName("happy_hour")
	private Vector<Coupon> happy_hour;

	public Vector<Coupon> getLive_coupon() {
		return live_coupon;
	}

	public void setLive_coupon(Vector<Coupon> live_coupon) {
		this.live_coupon = live_coupon;
	}

	public Vector<Coupon> getHappy_hour() {
		return happy_hour;
	}

	public void setHappy_hour(Vector<Coupon> happy_hour) {
		this.happy_hour = happy_hour;
	}

}
