package com.sssta.ganmaqu;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private Context context;  
  
    private List<String> list;  
  
    public GroupAdapter(Context context, List<String> list) {  
  
        this.context = context;  
        this.list = list;  
  
    }  
  
    @Override  
    public int getCount() {  
        return list.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
  
        return list.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup viewGroup) {  
  
          
        ViewHolder holder;  
        if (convertView==null) {  
            convertView=LayoutInflater.from(context).inflate(R.layout.group_item, null);  
            holder=new ViewHolder();  
              
            convertView.setTag(holder);  
              
            holder.groupItem=(TextView) convertView.findViewById(R.id.textView_group);  
             holder.imageView = (ImageView)convertView.findViewById(R.id.imageView_menu);
        }  
        else{  
            holder=(ViewHolder) convertView.getTag();  
        }  
        //holder.groupItem.setTextColor(Color.BLACK);  
        holder.groupItem.setText(list.get(position));  
        if (position==0) {
			holder.groupItem.setBackgroundResource(R.drawable.marker_list_top);
			holder.imageView.setImageResource(R.drawable.icon_small_high);
		}
        if (position==1) {
        	holder.imageView.setImageResource(R.drawable.icon_small_low);
		}
        if (position==2) {
        	holder.groupItem.setBackgroundResource(R.drawable.marker_list_bottom);
        	holder.imageView.setImageResource(R.drawable.icon_small_random);
		}
        return convertView;  
    }  
  
    static class ViewHolder {  
        TextView groupItem;  
        ImageView imageView;
    }  

}