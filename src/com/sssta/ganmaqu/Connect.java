package com.sssta.ganmaqu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.util.Log;

public final class Connect {
	private  String ipString;
	public Connect (String ip)
	{
		this.ipString = ip;
	}
	public String getIpString() {
		return ipString;
	}
	public void setIpString(String ipString) {
		this.ipString = ipString;
	}
	/**
	 * �õ�һ����·��
	 * @param typeString ���� ������/����/���£�
	 * @param pos_x ��������
	 * @param pos_y γ������
	 * @param userid �û�id
	 * @param circle ��Ȧ
	 * @param cityString ����
	 * @return json��ʽ һ��·��
	 * @throws JSONException
	 */
	public  String GetFullRoute(String typeString, double pos_x,
			double pos_y, String userid, String circle, String cityString)
			throws JSONException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			// getApplicationContext().getMainLooper();
			// Looper.prepare();

			HttpHost target = new HttpHost(ipString, 8080, "http");
			// String request="/?type=���³���&pos_x=108.947039&pos_y=34.259203";
			String request = "/?command=full&type=" + typeString + "&city="
					+ cityString + "&id=" + userid + "&circleName=" + circle;
			Log.i("request string", request);
			HttpGet req = new HttpGet(request);
			// System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			line = br.readLine();
			if (line != null) {

				System.out.println(line);
				return line;

			} else {
				System.out.println("line is null");
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;

	}
     /**
      * ����ٶȵ�ͼAPI �õ���ǰλ��
      * @param pos_x_add ��������
      * @param pos_y_add γ������
      * @return json�ַ��� 
      */
	public String GetCurrentAddress(String pos_x_add, String pos_y_add) {
		if (pos_x_add == null || pos_y_add == null) {
			return null;
		}
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {

			HttpHost target = new HttpHost("api.map.baidu.com", 80, "http");
			// String request="/?type=���³���&pos_x=108.947039&pos_y=34.259203";
			String request = "/geocoder?output=json&location=" + pos_x_add
					+ "," + pos_y_add + "&key=APP_KEY";
			// Log.i("request string",request);
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuilder output = new StringBuilder();
			// line = br.readLine();
			while ((line = br.readLine()) != null) {
				output.append(line);

			}
			System.out.println(output);
			return output.toString();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;

	}
	/**
	 * ���Ͳ�ϲ�������͵�������
	 * @param json  json�ַ���   item1: xxx ,item2:xxx ...
	 * @return success or exception 
	 */
	public String PostDislike(String json)
	{
		try
		{
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://" + ipString+ ":8080/");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("command", "setdislike"));
			params.add(new BasicNameValuePair("item", json));
			UrlEncodedFormEntity encodedValues = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(encodedValues);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			/*int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			StringBuilder responseString = new StringBuilder();
			responseString.append("Status Code: ");
			responseString.append(httpStatusCode);
			responseString.append("\nResponse:\n");
			responseString.append(httpResponse.getStatusLine().getReasonPhrase().toString() + "\n");
			System.out.println(responseString);
			if (httpStatusCode < 200 || httpStatusCode > 299)
			{
				throw new Exception("Error posting to URL: " + "http://127.0.0.1:8080/" + " due to " + httpResponse.getStatusLine().getReasonPhrase());
			}*/
			HttpEntity entity = httpResponse.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	
		return "EXCEPTION IN POST DISLIKE";
	}
	/**
	 * �õ����û���ϲ��������
	 * @param id �û�id
	 * @return json��ʽ�ַ���
	 */
	public  String GetDislike(String id)
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getdislike&id=" + id;
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		return "EXCETION IN GETDISLIKE";
	}
	/**
	 * �õ�һ�����ݳ޵�·��
	 * @param type
	 * @param pos_x
	 * @param pos_y
	 * @param cost
	 * @param cityString
	 * @return ·�� json�ַ��� 
	 */
	public String GetUpper(String type, String pos_x, String pos_y,
			String cost,String cityString) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");

			String request = "/?command=upper&type=" + type + "&pos_x=" + pos_x
					+ "&pos_y=" + pos_y + "&cost=" + cost + "&city="
							+ cityString;
			Log.i("changeRequest", request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * �õ�һ�������˵�·��
	 * @param type
	 * @param pos_x
	 * @param pos_y
	 * @param cost
	 * @param cityString
	 * @return
	 */
	public String GetLower(String type, String pos_x, String pos_y,
			String cost,String cityString) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");

			String request = "/?command=lower&type=" + type + "&pos_x=" + pos_x
					+ "&pos_y=" + pos_y + "&cost=" + cost + "&city="
							+ cityString;
			Log.i("changeRequest", request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * �����û�ȥ���ĵص�
	 * @param shopId
	 * @param userId
	 * @return success or exception
	 */
	public String SendArrive(String shopId, String userId) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=arrive&shopId=" + shopId + "&userId="
					+ userId;
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
		return null;

	}
	/**
	 * �����û�ɾ�����ĵص�
	 * @param shopId
	 * @param userId
	 * @return success or exception
	 */
	public String SendDelete(String shopId, String userId) {

		Log.i("shopId + userId", shopId + " " + userId);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=delete&shopId=" + shopId + "&userId="
					+ userId;
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				return line;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * ����һ���������߸�Զ��·��
	 * @param pos_x
	 * @param pos_y
	 * @param shop_x
	 * @param shop_y
	 * @param change
	 * @param cost
	 * @param id
	 * @param type �ľ��룬Զһ��changeΪ1����Ϊ-1
	 * @return
	 */
	public String GetDisRoute(double pos_x, double pos_y, double shop_x,
			double shop_y, int change, int cost, String id, String type) 
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=changedis&id=" + id + "&pos_x=" + pos_x
					+ "&pos_y=" + pos_y + "&shop_x=" + shop_x + "&shop_y="
					+ shop_y + "&dis=" + change + "&cost=" + cost + "&type="
					+ type;
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
		return null;
	}
	/**
	 * �õ������Ȧ
	 * @param pos_x
	 * @param pos_y
	 * @param city
	 * @return �����ȦString
	 */
	public  String GetShopCircle(double pos_x, double pos_y, String city) // �����꣬���������Ȧ����
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getshopcircle&city=" + city + "&pos_x="
					+ pos_x + "&pos_y=" + pos_y;
			System.out.println(request);
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

		return null;
	}
	/**
	 * ���ĳ�����е���Ȧ�б�
	 * @param city
	 * @return item1,2,3....
	 */
	public String GetCircleList(String city) 
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getcirclelist&city=" + city + "";
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				Log.i("return circles line", line);
				return line;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * �õ�����֧�ֵĳ���
	 * @return
	 */
	public String GetAvailableCity()
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=getavailablecity";
			HttpGet req = new HttpGet(request);
			System.out.println("executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				return line;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * �õ������ĵ����ص�
	 * @param type
	 * @param pos_x
	 * @param pos_y
	 * @param time
	 * @param shopname
	 * @param cost
	 * @param citysString
	 * @return json
	 */
	public String GetChangeSingle(String type, String pos_x,
			String pos_y, String time, String shopname, String cost,String citysString) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpHost target = new HttpHost(ipString, 8080, "http");
			String request = "/?command=change&type=" + type + "&pos_x="
					+ pos_x + "&pos_y=" + pos_y + "&time=" + time
					+ "&shopName=" + shopname + "&cost=" + cost + "&city="
					+ citysString;
			Log.i("changeRequest", request);
			HttpGet req = new HttpGet(request);
			Log.i("excute", "executing request to " + target);
			HttpResponse rsp = httpclient.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				return line;
			}
			// System.out.println(entity.getContent());
			/*
			 * System.out.println("----------------------------------------");
			 * System.out.println(rsp.getStatusLine()); Header[] headers =
			 * rsp.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
			 * System.out.println(headers[i]); }
			 * System.out.println("----------------------------------------");
			 * BufferedWriter fout = new BufferedWriter(new
			 * FileWriter("E:\\JavaProject\\output.txt")); if (entity != null) {
			 * // System.out.println(EntityUtils.toString(entity));
			 * fout.write(EntityUtils.toString(entity)); fout.newLine(); }
			 * fout.flush(); fout.close();
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * ��֤�˻�����
	 * @param id
	 * @param password
	 * @return
	 */
	public  String AuthLogin(String id, String password) {
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
	/**
	 * �û�ע��
	 * @param id
	 * @param password
	 * @return
	 */
	public  String RegUser(String id, String password) // �û�ע��
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
