package com.sssta.ganmaqu;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;

public class WarpDSLV extends ListActivity {

	// private ArrayAdapter<String> adapter;
	private FinalDb db, db_user;
	private int cost = 0;
	private ArrayAdapter<place> adapter;
	private String jsonString;
	private String[] array;
	private ArrayList<String> list;
	private ArrayList<String> list_time;
	private List<place> places;
	private Button button_toMap, button_saveToDB;
	private TextView textView_cost;
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			place item = adapter.getItem(from);

			adapter.notifyDataSetChanged();
			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// adapter.remove(adapter.getItem(which));
			adapter.notifyDataSetChanged();
		}
	};

//	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
//		@Override
//		public float getSpeed(float w, long t) {
//			if (w > 0.8f) {
//				// Traverse all views in a millisecond
//				return ((float) adapter.getCount()) / 0.001f;
//			} else {
//				return 10.0f * w;
//			}
//		}
//	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.warp_main);
		db = FinalDb.create(this);
		db_user = FinalDb.create(this);
		DragSortListView lv = (DragSortListView) getListView();

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
//		lv.setDragScrollProfile(ssProfile);
		button_toMap = (Button) findViewById(R.id.button_toMap);
		button_toMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MapActivity.class);
				intent.putExtra("places", (Serializable) places);
				startActivity(intent);

			}
		});
		button_saveToDB = (Button) findViewById(R.id.button_SaveToDB);

		/**
		 * read from assets json files
		 */
		// Log.i("file_content",jsonString);

		if ((getIntent().getSerializableExtra("places")) == null) {
			try {
				jsonString = getFromAssets("test.json");
				decodeJson objdecodeJson = new decodeJson(jsonString);
				Log.i("top", objdecodeJson.getTop());
				places = objdecodeJson.JsonToPlaceList(objdecodeJson
						.getJsonArray());
				for (int i = 0; i < places.size(); i++) {
					Log.i("places info", places.get(i).getShopName());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			places = (List<place>) getIntent().getSerializableExtra("places");
		}

		button_saveToDB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveToDB(places);
			}
		});

		// calculate cost
		textView_cost = (TextView) findViewById(R.id.cost);

		list = new ArrayList<String>();
		list_time = new ArrayList<String>();
		for (int i = 0; i < places.size(); i++) {

			list.add(
					 places.get(i).getDetailType() + "  人均"
					+ String.valueOf(places.get(i).getCost()) + "元");
			list_time.add(places.get(i).getTime());
			cost += places.get(i).getCost();

		}
		Log.i("cost", "人均消费" + String.valueOf(cost));
		textView_cost.setText("人均消费总计 : " + String.valueOf(cost) + "元");

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						"This is " + String.valueOf(adapter.getItem(arg2))
								+ " item", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), WebActivity.class);
				intent.putExtra("shopId",
						String.valueOf(places.get(arg2).getId()));
				startActivity(intent);
			}
		});
		// adapter = new ArrayAdapter<String>(this,
		// R.layout.list_item_handle_left, R.id.text, list);
		ArrayList<place> places_arraylist = new ArrayList<place>();
		places_arraylist.addAll(places);
		adapter = new placeAdapter(places_arraylist);
		setListAdapter(adapter);

	}

	// 从assets中读取数据
	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void saveToDB(List<place> places) {
		User user = db_user.findById(1, User.class);
		int route_num = user.getRoute_num();
		Log.i("user get route_num", String.valueOf(route_num));
		for (int i = 0; i < places.size(); i++) {
			places.get(i).setRoute_id(route_num+1);
			db.save(places.get(i));
			Log.i("DBsave", String.valueOf(i));
		}
        int num = user.getRoute_num()+1;
		user.setRoute_num(num);
		Log.i("user set route_num", String.valueOf(user.getRoute_num()));
		db_user.update(user);
	}

	/**
	 * create placeAdapter extend ArrayAdapter
	 * 
	 */
	private class ViewHolder {
		public TextView detailView;
		public ImageView dragImageView;
	}

	private class placeAdapter extends ArrayAdapter<place> {

		public placeAdapter(List<place> places) {
			super(WarpDSLV.this, R.layout.list_item_handle_left,
					R.id.text, places);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);

			if (v != convertView && v != null) {
				ViewHolder holder = new ViewHolder();

				TextView tv = (TextView) v.findViewById(R.id.text_detail);
				ImageView iv = (ImageView) v.findViewById(R.id.drag_handle);
				holder.detailView = tv;
				holder.dragImageView=iv;
				v.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) v.getTag();
			//String detail = places.get(position).getAddress();
			
			holder.detailView.setText(list.get(position).toString());
			if (list_time.get(position).toString().equals("上午")) {
				holder.dragImageView.setImageResource(R.drawable.morning);
			}
			if (list_time.get(position).toString().equals("中午")) {
				holder.dragImageView.setImageResource(R.drawable.launch);
			}
			if (list_time.get(position).toString().equals("下午")) {
				holder.dragImageView.setImageResource(R.drawable.afternoon);
			}
			if (list_time.get(position).toString().equals("晚餐")) {
				holder.dragImageView.setImageResource(R.drawable.dinner);
			}
			if (list_time.get(position).toString().equals("晚上")) {
				holder.dragImageView.setImageResource(R.drawable.evening);
			}
			return v;
		}
	}
}
