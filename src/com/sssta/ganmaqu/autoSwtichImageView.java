package com.sssta.ganmaqu;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class autoSwtichImageView extends ImageView{
	private List<String> urls;  //url地址
	private List<Bitmap> bitmaps; //bitmap集合容器

	public autoSwtichImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/**  
     * 该构造方法在静态引入XML文件中是必须的  
     * @param context  
     * @param paramAttributeSet  
     */    
    public autoSwtichImageView(Context context,AttributeSet paramAttributeSet){    
        super(context,paramAttributeSet);    
    }  
    
    public void  setUrls(List<String> list)
    {
    	urls= list;
    }
    public  void setContent()
    {
    	
    }
    /**
	 * 到Url下载图片 并进行裁剪
	 * @param imgUrl
	 * @return Bitmap
	 */
    public class getPic extends AsyncTask<String, Integer, List<Bitmap>>
    {

		@Override
		protected List<Bitmap> doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return getBitmapFromUrl(params);
		}
		@Override
		protected void onPostExecute(List<Bitmap> bitmapList)
		{
			
		}
    	
    }
	private List<Bitmap> getBitmapFromUrl(String... imgUrl) {
		List<Bitmap> bitmapList =  new ArrayList<Bitmap>();
		int cut_from_x=100; //从图片的x轴的x处开始裁剪 
		int cut_from_y=100; //从图片的y轴的y处开始裁剪 
		int image_width_x=350; //裁剪生成新图皮的宽 
		int image_height_y=150; //裁剪生成新图皮的高 
		for (int i = 0; i < imgUrl.length; i++) {
			URL url;
			Bitmap bitmap = null;
			Bitmap cutBitmap= null;
			try {
				url = new URL(imgUrl[i]);
				InputStream is = url.openConnection().getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bitmap = BitmapFactory.decodeStream(bis);
				bis.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cutBitmap = Bitmap.createBitmap(bitmap,cut_from_x,cut_from_y,image_width_x,image_height_y);
			bitmapList.add(cutBitmap);
		}
		return bitmapList;
	}

}
