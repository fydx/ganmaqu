package com.sssta.ganmaqu;

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
	
	public String getTop() throws JSONException
	{
		
		return jsonArray.getJSONObject(0).toString();
	}
	
}
