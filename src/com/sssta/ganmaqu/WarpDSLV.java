package com.sssta.ganmaqu;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;


public class WarpDSLV extends ListActivity {

  //  private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter;
    private String jsonString;
    private String[] array;
    private ArrayList<String> list;
    private List<place> places;
    private Button button_toMap;
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
      
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        
        DragSortListView lv = (DragSortListView) getListView(); 

        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragScrollProfile(ssProfile);
        button_toMap = (Button)findViewById(R.id.button_toMap);
        button_toMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MapActivity.class);
				intent.putExtra("places", (Serializable)places);
				startActivity(intent);
				
			}
		});
        /**
   		 *read from assets json files	
         */
        jsonString = getFromAssets("test.json");
        Log.i("file_content",jsonString);
        try {
			decodeJson objdecodeJson = new decodeJson(jsonString);
			Log.i("top", objdecodeJson.getTop());
			places= objdecodeJson.JsonToPlaceList(objdecodeJson.getJsonArray());
			for(int i=0;i<places.size();i++)
			{
				Log.i("places info", places.get(i).getShopName());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //array = getResources().getStringArray(R.array.countries);
        //list = new ArrayList<String>(Arrays.asList(array));
        list= new ArrayList<String>();
        for (int i = 0; i < places.size(); i++) {
        
        	list.add(places.get(i).getShopName() + "\n" + places.get(i).getMainType() + "  "+ places.get(i).getDetailType());

		}
     
    /*adapter = new SimpleAdapter(this, //activity
				mylist,//ArrayList<HashMap>
				R.layout.list_item_handle_left,// what is listitem ? 
				//  key
				new String[] { "name", "type" },
				// TextView ID
				new int[] { R.id.text_name, R.id.text_type });
		               // display
	//	   lv.setAdapter(adapter);
        setListAdapter(adapter);
        /*   list = new ArrayList<String>();
        list.add("上午 ： 汤姆熊欢乐世界");
        list.add("中午 ： 骡马市必胜客");
        list.add("下午 ： 骡马市民生百货");*/
        
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "This is "+ String.valueOf(adapter.getItem(arg2)) + " item", Toast.LENGTH_SHORT).show();
			}
        	
		});
  
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_left, R.id.text, list);
        setListAdapter(adapter);
        
       
        
        
    }
    //从assets中读取数据
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
