package com.sssta.ganmaqu;

import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortController;

public class WarpDSLV extends ListActivity {

    private ArrayAdapter<String> adapter;
    private String jsonString;
    private String[] array;
    private ArrayList<String> list;

    private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                String item=adapter.getItem(from);

                adapter.notifyDataSetChanged();
                adapter.remove(item);
                adapter.insert(item, to);
            }
        };

    private DragSortListView.RemoveListener onRemove = 
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                adapter.remove(adapter.getItem(which));
            }
        };

    private DragSortListView.DragScrollProfile ssProfile =
        new DragSortListView.DragScrollProfile() {
            @Override
            public float getSpeed(float w, long t) {
                if (w > 0.8f) {
                    // Traverse all views in a millisecond
                    return ((float) adapter.getCount()) / 0.001f;
                } else {
                    return 10.0f * w;
                }
            }
        };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warp_main);
     
        DragSortListView lv = (DragSortListView) getListView(); 

        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragScrollProfile(ssProfile);

        array = getResources().getStringArray(R.array.countries);
        list = new ArrayList<String>(Arrays.asList(array));
        /*   list = new ArrayList<String>();
        list.add("上午 ： 汤姆熊欢乐世界");
        list.add("中午 ： 骡马市必胜客");
        list.add("下午 ： 骡马市民生百货");*/
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "This is "+ String.valueOf(arg2+1) + " item", Toast.LENGTH_SHORT).show();
			}
        	
		});
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_left, R.id.text, list);
        setListAdapter(adapter);
        jsonString = getFromAssets("test.json");
        Log.i("file_content",jsonString);
        try {
			decodeJson objdecodeJson = new decodeJson(jsonString);
			Log.i("top", objdecodeJson.getTop());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
    public String getFromAssets(String fileName){  
        String result = "";  
            try {  
                InputStream in = getResources().getAssets().open(fileName);  
                //获取文件的字节数  
                int lenght = in.available();  
                //创建byte数组  
                byte[]  buffer = new byte[lenght];  
                //将文件中的数据读到byte数组中  
                in.read(buffer);  
                result = EncodingUtils.getString(buffer, "UTF-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return result;  
    }
}
