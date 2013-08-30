package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.WheelView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.sssta.ganmaqu.GifView.GifImageType;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Address address;
	private List<place> places;
	private String ipString;
	final String Types[] = new String[] { "亲子出行", "朋友出行", "情侣出行" };
	private WheelView typeWheel;
	private double lat;
	private double lng;
	private Dialog dialog;
	private Gallery galleryFlow;
	private GifView gifView;
	private int count ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		count = 0;
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
		// set gallery
		Integer[] images = { R.drawable.child, R.drawable.friend,
				R.drawable.couple };

		ImageAdapter adapter = new ImageAdapter(this, images);
		adapter.createReflectedImages();
		gifView = (GifView)findViewById(R.id.gifview);
		gifView.setGifImage(R.drawable.locgif);
		
		gifView.setGifImageType(GifImageType.COVER);
		galleryFlow = (Gallery) findViewById(R.id.Gallery01);
	   
		galleryFlow.setAdapter(adapter);
		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

	
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
		              long id) {
		         // 选中Gallery中某个图像时，放大显示该图像
		          ImageView imageview = (ImageView)view;
		    
		         view.setLayoutParams(new Gallery.LayoutParams(570 / 3, 370 / 3));
		       
		          for(int i=0; i<parent.getChildCount();i++){
		             //缩小选中图片旁边的图片
		             ImageView local_imageview = (ImageView)parent.getChildAt(i);
		           if(local_imageview!=imageview){
		                 local_imageview.setLayoutParams(new Gallery.LayoutParams(520/4, 318/4));
		                 local_imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		                 local_imageview.setAlpha(0.1f);
		             }
		           else {
		        	   local_imageview.setAlpha(1f);
				}
		         }
		     }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Button button_yes = (Button) findViewById(R.id.button_yes);
		ImageView routeImageView = (ImageView) findViewById(R.id.routeList);
		routeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent2 = new Intent();
				intent2.setClass(MainActivity.this, RouteListActivity.class);
				startActivity(intent2);
			}
		});
		// WheelView NumberOfPerson = (WheelView)
		// findViewById(R.id.NumberOfPerson);
		// String Numbers[] = new String[] {"1", "2", "3",
		// "4","5","6","7","8","9","10"};

		// final WheelView numberWheel = (WheelView)
		// findViewById(R.id.NumberOfPerson);
		// String countries[] = new String[] { "2", "3", "4", "5", "6", "7", "8"
		// };
		// numberWheel.setVisibleItems(5);
		// numberWheel.setCyclic(false);
		// numberWheel.setAdapter(new ArrayWheelAdapter<String>(countries));
		final String cities[][] = new String[][] { Types, Types, Types, Types,
				Types, Types, Types };
		// temp cancel wheel
		// typeWheel = (WheelView) findViewById(R.id.Type);
		// typeWheel.setAdapter(new ArrayWheelAdapter<String>(Types));
		// typeWheel.setVisibleItems(5);

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
				
				dialog = new Dialog(MainActivity.this,
						R.style.activity_translucent);
				dialog.setContentView(R.layout.dialog_connect);
				dialog.show();
				// Toast.makeText(getApplicationContext(),
				// Types[typeWheel.getCurrentItem()], Toast.LENGTH_SHORT)
				// .show();
				// Log.i("Current Item", Types[typeWheel.getCurrentItem()]);

				if (location == null) {
					new RequestTask().execute(Types[galleryFlow
							.getSelectedItemPosition()]);
				} else {
					new RequestTask().execute(
							Types[galleryFlow.getSelectedItemPosition()],
							String.valueOf(location.getLongitude()),
							String.valueOf(location.getLatitude()));
				}

			}
		});
		// numberWheel.setCurrentItem(3);
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
		// TextView myLocationText = (TextView)
		// findViewById(R.id.text_location);
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			latLongString = "纬度:" + lat + "\n经度:" + lng;
			
		} else {
			latLongString = "无法获取地理信息";
		}
		if(count==0)
		{
			Log.i("address request start", "execute");
			// TODO 修改坐标
			new AddressRequestTask().execute("34.238225","108.924703");
			
		}
		count++;
		Toast.makeText(
				getApplicationContext(),
				"(main)您当前的位置是: " + "\n" + latLongString + "\n"
						+ getAddressbyGeoPoint(location), Toast.LENGTH_LONG)
				.show();
		// myLocationText.setText("您当前的位置是:/n" + latLongString + "/n"
		// + getAddressbyGeoPoint(location));

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

	public class RequestTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			
			double pos_x = 108.947039, pos_y = 34.259203;

			if (params.length > 1) {
				pos_x = Double.parseDouble(params[1]);
				pos_y = Double.parseDouble(params[2]);
			}
			try {
				return RequestToServer(params[0], pos_x, pos_y);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {// 在调用publishProgress之后被调用，在ui线程执行
			// mProgressBar.setProgress(progress[0]);
			Log.i("Progress", String.valueOf(progress[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			decodeJson objdecodeJson = null;
			try {
				objdecodeJson = new decodeJson(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Log.i("top", objdecodeJson.getTop());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			places = objdecodeJson
					.JsonToPlaceList(objdecodeJson.getJsonArray());
			for (int i = 0; i < places.size(); i++) {
				Log.i("places info", places.get(i).getShopName());

			}

			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), WarpDSLV.class);
			intent.putExtra("places", (Serializable) places);
			intent.putExtra("type",
					Types[galleryFlow.getSelectedItemPosition()]);
			intent.putExtra("loclat", lat);
			intent.putExtra("loclng", lng);
			dialog.dismiss();
			startActivity(intent);
		}

	}
	public class AddressRequestTask extends AsyncTask<String, integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return AddressRequset(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result)
		{
			try {
				JSONObject jsonObject = new JSONObject(result);
				JSONObject jsonResult = new JSONObject(jsonObject.getString("result"));
				Toast.makeText(getApplicationContext(), jsonResult.getString("formatted_address"), Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public String RequestToServer(String typeString, double pos_x, double pos_y)
			throws JSONException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			// getApplicationContext().getMainLooper();
			// Looper.prepare();

			HttpHost target = new HttpHost(ipString, 8080, "http");
			// String request="/?type=情侣出行&pos_x=108.947039&pos_y=34.259203";
			String request = "/?command=full&type=" + typeString + "&pos_x="
					+ String.valueOf(pos_x) + "&pos_y=" + String.valueOf(pos_y);
			Log.i("request string", request);
			HttpGet req = new HttpGet(request);
			// System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			line = br.readLine();
			if (line != null) {

				System.out.println(line);
				return line;

			} else {
				System.out.println("line is null");
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;

	}
	public static String AddressRequset(String pos_x_add,String pos_y_add)
	 {
	DefaultHttpClient httpclient = new DefaultHttpClient();
	try {

		
		HttpHost target = new HttpHost("api.map.baidu.com", 80, "http");
		// String request="/?type=情侣出行&pos_x=108.947039&pos_y=34.259203";
		String request = "/geocoder?output=json&location=" + pos_x_add+ "," + pos_y_add +"&key=APP_KEY";
		//Log.i("request string",request);
		HttpGet req = new HttpGet(request);
		System.out.println("executing request to " + target);
		HttpResponse rsp = httpclient.execute(target, req);
		HttpEntity entity = rsp.getEntity();
		InputStreamReader isr = new InputStreamReader(entity.getContent(),
				"utf-8");
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		StringBuilder output = new StringBuilder();
		//line = br.readLine();
		while ((line = br.readLine()) != null)
		{
			output.append(line);
			
		}
		System.out.println(output);
		return output.toString();
		
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();
	}
	return null;

}
}
