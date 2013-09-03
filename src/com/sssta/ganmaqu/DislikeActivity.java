package com.sssta.ganmaqu;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

public class DislikeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dislike);
		myGridView gridView = (myGridView) findViewById(R.id.gridView_dislike);
		myGridView grdView_type = (myGridView) findViewById(R.id.gridView_dislike_type);
		GridAdapter gridAdapter = new GridAdapter(this);
		GridAdapter gridAdapter_type = new GridAdapter(this);
		String eatType[] = { "川菜", "湘菜", "粤菜", "江浙菜", "海鲜", "火锅", "烧烤", "日本料理",
				"韩国料理", "西餐", "自助餐" };
		gridAdapter.setList(eatType);
		String dislikeType[] = { "KTV", "电影院", "综合商场", "游乐游艺", "景点/郊游", "桌面游戏",
				"酒吧" };
		gridAdapter_type.setList(dislikeType);
		gridView.setAdapter(gridAdapter);
		grdView_type.setAdapter(gridAdapter_type);

	}

	public class GridAdapter extends BaseAdapter {
		private String[] dislikeType;

		private class holder {
			CheckBox checkBox;
			TextView textView;
		}
		// 用来控制CheckBox的选中状况  
	    private HashMap<Integer,Boolean> isSelected;  
		private Context context;
		private LayoutInflater mInflater;

		public GridAdapter(Context c) {
			super();
			this.context = c;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return dislikeType.length;
		}

		@Override
		public Object getItem(int index) {

			return dislikeType[index];
		}

		public void setList(String[] array) {
			this.dislikeType = array;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			holder holder1;

			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.griditem_dislike, null);
				holder1 = new holder();
				holder1.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBox_dislike);
				holder1.textView = (TextView) convertView
						.findViewById(R.id.text_dislike);
				convertView.setTag(holder1);
				Log.i("convertview", "null");
			} else {
				holder1 = (holder) convertView.getTag();
			}
			
			holder1.textView.setText(dislikeType[position]);
			//holder1.checkBox.get
			return convertView;
		}
		public HashMap<Integer,Boolean> getIsSelected() {  
	        return isSelected;  
	    }  
	  
	    public void setIsSelected(HashMap<Integer,Boolean> isSelected) {  
	        this.isSelected = isSelected;  
	    }  

	}

	
}
