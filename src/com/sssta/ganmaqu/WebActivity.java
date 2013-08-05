package com.sssta.ganmaqu;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebActivity extends Activity {
	private String loadString;
	private String idString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		idString = getIntent().getStringExtra("shopId");
		WebView webpageView = (WebView)findViewById(R.id.webPage);
		WebSettings webSettings = webpageView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		loadString = "http://m.dianping.com/shop/" + idString ;
		Log.i("loadurl", loadString);
		webpageView.loadUrl(loadString);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

}
