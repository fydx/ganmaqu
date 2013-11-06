package com.sssta.ganmaqu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GuideActivity extends Activity {
	ViewPager viewPager;
	ArrayList<View> list;
	ViewGroup main, group;
	TextView textView;
	TextView[] textViews;
	SharedPreferences sp;
	int count ;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Set NO titlebar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		LayoutInflater inflater = getLayoutInflater();
		list = new ArrayList<View>();
		list.add(inflater.inflate(R.layout.item1, null));
		list.add(inflater.inflate(R.layout.item2, null));
		list.add(inflater.inflate(R.layout.item3, null));
		list.add(inflater.inflate(R.layout.item4, null));
		list.add(inflater.inflate(R.layout.item5, null));
		list.add(inflater.inflate(R.layout.item6, null));
//		View view = getLayoutInflater().inflate(R.layout.item4, null);
//		Button button = (Button) view.findViewById(R.id.button_enter);
//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		textViews = new TextView[list.size()];
		ViewGroup main = (ViewGroup) inflater.inflate(R.layout.main, null);
		// group是R.layou.main中的负责包裹小圆点的LinearLayout.
		ViewGroup group = (ViewGroup) main.findViewById(R.id.viewGroup);

		viewPager = (ViewPager) main.findViewById(R.id.viewPager);

		for (int i = 0; i < list.size(); i++) {
			textView = new TextView(GuideActivity.this);
			textView.setLayoutParams(new LayoutParams(20, 20));
			textView.setPadding(0, 0, 2, 0);
			textViews[i] = textView;
			if (i == 0) {
				// 默认进入程序后第一张图片被选中;
				textViews[i].setBackgroundResource(R.drawable.radio_sel);
			} else {
				textViews[i].setBackgroundResource(R.drawable.radio);
			}
			group.addView(textViews[i]);
		}

		setContentView(main);
		sp=getApplicationContext().getSharedPreferences("userInfo", 0);
		
		count =sp.getInt("first", 0);
		Log.i("count onCreate", String.valueOf(count));
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyListener());
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup
		 * , int, java.lang.Object)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(list.get(position));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#finishUpdate(android.view.ViewGroup
		 * )
		 */
		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.finishUpdate(container);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object
		 * )
		 */
		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return super.getPageTitle(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#instantiateItem(android.view
		 * .ViewGroup, int)
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(list.get(position));
			return list.get(position);
		}
	}

	class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < textViews.length; i++) {
				textViews[arg0].setBackgroundResource(R.drawable.radio_sel);
				if (arg0 != i) {
					textViews[i].setBackgroundResource(R.drawable.radio);
				}
			}

		}

	}
	public void jumptoMain(View v)
	{
		Log.i("count JumpToMain", String.valueOf(count));
		if (count == 0) {
		//	Intent intent = new Intent();
		//	Log.i("selected", "select");
		//	intent.setClass(getApplicationContext(), MainActivity.class);
		//	startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			finish();
		}
		else {
			finish();
		}
		
	}
}