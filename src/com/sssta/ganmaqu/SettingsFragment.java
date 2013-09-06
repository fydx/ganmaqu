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

public class SettingsFragment extends android.support.v4.app.Fragment {
	private View view;
	private static String ipString;
	private SharedPreferences userInfo;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ��ȡ����Activity�����á�
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
		button_prefer.setTextColor(0xff000000);
		button_account.setTextColor(0xff000000);
		button_about.setTextColor(0xff000000);
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
			button_account.setText("�ѵ�¼ : " + username_init);
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
				 * AlertDialog.Builder(view.getContext()) .setTitle("��¼��ʾ")
				 * .setMessage("�Ƿ��¼") .setPositiveButton("ȷ��", new
				 * DialogInterface.OnClickListener() { ProgressDialog p_dialog;
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { // TODO Auto-generated method stub
				 * 
				 * 
				 * } }) .setNegativeButton("�˳�", new
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
				.setTitle("��½��")
				.setView(DialogView)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					ProgressDialog p_dialog;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub
						p_dialog = ProgressDialog.show(view.getContext(),
								"��ȴ�", "����Ϊ����¼...", true);
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
				.setNegativeButton("ע��", new DialogInterface.OnClickListener() {

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
				.setTitle("��ʾ")
				.setMessage("ȷ�ϵǳ���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						userInfo.edit().clear().commit();
						Button accountButton = (Button) view
								.findViewById(R.id.button_account);
						accountButton.setText("�˻�����");
					}

				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
				.setTitle("ע��")
				.setView(DialogView)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					ProgressDialog p_dialog;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO
						// Auto-generated
						// method
						// stub
						p_dialog = ProgressDialog.show(view.getContext(),
								"��ȴ�", "����Ϊ��ע��...", true);
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
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
				Toast.makeText(view.getContext(), "��½�ɹ�", Toast.LENGTH_SHORT)
						.show();
				Button button_account = (Button) view
						.findViewById(R.id.button_account);
				button_account.setText("�ѵ�¼ : " + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view.getContext(), "��¼ʧ��\n" + "ԭ��" + result,
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
				Toast.makeText(view.getContext(), "ע��ɹ�", Toast.LENGTH_SHORT)
						.show();
				Button button_account = (Button) view
						.findViewById(R.id.button_account);
				button_account.setText("�ѵ�¼ : " + username);
				userInfo.edit().putString("userid", username).commit();
			} else {
				Toast.makeText(view.getContext(), "ע��ʧ��\n" + "ԭ��" + result,
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

	public static String testRegisterUserDB(String id, String password) // �û�ע��
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

}