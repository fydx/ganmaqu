package com.sssta.ganmaqu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsFragment extends android.support.v4.app.Fragment {
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
		  View view = inflater.inflate(R.layout.activity_settings, container, false);
		  Button button_account =
					 (Button)view.findViewById(R.id.button_account);
					 Button button_about =
					 (Button)view.findViewById(R.id.button_about);
					Button button_prefer = (Button) view.findViewById(
							R.id.button_prefer);
					button_prefer.setTextColor(0xff000000);
					button_account.setTextColor(0xff000000);
					button_about.setTextColor(0xff000000);
					button_prefer.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent();
							intent.setClass(getActivity().getApplicationContext(),
									DislikeActivity.class);
							
							startActivity(intent);
						}
					});
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		

	}

}
