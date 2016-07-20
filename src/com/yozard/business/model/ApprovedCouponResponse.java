package com.yozard.business.model;

import com.google.gson.annotations.SerializedName;

public class ApprovedCouponResponse {
	@SerializedName("approved_coupon")
	PendingCoupons pending_coupon;
	@SerializedName("message")
	private String message;

	public PendingCoupons getPending_coupon() {
		return pending_coupon;
	}

	public void setPending_coupon(PendingCoupons pending_coupon) {
		this.pending_coupon = pending_coupon;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
