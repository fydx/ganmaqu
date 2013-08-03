package com.sssta.ganmaqu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		final WebView mapView = (WebView)findViewById(R.id.mapView);
		WebSettings webSettings = mapView.getSettings();
		//WebView启用Javascript脚本执行
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		//加载指定Url
		mapView.loadUrl("file:///android_asset/index.html");  
		//mapView.loadUrl("file:///android_asset/js.html");
		Button jsButton = (Button)findViewById(R.id.test_button);
		jsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//执行js代码
			mapView.loadUrl("javascript:calcRoute()");
				
				Toast.makeText(getApplicationContext(), "run js", Toast.LENGTH_SHORT).show();
			}
		});
		/*mapView.setWebChromeClient(new WebChromeClient() {
		    @Override
		    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		        AlertDialog.Builder b2 = new AlertDialog.Builder(getApplicationContext())
		                .setTitle(R.string.app_name).setMessage(message)
		                .setPositiveButton("ok",
		                        new AlertDialog.OnClickListener() {
		                            @Override
		                            public void onClick(DialogInterface dialog,
		                                    int which) {
		                                result.confirm();
		                                // MyWebView.this.finish();
		                            }
		                        });

		        b2.setCancelable(false);
		        b2.create();
		        b2.show();
		        return true;
		    }
		});	*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}
