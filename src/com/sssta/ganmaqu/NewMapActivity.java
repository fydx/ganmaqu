package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.drawable;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
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
	private List<place> places;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DemoApplication app = (DemoApplication) this.getApplication();
		final int markers_id[] = { R.drawable.icon_1, R.drawable.icon_2,R.drawable.icon_3,R.drawable.icon_4,R.drawable.icon_5};
		//BMapManager ������setContentviewǰ���ʼ�������򱨴�
		   if (app.mBMapManager == null) {
	             app.mBMapManager = new BMapManager(this);
	             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
	         }
		setContentView(R.layout.activity_new_map);
		 places = (List<place>) getIntent().getSerializableExtra(
				"places");
		Log.i("places nums", String.valueOf(places.size()));
		
		
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
        //��װ�ص����굽list��
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        for (int i = 0; i < places.size(); i++) {
        	 geoPoints.add(new GeoPoint((int)(places.get(i).getPos_y()* 1E6), (int)(places.get(i).getPos_x()* 1E6)));
		}
        List<Drawable> markers = new ArrayList<Drawable>();
        for (int i = 0; i < markers_id.length; i++) {
			markers.add(getResources().getDrawable(markers_id[i]));
		}
        List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
       for (int i = 0; i < markers_id.length; i++) {
    	 OverlayItem  temp_overlayItem = new OverlayItem(geoPoints.get(i), places.get(i).getShopName(), places.get(i).getShopName());
    	   temp_overlayItem.setMarker(markers.get(i));
		 overlayItems.add(temp_overlayItem);
		 
     	}
     
     //����IteminizedOverlay  
       OverlayTest itemOverlay = new OverlayTest(markers.get(0), mMapView);  
       //��IteminizedOverlay��ӵ�MapView��  
         
       mMapView.getOverlays().clear();  
       mMapView.getOverlays().add(itemOverlay);  
       itemOverlay.addItem(overlayItems);
       mMapView.refresh();
        
		
		
	}
	class OverlayTest extends ItemizedOverlay<OverlayItem> {  
	    //��MapView����ItemizedOverlay  
	    public OverlayTest(Drawable mark,MapView mapView){  
	            super(mark,mapView);  
	    }  
	    protected boolean onTap(int index) {  
	        //�ڴ˴���item����¼�  
	        System.out.println("item onTap: "+index);  
	        Toast.makeText(getApplicationContext(), places.get(index).getShopName(), Toast.LENGTH_LONG).show();
	        return true;  
	    }  
	        public boolean onTap(GeoPoint pt, MapView mapView){  
	                //�ڴ˴���MapView�ĵ���¼��������� trueʱ  
	                super.onTap(pt,mapView);  
	                return false;  
	        }  
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
