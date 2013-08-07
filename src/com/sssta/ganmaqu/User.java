package com.sssta.ganmaqu;


public class User {
	private int route_num;
	private String id;
	public int getRoute_num() {
		return route_num;
	}
	public void setRoute_num(int route_num) {
		this.route_num = route_num;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public User(String id,int route_num) {
		super();
		this.route_num = route_num;
		this.id = id;
	}
   public User()
   {
	   
   }
}
