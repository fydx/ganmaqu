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
	// ��ͼ���
	MapView mMapView = null; // ��ͼView
	// UI���
	Button mBtnReverseGeoCode = null; // �����귴����Ϊ��ַ
	Button mBtnGeoCode = null; // ����ַ����Ϊ����
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	 ArrayList<MKPoiInfo> mkpoi = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication) this.getApplication();
		//BMapManager ������setContentviewǰ���ʼ�������򱨴�
		   if (app.mBMapManager == null) {
	             app.mBMapManager = new BMapManager(this);
	             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
	         }
		setContentView(R.layout.activity_new_map);
		
          mkpoi = new ArrayList<MKPoiInfo>();
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(14);

        /**
         * �趨��ͼ���ĵ� ��������¥��
         */
        GeoPoint p = new GeoPoint((int)(34.265733 * 1E6), (int)(108.953906* 1E6));
        mMapView.getController().setCenter(p);
        
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		
		mSearch.init(app.mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("����ţ�%d", error);
					Toast.makeText(NewMapActivity.this, str, Toast.LENGTH_LONG)
							.show();
					return;
				}
				// ��ͼ�ƶ����õ�
				mMapView.getController().animateTo(res.geoPt);
				if (res.type == MKAddrInfo.MK_GEOCODE) {
					// ������룺ͨ����ַ���������
					String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
							res.geoPt.getLatitudeE6() / 1e6,
							res.geoPt.getLongitudeE6() / 1e6);
					Toast.makeText(NewMapActivity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					// ��������룺ͨ������������ϸ��ַ���ܱ�poi
					String strInfo = res.strAddr;
					Toast.makeText(NewMapActivity.this, strInfo,
							Toast.LENGTH_LONG).show();

				}
				// ����ItemizedOverlayͼ��������ע�����
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(
						null, mMapView);
				// ����Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				// �õ���Ҫ���ڵ�ͼ�ϵ���Դ
				Drawable marker = getResources().getDrawable(R.drawable.icon_1);
				// Ϊmaker����λ�úͱ߽�
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());
				// ��item����marker
				item.setMarker(marker);
				// ��ͼ�������item
				itemOverlay.addItem(item);

				// �����ͼ����ͼ��
				mMapView.getOverlays().clear();
				// ���һ����עItemizedOverlayͼ��
				mMapView.getOverlays().add(itemOverlay);
				// ִ��ˢ��ʹ��Ч
				mMapView.refresh();
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				 // ����ſɲο�MKEvent�еĶ���
                if (error != 0 || res == null) {
                    Toast.makeText(NewMapActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_LONG).show();
                    return;
                }
                // ����ͼ�ƶ�����һ��POI���ĵ�
                if (res.getCurrentNumPois() > 0) {
                    // ��poi�����ʾ����ͼ��
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(NewMapActivity.this, mMapView, mSearch);
                    
                  
                    mkpoi.add(res.getPoi(0));
                    
                    poiOverlay.setData(mkpoi);
                    
               //     poiOverlay.setData(res.getAllPoi());
                    mMapView.getOverlays().clear();
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //��ePoiTypeΪ2��������·����4��������·��ʱ�� poi����Ϊ��
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
                } else if (res.getCityListNum() > 0) {
                	//������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
                    String strInfo = "��";
                    for (int i = 0; i < res.getCityListNum(); i++) {
                        strInfo += res.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    strInfo += "�ҵ����";
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
		
		 mSearch.poiSearchInCity("����", 
               "�������ӿƼ���ѧ");
		
		 final Timer timer = new Timer();
		 
		 final TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSearch.poiSearchInCity("����", 
			               "������ҵ��ѧ");
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
