package com.neusoft.legou.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



public class Order implements Serializable {
	
	private static final long serialVersionUID = 3253132788055432197L;

	private Integer orderId;
	private Integer userID;
	private Integer proId;
	private Date orderTime;
	private Integer orderNum;
	private BigDecimal orderPrice;
	private String orderPay;
	private String orderIsDel;
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public BigDecimal getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}
	public String getOrderPay() {
		return orderPay;
	}
	public void setOrderPay(String orderPay) {
		this.orderPay = orderPay;
	}
	public String getOrderIsDel() {
		return orderIsDel;
	}
	public void setOrderIsDel(String orderIsDel) {
		this.orderIsDel = orderIsDel;
	}
	public Integer getProId() {
		return proId;
	}
	public void setProId(Integer proId) {
		this.proId = proId;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	
	
	
}
