package com.sssta.ganmaqu;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.Inflater;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

public class SettingsActivity extends Activity {
	private View view;
	private static String ipString;
	private SharedPreferences userInfo;
	private Connect connect;
	private String nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR); // Add this line
		setContentView(R.layout.activity_settings);
		view =(View)findViewById(R.id.rtlayout_settings);
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
//		actionBar.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.actionbar_banner));
		
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("设置");
		actionBar.setDisplayHomeAsUpEnabled(true);
		final Button button_account = (Button) findViewById(R.id.button_account);
		Button button_about = (Button) findViewById(R.id.button_about);
		Button button_prefer = (Button) findViewById(R.id.button_prefer);
		Button button_guide = (Button) findViewById(R.id.button_guide);
		button_prefer.setTextColor(0xff000000);
		button_account.setTextColor(0xff000000);
		button_about.setTextColor(0xff000000);
		button_guide.setTextColor(0xff000000);
		userInfo = getSharedPreferences("userInfo", 0);
		ipString = getResources().getString(R.string.ip);
		final String username_init = userInfo.getString("userid", "NULL");
		connect = new Connect(ipString);
		Log.i("已验证用户id", username_init);
		if (!userInfo.getString("userid", "NULL").equals("NULL")) {
			if (userInfo.getString("accountType", "NULL").equals("weibo")) {
				Platform weibo = ShareSDK
						.getPlatform(getApplicationContext(),
								SinaWeibo.NAME);
				nickname = weibo.getDb().getUserName();
				new getuserIcon().execute(weibo.getDb().getUserIcon());
				button_account.setText("已登录(新浪微博用户):" + nickname);
			} else if (userInfo.getString("accountType", "NULL").equals(
					"tencent")) {
				final Platform tencent = ShareSDK.getPlatform(
						getApplicationContext(), QZone.NAME);
				nickname = tencent.getDb().getUserName();
				Log.i("icon", tencent.getDb().getUserIcon());
				new getuserIcon().execute(tencent.getDb().getUserIcon());
				button_account.setText("已登录(QQ用户):" + nickname);
			} else {
				button_account.setText("已登录(原有用户系统):" + username_init);
			}

		}
	
		button_guide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
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
				intent.setClass(getApplicationContext(),
						AboutActivity.class);

				startActivity(intent);
				// shareWeibo();

			}
		});
		button_prefer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						DislikeActivity.class);

				startActivity(intent);
			}
		});
		
		button_account.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (userInfo.getString("userid", "NULL").equals("NULL")) {

					final String[] LogMethods = { "通过新浪微博认证登陆", "通过QQ认证登陆",
							"通过原有账号系统登陆" };
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingsActivity.this);
					builder.setTitle("选择登录方式");
					builder.setItems(LogMethods,
							new DialogInterface.OnClickListener() {
							String addString;
								PlatformActionListener paListener = new PlatformActionListener() {

									@Override
									public void onError(Platform arg0,
											int arg1, Throwable arg2) {
										// TODO Auto-generated method stub
										Log.e("Throwable", arg2.toString());
									}

									@Override
									public void onComplete(
											Platform platform, int arg1,
											HashMap<String, Object> arg2) {
										// TODO Auto-generated method stub
									
										Platform weibo = ShareSDK.getPlatform(
														getApplicationContext(),
												SinaWeibo.NAME);
										Platform tencent = ShareSDK.getPlatform(
														getApplicationContext(),
												QZone.NAME);
										
										String id = platform.getDb()
												.getUserId();
										Log.i("id_fragment", id);
										nickname = platform.getDb()
												.getUserName();
										Log.i("nickname", nickname);
										userInfo.edit()
												.putString("userid", id)
												.commit();
										if (platform.equals(weibo)) {
											userInfo.edit()
													.putString(
															"accountType",
															"weibo")
													.commit();
											addString = "(新浪微博用户)";
										}
										if (platform.equals(tencent)) {
											userInfo.edit()
													.putString(
															"accountType",
															"tencent")
													.commit();
											addString = "(QQ用户)";
										}
										new LooperThread().start();
										button_account
												.post(new Runnable() {

													@Override
													public void run() {
														// TODO
														// Auto-generated
														// method stub
														button_account
																.setText("已登录:"
																		+addString + " " + nickname);
													}
												});
										new getuserIcon().execute(platform.getDb().getUserIcon());
									}

									@Override
									public void onCancel(Platform arg0,
											int arg1) {
										// TODO Auto-generated method stub

									}
								};

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									switch (which) {
									case 0:
										// set sina authorize()

										Platform weibo = ShareSDK.getPlatform(
												
														getApplicationContext(),
												SinaWeibo.NAME);
										// try {
										// String
										// id=weibo.getDb().getUserId();
										// Log.i("id2_sina", id);
										// } catch (Exception e) {
										// // TODO: handle exception.
										// Log.i("weibo account",
										// "failed to be load");
										// }

										weibo.setPlatformActionListener(paListener);
										weibo.authorize();
										// weibo.showUser(account);
										break;
									case 1:
										Platform tencent = ShareSDK.getPlatform(
														getApplicationContext(),
												QZone.NAME);
										tencent.setPlatformActionListener(paListener);
										tencent.authorize();
										break;
									case 2:
										createLoginDialog();
										break;
									default:
										break;
									}
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				
				else {
					createLogoutDialog();
				}
			}
		
		});
	
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
			return connect.AuthLogin(params[0], params[1]);
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
			return connect.RegUser(params[0], params[1]);
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
	/**
	 * 到Url下载图片
	 * 
	 * @param imgUrl
	 * @return Bitmap
	 */
	private Bitmap getBitmapFromUrl(String imgUrl) {
		URL url;
		Bitmap bitmap = null;
		try {
			url = new URL(imgUrl);
			InputStream is = url.openConnection().getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public class getuserIcon extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			return getBitmapFromUrl(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
		//	usericonImageView.setVisibility(View.VISIBLE);
		//	usericonImageView.setImageBitmap(bitmap);
		}

	}
	class LooperThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();
			Toast.makeText(getApplicationContext(), "登陆成功",
					Toast.LENGTH_SHORT).show();

			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// process incoming messages here

				}
			};

			Looper.loop();
		}
	}
}
