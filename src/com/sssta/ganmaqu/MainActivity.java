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
	final String Types[] = new String[] { "���ӳ���", "���ѳ���", "���³���" };
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

		city = userInfo.getString("city", "������");
		count_first = userInfo.getInt("first", 0);
		count_city = userInfo.getInt("count_city", 0);
		Log.i("city from sharedperferece", city);
		setSlidingActionBarEnabled(true);
		/*
		 * ���ý��Զ���
		 */
		// ����һ��AnimationSet���󣬲���ΪBoolean�ͣ�
		// true��ʾʹ��Animation��interpolator��false����ʹ���Լ���
		AnimationSet animationSet = new AnimationSet(true);
		// ����һ��AlphaAnimation���󣬲�������ȫ��͸���ȣ�����ȫ�Ĳ�͸��
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		// ���ö���ִ�е�ʱ��
		alphaAnimation.setDuration(3000);
		// ���ö����ӳ�ִ��ʱ��
		alphaAnimation.setStartOffset(1300);
		// ��alphaAnimation������ӵ�AnimationSet����
		animationSet.addAnimation(alphaAnimation);
		/*
		 * ����λ�������ƲʵĶ���
		 */
		AnimationSet animationSetTrans = new AnimationSet(false);
		// ����2��x��Ŀ�ʼλ��
		// ����4��x��Ľ���λ��
		// ����6��y��Ŀ�ʼλ�� 
		// ����8��y��Ľ���λ��
		// ����1,3,5,7 : fromX/YType
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
		 * ����λ�������ƲʵĶ���
		 */
		AnimationSet animationSetTrans_top = new AnimationSet(false);
		// ����2��x��Ŀ�ʼλ��
		// ����4��x��Ľ���λ��
		// ����6��y��Ŀ�ʼλ�� 
		// ����8��y��Ľ���λ��
		// ����1,3,5,7 : fromX/YType
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
		 * �ؼ���
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
		 * ���ö���
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
	//	 weibo.setPlatformActionListener(paListener); // ���÷����¼��ص�
		imageView_change.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<place> placeList = db.findAll(place.class);
				if (placeList.isEmpty()) {
					Toast.makeText(getApplicationContext(), "��Ŷ���㻹û�б����·��",
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
					.setTitle("�������")
					.setMessage("��������ʧ�ܣ���ȷ����������")
					.setPositiveButton("ȷ��",
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

				// ������ؼ��رջ����˵�
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
		// Toast.makeText(getApplicationContext(), "��Ŷ���㻹û�б����·��",
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
		// new getCircles().execute("����");

		userid = userInfo.getString("userid", "root");
		// ��ȡLocationManager����
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// ��ȡLocation Provider
		getProvider();
		// ���δ����λ��Դ����GPS���ý���
		openGPS();
		// ��ȡλ��
		location = locationManager.getLastKnownLocation(provider);
		// ��ʾλ����Ϣ�����ֱ�ǩ
		updateWithNewLocation(location);
		// ע�������locationListener����2��3���������Կ��ƽ���gps��Ϣ��Ƶ���Խ�ʡ��������2������Ϊ���룬
		// ��ʾ����listener�����ڣ���3������Ϊ��,��ʾλ���ƶ�ָ�������͵���listener
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
							"��λʧ�ܣ���򿪶�λ������Ժ�����", Toast.LENGTH_SHORT).show();
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
								"������"));
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

	// �ж��Ƿ���GPS����δ��������GPS���ý���
	private void openGPS() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				|| locationManager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			Log.i("location service", "λ��Դ�����ã�");
			// Toast.makeText(this, , Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this, "��δ������λ����", Toast.LENGTH_SHORT).show();
		createLogoutDialog();

	}

	// ��ȡLocation Provider
	private void getProvider() {
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
	private final LocationListener locationListener = new LocationListener() {
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
		// TextView myLocationText = (TextView)
		// findViewById(R.id.text_location);
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			latLongString = "γ��:" + lat + "\n����:" + lng;

		} else {
			latLongString = "�޷���ȡ������Ϣ";
		}
		if (count == 0) {
			Log.i("address request start", "execute");
			// TODO �޸�����
			// new AddressRequestTask().execute("34.238225","108.924703");
			// //Test
			new getCurrentCircle().execute(String.valueOf(lng),
					String.valueOf(lat), userInfo.getString("city", "������"));
			Log.i("loaction", String.valueOf(lat) + " " + String.valueOf(lng));
//			if (count_first != 0) {
//				new getCurrentCircle().execute(String.valueOf(lng),
//						String.valueOf(lat), userInfo.getString("city", "������"));
//			}
			ProfileFragment fragment = (ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.menu_right);
			fragment.new AddressRequestTask().execute(String.valueOf(lat),
					String.valueOf(lng));
			
			

		}
		count++;
		// Toast.makeText(
		// getApplicationContext(),
		// "(main)����ǰ��λ����: " + "\n" + latLongString + "\n"
		// + getAddressbyGeoPoint(location), Toast.LENGTH_LONG)
		// .show();
		// myLocationText.setText("����ǰ��λ����:/n" + latLongString + "/n"
		// + getAddressbyGeoPoint(location));

	}

	// ��ȡ��ַ��Ϣ
	private List<Address> getAddressbyGeoPoint(Location location) {
		List<Address> result = null;
		// �Ƚ�Locationת��ΪGeoPoint
		// GeoPoint gp=getGeoByLocation(location);
		try {
			if (location != null) {
				// ��ȡGeocoder��ͨ��Geocoder�Ϳ����õ���ַ��Ϣ
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

		protected void onProgressUpdate(Integer... progress) {// �ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��
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
				circleButton.setText("��ʱ�޷���ȡλ��");
			} else {
				circleButton.setText(result);
			}
		}

	}

	public void startrequest() {
		new getCurrentCircle().execute(String.valueOf(lng),
				String.valueOf(lat), userInfo.getString("city", "������"));
		Log.i("start", "start");
	}

	public void createLogoutDialog()

	{

		AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
				.setTitle("��ʾ")
				.setMessage("��û�п�����λ�������Ӱ�����ȥ��ʹ��Ч�����Ƿ�������ÿ�����λ����")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// ת��GPS���ý���
						Intent intent = new Intent(
								Settings.ACTION_SECURITY_SETTINGS);
						startActivityForResult(intent, 0);
					}

				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dlg.show();

	}

	public static boolean isConnect(Context context) {

		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�����
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
