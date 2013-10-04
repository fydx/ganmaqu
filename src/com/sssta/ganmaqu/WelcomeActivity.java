package com.sssta.ganmaqu;

import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {
	private FinalDb db_user;
	private SharedPreferences sp;
	private int count1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    //Set NO titlebar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //璁剧疆鍏ㄥ睆
		setContentView(R.layout.activity_welcome);
		// 判断是否为第一次进入程序
				sp = getSharedPreferences("COUNT", Context.MODE_PRIVATE);
				count1 = sp.getInt("COUNT", 0);
				Editor e = sp.edit();
				e.putInt("COUNT", ++count1);
				e.commit();
				// 判断结束
		final Intent intent2 = new Intent(WelcomeActivity.this, NewMainActivity.class);
		Timer timer = new Timer(); //设置Timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent2);
				finish(); 
			}
		};
		timer.schedule(task, 1000 * 1);
		if (count1==1) {
			Log.i("firstboot", "True");
			db_user = FinalDb.create(this);
			User user = new User(1,1);
			db_user.save(user);
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
