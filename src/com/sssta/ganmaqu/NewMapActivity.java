package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sssta.ganmaqu.SettingsFragment.loginTask;

public class NewMapActivity extends Activity {
	// 地图相关
	MapView mMapView = null; // 地图View
	// UI相关
	Button mBtnReverseGeoCode = null; // 将坐标反编码为地址
	Button mBtnGeoCode = null; // 将地址编码为坐标
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	 ArrayList<MKPoiInfo> mkpoi = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication) this.getApplication();
		//BMapManager 必须在setContentview前面初始化，否则报错
		   if (app.mBMapManager == null) {
	             app.mBMapManager = new BMapManager(this);
	             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
	         }
		setContentView(R.layout.activity_new_map);
		
          mkpoi = new ArrayList<MKPoiInfo>();
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(14);

        /**
         * 设定地图中心点 （西安钟楼）
         */
        GeoPoint p = new GeoPoint((int)(34.265733 * 1E6), (int)(108.953906* 1E6));
        mMapView.getController().setCenter(p);
        
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		
		mSearch.init(app.mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(NewMapActivity.this, str, Toast.LENGTH_LONG)
							.show();
					return;
				}
				// 地图移动到该点
				mMapView.getController().animateTo(res.geoPt);
				if (res.type == MKAddrInfo.MK_GEOCODE) {
					// 地理编码：通过地址检索坐标点
					String strInfo = String.format("纬度：%f 经度：%f",
							res.geoPt.getLatitudeE6() / 1e6,
							res.geoPt.getLongitudeE6() / 1e6);
					Toast.makeText(NewMapActivity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					// 反地理编码：通过坐标点检索详细地址及周边poi
					String strInfo = res.strAddr;
					Toast.makeText(NewMapActivity.this, strInfo,
							Toast.LENGTH_LONG).show();

				}
				// 生成ItemizedOverlay图层用来标注结果点
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(
						null, mMapView);
				// 生成Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				// 得到需要标在地图上的资源
				Drawable marker = getResources().getDrawable(R.drawable.icon_1);
				// 为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());
				// 给item设置marker
				item.setMarker(marker);
				// 在图层上添加item
				itemOverlay.addItem(item);

				// 清除地图其他图层
				mMapView.getOverlays().clear();
				// 添加一个标注ItemizedOverlay图层
				mMapView.getOverlays().add(itemOverlay);
				// 执行刷新使生效
				mMapView.refresh();
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				 // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(NewMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(NewMapActivity.this, mMapView, mSearch);
                    
                  
                    mkpoi.add(res.getPoi(0));
                    
                    poiOverlay.setData(mkpoi);
                    
               //     poiOverlay.setData(res.getAllPoi());
                    mMapView.getOverlays().clear();
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
                } else if (res.getCityListNum() > 0) {
                	//当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (int i = 0; i < res.getCityListNum(); i++) {
                        strInfo += res.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(NewMapActivity.this, strInfo, Toast.LENGTH_LONG).show();
                }
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}

		});
		
		 mSearch.poiSearchInCity("西安", 
               "西安电子科技大学");
		
		 final Timer timer = new Timer();
		 
		 final TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSearch.poiSearchInCity("西安", 
			               "西北工业大学");
			}
		};
		new Thread()
		{
			@Override 
			public void run()
			{
				Log.i("task","start");
				timer.schedule(timerTask, 1000*10);
				Log.i("task", "end");
			}
		}.start();
	
		
		
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
}
