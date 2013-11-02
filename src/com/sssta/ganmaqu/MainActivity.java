package com.sssta.ganmaqu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sssta.ganmaqu.ProfileFragment.OnFragmentInteractionListener;

public class MainActivity extends SlidingFragmentActivity implements OnFragmentInteractionListener {
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private List<place> places;
	private static String ipString;
	final String Types[] = new String[] { "亲子出行", "朋友出行", "情侣出行" };
	private static double lat;
	private static double lng;
	private Dialog dialog;
	private int count, count_city, count_type;
	private static Button circleButton;
	private Button button_type, button_yes;
	private FinalDb db;
	private String userid;
	private static String city;
	private CircleDialog circleDialog;
	private SlidingMenu menu;
	private SharedPreferences userInfo;
	private int count_first;
	public Connect connect;
	private ImageView imageView_change;
	public static double getLat()
	{
		return lat;
	}
	public static double getLng(){
		return lng;
	}
	public static Button getCircleButton()
	{
		return circleButton;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // no title bar
		setBehindContentView(R.layout.menu_right);
		ShareSDK.initSDK(this);
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		connect = new Connect(ipString);
		count_type = 0;
		// Platform[] platformList =
		// ShareSDK.getPlatformList(NewMainActivity.this) ;
		// platformList[0].authorize();
		setContentView(R.layout.activity_selectedmain);
		userInfo = getApplicationContext().getSharedPreferences("userInfo", 0);

		city = userInfo.getString("city", "西安市");
		count_first = userInfo.getInt("first", 0);
		count_city = userInfo.getInt("count_city", 0);
		Log.i("city from sharedperferece", city);
		setSlidingActionBarEnabled(true);
		/*
		 * 设置渐显动画
		 */
		// 创建一个AnimationSet对象，参数为Boolean型，
		// true表示使用Animation的interpolator，false则是使用自己的
		AnimationSet animationSet = new AnimationSet(true);
		// 创建一个AlphaAnimation对象，参数从完全的透明度，到完全的不透明
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		// 设置动画执行的时间
		alphaAnimation.setDuration(3000);
		// 设置动画延迟执行时间
		alphaAnimation.setStartOffset(1300);
		// 将alphaAnimation对象添加到AnimationSet当中
		animationSet.addAnimation(alphaAnimation);
		/*
		 * 设置位移下面云彩的动画
		 */
		AnimationSet animationSetTrans = new AnimationSet(false);
		// 参数2：x轴的开始位置
		// 参数4：x轴的结束位置
		// 参数6：y轴的开始位置 
		// 参数8：y轴的结束位置
		// 参数1,3,5,7 : fromX/YType
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -0.4f, Animation.RELATIVE_TO_SELF,
				-0.2f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setDuration(40000);
		translateAnimation.setStartOffset(200);
		translateAnimation.setInterpolator(AnimationUtils.loadInterpolator(
				MainActivity.this, android.R.anim.cycle_interpolator));
		translateAnimation.setRepeatCount(-1);
		animationSetTrans.addAnimation(translateAnimation);
		/*
		 * 设置位移上面云彩的动画
		 */
		AnimationSet animationSetTrans_top = new AnimationSet(false);
		// 参数2：x轴的开始位置
		// 参数4：x轴的结束位置
		// 参数6：y轴的开始位置 
		// 参数8：y轴的结束位置
		// 参数1,3,5,7 : fromX/YType
		TranslateAnimation translateAnimation_top = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF,
				0.05f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation_top.setDuration(33000);
		translateAnimation_top.setStartOffset(150);
		translateAnimation_top.setInterpolator(AnimationUtils.loadInterpolator(
				MainActivity.this, android.R.anim.cycle_interpolator));
		translateAnimation_top.setRepeatCount(-1);
		animationSetTrans_top.addAnimation(translateAnimation_top);
		/*
		 * 控件绑定
		 */
		button_type = (Button) findViewById(R.id.button_type);
		ImageView navigation_drawer = (ImageView) findViewById(R.id.navigation_drawer);
		TextView textView1 = (TextView) findViewById(R.id.tuijianshangquan);
		TextView textView2 = (TextView) findViewById(R.id.chuxingleixing);
		circleButton = (Button) findViewById(R.id.button_circle);
		button_type.setAnimation(animationSet);
		button_yes = (Button) findViewById(R.id.button_go);
		ImageView cloud_top = (ImageView) findViewById(R.id.imageView_cloud_top);
		ImageView cloud_bottom = (ImageView) findViewById(R.id.imageView_cloud_bottom);
		imageView_change= (ImageView)findViewById(R.id.imageView_last);
		/*
		 * 设置动画
		 */
		navigation_drawer.setAnimation(animationSet);
		textView1.setAnimation(animationSet);
		textView2.setAnimation(animationSet);
		circleButton.setAnimation(animationSet);
		button_yes.setAnimation(animationSet);
		imageView_change.setAnimation(animationSet);
		cloud_top.setAnimation(animationSetTrans_top);
		cloud_bottom.setAnimation(animationSetTrans);
		// set sina authorize()
//		
		db = FinalDb.create(this);
		PlatformActionListener paListener = new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				 String id=arg0.getDb().getUserId();
				 Log.i("id", id);

			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		};
//		final Platform weibo = ShareSDK.getPlatform(MainActivity.this,
//				SinaWeibo.NAME);
//		weibo.removeAccount();
//		String id=weibo.getDb().getUserId();
//		Log.i("id_in Main", id);
		
//		 weibo.setPlatformActionListener(paListener);
//		weibo.authorize();
	//	weibo.showUser(id);
	//	 weibo.setPlatformActionListener(paListener); // 设置分享事件回调
		imageView_change.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<place> placeList = db.findAll(place.class);
				if (placeList.isEmpty()) {
					Toast.makeText(getApplicationContext(), "啊哦，你还没有保存过路线",
							Toast.LENGTH_SHORT).show();
				} else {
					int route_id = placeList.get(placeList.size() - 1)
							.getRoute_id();
					List<place> lastLine = db.findAllByWhere(place.class,
							"route_id = " + String.valueOf(route_id));
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), WarpDSLV.class);
					intent.putExtra("places", (Serializable) lastLine);
					intent.putExtra("type", lastLine.get(0).getRouteType());
					startActivity(intent);
				}
			}
		});
		// set sliding menu
		menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);

		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
//		menu.setSecondaryMenu(R.layout.menu_right);
//		menu.setSecondaryShadowDrawable(R.drawable.shadowright);
		// menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// menu.setMenu(R.layout.menu);
		
		if (isConnect(this) == false) {
			new AlertDialog.Builder(this)
					.setTitle("网络错误")
					.setMessage("网络连接失败，请确认网络连接")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);
								}
							}).show();
		}
		if (count_first == 0) {

			// SimpleDialog SimpleDialog = new SimpleDialog(
			// NewMainActivity.this, R.drawable.mainpageguide);
			//
			// SimpleDialog.show();

		}

		navigation_drawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 点击返回键关闭滑动菜单
				if (menu.isMenuShowing()) {
					menu.showContent();
				} else {
					menu.showMenu();
				}
			}
		});

		button_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				button_type.setText(Types[count_type % 3]);
				count_type++;
			}
		});
		// RelativeLayout relativeLayout = (RelativeLayout)
		// findViewById(R.id.city);
		// relativeLayout.getBackground().setAlpha(100);

		// cityTextView = (TextView) findViewById(R.id.textView_city);
		// cityTextView.setText(city);
		// changecityButton = (Button) findViewById(R.id.button_changecity);
		// changecityButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// ChangeCityDialog changeCityDialog = new ChangeCityDialog(
		// MainActivity.this, String.valueOf(lng), String
		// .valueOf(lat));
		// changeCityDialog.setTextView(cityTextView);
		// changeCityDialog.setcircleButton(circleButton);
		// changeCityDialog.show();
		//
		// }
		// });
		// Button button_right = (Button) actionbar_title
		// .findViewById(R.id.button_right);
		// button_right.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// List<place> placeList = db.findAll(place.class);
		// if (placeList.isEmpty()) {
		// Toast.makeText(getApplicationContext(), "啊哦，你还没有保存过路线",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// int route_id = placeList.get(placeList.size() - 1)
		// .getRoute_id();
		// List<place> lastLine = db.findAllByWhere(place.class,
		// "route_id = " + String.valueOf(route_id));
		// Intent intent = new Intent();
		// intent.setClass(getApplicationContext(), WarpDSLV.class);
		// intent.putExtra("places", (Serializable) lastLine);
		// intent.putExtra("type", lastLine.get(0).getRouteType());
		// startActivity(intent);
		// }
		// }
		// });
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// LayoutParams params = new ActionBar.LayoutParams(
		// ActionBar.LayoutParams.MATCH_PARENT,
		// ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		// actionBar.setCustomView(actionbar_title, params);
		// actionBar.setDisplayShowCustomEnabled(true);

		// locTextView.setBackgroundColor(0xe0FFFFFF);

		circleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				circleDialog = new CircleDialog(MainActivity.this, city);
				// circleDialog.setCity(city);
				circleDialog.setbutton(circleButton);
				circleDialog.show();

			}
		});

		count = 0;
		// new getCircles().execute("西安");

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

		// set gallery
		Integer[] images = { R.drawable.child, R.drawable.friend,
				R.drawable.couple };

		ImageAdapter adapter = new ImageAdapter(this, images);
		adapter.createReflectedImages();

		button_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (lat == 0.0 || lng == 0.0) {
					Toast.makeText(getApplicationContext(),
							"定位失败，请打开定位服务或稍后再试", Toast.LENGTH_SHORT).show();
				} else {
					dialog = new Dialog(MainActivity.this,
							R.style.activity_translucent);
					dialog.setContentView(R.layout.dialog_connect);
					dialog.show();
					// Toast.makeText(getApplicationContext(),
					// Types[typeWheel.getCurrentItem()], Toast.LENGTH_SHORT)
					// .show();
					// Log.i("Current Item", Types[typeWheel.getCurrentItem()]);

					if (location == null) {
						new RequestTask().execute(button_type.getText()
								.toString());
					} else {
						new RequestTask().execute(button_type.getText()
								.toString(), String.valueOf(location
								.getLongitude()), String.valueOf(location
								.getLatitude()), userInfo.getString("city",
								"西安市"));
					}

				}
			}
		});
		// numberWheel.setCurrentItem(3);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	// 判断是否开启GPS，若未开启，打开GPS设置界面
	private void openGPS() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				|| locationManager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			Log.i("location service", "位置源已设置！");
			// Toast.makeText(this, , Toast.LENGTH_SHORT).show();
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
		if (count == 0) {
			Log.i("address request start", "execute");
			// TODO 修改坐标
			// new AddressRequestTask().execute("34.238225","108.924703");
			// //Test
			new getCurrentCircle().execute(String.valueOf(lng),
					String.valueOf(lat), userInfo.getString("city", "西安市"));
			Log.i("loaction", String.valueOf(lat) + " " + String.valueOf(lng));
//			if (count_first != 0) {
//				new getCurrentCircle().execute(String.valueOf(lng),
//						String.valueOf(lat), userInfo.getString("city", "西安市"));
//			}
			ProfileFragment fragment = (ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.menu_right);
			fragment.new AddressRequestTask().execute(String.valueOf(lat),
					String.valueOf(lng));
			
			

		}
		count++;
		// Toast.makeText(
		// getApplicationContext(),
		// "(main)您当前的位置是: " + "\n" + latLongString + "\n"
		// + getAddressbyGeoPoint(location), Toast.LENGTH_LONG)
		// .show();
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
			typeString = params[0];
			double pos_x = 108.947039, pos_y = 34.259203;

			if (params.length > 1) {
				pos_x = Double.parseDouble(params[1]);
				pos_y = Double.parseDouble(params[2]);
			}
			
			try {
				return connect.GetFullRoute(params[0], pos_x, pos_y, userid,
						circleButton.getText().toString(), params[3]);

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
			intent.putExtra("type", button_type.getText().toString());
			intent.putExtra("loclat", lat);
			intent.putExtra("loclng", lng);
			intent.putExtra("type", typeString);

			intent.putExtra("circle", circleButton.getText().toString());
			dialog.dismiss();
			startActivity(intent);
		}

	}

	public class getCircles extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return connect.GetCircleList(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				HashMap<Integer, String> hashMapCircle = hashCircle(result);
				Log.i("circle1", hashMapCircle.get(0));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public HashMap<Integer, String> hashCircle(String resultString)
			throws JSONException

	{

		HashMap<Integer, String> ansHashMap = new HashMap<Integer, String>();
		int i = 1;
		JSONObject circle = new JSONObject(resultString);
		// Log.i("result String", circle.toString());

		while (circle.has("item" + String.valueOf(i))) {
			String temp = circle.getString("item" + String.valueOf(i));
			ansHashMap.put(i - 1, temp);
			i++;
		}
		// Log.i("hasmap", ansHashMap.toString());
		return ansHashMap;
	}

	public class getCurrentCircle extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// Log.i("params, msg)
			return connect.GetShopCircle(Double.parseDouble(params[0]),
					Double.parseDouble(params[1]), params[2]);
		}

		@Override
		protected void onPostExecute(String result) {
			// circleString = result;
			if (result == null) {
				circleButton.setText("暂时无法获取位置");
			} else {
				circleButton.setText(result);
			}
		}

	}

	public void startrequest() {
		new getCurrentCircle().execute(String.valueOf(lng),
				String.valueOf(lat), userInfo.getString("city", "西安市"));
		Log.i("start", "start");
	}

	public void createLogoutDialog()

	{

		AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示")
				.setMessage("您没有开启定位服务，这会影响干嘛去的使用效果，是否进入设置开启定位服务？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 转至GPS设置界面
						Intent intent = new Intent(
								Settings.ACTION_SECURITY_SETTINGS);
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

	public static boolean isConnect(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);

	}
	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

}
