package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class NewMapActivity extends Activity {
	MapView mMapView = null; // ��ͼView
	// UI���
	Button mBtnReverseGeoCode = null; // �����귴����Ϊ��ַ
	Button mBtnGeoCode = null; // ����ַ����Ϊ����
	private Button preButton, nextButton;
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	ArrayList<MKPoiInfo> mkpoi = null;
	private OverlayTest itemOverlay = null;
	private List<place> places;
	private GeoPoint locGeoPoint;
	private List<GeoPoint> geoPoints;
	private PopupWindow mPopupWindow ;
	private int searchType = -1;// ��¼���������ͣ����ּݳ�/���к͹���
	String tmp = null;
	RouteOverlay routeOverlay = null;
	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	// ��λͼ��
	MyLocationOverlay myLocationOverlay = null;
	// UI���
	OnCheckedChangeListener radioButtonListener = null;
	private int countRoute = 0,isFindBus = 0;
	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	private Button requestLocButton = null;
	// ����·�����
	TransitOverlay transitOverlay = null;// ���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��

	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop = null;
	private ArrayList<OverlayItem> mItems = null;
	private TextView popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem mCurItem = null;
	private SharedPreferences userInfo ;
	private int mode ;
	private String city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		DemoApplication app = (DemoApplication) this.getApplication();
		final int markers_id[] = { R.drawable.icon_1, R.drawable.icon_2,
				R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5 };
		// BMapManager ������setContentviewǰ���ʼ�������򱨴�
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(DemoApplication.strKey,
					new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.activity_new_map);
		//��ʼ��sharedpreference
		userInfo = getApplicationContext().getSharedPreferences("userInfo", 0);
		mode = userInfo.getInt("mode", 0);
		city = userInfo.getString("city", "������");
		places = (List<place>) getIntent().getSerializableExtra("places");
		Log.i("places nums", String.valueOf(places.size()));
		requestLocButton = (Button) findViewById(R.id.button_loc);
		requestLocButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				requestLocClick();
			}
		});
		preButton = (Button) findViewById(R.id.button_pre);
		preButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (countRoute < 0) {
					Toast.makeText(getApplicationContext(), "���ǵ�һ��·��",
							Toast.LENGTH_SHORT).show();
					countRoute = 0;
					if (routeOverlay != null) {
						mMapView.getOverlays().remove(routeOverlay);
						initBusLine();
					}
				} else {
					MKPlanNode start = new MKPlanNode();
					start.pt = geoPoints.get(countRoute);
					MKPlanNode end = new MKPlanNode();
					end.pt = geoPoints.get(countRoute + 1);
					mSearch.walkingSearch(null, start, null, end);
					countRoute--;
				}

			}
		});
		nextButton = (Button) findViewById(R.id.button_next);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (transitOverlay != null) {
					mMapView.getOverlays().remove(transitOverlay);
					mMapView.refresh();
				}
				if (countRoute < 0) {
					countRoute = 0;
				}
				MKPlanNode start = new MKPlanNode();
				start.pt = geoPoints.get(countRoute);
				MKPlanNode end = new MKPlanNode();
				end.pt = geoPoints.get(countRoute + 1);
				mSearch.walkingSearch(null, start, null, end);
				countRoute++;
				if (countRoute > 3) {
					countRoute = 3;
					Toast.makeText(getApplicationContext(), "�������һ��·����",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mkpoi = new ArrayList<MKPoiInfo>();
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapsView);
		/**
		 * ��ȡ��ͼ������
		 */
		mMapController = mMapView.getController();
		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapController.enableClick(true);
		/**
		 * ���õ�ͼ���ż���
		 */
		mMapController.setZoom(14);
		/**
		 * ��ʾ�������ſؼ�
		 */
		//mMapView.setBuiltInZoomControls(true);

		/**
		 * �趨��ͼ���ĵ� ��������¥��
		 */
		GeoPoint p = new GeoPoint((int) (34.265733 * 1E6),
				(int) (108.953906 * 1E6));
//		GeoPoint p = new GeoPoint((int) (36.065159 * 1E6),
//				(int) (103.833222 * 1E6));
		mMapController.setCenter(p);

		// ��λ��ʼ��
		initLoc();
		// ��λ���ֽ���
		// setLocation(103.833222,36.065159);

		// ��װ�ص����굽list��
		geoPoints = null;
		geoPoints = new ArrayList<GeoPoint>();
		for (int i = 0; i < places.size(); i++) {
			geoPoints.add(new GeoPoint((int) (places.get(i).getPos_y() * 1E6),
					(int) (places.get(i).getPos_x() * 1E6)));
			Log.i("places info in GeoPoint", places.get(i).getShopName());
		}
		List<Drawable> markers = null;
		markers = new ArrayList<Drawable>();
		for (int i = 0; i < markers_id.length; i++) {
			markers.add(getResources().getDrawable(markers_id[i]));
		}
		List<OverlayItem> overlayItems = null;
		overlayItems = new ArrayList<OverlayItem>();
		for (int i = 0; i < markers_id.length; i++) {
			OverlayItem temp_overlayItem = new OverlayItem(geoPoints.get(i),
					places.get(i).getShopName(), places.get(i).getShopName());
			temp_overlayItem.setMarker(markers.get(i));
			overlayItems.add(temp_overlayItem);

		}
		Log.i("overlay items ", String.valueOf(overlayItems.size()));

		// ����IteminizedOverlay
		itemOverlay = new OverlayTest(markers.get(0), mMapView);

		// ��IteminizedOverlay��ӵ�MapView��
		itemOverlay.addItem(overlayItems);

		// clearOverlay(mMapView);
		// mMapView.getOverlays().clear();
		// mMapView.refresh();
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mMapView.getOverlays().add(itemOverlay);
				mMapView.refresh();

			}
		};
		timer.schedule(timerTask, 100);
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult busResult, int code) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// �������е�ַ
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					// Toast.makeText(NewMapActivity.this, "��Ǹ��δ�ҵ����",
					// Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 1;
				transitOverlay = new TransitOverlay(NewMapActivity.this,
						mMapView);
				// �˴���չʾһ��������Ϊʾ��
				transitOverlay.setData(res.getPlan(0));
				// �������ͼ��
				// mMapView.getOverlays().clear();
				// ���·��ͼ��
				mMapView.getOverlays().add(transitOverlay);
				// ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(
						transitOverlay.getLatSpanE6(),
						transitOverlay.getLonSpanE6());
				// �ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);
				// ����·�߽ڵ��������ڵ����ʱʹ��
				// nodeIndex = 0;
				int num_line = res.getPlan(0).getNumLines() - 1;
				Log.i("fydx",
						"��Ҫ��"
								+ String.valueOf(res.getPlan(0).getNumLines() - 1)
								+ "�γ�");

				if (num_line == 0) {
					tmp = "�� " + res.getPlan(0).getLine(0).getGetOnStop().name
							+ " " + res.getPlan(0).getLine(0).getTip();
				} else {
					tmp = "��" + res.getPlan(0).getLine(0).getGetOnStop().name
							+ res.getPlan(0).getLine(0).getTip() + "\n��"
							+ res.getPlan(0).getLine(1).getTip();
				}
//				Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT)
//						.show();
				showRoutePopUp();
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				// TODO Auto-generated method stub
				if (mPopupWindow!=null&&mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// �������е�ַ
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					// Toast.makeText(NewMapActivity.this, "��Ǹ��δ�ҵ����",
					// Toast.LENGTH_SHORT).show();
					return;
				}
				// if (transitOverlay!= null) {
				// mMapView.getOverlays().remove(transitOverlay);
				// mMapView.refresh();
				// }
				if (routeOverlay != null) {
					mMapView.getOverlays().remove(routeOverlay);
					mMapView.refresh();
				}
				searchType = 1;
				routeOverlay = new RouteOverlay(NewMapActivity.this, mMapView);

				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				routeOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.empty));
				routeOverlay.setStMarker(getResources().getDrawable(
						R.drawable.empty));
				// �������ͼ��
				// mMapView.getOverlays().clear();
				// ���·��ͼ��
				mMapView.getOverlays().add(routeOverlay);
				// ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				// �ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);

			}

		});

	}

	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// ��MapView����ItemizedOverlay
		public OverlayTest(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			// �ڴ˴���item����¼�
			System.out.println("item onTap: " + index);
			Toast.makeText(getApplicationContext(),
					places.get(index).getShopName(), Toast.LENGTH_LONG).show();
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// �ڴ˴���MapView�ĵ���¼��������� trueʱ
			super.onTap(pt, mapView);
			return false;
		}
	}

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mCurItem = item;

			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}

	}

	/**
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {

		mOverlay.removeAll();

		if (pop != null) {
			pop.hidePop();
		}
		// mMapView.removeView(button);
		mMapView.refresh();
	}

	/**
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		// ����add overlay
		mOverlay.addItem(mItems);
		mMapView.refresh();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		// �˳�ʱ���ٶ�λ
		if (mLocClient != null)
			mLocClient.stop();

		DemoApplication app = (DemoApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			// ���¶�λ����
			myLocationOverlay.setData(locData);
			// ����ͼ������ִ��ˢ�º���Ч
			mMapView.refresh();
			// ���ֶ�����������״ζ�λʱ���ƶ�����λ��
			if (isRequest || isFirstLoc) {
				// �ƶ���ͼ����λ��
				locGeoPoint = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMapController.animateTo(locGeoPoint);
				initBusLine();

				isRequest = false;
			}
			// �״ζ�λ���
			isFirstLoc = false;

		}

		public void onReceivePoi(BDLocation poiLocation) {
			// if (poiLocation == null){
			// return ;
			// }
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("Poi time : ");
			// sb.append(poiLocation.getTime());
			// sb.append("\nerror code : ");
			// sb.append(poiLocation.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(poiLocation.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(poiLocation.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(poiLocation.getRadius());
			// if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
			// sb.append("\naddr : ");
			// sb.append(poiLocation.getAddrStr());
			// }
			// if(poiLocation.hasPoi()){
			// sb.append("\nPoi:");
			// sb.append(poiLocation.getPoi());
			// }else{
			// sb.append("noPoi information");
			// }
			// Log.i("location msg", sb.toString());
		}
	}

	/**
	 * ��λ����
	 */
	public void initLoc() {
		// ��λ��ʼ��
		mLocClient = new LocationClient(getApplicationContext());

		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// ��λͼ���ʼ��
		myLocationOverlay = new MyLocationOverlay(mMapView);
		// ���ö�λ����
		myLocationOverlay.setData(locData);

		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);
		// Toast.makeText(getApplicationContext(),
		// String.valueOf(locData.longitude) + "  " +
		// String.valueOf(locData.latitude), Toast.LENGTH_SHORT).show();
		myLocationOverlay.enableCompass();
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();
	}

	/**
	 * �ֶ�����һ�ζ�λ����
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(NewMapActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * ��ʼ������·��
	 */
	public void initBusLine() {
		MKPlanNode start = new MKPlanNode();
		start.pt = locGeoPoint;
		MKPlanNode end = new MKPlanNode();
//		Toast.makeText(
//				getApplicationContext(),
//				String.valueOf(locGeoPoint.getLatitudeE6() / 1E6) + " "
//						+ String.valueOf(locGeoPoint.getLongitudeE6() / 1E6),
//				Toast.LENGTH_SHORT).show();
		end.pt = new GeoPoint(geoPoints.get(0).getLatitudeE6()
				+ (int) (0.0002 * 1E6), geoPoints.get(0).getLongitudeE6()
				+ (int) (0.0002 * 1E6));
		
		mSearch.transitSearch(city, start, end);

	}

	/**
	 * set fake Location
	 * 
	 * @param longitude
	 * @param latitude
	 */
	private void setLocation(double longitude, double latitude) {
		String mMockProviderName = "spoof";
		// int mPostDelayed = 10000;
		LocationManager locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.getProvider(mMockProviderName) == null) {
			locationManager.addTestProvider(mMockProviderName, false, true,
					false, false, false, false, false, 0, 5);
			locationManager.setTestProviderEnabled(mMockProviderName, true);
		}
		Location loc = new Location(mMockProviderName);
		loc.setTime(System.currentTimeMillis());
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		locationManager.setTestProviderLocation(mMockProviderName, loc);

		Log.i("gps", String.format("once: x=%s y=%s", longitude, latitude));
	}

	public void showRoutePopUp() {
		Context mContext = NewMapActivity.this;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View route = mLayoutInflater.inflate(R.layout.popup_map, null);
		TextView textView_route = (TextView) route
				.findViewById(R.id.textView_route);
		textView_route.setText(tmp);
		 mPopupWindow = new PopupWindow(route,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		mPopupWindow.showAtLocation(findViewById(R.id.bmapsView), Gravity.TOP
				| Gravity.CENTER_HORIZONTAL, 0, 40);
	}
}
