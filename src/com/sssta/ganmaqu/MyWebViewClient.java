package com.sssta.ganmaqu;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
	@Override
	 public boolean shouldOverrideUrlLoading(WebView view, String url){  
	    // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边  
	    //   view.loadUrl(url);  
	       Intent intent = new Intent();
	       intent.setClass(view.getContext(),WebActivity.class);
	       intent.putExtra("url", url);
	       view.getContext().startActivity(intent);
	       return true;  
	  
	       }  
	  
}
