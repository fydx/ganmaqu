package com.sssta.ganmaqu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RouteListActivity extends Activity {
	FinalDb db, db_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_list);
		StringBuffer detailString = new StringBuffer();
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		ListView listView_route = (ListView) findViewById(R.id.listView_route);
		 db = FinalDb.create(this);
		 db_user = FinalDb.create(this);
		List<place> places = db.findAll(place.class);
		Log.i("DB item num", String.valueOf(places.size()));
		User user = db_user.findById(1, User.class);
		int route_num = user.getRoute_num() ;
		/**
		 * 由于现在数据库User是 (1,1)，so，k从1开始，因为已经加了1，所以k从2开始，终止值是getRoute_num
		 */

		for (int k = 2; k <= route_num; k++) {
			detailString = null;
			detailString = new StringBuffer();
			places = db.findAllByWhere(place.class,
					"route_id = " + String.valueOf(k));
			Log.i("item num in route", String.valueOf(places.size()));
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("time", "2013-8-1");

			for (int i = 0; i < places.size() - 1; i++) {
				detailString.append(places.get(i).getShopName()).append("-->");
			}
			detailString.append(places.get(places.size() - 1).getShopName());
			map.put("detail", detailString.toString());
			mylist.add(map);
		}
		SimpleAdapter mSchedule = new SimpleAdapter(this, // activity
				mylist, R.layout.list_item_route_list,// ListItem
				// Key
				new String[] { "time", "detail" },
				// ListItem TextView ID
				new int[] { R.id.text_time, R.id.text_detail });
		//
		listView_route.setDividerHeight(0); // no divider
		listView_route.setAdapter(mSchedule);
		listView_route.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), WarpDSLV.class);
				List<place> places_temp = db.findAllByWhere(place.class,
						"route_id = " + String.valueOf(arg2+2));
				
				intent.putExtra("places",(Serializable)places_temp);
				startActivity(intent);
			

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_list, menu);
		return true;
	}

}
