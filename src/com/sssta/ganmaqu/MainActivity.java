package com.sssta.ganmaqu;

import kankan.wheel.widget.ArrayWheelAdapter;
import kankan.wheel.widget.WheelView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_main);
		Button button_yes  = (Button)findViewById(R.id.button_yes);
		//WheelView NumberOfPerson = (WheelView) findViewById(R.id.NumberOfPerson);
		//String Numbers[] = new String[] {"1", "2", "3", "4","5","6","7","8","9","10"};
		String Types[] = new String[] {"亲子出行", "朋友聚会", "情侣约会"};
		 final WheelView numberWheel = (WheelView) findViewById(R.id.NumberOfPerson);
	        String countries[] = new String[] {"2", "3", "4", "5","6","7","8"};
	        numberWheel.setVisibleItems(5);
	        numberWheel.setCyclic(false);//
	        numberWheel.setAdapter(new ArrayWheelAdapter<String>(countries));
	       final String cities[][] = new String[][] {
	    		   Types,Types,Types,Types,Types,Types,Types
	        		};
	        final WheelView typeWheel = (WheelView) findViewById(R.id.Type);
	      typeWheel.setAdapter(new ArrayWheelAdapter<String>(Types));
	        typeWheel.setVisibleItems(5);
	       
	    /*    numberWheel.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					typeWheel.setAdapter(new ArrayWheelAdapter<String>(cities[newValue]));
					typeWheel.setCurrentItem(cities[newValue].length / 2);
				}
			});*/
	        button_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 Toast.makeText(getApplicationContext(), String.valueOf(numberWheel.getCurrentItem()+2)+"人  "+typeWheel.getCurrentItem(), Toast.LENGTH_SHORT).show();
					 Intent intent = new Intent();
					 intent.setClass(getApplicationContext(), WarpDSLV.class);
					 startActivity(intent);
					 
				}
			});
	        numberWheel.setCurrentItem(3);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
