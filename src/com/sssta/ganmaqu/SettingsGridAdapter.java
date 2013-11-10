package com.sssta.ganmaqu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsGridAdapter extends BaseAdapter {
	private final String settingsType[] = {"收藏路线","设置","帮助"};
	private final int settingsDrawable[] = {R.drawable.button_prefer,R.drawable.button_setting,R.drawable.help};
	private Context context;
	private LayoutInflater mInflater;
	private class holder{
		ImageView imageView;
		TextView textView;
	}
	public SettingsGridAdapter(Context c) {
		super();
		this.context = c;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return settingsType.length;
	}

	@Override
	public Object getItem(int index) {
		return settingsType[index];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder holder1;
		if (convertView == null)
		{
			convertView = mInflater
					.inflate(R.layout.griditem_settings, null);
			holder1 = new holder();
			holder1.textView=(TextView)convertView.findViewById(R.id.textView_settingsItem);
			holder1.imageView=(ImageView)convertView.findViewById(R.id.button_settingsItem);
			convertView.setTag(holder1);
		}
		 else {
				holder1 = (holder) convertView.getTag();
			}
		holder1.textView.setText(settingsType[position]);
		holder1.imageView.setImageDrawable(convertView.getContext().getResources().getDrawable(settingsDrawable[position]));
		
		return convertView;
	}
	

}
