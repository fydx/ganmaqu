package com.sssta.ganmaqu;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;

public class WarpDSLV extends FragmentActivity {

	// private ArrayAdapter<String> adapter;
	private String ipString;
	private final String[] types = { "美食", "购物", "电影院", "风景", "咖啡/甜点", "KTV" };
	private DemoApplication demoApplication;
	private FinalDb db, db_user;
	private int cost = 0, tempCost = 0;
	private ArrayAdapter<place> adapter;
	private String jsonString;
	private String[] array;
	private ArrayList<String> list;
	private ArrayList<String> list_time;
	private double loclat, loclng;
	private List<place> places;
	private Connect connect;
	private TextView textView_cost;
	private ArrayList<place> places_arraylist;
	private String type;
	private List<String> distances;
	private SharedPreferences userInfo;
	private String userid;
	private String circleString;
	private CustomPopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	private View top_title;
	private TextView tvtitle;
	private List<String> groups;
	private String city;
	private DragSortListView lv;
	private ImageView mapImageView;
	private FinalBitmap fb;
	private String[] picurls;
	private int picCount;
	private AnimationSet animationSet;
	private int[] pics;
	private JSONObject json;
	private JSONArray item;
	private String[] picStrings = { "http://115.28.17.121/pics/xian_01.png",
			"http://115.28.17.121/pics/xian_02.png" };

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			place item = adapter.getItem(from);
			// new changeTask(type,places)
			// adapter.notifyDataSetChanged();
			// adapter.remove(item);
			// adapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// adapter.remove(adapter.getItem(which));
			// adapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "请稍后,马上为您推荐一个新的地点",
					Toast.LENGTH_SHORT).show();
			Log.i("Remove Which", String.valueOf(which));
			new changeTask().execute(type, city, places.get(which)
					.getShopName(), String
					.valueOf(places.get(which).getPos_x()), String
					.valueOf(places.get(which).getPos_y()), places.get(which)
					.getTime(), String.valueOf(places.get(which).getCost()),
					String.valueOf(places.get(which).getWeight()), String
							.valueOf(which));
			new sendDeleteTask().execute(
					String.valueOf(places.get(which).getId()), userid);
		}
	};

	// private DragSortListView.DragScrollProfile ssProfile = new
	// DragSortListView.DragScrollProfile() {
	// @Override
	// public float getSpeed(float w, long t) {
	// if (w > 0.8f) {
	// // Traverse all views in a millisecond
	// return ((float) adapter.getCount()) / 0.001f;
	// } else {
	// return 10.0f * w;
	// }
	// }
	// };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.warp_main);
		demoApplication = (DemoApplication) getApplication();
		userInfo = getSharedPreferences("userInfo", 0);
		userid = userInfo.getString("userid", "root");
		city = userInfo.getString("city", "西安市");
		json = new JSONObject();
		item = new JSONArray();
		for (int i = 0; i < types.length; i++) {
			if (demoApplication.selectType[i] == true) {
				item.put(types[i]);
			}

		}
		try {
			json.put("item", item);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fb = FinalBitmap.create(this);
		/*
		 * actionbar 设置
		 */
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.actionbar_banner));
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.result_actionbar));
		// actionBar.setSplitBackgroundDrawable(getResources().getDrawable(
		// R.drawable.actionbar_split_bg));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("推荐路线");
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		TextView title = (TextView) findViewById(titleId);
		title.setTextColor(Color.parseColor("#FFFFFF"));
		actionBar.setDisplayHomeAsUpEnabled(true);
		// 创建一个AlphaAnimation对象，参数从完全的透明度，到完全的不透明
		AlphaAnimation alphaAnimation_out = new AlphaAnimation(1, 0);
		// 设置动画执行的时间
		alphaAnimation_out.setDuration(1000);
		alphaAnimation_out.setStartOffset(9000);
		alphaAnimation_out.setRepeatCount(-1);
		// 将alphaAnimation对象添加到AnimationSet当中
		AlphaAnimation alphaAnimation_in = new AlphaAnimation(0, 1);
		alphaAnimation_in.setDuration(10000);
		alphaAnimation_in.setInterpolator(AnimationUtils.loadInterpolator(
				WarpDSLV.this,
				android.R.anim.accelerate_decelerate_interpolator));
		/*
		 * 设置位移动画
		 */
		animationSet = new AnimationSet(false);
		// 参数2：x轴的开始位置
		// 参数4：x轴的结束位置
		// 参数6：y轴的开始位置
		// 参数8：y轴的结束位置
		// 参数1,3,5,7 : fromX/YType
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.02f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -0.02f);
		translateAnimation.setDuration(9000);
		translateAnimation.setStartOffset(500);
		translateAnimation.setInterpolator(AnimationUtils.loadInterpolator(
				WarpDSLV.this, android.R.anim.cycle_interpolator));
		translateAnimation.setRepeatCount(-1);
		animationSet.addAnimation(translateAnimation);
		// animationSet.addAnimation(alphaAnimation_out);
		// animationSet.addAnimation(alphaAnimation_in);
		mapImageView = (ImageView) findViewById(R.id.map_preview);
		mapImageView.setAnimation(animationSet);
		ipString = getResources().getString(R.string.ip);
		connect = new Connect(ipString);
		db = FinalDb.create(this);
		db_user = FinalDb.create(this);
		lv = (DragSortListView) findViewById(R.id.dslv_result);
		type = getIntent().getStringExtra("type");
		circleString = getIntent().getStringExtra("circle");
		loclat = getIntent().getDoubleExtra("loclat", 34.265733);
		loclng = getIntent().getDoubleExtra("loclng", 108.953906);
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		// TextView textView_type = (TextView) findViewById(R.id.text_type);
		// textView_type.setText(type);
		// lv.setDragScrollProfile(ssProfile);
		// button_toMap = (Button) findViewById(R.id.button_toMap);
		// button_toMap.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// // intent.setClass(getApplicationContext(), MapActivity.class);
		// intent.setClass(getApplicationContext(), NewMapActivity.class); //
		// set
		// // new
		// // map
		// // activity
		// intent.putExtra("places", (Serializable) places);
		// startActivity(intent);
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);
		//
		// }
		// });
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
			Log.i("places", places.toString());
		}
		distances = calcDistances(places);

		List<String> picUrlsList = new ArrayList<String>();
		for (int i = 0; i < places.size(); i++) {
			if (!(places.get(i).getPicUrl() == null)) {
				picUrlsList.add(places.get(i).getPicUrl());
			}
		}

		picurls = picUrlsList.toArray(new String[picUrlsList.size()]);
		picCount = 0;
		if (!(places.get(0).getPicUrl() == null)) {
			final Handler myHandler = new Handler() {// 创建一个Handler对象
				public void handleMessage(Message msg) {// 重写接收消息的方法
					// mapImageView.clearAnimation();
					// fb.display(mapImageView, picStrings[msg.what]);
					// mapImageView.setAnimation(animationSet);
					super.handleMessage(msg);
				}
			};
			new Thread() {
				public void run() {
					int i = 0;
					while (true) {// 循环
						myHandler.sendEmptyMessage((i++) % picStrings.length);// 发送消息

						try {
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				};
			}.start();
		}

		// new getPic().execute(picurls);
		// button_saveToDB.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// saveToDB(places);
		// finish();
		// }
		// });
		// button_low.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// int tempCost = 0 ;
		// for (int i = 0; i < places.size(); i++) {
		// if(places.get(i).getMainType().equals("美食"))
		// {
		// tempCost+= places.get(i).getCost();
		// }
		// }
		//
		// new
		// lowerTask().execute(type,String.valueOf(loclng),String.valueOf(loclat),String.valueOf(tempCost));
		// }
		// });
		// button_up.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Dialog dialog = new Dialog(WarpDSLV.this,
		// R.style.activity_translucent);
		// dialog.setContentView(R.layout.dialog_layout);
		// dialog.show();
		// int tempCost = 0 ;
		// for (int i = 0; i < places.size(); i++) {
		// if(places.get(i).getMainType().equals("美食"))
		// {
		// tempCost+= places.get(i).getCost();
		// }
		// }
		//
		// new
		// upperTask().execute(type,String.valueOf(loclng),String.valueOf(loclat),String.valueOf(tempCost));
		//
		// }
		// });
		// calculate cost
		textView_cost = (TextView) findViewById(R.id.cost);

		list = new ArrayList<String>();
		list_time = new ArrayList<String>();
		for (int i = 0; i < places.size(); i++) {

			list.add(places.get(i).getDetailType());
			list_time.add(places.get(i).getTime());

			if (places.get(i).getMainType().equals("美食")) {

				cost += places.get(i).getCost();
			} else {

			}

		}

		Log.i("cost", "人均消费" + String.valueOf(cost));
		textView_cost.setText(String.valueOf(cost));

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// Toast.makeText(
				// getApplicationContext(),
				// "This is " + String.valueOf(adapter.getItem(arg2))
				// + " item", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), WebActivity.class);
				intent.putExtra("shopId",
						String.valueOf(places.get(arg2).getId()));
				startActivity(intent);
			}
		});
		// adapter = new ArrayAdapter<String>(this,
		// R.layout.list_item_handle_left, R.id.text, list);
		places_arraylist = new ArrayList<place>();
		places_arraylist.addAll(places);

		adapter = new placeAdapter(places_arraylist);
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);

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
		int route_num;
		User user = db_user.findById(1, User.class);
		try {

			route_num = user.getRoute_num();
		} catch (Exception e) {
			// TODO: handle exception
			route_num = 1;
		}

		Log.i("user get route_num", String.valueOf(route_num));
		for (int i = 0; i < places.size(); i++) {
			places.get(i).setRoute_id(route_num + 1);
			places.get(i).setRouteType(type);
			db.save(places.get(i));
			new sendArriveTask().execute(String.valueOf(places.get(i).getId()),
					userid);
			Log.i("DBsave", String.valueOf(i));
		}
		int num = user.getRoute_num() + 1;
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
		public TextView addressTextView;
		public TextView costTextView;
		public ImageView dragImageView;
		public Button distanceButton;
	}

	private class placeAdapter extends ArrayAdapter<place> {

		public placeAdapter(List<place> places) {
			super(WarpDSLV.this, R.layout.list_item_handle_left, R.id.text,
					places);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);

			if (v != convertView && v != null) {
				ViewHolder holder = new ViewHolder();

				TextView tv = (TextView) v.findViewById(R.id.text_detail);
				TextView address = (TextView) v
						.findViewById(R.id.textView_address);
				TextView cost = (TextView) v.findViewById(R.id.textView_cost);
				ImageView iv = (ImageView) v.findViewById(R.id.imageView_rank);
				Button bt = (Button) v.findViewById(R.id.drag_handle);
				holder.detailView = tv;
				holder.addressTextView = address;
				holder.costTextView = cost;
				holder.dragImageView = iv;
				holder.distanceButton = bt;
				// holder.dragImageView = iv;
				v.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) v.getTag();
			holder.detailView.setText(places.get(position).getDetailType());
			holder.addressTextView.setText(places.get(position).getAddress());
			holder.costTextView.setText(String.valueOf(places.get(position)
					.getCost()));
			holder.distanceButton.setText(distances.get(position));

			// String detail = places.get(position).getAddress();
			Log.i("position", String.valueOf(position));
			int[] icons = { R.drawable.icon_rank_1, R.drawable.icon_rank_2,
					R.drawable.icon_rank_3, R.drawable.icon_rank_4,
					R.drawable.icon_rank_5, R.drawable.icon_rank_6, };
			holder.dragImageView.setImageResource(icons[position]);

			return v;
		}
	}

	public class distanceTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return connect.GetDisRoute(loclng, loclat,
					Double.parseDouble(params[0]),
					Double.parseDouble(params[1]), Integer.parseInt(params[2]),
					Integer.parseInt(params[3]), params[4], params[5]);
		}

		@Override
		protected void onPostExecute(String result) {
			decodeJson objdecodeJson = null;
			try {
				objdecodeJson = new decodeJson(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Log.i("top", objdecodeJson.getTop());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			places = objdecodeJson
					.JsonToPlaceList(objdecodeJson.getJsonArray());
			for (int i = 0; i < places.size(); i++) {
				Log.i("places info", places.get(i).getShopName());
			}
			places_arraylist.clear();

			places_arraylist.addAll(places);
			adapter.notifyDataSetChanged();
			cost = 0;
			for (int i = 0; i < places.size(); i++) {

				if (places.get(i).getTime().equals("中午")
						|| places.get(i).getTime().equals("晚餐")) {
					// cost += places.get(i).getCost();
					cost += places.get(i).getCost();
				} else {

				}

			}

			Log.i("cost", "人均消费 new" + String.valueOf(cost));
			textView_cost.setText(String.valueOf(cost));
		}
	}

	public class randomTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				return connect.GetFullRoute(params[0],
						params[1],params[2],params[3],params[4]);

			} catch (JSONException e) {

				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			decodeJson objdecodeJson = null;
			try {
				objdecodeJson = new decodeJson(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Log.i("top", objdecodeJson.getTop());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			places = objdecodeJson
					.JsonToPlaceList(objdecodeJson.getJsonArray());
			for (int i = 0; i < places.size(); i++) {
				Log.i("places info", places.get(i).getShopName());
			}
			places_arraylist.clear();

			places_arraylist.addAll(places);
			adapter.notifyDataSetChanged();
			cost = 0;
			for (int i = 0; i < places.size(); i++) {
				if (places.get(i).getTime().equals("中午")
						|| places.get(i).getTime().equals("晚餐")) {
					// cost += places.get(i).getCost();
					cost += places.get(i).getCost();
				} else {
				}
			}
			Log.i("cost", "人均消费 new" + String.valueOf(cost));
			textView_cost.setText(String.valueOf(cost));
		}

	}

	public class changeTask extends AsyncTask<String, integer, String> {

		String rankString;

		@Override
		protected String doInBackground(String... param) {
			// new changeTask().execute(type, String.valueOf(places.get(which)
			// .getPos_x()), String.valueOf(places.get(which).getPos_y()),
			// places.get(which).getTime(), places.get(which)
			// .getShopName(), String.valueOf(which), String
			// .valueOf(places.get(which).getCost()), city);
			rankString = param[8];
			return connect.GetChangeSingle(param[0], param[1], param[2],
					Double.parseDouble(param[3]), Double.parseDouble(param[4]),
					param[5], Integer.parseInt(param[6]),
					Integer.parseInt(param[7]));
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Log.i("jsonString Change", result);
				decodeJson json = new decodeJson(result);
				JSONArray jsonArray = json.getJsonArray();
				List<place> tempList = json.JsonToPlaceList(jsonArray);
				// JSONObject jsonObject = new JSONObject(result);
				place changedPlace = tempList.get(0);
				places.set(Integer.parseInt(rankString), changedPlace);
				places_arraylist.clear();
				places_arraylist.addAll(places);
				Log.i("change item", changedPlace.getShopName());
				// Log.i(tag, msg)
				adapter.notifyDataSetChanged();
				cost = 0;
				for (int i = 0; i < places.size(); i++) {

					if (places.get(i).getTime().equals("中午")
							|| places.get(i).getTime().equals("晚餐")) {
						// cost += places.get(i).getCost();
						cost += places.get(i).getCost();
					} else {

					}

				}

				Log.i("cost", "人均消费 new" + String.valueOf(cost));
				textView_cost.setText(String.valueOf(cost));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class lowerTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			return connect.GetLower(params[0], params[1], params[2],
			params[3], params[4], params[5]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Log.i("result", result);
				if (result.equals("Can not find")) {
					Toast.makeText(getApplicationContext(), "Can not find",
							Toast.LENGTH_SHORT).show();
				} else {
					Log.i("Lower! jsonString Change! ", result);
					decodeJson json = new decodeJson(result);
					JSONArray jsonArray = json.getJsonArray();
					places.clear();
					places = json.JsonToPlaceList(jsonArray);
					// JSONObject jsonObject = new JSONObject(result);
					places_arraylist.clear();

					places_arraylist.addAll(places);
					Log.i("Lower item Sum", String.valueOf(places.size()));

					// Log.i(tag, msg)
					adapter.notifyDataSetChanged();
					cost = 0;
					for (int i = 0; i < places.size(); i++) {

						if (places.get(i).getTime().equals("中午")
								|| places.get(i).getTime().equals("晚餐")) {
							// cost += places.get(i).getCost();
							cost += places.get(i).getCost();
						} else {

						}

					}

					Log.i("cost", "人均消费 new" + String.valueOf(cost));
					textView_cost.setText(String.valueOf(cost));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class upperTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			 return connect.GetUpper(params[0], params[1], params[2],
			 params[3], params[4], params[5]);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.equals("Can not find")) {
					Toast.makeText(getApplicationContext(), "Can not find",
							Toast.LENGTH_SHORT).show();
				} else {
					Log.i("Upper! jsonString Change! ", result);
					decodeJson json = new decodeJson(result);
					JSONArray jsonArray = json.getJsonArray();
					places.clear();
					places = json.JsonToPlaceList(jsonArray);
					// JSONObject jsonObject = new JSONObject(result);
					places_arraylist.clear();
					places_arraylist.addAll(places);
					Log.i("Upper item Sum", String.valueOf(places.size()));

					// Log.i(tag, msg)
					adapter.notifyDataSetChanged();
					cost = 0;
					for (int i = 0; i < places.size(); i++) {

						if (places.get(i).getTime().equals("中午")
								|| places.get(i).getTime().equals("晚餐")) {
							// cost += places.get(i).getCost();
							cost += places.get(i).getCost();
						} else {

						}

					}

					Log.i("cost", "人均消费 new" + String.valueOf(cost));
					textView_cost.setText(String.valueOf(cost));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class sendArriveTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return connect.SendArrive(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("arrive info has been recorded")) {
				Log.i("SendArrive", result);
			} else {
				Log.e("SendArrive", "Error in Send Arrive");
			}
		}

	}

	public class sendDeleteTask extends AsyncTask<String, integer, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return connect.SendDelete(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("result in Send Delete", result);
			if (result.equals("delete info has been recorded")) {
				Log.i("SendDelete", result);
			} else {
				Log.e("SendDelete", "Error in Send Delete");
			}
		}

	}

	public List<place> getPlaces() {
		return places;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.getMenuInflater().inflate(R.menu.result, menu);
		// final MenuItem change = menu.add(0, 1, 0, "Change").setIcon(
		// getResources().getDrawable(R.drawable.button_change_new));
		// change.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// // TODO Auto-generated method stub
		// showWindow(item.getActionView());
		// return true;
		// }
		// });
		// MenuItem save = menu.add(0, 2, 1, "save").setIcon(
		// getResources().getDrawable(R.drawable.button_save_new));
		// save.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// // TODO Auto-generated method stub
		// saveToDB(places);
		// // finish();
		// return true;
		// }
		// });
		// MenuItem map = menu.add(0, 3, 2, "Map").setIcon(
		// getResources().getDrawable(R.drawable.button_change_new));
		// map.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// // intent.setClass(getApplicationContext(), MapActivity.class);
		// intent.setClass(getApplicationContext(), NewMapActivity.class); //
		// set
		// // new
		// // map
		// // activity
		// intent.putExtra("places", (Serializable) places);
		// startActivity(intent);
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);
		// return true;
		// }
		// });
		// change.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// map.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i1 = item.getItemId();
		if (i1 == R.id.change) {
			View view = (findViewById(R.id.change));
			DemoApplication demoApplication = (DemoApplication) getApplication();
			if (demoApplication.allDay == true) {
				showWindow(view);
			} else {
				String[] types = { "美食", "购物", "电影院", "风景", "咖啡/甜点", "KTV" };
				JSONObject json = new JSONObject();
				JSONArray item2 = new JSONArray();
				for (int i = 0; i < types.length; i++) {
					if (demoApplication.selectType[i] == true) {
						item2.put(types[i]);
					}

				}
				try {
					json.put("item", item2);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					new RequestPartTaskAgain().execute(String.valueOf(loclng)
							, String.valueOf(loclat),
							json.getString("item"), userid);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if (i1 == R.id.save) {
			saveToDB(places);
			Intent intent3 = new Intent();
			intent3.setClass(getApplicationContext(), ShareActivity.class);
			intent3.putExtra("places", (Serializable) places);
			startActivity(intent3);
			finish();

		} else if (i1 == R.id.map) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), NewMapActivity.class); // set
			// new
			// map
			// activity
			intent.putExtra("places", (Serializable) places);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);

		} else {
		}

		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void showWindow(View parent) {

		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.group_list, null);

			lv_group = (ListView) view.findViewById(R.id.lvGroup);
			// 加载数据
			groups = new ArrayList<String>();
			groups.add("更奢侈");
			groups.add("更便宜");
			groups.add("随心换");
			lv_group.setDividerHeight(0);

			GroupAdapter groupAdapter = new GroupAdapter(this, groups);
			lv_group.setAdapter(groupAdapter);
			// 创建一个PopuWidow对象
			popupWindow = new CustomPopupWindow(view, 300, 200);
		}

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = windowManager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		Log.i("coder", "xPos:" + xPos);

		// popupWindow.showAsDropDown(parent, xPos, 0);
		popupWindow.showAsDropDown(parent);
		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				switch (position) {
				case 0:
					startExpensive();
					break;
				case 1:
					startCheap();
					break;
				case 2:
	
					try {
						new randomTask().execute(type, String.valueOf(places.get(0).getPos_x()),
								String.valueOf(places.get(0).getPos_y()),
								json.getString("item"), userid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
				// Toast.makeText(WarpDSLV.this, groups.get(position), 1000)
				// .show();

				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
	}

	public void startExpensive() {
		System.out.println("奢侈点 Start");
		tempCost = 0;
		for (int i = 0; i < places.size(); i++) {
			if (places.get(i).getMainType().equals("美食")) {
				tempCost += places.get(i).getCost();
			}
		}

		try {
			new upperTask().execute(type, String.valueOf(places.get(0).getPos_x()),
					String.valueOf(places.get(0).getPos_y()),
					String.valueOf(tempCost), json.getString("item"), userid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startCheap() {
		System.out.println("便宜点 Start");
		tempCost = 0;
		for (int i = 0; i < places.size(); i++) {
			if (places.get(i).getMainType().equals("美食")) {
				tempCost += places.get(i).getCost();
			}
		}

		try {
			new lowerTask().execute(type, String.valueOf(places.get(0).getPos_x()),
					String.valueOf(places.get(0).getPos_y()),String.valueOf(tempCost), json.getString("item"), userid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> calcDistances(List<place> places) {
		List<String> distance = null;
		distance = new ArrayList<String>();
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		distance.add(String.valueOf(dcmFmt.format(distanceByLngLat(loclng,
				loclat, places.get(0).getPos_x(), places.get(0).getPos_y())))
				+ "\nkm");
		for (int i = 0; i < places.size() - 1; i++) {
			distance.add(String.valueOf(dcmFmt
					.format(distanceByLngLat(places.get(i).getPos_x(), places
							.get(i).getPos_y(), places.get(i + 1).getPos_x(),
							places.get(i + 1).getPos_y())))
					+ "\nkm");
		}
		return distance;
	}

	/**
	 * 根据经纬度，获取两点间的距离
	 * 
	 * @param lng1
	 *            经度
	 * @param lat1
	 *            纬度
	 * @param lng2
	 * @param lat2
	 * @return
	 * 
	 */
	public double distanceByLngLat(double lng1, double lat1, double lng2,
			double lat2) {
		double radLat1 = lat1 * Math.PI / 180;
		double radLat2 = lat2 * Math.PI / 180;
		double a = radLat1 - radLat2;
		double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
		s = Math.floor(s * 10000) / 10000;
		Log.i("s", String.valueOf(s));
		return s / 1000;
	}

	/**
	 * 到Url下载图片 并进行裁剪
	 * 
	 * @param
	 * @return Bitmap
	 */
	public class getPic extends AsyncTask<String, Integer, List<Bitmap>> {

		@Override
		protected List<Bitmap> doInBackground(String... params) {
			// TODO Auto-generated method stub

			return getBitmapFromUrl(params);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Toast.makeText(getApplicationContext(), values[0].toString(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(List<Bitmap> bitmapList) {
			int index = 0;
			while (true) {
				index = index++ % bitmapList.size();

				mapImageView.setImageBitmap(bitmapList.get(index));

			}

		}

	}

	private List<Bitmap> getBitmapFromUrl(String... imgUrl) {
		List<Bitmap> bitmapList = new ArrayList<Bitmap>();

		int cut_from_x = 40; // 从图片的x轴的x处开始裁剪
		int cut_from_y = 13; // 从图片的y轴的y处开始裁剪
		int image_width_x = 300; // 裁剪生成新图皮的宽
		int image_height_y = 130; // 裁剪生成新图皮的高
		for (int i = 0; i < imgUrl.length; i++) {
			Log.i("urls", imgUrl[i]);
			URL url;
			Bitmap bitmap = null;
			Bitmap cutBitmap = null;
			try {
				url = new URL(imgUrl[i]);
				InputStream is = url.openConnection().getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bitmap = BitmapFactory.decodeStream(bis);
				bis.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cutBitmap = Bitmap.createBitmap(bitmap, cut_from_x, cut_from_y,
					image_width_x, image_height_y);
			bitmapList.add(cutBitmap);
		}
		return bitmapList;
	}

	public class RequestPartTaskAgain extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return connect.GetPartRoute(params[0], params[1], params[2],
					params[3]);
		}

		@Override
		protected void onPostExecute(String result) {
			decodeJson objdecodeJson = null;
			try {
				objdecodeJson = new decodeJson(result);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Log.i("top", objdecodeJson.getTop());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			places = objdecodeJson
					.JsonToPlaceList(objdecodeJson.getJsonArray());
			for (int i = 0; i < places.size(); i++) {
				Log.i("places info", places.get(i).getShopName());
			}
			places_arraylist.clear();

			places_arraylist.addAll(places);
			adapter.notifyDataSetChanged();
			cost = 0;
			for (int i = 0; i < places.size(); i++) {
				if (places.get(i).getTime().equals("中午")
						|| places.get(i).getTime().equals("晚餐")
						|| places.get(i).getMainType().equals("美食")) {
					// cost += places.get(i).getCost();
					cost += places.get(i).getCost();
				}

			}

			Log.i("cost", "人均消费 new" + String.valueOf(cost));
			textView_cost.setText(String.valueOf(cost));
		}

	}

}
