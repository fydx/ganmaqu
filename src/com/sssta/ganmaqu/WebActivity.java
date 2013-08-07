package com.sssta.ganmaqu;

import java.net.URL;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebActivity extends Activity {
	private String loadString;
	private String idString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		
		WebView webpageView = (WebView)findViewById(R.id.webPage);
		WebSettings webSettings = webpageView.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		webpageView.setWebViewClient(new MyWebViewClient());
		idString = getIntent().getStringExtra("shopId");
		if (idString != null) {
			
			loadString = "http://m.dianping.com/shop/" + idString ;
			Log.i("loadurl", loadString);
			webpageView.loadUrl(loadString);
		}
		else {
			String urlString = getIntent().getStringExtra("url");
			Log.i("url", urlString);
			webpageView.loadUrl(urlString);
			
		}
		
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

}
