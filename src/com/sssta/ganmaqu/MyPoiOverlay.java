package com.sssta.ganmaqu;

import android.app.Activity;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyPoiOverlay extends PoiOverlay {
    
    MKSearch mSearch;
    MapView mmapView;
    public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
        super(activity, mapView);
        mSearch = search;
        mmapView = mapView;
    }

    @Override
    public boolean onTap(int i) {
        super.onTap(i);
        MKPoiInfo info = getPoi(i);
        if (info.hasCaterDetails) {
            mSearch.poiDetailSearch(info.uid);
        }
        return true;
    }
    
	
}
    
    
