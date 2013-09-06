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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MapFragment extends android.support.v4.app.Fragment {
	// data for fragment
	private dataFromJs data;
	private String jsString;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private WebView mapView;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ��ȡ����Activity�����á�
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_map, container, false);
		setLocationManager();
		data= new dataFromJs();
		mapView = (WebView) view.findViewById(R.id.mapView);
		final List<place> places = (List<place>) getActivity().getIntent().getSerializableExtra(
				"places");
		Log.i("places nums", String.valueOf(places.size()));
		WebSettings webSettings = mapView.getSettings();
		// WebView ���� javascript
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
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mapView.loadUrl("file:///android_asset/js.html");
//		Log.i("route", "javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
//		String.valueOf(places.get(0).getPos_x()+")"));
		Button jsButton = (Button) view.findViewById(R.id.test_button);
//		mapView.loadUrl("javascript:calcRoute2("+String.valueOf(places.get(0).getPos_y())+","+
//				String.valueOf(places.get(0).getPos_x()+")"));
		
		//�������ԣ������������timer����ִ�У�Ҳ����ҳ���ļ���Ҫ������Ϻ����ʹ��������js����
		//ʱ���趨Ϊ0.2s����
		Timer timer = new Timer(); //����Timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				getActivity().getApplicationContext().getMainLooper();
				Looper.prepare();
//				
//				mapView.loadUrl("javascript:calcRoute("+String.valueOf(places.get(0).getPos_y())+","+
//						String.valueOf(places.get(0).getPos_x()+")"));
					updateWithNewLocation(location);
					for (int i = 0; i < places.size(); i++) {
					String	contentString ;
					contentString ="<p><b>" + places.get(i).getShopName() + "</b></p>" + "<p>"
					    + places.get(i).getAddress() + "</p>" + "<p><a href=http://m.dianping.com/shop/" + 
							String.valueOf(places.get(i).getId()) + ">" + "��ϸ��Ϣ>></a>" ;
//					String loadString = "javascript:addMessage(" +String.valueOf(places.get(i).getPos_y())+","+
//							String.valueOf(places.get(i).getPos_x()+ "," +  "\"" +  contentString + "\""+")");
//					String loadString = "javascript:codeAddress(" + "\"" +  places.get(i).getShopName() +"\"" + "," +  "\"" +  contentString + "\"" +  "," +String.valueOf(places.get(i).getPos_x())+","+
//							String.valueOf(places.get(i).getPos_y())+ ")" ; 
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					String loadString = "javascript:codeAddress(" + "\"" +  places.get(i).getShopName() +"\"" + "," +  "\"" +  contentString + "\"" +  ")" ; 
					Log.i("load String", loadString);
					mapView.loadUrl(loadString);
					//��ͼ������Ϻ� update location
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					//mapView.loadUrl("javascript:addLocation(34.139,108.84199)");
					//mapView.loadUrl("javascript:calcRoute()");
				}
			}
		};
		timer.schedule(task, 1500 * 1);
		
		Timer timer_2 = new Timer(); //����Timer
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
//		// return super.onCreateView(inflater, container, savedInstanceState); 
//	

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// methods for fragment
	public void setLocationManager() {
		/**
		 * ����LocationManager ���õ���λ�÷���
		 */
		// ��ȡLocationManager����
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		// ��ȡLocation Provider
		getProvider();
		// ���δ����λ��Դ����GPS���ý���
		openGPS();
		// ��ȡλ��
		location = locationManager.getLastKnownLocation(provider);
		// ��ʾλ����Ϣ�����ֱ�ǩ

		// ע�������locationListener����2��3���������Կ��ƽ���gps��Ϣ��Ƶ���Խ�ʡ��������2������Ϊ���룬
		// ��ʾ����listener�����ڣ���3������Ϊ��,��ʾλ���ƶ�ָ�������͵���listener
		locationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);
	}

	// �ж��Ƿ���GPS����δ��������GPS���ý���
	public void openGPS() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				|| locationManager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(getActivity().getApplicationContext(), "λ��Դ�����ã�",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(getActivity().getApplicationContext(), "λ��Դδ���ã�",
				Toast.LENGTH_SHORT).show();
		// ת��GPS���ý���
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, 0);
	}

	// ��ȡLocation Provider
	public void getProvider() {
		// ����λ�ò�ѯ����
		Criteria criteria = new Criteria();
		// ��ѯ���ȣ���
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// �Ƿ��ѯ��������
		criteria.setAltitudeRequired(false);
		// �Ƿ��ѯ��λ��:��
		criteria.setBearingRequired(false);
		// �Ƿ������ѣ���
		criteria.setCostAllowed(true);
		// ����Ҫ�󣺵�
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// ��������ʵķ���������provider����2������Ϊtrue˵��,���ֻ��һ��provider����Ч��,�򷵻ص�ǰprovider
		provider = locationManager.getBestProvider(criteria, true);
	}

	// Gps��Ϣ������
	public final LocationListener locationListener = new LocationListener() {
		// λ�÷����ı�����
		public void onLocationChanged(Location location) {

			updateWithNewLocation(location);
		}

		// provider���û��رպ����
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		// provider���û����������
		public void onProviderEnabled(String provider) {

		}

		// provider״̬�仯ʱ����
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};

	// Gps���������ã�����λ����Ϣ
	private void updateWithNewLocation(Location location) {
		String latLongString;
		double lat = 0;
		double lng = 0;
		// TextView myLocationText = (TextView)
		// findViewById(R.id.text_location);
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			latLongString = "γ��:" + lat + "\n����:" + lng;
		} else {
			latLongString = "�޷���ȡ������Ϣ";
		}
		Toast.makeText(getActivity().getApplicationContext(),
				"����ǰ��λ����: " + "\n" + latLongString + "\n", Toast.LENGTH_LONG)
				.show();
		// mapView.loadUrl("javascript:deleteLocation()");
		// mapView.loadUrl("javascript:addLocation(34.139,108.84199)");f
		// mapView.loadUrl("javascript:addLocation(" +String.valueOf(lng)+
		// ","+String.valueOf(lat) +")");
		Log.i("addLocation", "javascript:setLocation_1(" + String.valueOf(lng)
				+ "," + String.valueOf(lat) + ")");
		// mapView.loadUrl("javascript:setLocation_1(" +String.valueOf(lng)+
		// ","+String.valueOf(lat) +")");

		// myLocationText.setText("����ǰ��λ����:/n" + latLongString + "/n"
		// + getAddressbyGeoPoint(location));

	}
}
