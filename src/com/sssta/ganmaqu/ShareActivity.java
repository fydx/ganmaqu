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
	/** ΢��OpenAPI������� */
    IWeiboAPI mWeiboAPI = null;
    private List<place> places;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		// ����΢������ӿ�ʵ��
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
		actionBar.setTitle("����");
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
     * �ӱ�Ӧ��->΢��->��Ӧ��
     * ������Ӧ���ݣ��÷��������á�
     * ע�⣺ȷ��{@link #onCreate(Bundle)} �� {@link #onNewIntent(Intent)}�У�
     * ���� mWeiboAPI.responseListener(intent, this)
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
            Toast.makeText(this, "�ɹ�����", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, "�û�ȡ������", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, baseResp.errMsg + ":ʧ�ܣ���", Toast.LENGTH_LONG).show();
            break;
        }
    }

//    /**
//     * �û��������ť������΢���ͻ��˽��з���
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
            Toast.makeText(this, "��ǰ΢���汾֧��SDK����", Toast.LENGTH_SHORT).show();
            
            int supportApi = mWeiboAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351) {
                reqSingleMsg();
            }
        } else {
            Toast.makeText(this, "��ǰ΢���汾��֧��SDK����", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * ������Ӧ�÷���������Ϣ��΢��������΢��������档
     * ��isWeiboAppSupportAPI() < 10351 ֻ֧�ַ�������Ϣ����
     * �ı���ͼƬ����ҳ�����֡���Ƶ�е�һ�֣���֧��Voice��Ϣ��
     * 
     * @param hasText    ����������Ƿ����ı�
     * @param hasImage   ����������Ƿ���ͼƬ
     * @param hasWebpage ����������Ƿ�����ҳ
     * @param hasMusic   ����������Ƿ�������
     * @param hasVideo   ����������Ƿ�����Ƶ
     */
    private void reqSingleMsg() {
        
        // 1. ��ʼ��΢���ķ�����Ϣ
        // �û����Է����ı���ͼƬ����ҳ�����֡���Ƶ�е�һ��
        WeiboMessage weiboMessage = new WeiboMessage();
            weiboMessage.mediaObject = getTextObj();
        
        
        
        // 2. ��ʼ���ӵ�������΢������Ϣ����
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        // ��transactionΨһ��ʶһ������
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = weiboMessage;
        
        // 3. ����������Ϣ��΢��������΢���������
        mWeiboAPI.sendRequest(this, req);
    }

    private String getActionUrl() {
        return "http://sina.com?eet" + System.currentTimeMillis();
    }
    /**
     * �ı���Ϣ���췽����
     * 
     * @return �ı���Ϣ����
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }
    /**
     * ͼƬ��Ϣ���췽����
     * 
     * @return ͼƬ��Ϣ����
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
		route.append("�����á�����ȥ��,һ���Ƽ�����·�ߵ��������ҵ�·���ǣ�");
		for(int i = 0;i<places.size()-1;i++)
		{
			route.append(places.get(i).getShopName());
			route.append("->");
		}
		route.append(places.get(places.size()-1).getShopName());
		
			
    	return route.toString();
    	
    }
}
