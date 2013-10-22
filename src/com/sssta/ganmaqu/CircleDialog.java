package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
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



public class CircleDialog extends Dialog {
	private String ipString;
	private myGridView gridView_circles;
	private String currentCircle;
	private GridAdapter gridAdapter;
	private String city;
	private Button button;
	private Connect connect;
	
	public CircleDialog(Context context) {
		super(context,R.style.CustomDialog);
		ipString = getContext().getResources()
				.getString(R.string.ip);
		Log.i("ipString", ipString);
		setCustomView(); 
		// TODO Auto-generated constructor stub
	}
	public CircleDialog(Context context,String cityString) {
		super(context,R.style.CustomDialog);
		ipString = getContext().getResources()
				.getString(R.string.ip);
		Log.i("ipString", ipString);
		connect = new Connect(ipString);
		this.city = cityString;
		setCustomView(); 
		// TODO Auto-generated constructor stub
	}
	public void setbutton(Button button_trans) {
		this.button = button_trans;
	}
	
	public void setCity(String cityString)
	{
		this.city = cityString;
	}
	 public CircleDialog(Context context, boolean cancelable,  
	            OnCancelListener cancelListener) {  
	        super(context, R.style.CustomDialog);  
	        this.setCancelable(cancelable);  
	        this.setOnCancelListener(cancelListener);  
	        setCustomView();  
	    }  
	/** 
     * 设置整个弹出框的视图 
     */  
    private void setCustomView(){  
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_main_circles, null);  
      
        gridView_circles = (myGridView)mView.findViewById(R.id.gridView_circles);
        gridAdapter = new GridAdapter(this.getContext());
        Log.i("city_dialog", city);
        new getCircles().execute(city);
        super.setContentView(mView);  
        
       
    }  
    @Override  
    public void setContentView(View view){  
        //重写本方法，使外部调用时不可破坏控件的视图。  
        //也可以使用本方法改变CustomDialog的内容部分视图，比如让用户把内容视图变成复选框列表，图片等。这需要获取mView视图里的其它控件  
    	 
    }  
    public class GridAdapter extends BaseAdapter {
		
		
		private  holder holder1;
		private class holder {
			Button button;
		}
		  
	    private HashMap<Integer, String>  circlesInfo;
	   
		private Context context;
		private LayoutInflater mInflater;
		
		public GridAdapter(Context c) {
			super();
			
			this.context = c;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public void setHashMap(HashMap<Integer, String> circles)
		{
			this.circlesInfo = circles;
		}
		@Override
		public int getCount() {
			return circlesInfo.size();
		}

		@Override
		public Object getItem(int index) {

			return circlesInfo.get("item" + String.valueOf(index+1));
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			

			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.item_dialog_circle, null);
				holder1 = new holder();
			     holder1.button = (Button)convertView.findViewById(R.id.button_circle);
				convertView.setTag(holder1);
				Log.i("convertview", "null");
			} else {
				holder1 = (holder) convertView.getTag();
			}
			
			holder1.button.setText(circlesInfo.get(position));
			Log.i("circles info", circlesInfo.toString());
			holder1.button.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentCircle = circlesInfo.get(position);
					button.setText(currentCircle);
					CircleDialog.this.dismiss();
					
				}
			});
				
				
			return convertView;
		}
		
	}
    public class getCircles extends AsyncTask<String, Integer, String>
    {

   	@Override
   	protected String doInBackground(String... params) {
   		// TODO Auto-generated method stub
   		return connect.GetCircleList(params[0]);
   	}
   	@Override
   	protected void onPostExecute(String result)
   	{
   		try {
   			TextView button_wait = (TextView)findViewById(R.id.textView_wait);
   			button_wait.setVisibility(View.GONE);
   			HashMap<Integer, String> hashMapCircle = hashCircle(result);
   			Log.i("circle1", hashMapCircle.get(0));
   			gridAdapter.setHashMap(hashMapCircle);
   			gridView_circles.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					return MotionEvent.ACTION_MOVE == event.getAction() ? true
                            : false;
				}
			});
   			gridView_circles.setAdapter(gridAdapter);
   		} catch (JSONException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   		
   	}
   	 
    }
   
    public HashMap<Integer,String> hashCircle(String resultString) throws JSONException
    
    {
    	
    	HashMap<Integer,String> ansHashMap = new HashMap<Integer, String>();
    	int i = 1 ;
    	JSONObject circle = new JSONObject(resultString);
    //	Log.i("result String", circle.toString());
    	
    	
    	while (circle.has("item" + String.valueOf(i))) {
    		String temp = circle.getString("item"+ String.valueOf(i));
    		ansHashMap.put(i-1, temp);
    		i++;
		}
    	//Log.i("hasmap", ansHashMap.toString());
    	return ansHashMap;
    }
}
