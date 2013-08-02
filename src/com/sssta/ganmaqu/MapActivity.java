package com.sssta.ganmaqu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		WebView mapView = (WebView)findViewById(R.id.mapView);
		WebSettings webSettings = mapView.getSettings();
		webSettings.setJavaScriptEnabled(true);  //设定可执行Javascript代码
		mapView.addJavascriptInterface(this, "javatojs"); 
		mapView.loadUrl("file:///android_asset/index.html");  
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}
