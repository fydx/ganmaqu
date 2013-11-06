package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class decodeJson {
	private String jsonString;
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	
	public decodeJson(String jsonString) throws JSONException {

		this.jsonString = jsonString;
		//jsonObject = new JSONObject(jsonString);
		jsonArray= new JSONArray(jsonString);
	}
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public String getTop() throws JSONException
	{
		
		return jsonArray.getJSONObject(0).toString();
	}
	public List<place> JsonToPlaceList(JSONArray jsonArray)
	{
		
		System.out.println("success 1");
		List<place> places = new  ArrayList<place>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				System.out.println(String.valueOf(i));
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				place temp_place = new place();
				temp_place = JsonobTOPlace(jsonObject);
				System.out.println(temp_place.toString());
				places.add(temp_place);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return places;
		
	}
	public static place JsonobTOPlace(JSONObject jsonObject) throws JSONException
	{
		place temp_place = new place();
		temp_place.setAddress(jsonObject.getString("address"));
		temp_place.setCost(jsonObject.getInt("cost"));
		temp_place.setDetailType(jsonObject.getString("detailType"));
		temp_place.setId(jsonObject.getInt("id"));
		temp_place.setMainType(jsonObject.getString("mainType"));
		temp_place.setPos_x(jsonObject.getDouble("pos_x"));
		temp_place.setPos_y(jsonObject.getDouble("pos_y"));
		temp_place.setRate(jsonObject.getInt("rate"));
		temp_place.setShopName(jsonObject.getString("shopName"));
		temp_place.setSuitType(jsonObject.getString("suitType"));
		temp_place.setTelNumber(jsonObject.getString("telNumber"));
		temp_place.setTime(jsonObject.getString("time"));
		temp_place.setPicUrl(jsonObject.getString("url"));
		return temp_place;
	}
}
