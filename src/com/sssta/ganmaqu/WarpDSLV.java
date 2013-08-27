package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;

import net.tsz.afinal.FinalDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
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
	private String ipString;
	private FinalDb db, db_user;
	private int cost = 0;
	private ArrayAdapter<place> adapter;
	private String jsonString;
	private String[] array;
	private ArrayList<String> list;
	private ArrayList<String> list_time;
	private double loclat,loclng;
	private List<place> places;
	private Button button_toMap, button_saveToDB,button_up,button_low;
	private TextView textView_cost;
	private ArrayList<place> places_arraylist;
	private String type;
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
			Log.i("Remove Which", String.valueOf(which));
			new changeTask().execute(type, String.valueOf(places.get(which)
					.getPos_x()), String.valueOf(places.get(which).getPos_y()),
					places.get(which).getTime(), places.get(which)
							.getShopName(), String.valueOf(which));
			new sendDeleteTask().execute(String.valueOf(places.get(which).getId()),"123");
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.warp_main);
		ipString = getResources().getString(R.string.ip);
		db = FinalDb.create(this);
		db_user = FinalDb.create(this);
		DragSortListView lv = (DragSortListView) getListView();
		type = getIntent().getStringExtra("type");
		loclat= getIntent().getDoubleExtra("loclat", 34.265733);
		loclng= getIntent().getDoubleExtra("loclng", 108.953906);
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		// lv.setDragScrollProfile(ssProfile);
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
		button_low = (Button)findViewById(R.id.button_low);
		button_up = (Button)findViewById(R.id.button_up);
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
		button_low.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int tempCost = 0 ;
				for (int i = 0; i < places.size(); i++) {
					if(places.get(i).getMainType().equals("美食"))
					{
						tempCost+= places.get(i).getCost();
					}
				}
				
				new lowerTask().execute(type,String.valueOf(loclng),String.valueOf(loclat),String.valueOf(tempCost));
			}
		});
		button_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int tempCost = 0 ;
				for (int i = 0; i < places.size(); i++) {
					if(places.get(i).getMainType().equals("美食"))
					{
						tempCost+= places.get(i).getCost();
					}
				}
				
				new upperTask().execute(type,String.valueOf(loclng),String.valueOf(loclat),String.valueOf(tempCost));
			
			}
		});
		// calculate cost
		textView_cost = (TextView) findViewById(R.id.cost);

		list = new ArrayList<String>();
		list_time = new ArrayList<String>();
		for (int i = 0; i < places.size(); i++) {

			list.add(places.get(i).getDetailType() + "  人均"
					+ String.valueOf(places.get(i).getCost()) + "元");
			list_time.add(places.get(i).getTime());
			if (places.get(i).getMainType().equals("购物")) {
				// cost += places.get(i).getCost();
			} else {
				cost += places.get(i).getCost();
			}

		}

		Log.i("cost", "人均消费" + String.valueOf(cost));
		textView_cost.setText("预计人均消费(未算购物) : " + String.valueOf(cost) + "元");

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
		places_arraylist = new ArrayList<place>();
		places_arraylist.addAll(places);

		adapter = new placeAdapter(places_arraylist);
		setListAdapter(adapter);
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
		User user = db_user.findById(1, User.class);
		int route_num = user.getRoute_num();
		Log.i("user get route_num", String.valueOf(route_num));
		for (int i = 0; i < places.size(); i++) {
			places.get(i).setRoute_id(route_num + 1);
			db.save(places.get(i));
			new sendArriveTask().execute(String.valueOf(places.get(i).getId()),"123");
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
		public ImageView dragImageView;
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
				ImageView iv = (ImageView) v.findViewById(R.id.drag_handle);
				holder.detailView = tv;
				holder.dragImageView = iv;
				v.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) v.getTag();
			// String detail = places.get(position).getAddress();
		//	Log.i("position", String.valueOf(position));
			holder.detailView.setText(places.get(position).getDetailType()
					+ "    " + "人均"
					+ String.valueOf(places.get(position).getCost()) + "元");
			if (places.get(position).getTime().equals("上午")) {
				holder.dragImageView.setImageResource(R.drawable.morning);
			}
			if (places.get(position).getTime().equals("中午")) {
				holder.dragImageView.setImageResource(R.drawable.launch);
			}
			if (places.get(position).getTime().equals("下午")) {
				holder.dragImageView.setImageResource(R.drawable.afternoon);
			}
			if (places.get(position).getTime().equals("晚餐")) {
				holder.dragImageView.setImageResource(R.drawable.dinner);
			}
			if (places.get(position).getTime().equals("晚上")) {
				holder.dragImageView.setImageResource(R.drawable.evening);
			}
			return v;
		}
	}

	public class changeTask extends AsyncTask<String, integer, String> {

		String rankString;

		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub
			rankString = param[5];
			return requestChangeStringToServer(param[0], param[1], param[2],
					param[3], param[4]);
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
				//	Log.i(tag, msg)
				adapter.notifyDataSetChanged();
				cost = 0;
				for (int i = 0; i < places.size(); i++) {
					
				
					if (places.get(i).getMainType().equals("购物")) {
						// cost += places.get(i).getCost();
					} else {
						cost += places.get(i).getCost();
					}

				}

				Log.i("cost", "人均消费 new" + String.valueOf(cost));
				textView_cost.setText("预计人均消费(未算购物) : " + String.valueOf(cost) + "元");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	public class lowerTask extends AsyncTask<String, integer, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return requestLowerToServer(params[0], params[1], params[2], params[3]);
		}
		@Override
		protected void onPostExecute(String result) {
			try {
				Log.i("result", result);
				if (result.equals("Can not find") ) {
					Toast.makeText(getApplicationContext(), "Can not find", Toast.LENGTH_SHORT).show();
				}
				else{
				Log.i("Lower! jsonString Change! ", result);
				decodeJson json = new decodeJson(result);
				JSONArray jsonArray = json.getJsonArray();
				places.clear();
				places = json.JsonToPlaceList(jsonArray);
				// JSONObject jsonObject = new JSONObject(result);
				places_arraylist.clear();
				
				places_arraylist.addAll(places);
				Log.i("Lower item Sum", String.valueOf(places.size()));
				
				//	Log.i(tag, msg)
				adapter.notifyDataSetChanged();
				cost = 0;
				for (int i = 0; i < places.size(); i++) {
					
				
					if (places.get(i).getMainType().equals("购物")) {
						// cost += places.get(i).getCost();
					} else {
						cost += places.get(i).getCost();
					}

				}

				Log.i("cost", "人均消费 new" + String.valueOf(cost));
				textView_cost.setText("预计人均消费(未算购物) : " + String.valueOf(cost) + "元");}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public class upperTask extends AsyncTask<String, integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return requestUpperToServer(params[0], params[1], params[2], params[3]);
		}
		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.equals("Can not find") ) {
					Toast.makeText(getApplicationContext(), "Can not find", Toast.LENGTH_SHORT).show();
				}
				else{
				Log.i("Upper! jsonString Change! ", result);
				decodeJson json = new decodeJson(result);
				JSONArray jsonArray = json.getJsonArray();
				places.clear();
				places = json.JsonToPlaceList(jsonArray);
				// JSONObject jsonObject = new JSONObject(result);
				places_arraylist.clear();
				places_arraylist.addAll(places);
				Log.i("Upper item Sum", String.valueOf(places.size()));
				
				//	Log.i(tag, msg)
				adapter.notifyDataSetChanged();
				cost = 0;
				for (int i = 0; i < places.size(); i++) {
					
				
					if (places.get(i).getMainType().equals("购物")) {
						// cost += places.get(i).getCost();
					} else {
						cost += places.get(i).getCost();
					}

				}

				Log.i("cost", "人均消费 new" + String.valueOf(cost));
				textView_cost.setText("预计人均消费(未算购物) : " + String.valueOf(cost) + "元");}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
	}
	public class sendArriveTask extends AsyncTask<String, integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return sendArrive(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("arrive info has been recorded")) {
				Log.i("SendArrive",result);
			}
			else {
				Log.e("SendArrive", "Error in Send Arrive");
			}
		}
		
	}
	public class sendDeleteTask extends AsyncTask<String, integer, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return sendDelete(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result) {
			Log.i("result in Send Delete", result);
			if (result.equals("delete info has been recorded")) {
				Log.i("SendDelete",result);
			}
			else {
				Log.e("SendDelete", "Error in Send Delete");
			}
		}
		
	}
	public String requestChangeStringToServer(String type, String pos_x,
			String pos_y, String time, String shopname) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=change&type=" + type + "&pos_x="
					+ pos_x + "&pos_y=" + pos_y + "&time=" + time
					+ "&shopName=" + shopname;
			Log.i("changeRequest",request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	public String requestLowerToServer(String type, String pos_x,
			String pos_y, String cost) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");

			String request = "/?command=lower&type=" + type + "&pos_x="
					+ pos_x + "&pos_y=" + pos_y + "&cost=" + cost;
			Log.i("changeRequest",request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	} 
	public String requestUpperToServer(String type, String pos_x,
			String pos_y, String cost) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");

			String request = "/?command=upper&type=" + type + "&pos_x="
					+ pos_x + "&pos_y=" + pos_y + "&cost=" + cost;
			Log.i("changeRequest",request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	} 
	public String sendArrive(String shopId,String userId)
	{
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=arrive&shopId="+shopId+"&userId="+userId;
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			httpclient.getConnectionManager().shutdown();
		}
		return null;
		
	}
	public String sendDelete(String shopId,String userId){
		
		Log.i("shopId + userId", shopId + " " + userId);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=delete&shopId="+shopId+"&userId="+userId;
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
}
