package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;

public class SettingsFragment extends android.support.v4.app.Fragment {
	private View view;
	private static String ipString;
	private SharedPreferences userInfo;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 获取到父Activity的引用。
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.activity_settings, container, false);
		userInfo = view.getContext().getSharedPreferences("userInfo", 0);
		ipString = view.getContext().getResources().getString(R.string.ip);
		final String username_init = userInfo.getString("userid", "NULL");

		Button button_account = (Button) view.findViewById(R.id.button_account);
		Button button_about = (Button) view.findViewById(R.id.button_about);
		Button button_prefer = (Button) view.findViewById(R.id.button_prefer);
		Button button_guide = (Button) view.findViewById(R.id.button_guide);
		button_prefer.setTextColor(0xff000000);
		button_account.setTextColor(0xff000000);
		button_about.setTextColor(0xff000000);
		button_guide.setTextColor(0xff000000);
		button_guide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(),
						GuideActivity.class);
				intent.putExtra("count", 1);
				startActivity(intent);
			}
		});
		button_about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(),
						AboutActivity.class);
				
				startActivity(intent);
			//	shareWeibo();
				
			}
		});
		button_prefer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(),
						DislikeActivity.class);

				startActivity(intent);
			}
		});
		if (!userInfo.getString("userid", "NULL").equals("NULL")) {
			button_account.setText("已登录 : " + username_init);
		}
		button_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (userInfo.getString("userid", "NULL").equals("NULL"))
					createLoginDialog();
				else {
					createLogoutDialog();
				}

				/*
				 * AlertDialog dialog = new
				 * AlertDialog.Builder(view.getContext()) .setTitle("登录提示")
				 * .setMessage("是否登录") .setPositiveButton("确定", new
				 * DialogInterface.OnClickListener() { ProgressDialog p_dialog;
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { // TODO Auto-generated method stub
				 * 
				 * 
				 * } }) .setNegativeButton("退出", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { // TODO Auto-generated method stub
				 * 
				 * } }).create(); dialog.show(); }
				 */
			}
		});
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void createLoginDialog() {
		LayoutInflater factory = LayoutInflater.from(view.getContext());
		final View DialogView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog dlg = new AlertDialog.Builder(view.getContext())
				.setTitle("登陆框")
				.setView(DialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					ProgressDialog p_dialog;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub
						p_dialog = ProgressDialog.show(view.getContext(),
								"请等待", "正在为您登录...", true);
						new Thread() {
							public void run() {
								try {
									// sleep(3000);
									EditText usernameEdittext = (EditText) DialogView
											.findViewById(R.id.UsernameEditText);
									EditText passwwordEditText = (EditText) DialogView
											.findViewById(R.id.PasswordEidtText);
									String username = usernameEdittext
											.getText().toString();
									String password = passwwordEditText
											.getText().toString();
									// String result = testLogin(username,
									// password);

									new loginTask().execute(username, password);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									p_dialog.dismiss();
								}
							}
						}.start();
					}
				})
				.setNegativeButton("注册", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub
						createRegDialog();
					}
				}).create();
		dlg.show();
	}

	public void createLogoutDialog()

	{
		LayoutInflater factory = LayoutInflater.from(view.getContext());
		AlertDialog dlg = new AlertDialog.Builder(view.getContext())
				.setTitle("提示")
				.setMessage("确认登出吗？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						userInfo.edit().clear().commit();
						Button accountButton = (Button) view
								.findViewById(R.id.button_account);
						accountButton.setText("账户设置");
					}

				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dlg.show();

	}

	public void createRegDialog() {
		LayoutInflater factory = LayoutInflater.from(view.getContext());
		final View DialogView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog dlg = new AlertDialog.Builder(view.getContext())
				.setTitle("注册")
				.setView(DialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					ProgressDialog p_dialog;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub
						p_dialog = ProgressDialog.show(view.getContext(),
								"请等待", "正在为您注册...", true);
						new Thread() {
							public void run() {
								try {
									// sleep(3000);
									EditText usernameEdittext = (EditText) DialogView
											.findViewById(R.id.UsernameEditText);
									EditText passwwordEditText = (EditText) DialogView
											.findViewById(R.id.PasswordEidtText);
									String username = usernameEdittext
											.getText().toString();
									String password = passwwordEditText
											.getText().toString();
									// String result = testLogin(username,
									// password);

									new regTask().execute(username, password);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									p_dialog.dismiss();
								}
							}
						}.start();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub

					}
				}).create();
		dlg.show();
	}

	public class loginTask extends AsyncTask<String, Integer, String> {
		private String username;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			username = params[0];
			return testLogin(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("login success")) {
				Toast.makeText(view.getContext(), "登陆成功", Toast.LENGTH_SHORT)
						.show();
				Button button_account = (Button) view
						.findViewById(R.id.button_account);
				button_account.setText("已登录 : " + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view.getContext(), "登录失败\n" + "原因" + result,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public class regTask extends AsyncTask<String, Integer, String> {
		private String username;
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			username = params[0];
			return testRegisterUserDB(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("register successful")) {
				Toast.makeText(view.getContext(), "注册成功", Toast.LENGTH_SHORT)
						.show();
				Button button_account = (Button) view
						.findViewById(R.id.button_account);
				button_account.setText("已登录 : " + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view.getContext(), "注册失败\n" + "原因" + result,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static String testLogin(String id, String password) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=login&id=" + id + "&password="
					+ password + "";
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "EXCEPTION IN LOGIN";
	}

	public static String testRegisterUserDB(String id, String password) // 用户注册
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=register&id=" + id + "&password="
					+ password;
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return "EXCPTION IN REG";
	}
	public void  share() {
		OnekeyShare oks = new OnekeyShare();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher, 
		view.getContext().getString(R.string.app_name));
		// address是接收人地址，仅在信息和邮件使用
		oks.setAddress("12345678901");
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(view.getContext().getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("ganmaqu");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath(MainActivity.TEST_IMAGE);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
		// 微信的两个平台、Linked-In支持此字段
		//oks.setImageUrl("http://sharesdk.cn/ rest.png");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// appPath是待分享应用程序的本地路劲，仅在微信中使用
		//oks.setAppPath(MainActivity.TEST_IMAGE);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment(view.getContext().getString(R.string.share));
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(view.getContext().getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		// venueName是分享社区名称，仅在Foursquare使用
		oks.setVenueName("Southeast in China");
		// venueDescription是分享社区描述，仅在Foursquare使用
		oks.setVenueDescription("This is a beautiful place!");
		// latitude是维度数据，仅在新浪微博、腾讯微博和Foursquare使用
		//oks.setLatitude(23.122619f);
		// longitude是经度数据，仅在新浪微博、腾讯微博和Foursquare使用
		//oks.setLongitude(113.372338f);
		// 是否直接分享（true则直接分享）
		oks.setSilent(false);
		// 指定分享平台，和slient一起使用可以直接分享到指定的平台
		Platform platform = ShareSDK.getPlatform(view.getContext(), SinaWeibo.NAME);
		if (platform != null) {
		        oks.setPlatform(SinaWeibo.NAME);
		}
		// 去除注释可通过OneKeyShareCallback来捕获快捷分享的处理结果
		// oks.setCallback(new OneKeyShareCallback());
		//通过OneKeyShareCallback来修改不同平台分享的内容
		oks.setShareContentCustomizeCallback(
				
		new ShareContentCustomizeCallback() {
			
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				// TODO Auto-generated method stub
				
			}
		});

		oks.show(view.getContext());
	}
	public void  shareWeibo()
	{
		Platform.ShareParams sp = new SinaWeibo.ShareParams();
		sp.text = "ganmaqu";
	//	sp.imagePath = “/mnt/sdcard/测试分享的图片.jpg”;

		Platform weibo = ShareSDK.getPlatform(view.getContext(), SinaWeibo.NAME);
	//	weibo.setPlatformActionListener(paListener); // 设置分享事件回调
		// 执行图文分享
		weibo.share(sp);
	}
}
