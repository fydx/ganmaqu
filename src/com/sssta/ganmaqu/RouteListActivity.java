package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RouteListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_list);
		StringBuffer detailString = new StringBuffer();
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		ListView listView_route = (ListView)findViewById(R.id.listView_route);
		FinalDb db = FinalDb.create(this);
		List<place> places = db.findAll(place.class);
		Log.i("DB item num", String.valueOf(places.size()));
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("time", "2013-8-1");
		for (int i=0;i<places.size()-1;i++) {
			detailString.append(places.get(i).getShopName()).append("-->" );	
		}
		detailString.append(places.get(places.size()-1).getShopName());
		map.put("detail", detailString.toString());
		mylist.add(map);
		SimpleAdapter mSchedule = new SimpleAdapter(this, //activity
				mylist,
				R.layout.list_item_route_list,// ListItem
				//  Key
				new String[] { "time", "detail" },
				// ListItem TextView ID
				new int[] { R.id.text_time, R.id.text_detail });
		               // 
		        listView_route.setAdapter(mSchedule);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_list, menu);
		return true;
	}

}
