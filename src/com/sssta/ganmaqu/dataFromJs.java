package com.sssta.ganmaqu;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class dataFromJs {
   private String dataString;

public String getDataString() {
	return dataString;
}
@JavascriptInterface 
public void setDataString(String dataString) {
	this.dataString = dataString;
}

public dataFromJs(String dataString) {
	super();
	this.dataString = dataString;
}
public dataFromJs() {
	this.dataString = null;
}
@JavascriptInterface 
public void showMessage()
{
	
	Log.i("dataFromJs", "run");
}
}
