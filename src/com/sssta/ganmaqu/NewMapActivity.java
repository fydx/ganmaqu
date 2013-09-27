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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class NewMapActivity extends Activity {
	MapView mMapView = null; // ��ͼView
	// UI���
	Button mBtnReverseGeoCode = null; // �����귴����Ϊ��ַ
	Button mBtnGeoCode = null; // ����ַ����Ϊ����
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	 ArrayList<MKPoiInfo> mkpoi = null;
	 private  OverlayTest itemOverlay = null ;
	private List<place> places;
	/**
	 *  ��MapController��ɵ�ͼ���� 
	 */
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popupText = null;
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
		  /**
         * ��ȡ��ͼ������
         */
        mMapController = mMapView.getController();
        /**
         *  ���õ�ͼ�Ƿ���Ӧ����¼�  .
         */
        mMapController.enableClick(true);
        /**
         * ���õ�ͼ���ż���
         */
        mMapController.setZoom(14);
        /**
         * ��ʾ�������ſؼ�
         */
        mMapView.setBuiltInZoomControls(true);
		
        /**
         * �趨��ͼ���ĵ� ��������¥��
         */
        GeoPoint p = new GeoPoint((int)(34.265733 * 1E6), (int)(108.953906* 1E6));
        mMapController.setCenter(p);
        
        
        //��װ�ص����굽list��
        List<GeoPoint> geoPoints = null;
        geoPoints =		new ArrayList<GeoPoint>();
        for (int i = 0; i < places.size(); i++) {
        	 geoPoints.add(new GeoPoint((int)(places.get(i).getPos_y()* 1E6), (int)(places.get(i).getPos_x()* 1E6)));
        	 Log.i("places info in GeoPoint", places.get(i).getShopName());
		}
        List<Drawable> markers = null;
        	markers =	new ArrayList<Drawable>();
        for (int i = 0; i < markers_id.length; i++) {
			markers.add(getResources().getDrawable(markers_id[i]));
		}
        List<OverlayItem> overlayItems =  null ;
        overlayItems = new ArrayList<OverlayItem>();
       for (int i = 0; i < markers_id.length; i++) {
    	 OverlayItem  temp_overlayItem = new OverlayItem(geoPoints.get(i), places.get(i).getShopName(), places.get(i).getShopName());
    	   temp_overlayItem.setMarker(markers.get(i));
		 overlayItems.add(temp_overlayItem);
		 
     	}
       Log.i("overlay items ", String.valueOf(overlayItems.size()));
     
     //����IteminizedOverlay  
      
       itemOverlay = new OverlayTest(markers.get(0), mMapView);  
   
       //��IteminizedOverlay��ӵ�MapView��  
       itemOverlay.addItem(overlayItems);
       
       //clearOverlay(mMapView);
       mMapView.getOverlays().clear();
       mMapView.refresh();
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
	 public class MyOverlay extends ItemizedOverlay{

			public MyOverlay(Drawable defaultMarker, MapView mapView) {
				super(defaultMarker, mapView);
			}
			

			@Override
			public boolean onTap(int index){
				OverlayItem item = getItem(index);
				mCurItem = item ;
			
				return true;
			}
			
			@Override
			public boolean onTap(GeoPoint pt , MapView mMapView){
				if (pop != null){
	                pop.hidePop();
	                mMapView.removeView(button);
				}
				return false;
			}
	    	
	    }
	 /**
	     * �������Overlay
	     * @param view
	     */
	    public void clearOverlay(View view){
	    	
	    	mOverlay.removeAll();
	    	
	    	if (pop != null){
	            pop.hidePop();
	    	}
	    //	mMapView.removeView(button);
	    	mMapView.refresh();
	    }
	 /**
	     * �������Overlay
	     * @param view
	     */
	    public void resetOverlay(View view){
	    	clearOverlay(null);
	    	//����add overlay
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
//        DemoApplication app = (DemoApplication) this.getApplication();
//        if(app.mBMapManager!=null){  
//            app.mBMapManager.destroy();  
//            app.mBMapManager=null;  
//        }  
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
