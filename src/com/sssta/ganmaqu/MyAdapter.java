package com.sssta.ganmaqu;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

public class MyAdapter extends AuthorizeAdapter {

	public void onCreate() { 
		  System.out.println("> ShareSDKUIShell created!"); 
		  System.out.println(">   PlatName: " + getPlatformName ()); 
		  System.out.println(">   TitleLayout: " + getTitleLayout()); 
		  System.out.println(">   WebBody: " + getWebBody()); 
		  getTitleLayout().getTvTitle().setText("This is MyAdapter"); 
		} 
		 
		public void onDestroy() { 
		  System.out.println("> ShareSDKUIShell will be destroyed."); 
		} 
}
