package com.wl.codescan.domain;

public class ResponseModel {
	private String ordercode;
	private ResultModel api;
	private ResultModel product;

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}

	public ResultModel getApi() {
		return api;
	}

	public void setApi(ResultModel api) {
		this.api = api;
	}

	public ResultModel getProduct() {
		return product;
	}

	public void setProduct(ResultModel product) {
		this.product = product;
	}

}
