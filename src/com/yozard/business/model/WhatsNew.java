package com.yozard.business.model;

import java.util.Vector;

import com.google.gson.annotations.SerializedName;

public class WhatsNew {
	@SerializedName("message")
	private Vector<String> message;
	@SerializedName("app_version")
	private String app_version;
	@SerializedName("mandatory")
	private String mandatory;

	public Vector<String> getMessage() {
		return message;
	}

	public void setMessage(Vector<String> message) {
		this.message = message;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

}
