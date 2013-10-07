package com.sssta.ganmaqu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.about, menu);
//		return true;
//	}
//	public void jumptoGuide(View v)
//	{
		
//		overridePendingTransition(android.R.anim.fade_in,
//				android.R.anim.fade_out);
	//	finish();
//	}

}
