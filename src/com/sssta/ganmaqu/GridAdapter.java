package com.sssta.ganmaqu;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;



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