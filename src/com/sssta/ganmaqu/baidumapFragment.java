package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sssta.ganmaqu.NewMapActivity.OverlayTest;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class baidumapFragment extends android.support.v4.app.Fragment {
	MapView mMapView = null; // ��ͼView
	// UI���
	Button mBtnReverseGeoCode = null; // �����귴����Ϊ��ַ
	Button mBtnGeoCode = null; // ����ַ����Ϊ����
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	 ArrayList<MKPoiInfo> mkpoi = null;
	private List<place> places;
	private Activity activity_1;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity_1 = activity;
		// ��ȡ����Activity�����á�
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_new_map, container, false);
		DemoApplication app = (DemoApplication) getActivity().getApplication();
		final int markers_id[] = { R.drawable.icon_1, R.drawable.icon_2,R.drawable.icon_3,R.drawable.icon_4,R.drawable.icon_5};
		//BMapManager ������setContentviewǰ���ʼ�������򱨴�
		   if (app.mBMapManager == null) {
	             app.mBMapManager = new BMapManager(getActivity());
	             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
	         }
	
		 places = (List<place>) getActivity().getIntent().getSerializableExtra(
				"places");
		Log.i("places nums", String.valueOf(places.size()));
		
		
          mkpoi = new ArrayList<MKPoiInfo>();
		// ��ͼ��ʼ��
		mMapView = (MapView) view
				.findViewById(R.id.bmapsView);
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
       return view;
		
		
	}
	class OverlayTest extends ItemizedOverlay<OverlayItem> {  
	    //��MapView����ItemizedOverlay  
	    public OverlayTest(Drawable mark,MapView mapView){  
	            super(mark,mapView);  
	    }  
	    protected boolean onTap(int index) {  
	        //�ڴ˴���item����¼�  
	        System.out.println("item onTap: "+index);  
	        Toast.makeText(getActivity().getApplicationContext(), places.get(index).getShopName(), Toast.LENGTH_LONG).show();
	        return true;  
	    }  
	        public boolean onTap(GeoPoint pt, MapView mapView){  
	                //�ڴ˴���MapView�ĵ���¼��������� trueʱ  
	                super.onTap(pt,mapView);  
	                return false;  
	        }  
		

}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
}

