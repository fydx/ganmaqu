package com.sssta.ganmaqu;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

class GalleryImageAdapter extends BaseAdapter{
    private Context context;
    //Í¼Æ¬Ô´Êý×é
    private Integer[] imageInteger;
    public GalleryImageAdapter(Context c,Integer[] ImageIds){
    	imageInteger= ImageIds;
        context = c;
    }
    @Override
    public int getCount() {
        return imageInteger.length;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageInteger[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new Gallery.LayoutParams(136, 88));
        return imageView;
    }
    }
