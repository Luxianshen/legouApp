package com.neusoft.legou.entity;

import java.math.BigDecimal;

public class Product {

	private Integer proId;
	private String proName;
	private String proType;
	private BigDecimal proPrice;
	private Integer proNum;
	private String proSrc;
	private String prodescribe;
	
	private String proalrePay;
	private String proFare;
	private String proAddress;
	private Integer stock;
	
	public String getProalrePay() {
		return proalrePay;
	}

	public void setProalrePay(String proalrePay) {
		this.proalrePay = proalrePay;
	}

	public String getProFare() {
		return proFare;
	}

	public void setProFare(String proFare) {
		this.proFare = proFare;
	}

	public String getProAddress() {
		return proAddress;
	}

	public void setProAddress(String proAddress) {
		this.proAddress = proAddress;
	}

	public Integer getProId() {
		return proId;
	}

	public void setProId(Integer proId) {
		this.proId = proId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public BigDecimal getProPrice() {
		return proPrice;
	}

	public void setProPrice(BigDecimal proPrice) {
		this.proPrice = proPrice;
	}

	public Integer getProNum() {
		return proNum;
	}

	public void setProNum(Integer proNum) {
		this.proNum = proNum;
	}

	public String getProSrc() {
		return proSrc;
	}

	public void setProSrc(String proSrc) {
		this.proSrc = proSrc;
	}

	public String getProdescribe() {
		return prodescribe;
	}

	public void setProdescribe(String prodescribe) {
		this.prodescribe = prodescribe;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

}
