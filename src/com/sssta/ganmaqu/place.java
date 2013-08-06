package com.sssta.ganmaqu;

import java.io.Serializable;

public class place implements Serializable {
	/**
	 * Serializable 
	 */
	private int _id; //数据库主键
	private static final long serialVersionUID = 1L;
	private String address;
	private int cost;
	private String detailType;
	private int id;
	private int route_id; //路线的id
	private String mainType;
	private double pos_x;
	private double pos_y;
	private int rate;
	private String shopName;
	private String suitType;
	private String telNumber;
	private String time;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}
	
	public int getRoute_id() {
		return route_id;
	}

	public void setRoute_id(int route_id) {
		this.route_id = route_id;
	}
	
	public place() {

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getDetailType() {
		return detailType;
	}

	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	public int getId() {
		return id;
	}

	public void setId(int i) {
		this.id = i;
	}

	public String getMainType() {
		return mainType;
	}

	public void setMainType(String mainType) {
		this.mainType = mainType;
	}

	public double getPos_x() {
		return pos_x;
	}

	public void setPos_x(double pos_x) {
		this.pos_x = pos_x;
	}

	public double getPos_y() {
		return pos_y;
	}

	public void setPos_y(double pos_y) {
		this.pos_y = pos_y;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getSuitType() {
		return suitType;
	}

	public void setSuitType(String suitType) {
		this.suitType = suitType;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public place(String address, int cost, String detailType, int id,
			String mainType, double pos_x, double pos_y, int rate,
			String shopName, String suitType, String telNumber, String time) {
		super();
		this.address = address;
		this.cost = cost;
		this.detailType = detailType;
		this.id = id;
		this.mainType = mainType;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.rate = rate;
		this.shopName = shopName;
		this.suitType = suitType;
		this.telNumber = telNumber;
		this.time = time;
	}

}
