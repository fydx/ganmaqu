package com.sssta.ganmaqu;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class NewMapActivity extends Activity {
	// 地图相关
	MapView mMapView = null; // 地图View
	// UI相关
	Button mBtnReverseGeoCode = null; // 将坐标反编码为地址
	Button mBtnGeoCode = null; // 将地址编码为坐标
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication) this.getApplication();
		//BMapManager 必须在setContentview前面初始化，否则报错
		setContentView(R.layout.activity_new_map);
	
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);

        /**
         * 设定地图中心点
         */
        GeoPoint p = new GeoPoint((int)(34.265733 * 1E6), (int)(116.400191* 1E6));
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
		mSearch.geocode("西安市","钟楼");
		
		
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
