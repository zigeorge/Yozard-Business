package com.yozard.business.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Coupon {

	@SerializedName("cust_fname")
	private String cust_fname;
	@SerializedName("cust_lname")
	private String cust_lname;
	@SerializedName("cust_image")
	private String cust_image;
	@SerializedName("company_name")
	private String company_name;
	@SerializedName("company_image")
	private String company_image;
	@SerializedName("order_amount")
	private String order_amount;
	@SerializedName("request_time")
	private String request_time;
	@SerializedName("ref_id")
	private String ref_id;
	@SerializedName("clc_id")
	private String clc_id;
	@SerializedName("lc_unique_id")
	private String lc_unique_id;
	@SerializedName("lcc_id")
	private String lcc_id;
	@SerializedName("cust_id")
	private String cust_id;
	@SerializedName("claim_time")
	private String claim_time;
	@SerializedName("used_count")
	private String used_count;
	@SerializedName("lcc_offer")
	private String lcc_offer;
	//Happy Hour keys and variables
	@SerializedName("chh_id")
	private String chh_id;
	@SerializedName("tracking_id")
	private String tracking_id;
	@SerializedName("hhc_id")
	private String hhc_id;
	@SerializedName("booking_time")
	private String booking_time;
	@SerializedName("offer")
	private String offer;

	public String getCust_fname() {
		return cust_fname;
	}

	public void setCust_fname(String cust_fname) {
		this.cust_fname = cust_fname;
	}

	public String getCust_lname() {
		return cust_lname;
	}

	public void setCust_lname(String cust_lname) {
		this.cust_lname = cust_lname;
	}

	public String getCust_image() {
		return cust_image;
	}

	public void setCust_image(String cust_image) {
		this.cust_image = cust_image;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_image() {
		return company_image;
	}

	public void setCompany_image(String company_image) {
		this.company_image = company_image;
	}

	public String getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}

	public String getRequest_time() {
		return request_time;
	}

	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}

	public String getRef_id() {
		return ref_id;
	}

	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}

	public String getClc_id() {
		return clc_id;
	}

	public void setClc_id(String clc_id) {
		this.clc_id = clc_id;
	}

	public String getLc_unique_id() {
		return lc_unique_id;
	}

	public void setLc_unique_id(String lc_unique_id) {
		this.lc_unique_id = lc_unique_id;
	}

	public String getLcc_id() {
		return lcc_id;
	}

	public void setLcc_id(String lcc_id) {
		this.lcc_id = lcc_id;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getClaim_time() {
		return claim_time;
	}

	public void setClaim_time(String claim_time) {
		this.claim_time = claim_time;
	}

	public String getUsed_count() {
		return used_count;
	}

	public void setUsed_count(String used_count) {
		this.used_count = used_count;
	}

	public String getLcc_offer() {
		return lcc_offer;
	}

	public void setLcc_offer(String lcc_offer) {
		this.lcc_offer = lcc_offer;
	}
	
	public Offer getCoupon_offer() {
		Gson g = new Gson();
		Offer coupon_offer = g.fromJson(getLcc_offer(), Offer.class);
		return coupon_offer;
	}

	public String getChh_id() {
		return chh_id;
	}

	public void setChh_id(String chh_id) {
		this.chh_id = chh_id;
	}

	public String getTracking_id() {
		return tracking_id;
	}

	public void setTracking_id(String tracking_id) {
		this.tracking_id = tracking_id;
	}

	public String getHhc_id() {
		return hhc_id;
	}

	public void setHhc_id(String hhc_id) {
		this.hhc_id = hhc_id;
	}

	public String getBooking_time() {
		return booking_time;
	}

	public void setBooking_time(String booking_time) {
		this.booking_time = booking_time;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}
	
	public Offer getHappyOffer(){
		Gson g = new Gson();
		Offer coupon_offer = g.fromJson(getOffer(), Offer.class);
		return coupon_offer;
	}
	
	

}
