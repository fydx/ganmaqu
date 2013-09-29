package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
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
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class NewMapActivity extends Activity {
	MapView mMapView = null; // 地图View
	// UI相关
	Button mBtnReverseGeoCode = null; // 将坐标反编码为地址
	Button mBtnGeoCode = null; // 将地址编码为坐标
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	ArrayList<MKPoiInfo> mkpoi = null;
	private OverlayTest itemOverlay = null;
	private List<place> places;
	private GeoPoint locGeoPoint;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	// 定位图层
	MyLocationOverlay myLocationOverlay = null;
	// UI相关
	OnCheckedChangeListener radioButtonListener = null;
	
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	private Button requestLocButton = null;
	//公交路线相关
	TransitOverlay transitOverlay = null;//保存公交路线图层数据的变量，供浏览节点时使用
	
	/**
	 * 用MapController完成地图控制
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DemoApplication app = (DemoApplication) this.getApplication();
		final int markers_id[] = { R.drawable.icon_1, R.drawable.icon_2,
				R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5 };
		// BMapManager 必须在setContentview前面初始化，否则报错
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(DemoApplication.strKey,
					new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.activity_new_map);
		places = (List<place>) getIntent().getSerializableExtra("places");
		Log.i("places nums", String.valueOf(places.size()));
		requestLocButton = (Button)findViewById(R.id.button_loc);
		requestLocButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				requestLocClick();
			}
		});
		mkpoi = new ArrayList<MKPoiInfo>();
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapsView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(14);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(true);

		/**
		 * 设定地图中心点 （西安钟楼）
		 */
		GeoPoint p = new GeoPoint((int) (34.265733 * 1E6),
				(int) (108.953906 * 1E6));
		mMapController.setCenter(p);
		
		// 定位初始化
		initLoc();
		//定位部分结束
		

		
		
		// 封装地点坐标到list中
		List<GeoPoint> geoPoints = null;
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

		// 创建IteminizedOverlay
		itemOverlay = new OverlayTest(markers.get(0), mMapView);

		// 将IteminizedOverlay添加到MapView中
		itemOverlay.addItem(overlayItems);

		// clearOverlay(mMapView);
	//	mMapView.getOverlays().clear();
	//	mMapView.refresh();
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
		
		

	}

	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// 用MapView构造ItemizedOverlay
		public OverlayTest(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			// 在此处理item点击事件
			System.out.println("item onTap: " + index);
			Toast.makeText(getApplicationContext(),
					places.get(index).getShopName(), Toast.LENGTH_LONG).show();
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
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
	 * 清除所有Overlay
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
	 * 重新添加Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		// 重新add overlay
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
		// DemoApplication app = (DemoApplication) this.getApplication();
		// if(app.mBMapManager!=null){
		// app.mBMapManager.destroy();
		// app.mBMapManager=null;
		// }
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
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				locGeoPoint =new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMapController.animateTo(locGeoPoint);
				
				isRequest = false;
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
                return ;
          }
         StringBuffer sb = new StringBuffer(256);
          sb.append("Poi time : ");
          sb.append(poiLocation.getTime());
          sb.append("\nerror code : ");
          sb.append(poiLocation.getLocType());
          sb.append("\nlatitude : ");
          sb.append(poiLocation.getLatitude());
          sb.append("\nlontitude : ");
          sb.append(poiLocation.getLongitude());
          sb.append("\nradius : ");
          sb.append(poiLocation.getRadius());
          if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
              sb.append("\naddr : ");
              sb.append(poiLocation.getAddrStr());
         } 
          if(poiLocation.hasPoi()){
               sb.append("\nPoi:");
               sb.append(poiLocation.getPoi());
         }else{             
               sb.append("noPoi information");
          }
         Log.i("location msg", sb.toString());
        }
	}

	
  //定为方法
	public void initLoc() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(NewMapActivity.this, "正在定位……", Toast.LENGTH_SHORT)
				.show();
	}

}
