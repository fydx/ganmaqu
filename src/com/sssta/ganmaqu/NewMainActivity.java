package com.sssta.ganmaqu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class NewMainActivity extends SlidingFragmentActivity {
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Address address;
	private List<place> places;
	private static String ipString;
	final String Types[] = new String[] { "���ӳ���", "���ѳ���", "���³���" };
	private static double lat;
	private static double lng;
	private Dialog dialog;
	private Gallery galleryFlow;
	private GifView gifView;
	private int count, count_city;
	private TextView locTextView;
	private static TextView circleTextView;
	private static TextView cityTextView;
	private Button changecityButton;
	private FinalDb db;
	private String userid;
	private static String city;
	// private String circleString;
	private CircleDialog circleDialog;
	private SlidingMenu menu;
	private SharedPreferences userInfo;
	private int count_first;
	public Connect connect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setBehindContentView(R.layout.menu);
		ShareSDK.initSDK(this);
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		connect = new Connect(ipString);
		// Platform[] platformList =
		// ShareSDK.getPlatformList(NewMainActivity.this) ;
		// platformList[0].authorize();
		setContentView(R.layout.activity_new_main);
		userInfo = getApplicationContext().getSharedPreferences("userInfo", 0);
		city = userInfo.getString("city", "������");
		count_first = userInfo.getInt("first", 0);
		count_city = userInfo.getInt("count_city", 0);
		Log.i("city from sharedperferece", city);
		setSlidingActionBarEnabled(true);
		// set sina authorize()
		// final Platform weibo = ShareSDK.getPlatform(NewMainActivity.this,
		// SinaWeibo.NAME);
		PlatformActionListener paListener = new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				// String id=weibo.getDb().getUserId();
				// Log.i("id", id);

			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		};
		// String id=weibo.getDb().getUserId();
		// Log.i("id2", id);
		// weibo.setPlatformActionListener(paListener);
		// weibo.authorize();
		// weibo.showUser(id);
		// weibo.setPlatformActionListener(paListener); // ���÷����¼��ص�

		// set sliding menu
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
		View actionbar_title = LayoutInflater.from(this).inflate(
				R.layout.actionbar_main, null);
		ActionBar actionBar = this.getActionBar();
		TextView title = (TextView) actionbar_title.findViewById(R.id.title);
		Button button_back = (Button) actionbar_title
				.findViewById(R.id.button_back);

		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ������ؼ��رջ����˵�
				if (menu.isMenuShowing()) {
					menu.showContent();
				} else {
					menu.showMenu();
				}
			}
		});
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.city);
		relativeLayout.getBackground().setAlpha(100);

		cityTextView = (TextView) findViewById(R.id.textView_city);
		cityTextView.setText(city);
		changecityButton = (Button) findViewById(R.id.button_changecity);
		changecityButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChangeCityDialog changeCityDialog = new ChangeCityDialog(
						NewMainActivity.this, String.valueOf(lng), String
								.valueOf(lat));
				changeCityDialog.setTextView(cityTextView);
				changeCityDialog.setCircleTextView(circleTextView);
				changeCityDialog.show();

			}
		});
		Button button_right = (Button) actionbar_title
				.findViewById(R.id.button_right);
		button_right.setOnClickListener(new OnClickListener() {

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
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		actionBar.setCustomView(actionbar_title, params);
		actionBar.setDisplayShowCustomEnabled(true);

		locTextView = (TextView) findViewById(R.id.textView_loc);
		// locTextView.setBackgroundColor(0xe0FFFFFF);
		locTextView.getBackground().setAlpha(100);
		circleTextView = (TextView) findViewById(R.id.textView_circle);
		circleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				circleDialog = new CircleDialog(NewMainActivity.this,
						cityTextView.getText().toString());
				// circleDialog.setCity(city);
				//circleDialog.setTextView(circleTextView);
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

		galleryFlow = (Gallery) findViewById(R.id.Gallery02);

		galleryFlow.setAdapter(adapter);
		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// ѡ��Gallery��ĳ��ͼ��ʱ���Ŵ���ʾ��ͼ��
				ImageView imageview = (ImageView) view;
				view.setLayoutParams(new Gallery.LayoutParams(650 / 3, 470 / 3));
				for (int i = 0; i < parent.getChildCount(); i++) {
					// ��Сѡ��ͼƬ�Աߵ�ͼƬ
					ImageView local_imageview = (ImageView) parent
							.getChildAt(i);
					if (local_imageview != imageview) {
						local_imageview
								.setLayoutParams(new Gallery.LayoutParams(
										520 / 4, 318 / 4));
						local_imageview
								.setScaleType(ImageView.ScaleType.FIT_CENTER);
						// local_imageview.setColorFilter(Color.parseColor("#000000"));
						local_imageview.setAlpha(0.1f);

					} else {
						// local_imageview.setColorFilter(Color.parseColor("#c70102"));
						local_imageview.setAlpha(1f);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button button_yes = (Button) findViewById(R.id.button_result);

		final String cities[][] = new String[][] { Types, Types, Types, Types,
				Types, Types, Types };

		button_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (lat == 0.0 || lng == 0.0) {
					Toast.makeText(getApplicationContext(),
							"��λʧ�ܣ���򿪶�λ������Ժ�����", Toast.LENGTH_SHORT).show();
				} else {
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
								String.valueOf(location.getLatitude()),
								cityTextView.getText().toString());
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
			new AddressRequestTask().execute(String.valueOf(lat),
					String.valueOf(lng));
			Log.i("loaction", String.valueOf(lat) + " " + String.valueOf(lng));
			if (count_first != 0) {
				new getCurrentCircle().execute(String.valueOf(lng),
						String.valueOf(lat), city);
			}

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
						circleTextView.getText().toString(), params[3]);

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

	public class AddressRequestTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return connect.GetCurrentAddress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {
				locTextView.setText("��ʱ�޷���ȡλ��");
			} else {
				try {
					Log.i("ADDRESS REQUEST", result);
					JSONObject jsonObject = new JSONObject(result);
					JSONObject jsonResult = new JSONObject(
							jsonObject.getString("result"));
					// Toast.makeText(getApplicationContext(),
					// jsonResult.getString("formatted_address"),
					// Toast.LENGTH_LONG).show();
					locTextView.setText(jsonResult
							.getString("formatted_address"));
					if (count_city == 0) {

						city = null;
						String addressComponent = jsonResult
								.getString("addressComponent");
						JSONObject address = new JSONObject(addressComponent);
						city = new String(address.getString("city"));
						Log.i("city", city);
						cityTextView.setText(city);
						userInfo.edit().putString("city", city).commit();
						count_city++;
						userInfo.edit().putInt("count_city", count_city)
								.commit();
						new getCurrentCircle().execute(String.valueOf(lng),
								String.valueOf(lat), cityTextView.getText()
										.toString());
						count_first++;
						userInfo.edit().putInt("first", count_first).commit();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

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
				circleTextView.setText("��ʱ�޷���ȡλ��");
			} else {
				circleTextView.setText(result);
			}
		}

	}

	public void startrequest() {
		new getCurrentCircle().execute(String.valueOf(lng),
				String.valueOf(lat), cityTextView.getText().toString());
		Log.i("start", "start");
	}

	public void createLogoutDialog()

	{

		AlertDialog dlg = new AlertDialog.Builder(NewMainActivity.this)
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

}
