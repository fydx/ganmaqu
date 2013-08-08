package com.sssta.ganmaqu;


public class User {
	private int route_num;
	private int id;
	public int getRoute_num() {
		return route_num;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setRoute_num(int route_num) {
		this.route_num = route_num;
	}
	
	public User(int id,int route_num) {
		super();
		this.route_num = route_num;
		this.id = id;
	}
   public User()
   {
	   
   }
}
