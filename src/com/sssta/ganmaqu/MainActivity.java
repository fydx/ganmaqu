package com.sssta.ganmaqu;

import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.ArrayWheelAdapter;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Address address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
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
		updateWithNewLocation(location);
		// 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
		// 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
		locationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);
		Button button_yes = (Button) findViewById(R.id.button_yes);
		ImageView routeImageView = (ImageView) findViewById(R.id.routeList);
		routeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent();
				intent2.setClass(MainActivity.this, RouteListActivity.class);
				startActivity(intent2);
			}
		});
		// WheelView NumberOfPerson = (WheelView)
		// findViewById(R.id.NumberOfPerson);
		// String Numbers[] = new String[] {"1", "2", "3",
		// "4","5","6","7","8","9","10"};
		String Types[] = new String[] { "亲子出行", "朋友聚会", "情侣约会" };
		final WheelView numberWheel = (WheelView) findViewById(R.id.NumberOfPerson);
		String countries[] = new String[] { "2", "3", "4", "5", "6", "7", "8" };
		numberWheel.setVisibleItems(5);
		numberWheel.setCyclic(false);//
		numberWheel.setAdapter(new ArrayWheelAdapter<String>(countries));
		final String cities[][] = new String[][] { Types, Types, Types, Types,
				Types, Types, Types };
		final WheelView typeWheel = (WheelView) findViewById(R.id.Type);
		typeWheel.setAdapter(new ArrayWheelAdapter<String>(Types));
		typeWheel.setVisibleItems(5);

		/*
		 * numberWheel.addChangingListener(new OnWheelChangedListener() {
		 * 
		 * @Override public void onChanged(WheelView wheel, int oldValue, int
		 * newValue) { typeWheel.setAdapter(new
		 * ArrayWheelAdapter<String>(cities[newValue]));
		 * typeWheel.setCurrentItem(cities[newValue].length / 2); } });
		 */
		button_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						String.valueOf(numberWheel.getCurrentItem() + 2)
								+ "人  " + typeWheel.getCurrentItem(),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), WarpDSLV.class);
				startActivity(intent);

			}
		});
		numberWheel.setCurrentItem(3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
//		TextView myLocationText = (TextView) findViewById(R.id.text_location);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "纬度:" + lat + "\n经度:" + lng;
		} else {
			latLongString = "无法获取地理信息";
		}
		Toast.makeText(getApplicationContext(), "(main)您当前的位置是: " + "\n" + latLongString + "\n"
				+ getAddressbyGeoPoint(location), Toast.LENGTH_LONG).show();
//		myLocationText.setText("您当前的位置是:/n" + latLongString + "/n"
//				+ getAddressbyGeoPoint(location));
		
	}

	// 获取地址信息
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
	}
}
