package com.sssta.ganmaqu;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
	@Override
	 public boolean shouldOverrideUrlLoading(WebView view, String url){  
	    // ��д�˷������������ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�  
	    //   view.loadUrl(url);  
	       Intent intent = new Intent();
	       intent.setClass(view.getContext(),WebActivity.class);
	       intent.putExtra("url", url);
	       view.getContext().startActivity(intent);
	       return true;  
	  
	       }  
	  
}
