package com.sssta.ganmaqu;

import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.demo.CitiesActivity;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //璁剧疆鍙栨秷鏍囬鏍�
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //璁剧疆鍏ㄥ睆
		setContentView(R.layout.activity_welcome);
		final Intent intent2 = new Intent(WelcomeActivity.this, MainActivity.class);
		Timer timer = new Timer(); //璁剧疆Timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent2);
				finish(); // 缁撴潫
			}
		};
		timer.schedule(task, 1000 * 2);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
