package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridView;

public class DetailActivity extends Activity {
	private GridAdapter eatGridAdapter, joyGridAdapter;
	private GridView eatGridView, joyGridView;
	private String ipString;
	final String eatType[] = { "川菜", "湘菜", "粤菜", "江浙菜", "海鲜", "火锅", "烧烤",
			"日本料理", "韩国料理", "西餐", "自助餐" };
	final String joyType[] = { "KTV", "电影院", "综合商场", "游乐游艺", "景点/郊游", "桌面游戏",
			"酒吧" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		ipString = getApplicationContext().getResources()
				.getString(R.string.ip);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		actionBar.setTitle("定制详细路线");
		actionBar.setDisplayShowHomeEnabled(false);
		eatGridView = (GridView) findViewById(R.id.gridView_eat);
		joyGridView = (GridView) findViewById(R.id.gridView_joy);
		eatGridAdapter = new GridAdapter(this);
		joyGridAdapter = new GridAdapter(this);
		eatGridAdapter.setList(eatType);
		joyGridAdapter.setList(joyType);
		eatGridView.setAdapter(eatGridAdapter);
		joyGridView.setAdapter(joyGridAdapter);

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.detail, menu);
	// return true;
	// }
	public class GetDetailRoute extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return testPostDetailRoute(params[0], params[1], params[2],
					params[3], params[4],
					Integer.parseInt(params[5]), Integer.parseInt(params[6]),
					params[7]);
		}

		@Override
		protected void onPostExecute(String result) {

		}

	}

	public String testPostDetailRoute(String type, String city,
			String circleName, String eat, String enjoy, int costLow,
			int costHigh, String id) {
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(ipString);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("command", "getdetailroute"));
			// System.out.println(eat);
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("circleName", circleName));
			params.add(new BasicNameValuePair("city", city));
			params.add(new BasicNameValuePair("eat", eat));
			params.add(new BasicNameValuePair("enjoy", enjoy));
			params.add(new BasicNameValuePair("costlow", costLow + ""));
			params.add(new BasicNameValuePair("costhigh", costHigh + ""));
			params.add(new BasicNameValuePair("id", id));
			UrlEncodedFormEntity encodedValues = new UrlEncodedFormEntity(
					params, "UTF-8");
			httpPost.setEntity(encodedValues);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			// System.out.println("ok");
			// System.out.println(httpResponse.getStatusLine().getStatusCode());
			HttpEntity entity = httpResponse.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
