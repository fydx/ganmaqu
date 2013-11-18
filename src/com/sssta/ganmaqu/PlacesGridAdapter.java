package com.sssta.ganmaqu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PlacesGridAdapter extends BaseAdapter {
    private final String[] types = {"美食","购物","电影院","风景","咖啡/甜点","KTV"};
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return types.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return types[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
