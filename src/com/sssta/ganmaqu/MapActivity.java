package com.sssta.ganmaqu;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {
	private dataFromJs data;
	private String jsString;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private WebView mapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		/**
		 * 设置LocationManager 设置地理位置服务
		 */
		// 获取LocationManager服务
				locationManager = (LocationManager) this
						.getSystemService(Context.LOCATION_SERVICE);
				// 获取Location Provider
				getProvider();
				// 如果未设置位置源，打开GPS设置界面
				openGPS();
				// 获取位置
				location = locationManager.getLastKnownLocation(provider);
				// 显示位置信息到文字标签
				
				// 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
				// 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
				locationManager.requestLocationUpdates(provider, 2000, 10,
						locationListener);
				
		final TextView routeTextView;
		routeTextView = (TextView)findViewById(R.id.text_route);
		data= new dataFromJs();
		mapView = (WebView) findViewById(R.id.mapView);
		final List<place> places = (List<place>) getIntent().getSerializableExtra(
				"places");
		Log.i("places nums", String.valueOf(places.size()));
		WebSettings webSettings = mapView.getSettings();
		// WebView 开启 javascript
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setDefaultTextEncodingName("utf-8");  
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
		mapView.loadUrl("file:///android_asset/test_baidu.html");
		// mapView.loadUrl("file:///android_asset/js.html");
//		Log.i("route", "javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
//		String.valueOf(places.get(0).getPos_x()+")"));
		Button jsButton = (Button) findViewById(R.id.test_button);
//		mapView.loadUrl("javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
//				String.valueOf(places.get(0).getPos_x()+")"));
		
		//经过测试，这里必须设置timer才能执行，也就是页面文件需要加载完毕后才能使用其他的js方法
		//时间设定为0.2s即可
		Timer timer = new Timer(); //设置Timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				getApplicationContext().getMainLooper();
				Looper.prepare();
//				
//				mapView.loadUrl("javascript:calcRoute("+String.valueOf(places.get(0).getPos_y())+","+
//						String.valueOf(places.get(0).getPos_x()+")"));
					updateWithNewLocation(location);
					for (int i = 0; i < places.size(); i++) {
					String	contentString ;
					contentString ="<p><b>" + places.get(i).getShopName() + "</b></p>" + "<p>"
					    + places.get(i).getAddress() + "</p>" + "<p><a href=http://m.dianping.com/shop/" + 
							String.valueOf(places.get(i).getId()) + ">" + "详细信息>></a>" ;
//					String loadString = "javascript:addMessage(" +String.valueOf(places.get(i).getPos_y())+","+
//							String.valueOf(places.get(i).getPos_x()+ "," +  "\"" +  contentString + "\""+")");
//					String loadString = "javascript:codeAddress(" + "\"" +  places.get(i).getShopName() +"\"" + "," +  "\"" +  contentString + "\"" +  "," +String.valueOf(places.get(i).getPos_x())+","+
//							String.valueOf(places.get(i).getPos_y())+ ")" ; 
					String loadString = "javascript:codeAddress(" + "\"" +  places.get(i).getShopName() +"\"" + "," +  "\"" +  contentString + "\"" +  ")" ; 
					Log.i("load String", loadString);
					mapView.loadUrl(loadString);
					//地图加载完毕后 update location
					
					//mapView.loadUrl("javascript:addLocation(34.139,108.84199)");
				}
			}
		};
		timer.schedule(task, 1000 * 1);
		
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
		timer_2.schedule(task_2, 1000 * 1);
		
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
	// 判断是否开启GPS，若未开启，打开GPS设置界面
		private void openGPS() {
			if (locationManager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
					|| locationManager
							.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
				Toast.makeText(this, "位置源已设置！", Toast.LENGTH_SHORT).show();
				return;
			}
			Toast.makeText(this, "位置源未设置！", Toast.LENGTH_SHORT).show();
			// 转至GPS设置界面
			Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			startActivityForResult(intent, 0);
		}

		// 获取Location Provider
		private void getProvider() {
			// 构建位置查询条件
			Criteria criteria = new Criteria();
			// 查询精度：高
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			// 是否查询海拨：否
			criteria.setAltitudeRequired(false);
			// 是否查询方位角:否
			criteria.setBearingRequired(false);
			// 是否允许付费：是
			criteria.setCostAllowed(true);
			// 电量要求：低
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			// 返回最合适的符合条件的provider，第2个参数为true说明,如果只有一个provider是有效的,则返回当前provider
			provider = locationManager.getBestProvider(criteria, true);
		}

		// Gps消息监听器
		private final LocationListener locationListener = new LocationListener() {
			// 位置发生改变后调用
			public void onLocationChanged(Location location) {

				updateWithNewLocation(location);
			}

			// provider被用户关闭后调用
			public void onProviderDisabled(String provider) {
				updateWithNewLocation(null);
			}

			// provider被用户开启后调用
			public void onProviderEnabled(String provider) {

			}

			// provider状态变化时调用
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

		};

		// Gps监听器调用，处理位置信息
		private void updateWithNewLocation(Location location) {
			String latLongString;
			double lat = 0;
			double lng = 0;
//			TextView myLocationText = (TextView) findViewById(R.id.text_location);
			if (location != null) {
				 lat = location.getLatitude();
				 lng = location.getLongitude();
				 latLongString = "纬度:" + lat + "\n经度:" + lng;
			} else {
				latLongString = "无法获取地理信息";
			}
			Toast.makeText(getApplicationContext(), "您当前的位置是: " + "\n" + latLongString + "\n"
					, Toast.LENGTH_LONG).show();
		//	mapView.loadUrl("javascript:deleteLocation()");
		//mapView.loadUrl("javascript:addLocation(34.139,108.84199)");f
			//mapView.loadUrl("javascript:addLocation(" +String.valueOf(lng)+ ","+String.valueOf(lat) +")");
			Log.i("addLocation", "javascript:setLocation_1(" +String.valueOf(lng)+ ","+String.valueOf(lat) +")");
			mapView.loadUrl("javascript:setLocation_1(" +String.valueOf(lng)+ ","+String.valueOf(lat) +")");
			
//			myLocationText.setText("您当前的位置是:/n" + latLongString + "/n"
//					+ getAddressbyGeoPoint(location));
			
		}

	/*	// 获取地址信息
		private List<Address> getAddressbyGeoPoint(Location location) {
			List<Address> result = null;
			// 先将Location转换为GeoPoint
			// GeoPoint gp=getGeoByLocation(location);
			try {
				if (location != null) {
					// 获取Geocoder，通过Geocoder就可以拿到地址信息
					Geocoder gc = new Geocoder(this, Locale.getDefault());
					result = gc.getFromLocation(location.getLatitude(),
							location.getLongitude(), 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}*/
}


