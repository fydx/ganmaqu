package com.sssta.ganmaqu;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link ProfileFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class ProfileFragment extends android.support.v4.app.Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private String ipString;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	private int count_city;
	private OnFragmentInteractionListener mListener;
	private TextView cityTextView, accountTextView;
	private SharedPreferences userInfo;
	private Connect connect;
	private String city;
	private myGridView settingsGridView;
	private SettingsGridAdapter settingsGridAdapter;
	private View view_profile;
	private Activity attach_activity;
	private Platform weibo;
	private String nickname;
	public String getARG_PARAM1() {
		return ARG_PARAM1;
	}
	
	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment ProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileFragment newInstance(String param1, String param2) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}
	class LooperThread extends Thread { 
		public Handler mHandler;   
        
	      public void run() {   
	          Looper.prepare();   
	          Toast.makeText(getActivity().getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
	         
	          mHandler = new Handler() {   
	              public void handleMessage(Message msg) {   
	                  // process incoming messages here   
	            	
	              }   
	          };   
	             
	          Looper.loop();   
	      }   
	}  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view_profile = inflater.inflate(R.layout.fragment_profile, container,
				false);
		cityTextView = (TextView) view_profile
				.findViewById(R.id.textView_currentCity);
		userInfo = view_profile.getContext()
				.getSharedPreferences("userInfo", 0);
		accountTextView = (TextView) view_profile
				.findViewById(R.id.textView_loginAccount);
		String username_init = userInfo.getString("userid", "NULL");
		if (!userInfo.getString("userid", "NULL").equals("NULL")) {
			accountTextView.setText("已登录:" + username_init);
		}
		ipString = view_profile.getContext().getResources()
				.getString(R.string.ip);
		city = userInfo.getString("city", "西安市");
		cityTextView.setText("当前城市:" + city);
		count_city = userInfo.getInt("count_city", 0);
		settingsGridView = (myGridView) view_profile
				.findViewById(R.id.gridView_rightMenu);
		
		settingsGridAdapter = new SettingsGridAdapter(view_profile.getContext());
		settingsGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:	
					if (userInfo.getString("userid", "NULL").equals("NULL"))
					{	
					
					final String[] LogMethods = {"通过新浪微博认证登陆","通过原有账号系统登陆"};
					AlertDialog.Builder builder = new  AlertDialog.Builder(view_profile.getContext());
					builder.setTitle("选择登录方式");
					builder.setItems(LogMethods, new DialogInterface.OnClickListener() {
						 PlatformActionListener paListener = new PlatformActionListener() {

								@Override
								public void onError(Platform arg0, int arg1, Throwable arg2) {
									// TODO Auto-generated method stub
									Log.e("Throwable", arg2.toString());
								}

								@Override
								public void onComplete(Platform platform, int arg1,
										HashMap<String, Object> arg2) {
									// TODO Auto-generated method stub
									String id=platform.getDb().getUserId();
									Log.i("id_sina_fragment", id);
									nickname  = platform.getDb().getUserName();
									Log.i("nickname", nickname);
									userInfo.edit().putString("userid", id).commit();
									new LooperThread().start();
									 accountTextView.post(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											accountTextView.setText("已登录(新浪微博):" + nickname);
										}
									});
										
									//userInfo.edit().putString("accountType", "weibo");
									
									
								}

								@Override
								public void onCancel(Platform arg0, int arg1) {
									// TODO Auto-generated method stub

								}
							};
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0:
								// set sina authorize()
								
								final Platform weibo = ShareSDK.getPlatform(attach_activity.getApplicationContext(),
								SinaWeibo.NAME);
//								try {
//								String id=weibo.getDb().getUserId();
//								 Log.i("id2_sina", id);
//								} catch (Exception e) {
//									// TODO: handle exception.
//									Log.i("weibo account", "failed to be load");
//								} 
								
								 weibo.setPlatformActionListener(paListener);
								 weibo.authorize();
								// weibo.showUser(account);
								break;
							case 1:
								createLoginDialog(view_profile);
							default:
								break;
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					}
					
					else {
						createLogoutDialog(view_profile);
					}
					break;
				case 1:
					Intent intent = new Intent();
					intent.setClass(getActivity().getApplicationContext(),
							DislikeActivity.class);

					startActivity(intent);
					break;
				case 3:
					Intent intent2 = new Intent();
					intent2.setClass(getActivity().getApplicationContext(),
							GuideActivity.class);

					startActivity(intent2);
					break;
				case 4:
					ChangeCityDialog changeCityDialog = new ChangeCityDialog(
							view_profile.getContext(), String
									.valueOf(MainActivity.getLng()), String
									.valueOf(MainActivity.getLng()));
					changeCityDialog.setTextView(cityTextView);
					changeCityDialog.setCircleTextView(MainActivity
							.getCircleButton());
					changeCityDialog.show();
					break;
				default:
					break;
				}
			}

		});
		settingsGridView.setAdapter(settingsGridAdapter);
		connect = new Connect(ipString);
		cityTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		// new
		// AddressRequestTask().execute(String.valueOf(MainActivity.getLat()),
		// String.valueOf(MainActivity.getLng()));
		// Log.i("start in fragment", "start");
		// Log.i("loaction_fragment",
		// String.valueOf(MainActivity.getLat()) + " "
		// + String.valueOf(MainActivity.getLng()));
		return view_profile;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attach_activity = activity;
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	public class AddressRequestTask extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return connect.GetCurrentAddress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {
				cityTextView.setText("地点未知");
			} else {
				try {
					Log.i("ADDRESS REQUEST", result);
					JSONObject jsonObject = new JSONObject(result);
					JSONObject jsonResult = new JSONObject(
							jsonObject.getString("result"));
					// Toast.makeText(getApplicationContext(),
					// jsonResult.getString("formatted_address"),
					// Toast.LENGTH_LONG).show();
					// cityTextView.setText(jsonResult
					// .getString("city"));
					if (count_city == 0) {

						city = null;
						String addressComponent = jsonResult
								.getString("addressComponent");
						JSONObject address = new JSONObject(addressComponent);
						city = new String(address.getString("city"));
						Log.i("city", city);
						cityTextView.setText(city);
						userInfo.edit().putString("city", city).commit();
						count_city++;
						userInfo.edit().putInt("count_city", count_city)
								.commit();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public void createLoginDialog(final View view) {
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
						createRegDialog(view);
					}
				}).create();
		dlg.show();
	}

	public void createLogoutDialog(final View view)

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
						TextView textview_account = (TextView) view_profile
								.findViewById(R.id.textView_loginAccount);
						textview_account.setText("未登录");
					
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

	public void createRegDialog(final View view) {
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
				Toast.makeText(view_profile.getContext(), "登陆成功",
						Toast.LENGTH_SHORT).show();
				TextView textview_account = (TextView) view_profile
						.findViewById(R.id.textView_loginAccount);
				textview_account.setText("已登录:" + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view_profile.getContext(),
						"登录失败\n" + "原因" + result, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(view_profile.getContext(), "注册成功",
						Toast.LENGTH_SHORT).show();
				TextView textview_account = (TextView) view_profile
						.findViewById(R.id.textView_loginAccount);
				textview_account.setText("已登录:" + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view_profile.getContext(),
						"注册失败\n" + "原因" + result, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
