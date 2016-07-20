package com.yozard.business.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Offer {

	@SerializedName("offer_type")
	private String offer_type;
	@SerializedName("offer_amount")
	private String offer_amount;
	@SerializedName("offer_title")
	private String offer_title;
	@SerializedName("offer_image")
	private String offer_image;

	public String getOffer_type() {
		return offer_type;
	}

	public void setOffer_type(String offer_type) {
		this.offer_type = offer_type;
	}

	public String getOffer_amount() {
		if (TextUtils.isEmpty(offer_amount)) {
			return "0";
		} else {
			return offer_amount;
		}
	}

	public void setOffer_amount(String offer_amount) {
		this.offer_amount = offer_amount;
	}

	public String getOffer_title() {
		return offer_title;
	}

	public void setOffer_title(String offer_title) {
		this.offer_title = offer_title;
	}

	public String getOffer_image() {
		return offer_image;
	}

	public void setOffer_image(String offer_image) {
		this.offer_image = offer_image;
	}

}
