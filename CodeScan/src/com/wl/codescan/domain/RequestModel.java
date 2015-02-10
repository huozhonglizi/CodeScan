package com.wl.codescan.domain;

import java.util.List;

/**
 * 
 * @类描述:请求实体
 * @创建人:wanglei
 * @创建时间: 2014-12-2
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 */
public class RequestModel {
	private String apiusername;
	private String apipwd;
	private String ordercode;
	private List<ProductSn> productSns;

	public String getApiusername() {
		return apiusername;
	}

	public void setApiusername(String apiusername) {
		this.apiusername = apiusername;
	}

	public String getApipwd() {
		return apipwd;
	}

	public void setApipwd(String apipwd) {
		this.apipwd = apipwd;
	}

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}

	public List<ProductSn> getProductSns() {
		return productSns;
	}

	public void setProductSns(List<ProductSn> productSns) {
		this.productSns = productSns;
	}

}
