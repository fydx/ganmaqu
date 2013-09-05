package com.sssta.ganmaqu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MapFragment extends android.support.v4.app.Fragment {
	
	@Override
	public void onAttach(Activity activity) {
	super.onAttach(activity);
	//获取到父Activity的引用。
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);
		  View view = inflater.inflate(R.layout.activity_map, container, false);
		  
		  return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		

	}
}
