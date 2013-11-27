package com.sssta.ganmaqu;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class typeSelectAdapter extends BaseAdapter {
	private Context context;  
	private int width;
	private int height;
    private List<String> list;  
  
    public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public typeSelectAdapter(Context context, List<String> list) {  
  
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
            convertView=LayoutInflater.from(context).inflate(R.layout.listitem_main, null);  
            holder=new ViewHolder();  
            
            convertView.setTag(holder);  
              
            holder.groupItem=(TextView) convertView.findViewById(R.id.textview_main_item);  
          //   holder.imageView = (ImageView)convertView.findViewById(R.id.imageView_menu);
        }  
        else{  
            holder=(ViewHolder) convertView.getTag();  
        }  
        //holder.groupItem.setTextColor(Color.BLACK);  
        holder.groupItem.setText(list.get(position));  
        holder.groupItem.setHeight(height);
        holder.groupItem.setWidth(width);
        holder.groupItem.setGravity(Gravity.CENTER);
        if (position==0) {
			//holder.groupItem.setBackgroundResource(R.drawable.bg_listview_top);
		holder.groupItem.setBackgroundColor(Color.parseColor("#abdd11"));
		//	holder.imageView.setImageResource(R.drawable.icon_small_high);
		}
        if (position==1) {
        holder.groupItem.setBackgroundColor(Color.parseColor("#91bf04"));
        //	holder.imageView.setImageResource(R.drawable.icon_small_low);
		}
    
        return convertView;  
    }  
  
    static class ViewHolder {  
        TextView groupItem;  
       
    }  
}
