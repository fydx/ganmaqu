package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.WheelView;
import net.tsz.afinal.FinalDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.menu;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sssta.ganmaqu.MainActivity.RequestTask;
import com.sssta.ganmaqu.MainActivity.getCurrentCircle;
import com.sssta.ganmaqu.SettingsFragment.loginTask;

public class NewMainActivity extends SlidingFragmentActivity{
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Address address;
	private List<place> places;
	private static String ipString;
	final String Types[] = new String[] { "亲子出行", "朋友出行", "情侣出行" };
	private WheelView typeWheel;
	private static double lat;
	private static double lng;
	private Dialog dialog;
	private Gallery galleryFlow;
	private GifView gifView;
	private int count ,count_city;
	private TextView locTextView;
	private static  TextView circleTextView;
	private static TextView cityTextView;
	private Button changecityButton;
	private FinalDb db;
	private String userid;
	private static String city ; 
	//private String circleString;
	private CircleDialog circleDialog;
	private  SlidingMenu menu;
	private SharedPreferences userInfo ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
	    setBehindContentView(R.layout.menu);
	    
		setContentView(R.layout.activity_new_main);
		userInfo = getApplicationContext().getSharedPreferences("userInfo", 0);
		city=userInfo.getString("city", "西安市");
		count_city=userInfo.getInt("count_city", 0);
		Log.i("city from sharedperferece", city);
		setSlidingActionBarEnabled(true);
		//set sliding menu
		  menu = getSlidingMenu();
	      menu.setMode(SlidingMenu.LEFT);
	      
	      menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	      menu.setShadowWidthRes(R.dimen.shadow_width);
	      menu.setShadowDrawable(R.drawable.shadow);
	      menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	      menu.setFadeDegree(0.35f);
	     // menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	     // menu.setMenu(R.layout.menu);
		db = FinalDb.create(this);
		
		
		View actionbar_title = LayoutInflater.from(this).inflate( 
                R.layout.actionbar_main, null);
		ActionBar actionBar = this.getActionBar();
        TextView title = (TextView) actionbar_title.findViewById(R.id.title);
        Button button_back = (Button) actionbar_title.findViewById(R.id.button_back); 
        
        button_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 点击返回键关闭滑动菜单
				if (menu.isMenuShowing()) {
					menu.showContent();
				} 
				else {
					menu.showMenu();
				}
			}
		});
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.city);
        relativeLayout.getBackground().setAlpha(100);
        
        cityTextView = (TextView)findViewById(R.id.textView_city);
        cityTextView.setText(city);
        changecityButton = (Button)findViewById(R.id.button_changecity);
        changecityButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChangeCityDialog changeCityDialog = new  ChangeCityDialog(NewMainActivity.this,String.valueOf(lng),String.valueOf(lat));
				changeCityDialog.setTextView(cityTextView);
				changeCityDialog.show();
				
			}
		});
        Button button_right = (Button) actionbar_title.findViewById(R.id.button_right); 
        button_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<place> placeList = db.findAll(place.class);
				if (placeList.isEmpty()) {
					Toast.makeText(getApplicationContext(), "啊哦，你还没有保存过路线", Toast.LENGTH_SHORT).show();
				}
				else {
					int route_id = placeList.get(placeList.size()-1).getRoute_id();
					List<place> lastLine =  db.findAllByWhere(place.class, "route_id = " + String.valueOf(route_id));
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), WarpDSLV.class);
					intent.putExtra("places", (Serializable)lastLine);
					intent.putExtra("type", lastLine.get(0).getRouteType());
					startActivity(intent);
				}
			}
		});
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
       LayoutParams params = new ActionBar.LayoutParams( 
                ActionBar.LayoutParams.MATCH_PARENT, 
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER); 
        actionBar.setCustomView(actionbar_title, params); 
        actionBar.setDisplayShowCustomEnabled(true);
        
		locTextView = (TextView)findViewById(R.id.textView_loc);
		//locTextView.setBackgroundColor(0xe0FFFFFF);
		locTextView.getBackground().setAlpha(100);
		circleTextView = (TextView)findViewById(R.id.textView_circle);
		circleTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				circleDialog = new CircleDialog(NewMainActivity.this,cityTextView.getText().toString());
			//	circleDialog.setCity(city);
				circleDialog.setTextView(circleTextView);
				circleDialog.show();
				
			}
		});
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		count = 0;
		//new getCircles().execute("西安");
		
		userid = userInfo.getString("userid", "root");
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
	
		
//		gifView = (GifView)findViewById(R.id.gifview);		
//		gifView.setGifImage(R.drawable.locgif);
//		
//		gifView.setGifImageType(GifImageType.COVER);
		//set settings button
//		Button button_settings = (Button)findViewById(R.id.button_settings);
//		button_settings.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(getApplicationContext(),SettingsActivity.class);
//				startActivity(intent);
//			}
//		});
		// set gallery
		Integer[] images = { R.drawable.child, R.drawable.friend,
				R.drawable.couple };

		ImageAdapter adapter = new ImageAdapter(this, images);
		adapter.createReflectedImages();

		galleryFlow = (Gallery) findViewById(R.id.Gallery02);
	   
		galleryFlow.setAdapter(adapter);
		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

	
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
		              long id) {
		         // 选中Gallery中某个图像时，放大显示该图像
		          ImageView imageview = (ImageView)view;
		          view.setLayoutParams(new Gallery.LayoutParams(650 / 3, 470 / 3));
		          for(int i=0; i<parent.getChildCount();i++){
		             //缩小选中图片旁边的图片
		             ImageView local_imageview = (ImageView)parent.getChildAt(i);
		           if(local_imageview!=imageview){
		                 local_imageview.setLayoutParams(new Gallery.LayoutParams(520/4, 318/4));
		                 local_imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		               //  local_imageview.setColorFilter(Color.parseColor("#000000"));
		                 local_imageview.setAlpha(0.1f);
		             
		             }
		           else {
		        	    //local_imageview.setColorFilter(Color.parseColor("#c70102"));
		        	   	local_imageview.setAlpha(1f);
				}
		         }
		     }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//set Button last
//		Button button_last = (Button)findViewById(R.id.button_last);
//		button_last.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				List<place> placeList = db.findAll(place.class);
//				if (placeList.isEmpty()) {
//					Toast.makeText(getApplicationContext(), "啊哦，你还没有保存过路线", Toast.LENGTH_SHORT).show();
//				}
//				else {
//					int route_id = placeList.get(placeList.size()-1).getRoute_id();
//					List<place> lastLine =  db.findAllByWhere(place.class, "route_id = " + String.valueOf(route_id));
//					Intent intent = new Intent();
//					intent.setClass(getApplicationContext(), WarpDSLV.class);
//					intent.putExtra("places", (Serializable)lastLine);
//					intent.putExtra("type", lastLine.get(0).getRouteType());
//					startActivity(intent);
//				}
//				
//			}
//		});
		
		Button button_yes = (Button) findViewById(R.id.button_result);
//		ImageView routeImageView = (ImageView) findViewById(R.id.routeList);
//		routeImageView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				Intent intent2 = new Intent();
//				intent2.setClass(NewMainActivity.this, RouteListActivity.class);
//				startActivity(intent2);
//			}
//		});
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
				
				dialog = new Dialog(NewMainActivity.this,
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
							String.valueOf(location.getLatitude()),cityTextView.getText().toString());
				}

			}
		});
		// numberWheel.setCurrentItem(3);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
	
	
	// 判断是否开启GPS，若未开启，打开GPS设置界面
	private void openGPS() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				|| locationManager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			Log.i("location service", "位置源已设置！");
		//	Toast.makeText(this, , Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this, "您未开启定位服务", Toast.LENGTH_SHORT).show();
		createLogoutDialog();
	
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
			//new AddressRequestTask().execute("34.238225","108.924703"); //Test
			new AddressRequestTask().execute(String.valueOf(lat),String.valueOf(lng));
			Log.i("loaction", String.valueOf(lat) + " " + String.valueOf(lng));
			new getCurrentCircle().execute(String.valueOf(lng),String.valueOf(lat),city);
			
		}
		count++;
//		Toast.makeText(
//				getApplicationContext(),
//				"(main)您当前的位置是: " + "\n" + latLongString + "\n"
//						+ getAddressbyGeoPoint(location), Toast.LENGTH_LONG)
//				.show();
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
		private String typeString;
		@Override
		protected String doInBackground(String... params) {
			typeString= params[0];
			double pos_x = 108.947039, pos_y = 34.259203;

			if (params.length > 1) {
				pos_x = Double.parseDouble(params[1]);
				pos_y = Double.parseDouble(params[2]);
			}
			try {
				return RequestToServer(params[0], pos_x, pos_y,userid,circleTextView.getText().toString(),params[3]);
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
			intent.putExtra("type", typeString);
		
			intent.putExtra("circle", circleTextView.getText().toString());
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
			
			if (result==null) {
				locTextView.setText("暂时无法获取位置");
			}
			else {
				try {
					Log.i("ADDRESS REQUEST", result);
					JSONObject jsonObject = new JSONObject(result);
					JSONObject jsonResult = new JSONObject(jsonObject.getString("result"));
					//Toast.makeText(getApplicationContext(), jsonResult.getString("formatted_address"), Toast.LENGTH_LONG).show();
					locTextView.setText(jsonResult.getString("formatted_address"));
					if (count_city==0) {
						
						city = null;
						String addressComponent = jsonResult.getString("addressComponent");
						JSONObject address =  new JSONObject(addressComponent);
						city = new String(address.getString("city"));
						Log.i("city", city);
						cityTextView.setText(city);
						userInfo.edit().putString("city", city).commit();
						count_city++;
						userInfo.edit().putInt("count_city", count_city).commit();
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}
	public static  String RequestToServer(String typeString, double pos_x, double pos_y,String userid,String circle,String cityString)
			throws JSONException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			// getApplicationContext().getMainLooper();
			// Looper.prepare();

			HttpHost target = new HttpHost(ipString, 8080, "http");
			// String request="/?type=情侣出行&pos_x=108.947039&pos_y=34.259203";
			String request = "/?command=full&type=" +typeString+ "&city=" + cityString + "&id=" + userid + "&circleName="+circle;
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
	public  String AddressRequset(String pos_x_add,String pos_y_add)
	 {
	if (pos_x_add==null||pos_y_add==null) {
		return null;
	}
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
 public class getCircles extends AsyncTask<String, Integer, String>
 {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		return GetCircleList(params[0]);
	}
	@Override
	protected void onPostExecute(String result)
	{
		try {
			HashMap<Integer, String> hashMapCircle = hashCircle(result);
			Log.i("circle1", hashMapCircle.get(0));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 
 }
 public  String GetCircleList(String city)  //获得某个城市的商圈列表，item1,2,3....
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getcirclelist&city="+city+"";
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				Log.i("return circles line", line);
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
    public HashMap<Integer,String> hashCircle(String resultString) throws JSONException
    
    {
    	
    	HashMap<Integer,String> ansHashMap = new HashMap<Integer, String>();
    	int i = 1 ;
    	JSONObject circle = new JSONObject(resultString);
    //	Log.i("result String", circle.toString());
    	
    	
    	while (circle.has("item" + String.valueOf(i))) {
    		String temp = circle.getString("item"+ String.valueOf(i));
    		ansHashMap.put(i-1, temp);
    		i++;
		}
    	//Log.i("hasmap", ansHashMap.toString());
    	return ansHashMap;
    }
    public static String GetShopCircle(double pos_x,double pos_y,String city)   //给坐标，返回最近商圈名称
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getshopcircle&city="+city+"&pos_x="+pos_x+"&pos_y="+pos_y;
			System.out.println(request);
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null;
	}
    public static class getCurrentCircle extends AsyncTask<String, Integer, String>
    {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//Log.i("params, msg)
			return GetShopCircle(Double.parseDouble(params[0]), Double.parseDouble(params[1]), params[2]);
		}
    	@Override
    	protected void onPostExecute(String result)
    	{
    		//circleString = result;
    		if (result==null) {
				circleTextView.setText("暂时无法获取位置");
			}
    		else {
    			circleTextView.setText(result);
			}
    	}
    	
    }
    public static void startrequest()
	{
		new getCurrentCircle().execute(String.valueOf(lng),String.valueOf(lat),cityTextView.getText().toString());
		Log.i("start", "start");
	}
    public void createLogoutDialog()

	{
		
		AlertDialog dlg = new AlertDialog.Builder(NewMainActivity.this)
				.setTitle("提示")
				.setMessage("您没有开启定位服务，这会影响干嘛去的使用效果，是否进入设置开启定位服务？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 转至GPS设置界面
						Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
						startActivityForResult(intent, 0);
					}

				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dlg.show();

	}
}

