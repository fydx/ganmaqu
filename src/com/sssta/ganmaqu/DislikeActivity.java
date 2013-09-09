package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class DislikeActivity extends Activity {
	private  String ipString;
	private String userid;
	private HashMap<Integer,Boolean> dislikeEatType;
	private GridAdapter gridAdapter;
	private GridAdapter gridAdapter_type;
	private myGridView gridView,gridView_type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dislike);
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		SharedPreferences userInfo = getApplicationContext().getSharedPreferences("userInfo", 0);
		userid = userInfo.getString("userid", "root");
		gridView = (myGridView) findViewById(R.id.gridView_dislike);
		gridView_type = (myGridView) findViewById(R.id.gridView_dislike_type);
		gridAdapter = new GridAdapter(this);
		gridAdapter_type = new GridAdapter(this);
		final String eatType[] = { "川菜", "湘菜", "粤菜", "江浙菜", "海鲜", "火锅", "烧烤", "日本料理",
				"韩国料理", "西餐", "自助餐" };
		gridAdapter.setList(eatType);
		final String dislikeType[] = { "KTV", "电影院", "综合商场", "游乐游艺", "景点/郊游", "桌面游戏",
				"酒吧" };
		gridAdapter_type.setList(dislikeType);
		gridView.setAdapter(gridAdapter);
		gridView_type.setAdapter(gridAdapter_type);
		new getDislikeEatType().execute(userid);
		new getDislikeType().execute(userid);
		Button button_confirm = (Button)findViewById(R.id.button_dislike_confirm);
		button_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<String> isSelected = new ArrayList<String>();
			
				HashMap<Integer, Boolean> state =gridAdapter.getState();	
				HashMap<Integer, Boolean> state_type =gridAdapter_type.getState();	
					
				for(int j=0;j<gridAdapter.getCount();j++){
						if (state.get(j)!=null) {
						
							isSelected.add(eatType[j]);
						}
				}
				for(int j=0;j<gridAdapter_type.getCount();j++){
					if (state_type.get(j)!=null) {
						
						isSelected.add(dislikeType[j]);
					}
			}
				//Post to server
				try {
					new AddDislikeToServer().execute(ConvertDislikeToJson(isSelected));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});

	}

	public class GridAdapter extends BaseAdapter {
		private String[] dislikeType;
		private int count ;
		private class holder {
			CheckBox checkBox;
			TextView textView;
		}
		// 用来控制CheckBox的选中状况  
	    private HashMap<Integer,Boolean> state = new HashMap<Integer, Boolean>();  
	    private HashMap<Integer,Boolean> state_eat = new HashMap<Integer, Boolean>();  
		private Context context;
		private LayoutInflater mInflater;
		
		public GridAdapter(Context c) {
			super();
			count = 0;
			this.context = c;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public void setHashMap(HashMap<Integer, Boolean> state_1)
		{
			this.state_eat=state_1;
			count++;
		}
		@Override
		public int getCount() {
			return dislikeType.length;
		}

		@Override
		public Object getItem(int index) {

			return dislikeType[index];
		}

		public void setList(String[] array) {
			this.dislikeType = array;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			holder holder1;

			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.griditem_dislike, null);
				holder1 = new holder();
				holder1.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBox_dislike);
				holder1.textView = (TextView) convertView
						.findViewById(R.id.text_dislike);
				convertView.setTag(holder1);
				Log.i("convertview", "null");
			} else {
				holder1 = (holder) convertView.getTag();
			}
			
			holder1.textView.setText(dislikeType[position]);
			Log.i("state_eat",state_eat.toString());
			if (count>0) {
				
				if (state_eat.get(position)!=null) {
					holder1.checkBox.setChecked(true);
				}
				
			}
			
			//holder1.checkBox.get
			holder1.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked)
						state.put(position, isChecked);
					else {
						state.remove(position);
					}
					
				}
			});
			return convertView;
		}
		public HashMap<Integer, Boolean> getState()
		{
			return state;
			
		}

	}
	public class getDislikeEatType extends AsyncTask<String, Integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return testGetDislike(params[0]);
			
		}
		@Override 
		protected void onPostExecute(String result)
		{
			try {
				Log.i("dislikeType", result);
				gridAdapter.setHashMap(getDetailPlace(result));
				
				gridAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	public class getDislikeType extends AsyncTask<String, Integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return testGetDislike(params[0]);
			
		}
		@Override 
		protected void onPostExecute(String result)
		{
			try {
				Log.i("dislikeType", result);
				gridAdapter_type.setHashMap(getDetailType(result));
				gridAdapter_type.notifyDataSetChanged();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	public  class AddDislikeToServer extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return PostDislike(params[0]);
		}
		@Override
		protected void onPostExecute(String result)
		{
			if (result.equals("dislike add successful") ) {
				Toast.makeText(getApplicationContext(), "偏好设置成功", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}
	public String ConvertDislikeToJson(List<String> list) throws JSONException
	{
		final String item = "item";
		JSONObject jsonObject = new JSONObject();
		for (int i = 0; i < list.size(); i++) {
			jsonObject.put(item + String.valueOf(i), list.get(i));
		}
		jsonObject.put("id", userid);
		Log.i("userid", userid);
		return jsonObject.toString();
	}
	public String PostDislike(String json)
	{
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://" + ipString+ ":8080/");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("command", "setdislike"));
			params.add(new BasicNameValuePair("item", json));
			UrlEncodedFormEntity encodedValues = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(encodedValues);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			/*int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			StringBuilder responseString = new StringBuilder();
			responseString.append("Status Code: ");
			responseString.append(httpStatusCode);
			responseString.append("\nResponse:\n");
			responseString.append(httpResponse.getStatusLine().getReasonPhrase().toString() + "\n");
			System.out.println(responseString);
			if (httpStatusCode < 200 || httpStatusCode > 299)
			{
				throw new Exception("Error posting to URL: " + "http://127.0.0.1:8080/" + " due to " + httpResponse.getStatusLine().getReasonPhrase());
			}*/
			HttpEntity entity = httpResponse.getEntity();
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
	
		return "EXCEPTION IN POST DISLIKE";
	}
	public  String testGetDislike(String id)
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getdislike&id=" + id;
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
		return "EXCETION IN GETDISLIKE";
	}
	public  HashMap<Integer, Boolean> getDetailPlace(String json) throws Exception
	{
		HashMap<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		String name[] = { "川菜", "湘菜", "粤菜", "江浙菜", "海鲜", "火锅", "烧烤", "日本料理", "韩国料理", "西餐", "自助餐" };
		int i = 1;
		JSONObject dislike = new JSONObject(json);
		while (dislike.has("item" + i))
		{
			String temp = dislike.getString("item" + i);
			if (temp.contains("西"))
			{
				ans.put(9, true);
			}
			else
			{
				for (int k = 0; k < name.length; k++)
				{
					if (name[k].equals(temp))
					{
						ans.put(k, true);
						break;
					}
				}
			}
			i++;
		}
		return ans;
	}
	public  HashMap<Integer, Boolean> getDetailType(String json) throws Exception
	{
		HashMap<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		String name[] = { "KTV", "电影院", "综合商场", "游乐游艺", "景点/郊游", "桌面游戏", "酒吧" };
		int i = 1;
		JSONObject dislike = new JSONObject(json);
		while (dislike.has("item" + i))
		{
			String temp = dislike.getString("item" + i);
			for (int k = 0; k < name.length; k++)
			{
				if (name[k].equals(temp))
				{
					ans.put(k, true);
					break;
				}
			}
			i++;
		}
		return ans;
	}
	
}
