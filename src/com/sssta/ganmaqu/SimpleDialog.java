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
     * �����������������ͼ 
     */  
    private void setCustomView(){  
     //   View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple, null);  
       
    //    super.setContentView(mView);  
       
        
       
    }  
    @Override  
    public void setContentView(View view){  
        //��д��������ʹ�ⲿ����ʱ�����ƻ��ؼ�����ͼ��  
        //Ҳ����ʹ�ñ������ı�CustomDialog�����ݲ�����ͼ���������û���������ͼ��ɸ�ѡ���б�ͼƬ�ȡ�����Ҫ��ȡmView��ͼ��������ؼ�  
//    	 ImageView im = (ImageView)findViewById(R.id.imageView_guide_main); 
//    	im.setImageDrawable(view.getResources().getDrawable(background));
    }  
}
