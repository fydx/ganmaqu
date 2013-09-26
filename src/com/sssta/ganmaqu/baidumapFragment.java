package com.sssta.ganmaqu;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class baidumapFragment extends android.support.v4.app.Fragment {

	private Activity activity_1;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity_1 = activity;
		// 获取到父Activity的引用。
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_new_map, container, false);
		return view;

}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
}

