package com.sssta.ganmaqu;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ChangeCityDialog extends Dialog {
	private String ipString;
	private String currentCity;
	private TextView textView;
	private myGridView gridView_city;
	private GridAdapter gridAdapter;
	private String lng, lat;
	private SharedPreferences userInfo;
	private Connect connect;
	private Button circleTextView;

	public ChangeCityDialog(Context context) {
		super(context, R.style.CustomDialog);
		ipString = getContext().getResources().getString(R.string.ip);
		Log.i("ipString", ipString);
		setCustomView();
	}

	public ChangeCityDialog(Context context, String lngString, String latString) {
		super(context, R.style.CustomDialog);
		ipString = getContext().getResources().getString(R.string.ip);
		connect = new Connect(ipString);
		this.lng = lngString;
		this.lat = latString;
		userInfo = context.getSharedPreferences("userInfo", 0);
		Log.i("ipString", ipString);
		setCustomView();
	}

	public ChangeCityDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, R.style.CustomDialog);
		this.setCancelable(cancelable);
		this.setOnCancelListener(cancelListener);
		setCustomView();
	}

	public void setTextView(TextView textView_trans) {
		this.textView = textView_trans;
	}
	public void setCircleTextView(Button textView2)
	{
		this.circleTextView= textView2;
	}
	/**
	 * 设置整个弹出框的视图
	 */
	private void setCustomView() {
		View mView = LayoutInflater.from(getContext()).inflate(
				R.layout.dialog_changecity, null);

		gridView_city = (myGridView) mView.findViewById(R.id.gridView_circles);
		gridAdapter = new GridAdapter(this.getContext());
		new getCities().execute();
		super.setContentView(mView);

	}

	@Override
	public void setContentView(View view) {
		// 重写本方法，使外部调用时不可破坏控件的视图。
		// 也可以使用本方法改变CustomDialog的内容部分视图，比如让用户把内容视图变成复选框列表，图片等。这需要获取mView视图里的其它控件

	}

	public class GridAdapter extends BaseAdapter {

		private holder holder1;

		private class holder {
			Button button;
		}

		private HashMap<Integer, String> citiesInfo;

		private Context context;
		private LayoutInflater mInflater;

		public GridAdapter(Context c) {
			super();

			this.context = c;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setHashMap(HashMap<Integer, String> circles) {
			this.citiesInfo = circles;
		}

		@Override
		public int getCount() {
			return citiesInfo.size();
		}

		@Override
		public Object getItem(int index) {

			return citiesInfo.get("item" + String.valueOf(index + 1));
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_dialog_circle,
						null);
				holder1 = new holder();
				holder1.button = (Button) convertView
						.findViewById(R.id.button_circle);
				convertView.setTag(holder1);
				Log.i("convertview", "null");
			} else {
				holder1 = (holder) convertView.getTag();
			}

			holder1.button.setText(citiesInfo.get(position));
			Log.i("circles info", citiesInfo.toString());
			holder1.button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentCity = citiesInfo.get(position);
					textView.setText("当前城市:"+currentCity);
					userInfo.edit().putString("city", currentCity).commit();
					
					new getCurrentCircle().execute(String.valueOf(lng),
							String.valueOf(lat),currentCity);
					ChangeCityDialog.this.dismiss();

				}
			});

			return convertView;
		}

	}

	public class getCities extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// return GetCircleList(params[0]);
			return connect.GetAvailableCity();
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				TextView textView_jixugengxin = (TextView) findViewById(R.id.textView_jixugengxin);
				textView_jixugengxin.setText("我们支持的城市还在持续增加中...");
				HashMap<Integer, String> hashMapCircle = hashCircle(result);
				Log.i("city1", hashMapCircle.get(0));
				gridAdapter.setHashMap(hashMapCircle);
				gridView_city.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return MotionEvent.ACTION_MOVE == event.getAction() ? true
								: false;
					}
				});
				gridView_city.setAdapter(gridAdapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public HashMap<Integer, String> hashCircle(String resultString)
			throws JSONException

	{

		HashMap<Integer, String> ansHashMap = new HashMap<Integer, String>();
		int i = 1;
		JSONObject circle = new JSONObject(resultString);
		// Log.i("result String", circle.toString());

		while (circle.has("item" + String.valueOf(i))) {
			String temp = circle.getString("item" + String.valueOf(i));
			ansHashMap.put(i - 1, temp);
			i++;
		}
		// Log.i("hasmap", ansHashMap.toString());
		return ansHashMap;
	}
	public class getCurrentCircle extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// Log.i("params, msg)
			return connect.GetShopCircle(Double.parseDouble(params[0]),
					Double.parseDouble(params[1]), params[2]);
		}

		@Override
		protected void onPostExecute(String result) {
			// circleString = result;
			if (result == null) {
				circleTextView.setText("正在获取..");
			} else {
				circleTextView.setText(result);
			}
		}

	}
}
