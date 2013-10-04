package com.sssta.ganmaqu;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;

public class ShareActivity extends Activity  implements  IWeiboHandler.Response{
	private Button shareButton;
	private String text;
	/** 微博OpenAPI访问入口 */
    IWeiboAPI mWeiboAPI = null;
    private List<place> places;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		// 创建微博对外接口实例
        mWeiboAPI = WeiboSDK.createWeiboAPI(this, ConstantS.APP_KEY);
        mWeiboAPI.responseListener(getIntent(), this);
        Log.i("status_weibo", mWeiboAPI.toString());
        places = (List<place>) getIntent().getSerializableExtra("places");
		text= codeToString(places);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("分享");
		shareButton = (Button)findViewById(R.id.button_share);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWeiboAPI.registerApp();
	            reqMsg();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}
	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboAPI.responseListener(getIntent(), this);
        Log.i("status_weibo", mWeiboAPI.toString());
    }
	/**
     * 从本应用->微博->本应用
     * 接收响应数据，该方法被调用。
     * 注意：确保{@link #onCreate(Bundle)} 与 {@link #onNewIntent(Intent)}中，
     * 调用 mWeiboAPI.responseListener(intent, this)
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
            Toast.makeText(this, "成功！！", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, "用户取消！！", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
            break;
        }
    }

//    /**
//     * 用户点击分享按钮，唤起微博客户端进行分享。
//     */
//    @Override
//    public void onClick(View v) {
//        if (R.id.button_share == v.getId()) {
//            mWeiboAPI.registerApp();
//            reqMsg();
//        }
//    }
    private void reqMsg() {
        
        if (mWeiboAPI.isWeiboAppSupportAPI()) {
            Toast.makeText(this, "当前微博版本支持SDK分享", Toast.LENGTH_SHORT).show();
            
            int supportApi = mWeiboAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351) {
                reqSingleMsg();
            }
        } else {
            Toast.makeText(this, "当前微博版本不支持SDK分享", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当isWeiboAppSupportAPI() < 10351 只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void reqSingleMsg() {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
            weiboMessage.mediaObject = getTextObj();
        
        
        
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboAPI.sendRequest(this, req);
    }

    private String getActionUrl() {
        return "http://sina.com?eet" + System.currentTimeMillis();
    }
    /**
     * 文本消息构造方法。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }
    /**
     * 图片消息构造方法。
     * 
     * @return 图片消息对象。
     */
//    private ImageObject getImageObj() {
//        ImageObject imageObject = new ImageObject();
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) mImage.getDrawable();
//        imageObject.setImageObject(bitmapDrawable.getBitmap());
//        return imageObject;
//    }
    public String codeToString(List<place> places)
    {
		StringBuffer route = new StringBuffer();
		route.append("我在用“干嘛去”,一键推荐出行路线的利器！我的路线是：");
		for(int i = 0;i<places.size()-1;i++)
		{
			route.append(places.get(i).getShopName());
			route.append("->");
		}
		route.append(places.get(places.size()-1).getShopName());
		
			
    	return route.toString();
    	
    }
}
