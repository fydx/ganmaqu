package com.sssta.ganmaqu;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class SimpleDialog extends Dialog {
	private int background;
	public SimpleDialog(Context context,int bg) {
		super(context,R.style.CustomDialog);
		this.background=bg;
		Log.i("background", String.valueOf(background));
		setCustomView();
	}
	/** 
     * 设置整个弹出框的视图 
     */  
    private void setCustomView(){  
     //   View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple, null);  
       
    //    super.setContentView(mView);  
       
        
       
    }  
    @Override  
    public void setContentView(View view){  
        //重写本方法，使外部调用时不可破坏控件的视图。  
        //也可以使用本方法改变CustomDialog的内容部分视图，比如让用户把内容视图变成复选框列表，图片等。这需要获取mView视图里的其它控件  
//    	 ImageView im = (ImageView)findViewById(R.id.imageView_guide_main); 
//    	im.setImageDrawable(view.getResources().getDrawable(background));
    }  
}
