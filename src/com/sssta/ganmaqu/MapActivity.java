package com.sssta.ganmaqu;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {
	private dataFromJs data;
	private String jsString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		final TextView routeTextView;
		routeTextView = (TextView)findViewById(R.id.text_route);
		data= new dataFromJs();
		final WebView mapView = (WebView) findViewById(R.id.mapView);
		final List<place> places = (List<place>) getIntent().getSerializableExtra(
				"places");
		Log.i("places nums", String.valueOf(places.size()));
		WebSettings webSettings = mapView.getSettings();
		// WebView����Javascript�ű�ִ��
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		
	    mapView.setWebViewClient(new MyWebViewClient());
	    mapView.setWebChromeClient(new WebChromeClient() {
	        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
	            Log.i("MyApplication", message + " -- From line "
	                                 + lineNumber + " of "
	                                 + sourceID);

	          }
	        });
	   // mapView.addJavascriptInterface(data, "mapView");
	    mapView.addJavascriptInterface(data, "dataFromJs");
		mapView.loadUrl("file:///android_asset/index.html");
		// mapView.loadUrl("file:///android_asset/js.html");
		Log.i("route", "javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
		String.valueOf(places.get(0).getPos_x()+")"));
		Button jsButton = (Button) findViewById(R.id.test_button);
//		mapView.loadUrl("javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
//				String.valueOf(places.get(0).getPos_x()+")"));
		
		//经过测试，这里必须设置timer才能执行，也就是页面文件需要加载完毕后才能使用其他的js方法
		//时间设定为0.2s即可
		Timer timer = new Timer(); //设置Timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				mapView.loadUrl("javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
						String.valueOf(places.get(0).getPos_x()+")"));
				
					for (int i = 0; i < places.size(); i++) {
					String	contentString ;
					contentString ="<p><b>" + places.get(i).getShopName() + "</b></p>" + "<p>"
					    + places.get(i).getAddress() + "</p>" + "<p><a href=http://m.dianping.com/shop/" + 
							String.valueOf(places.get(i).getId()) + ">" + "详细信息>></a>" ;
					String loadString = "javascript:addMessage(" +String.valueOf(places.get(i).getPos_y())+","+
							String.valueOf(places.get(i).getPos_x()+ "," +  "\"" +  contentString + "\""+")");
					Log.i("load String", loadString);
					mapView.loadUrl(loadString);
				}
			}
		};
		timer.schedule(task, 200 * 1);
		
		Timer timer_2 = new Timer(); //设置Timer
		TimerTask task_2 = new TimerTask() {
			@Override
			public void run() {
				if(data.getDataString()!= null)
				{
					Log.i("dataFromJs", data.getDataString());
				}
				else {
					Log.i("dataFromJs", "null");
				}
		
			}
		};
		timer_2.schedule(task_2, 3000 * 1);
		
		//mapView.loadUrl("javascript:getRouteInfo()");
		jsButton.setText("run");
		jsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
		      System.exit(0);
			}
		});
		
		/*
		 * mapView.setWebChromeClient(new WebChromeClient() {
		 * 
		 * @Override public boolean onJsAlert(WebView view, String url, String
		 * message, final JsResult result) { AlertDialog.Builder b2 = new
		 * AlertDialog.Builder(getApplicationContext())
		 * .setTitle(R.string.app_name).setMessage(message)
		 * .setPositiveButton("ok", new AlertDialog.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * result.confirm(); // MyWebView.this.finish(); } });
		 * 
		 * b2.setCancelable(false); b2.create(); b2.show(); return true; } });
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
}


