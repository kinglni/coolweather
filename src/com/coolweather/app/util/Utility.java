package com.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Province;

public class Utility {
	
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			try {
				JSONArray ja=new JSONArray(response);
				for(int i=0;i<ja.length();i++){
					City city=new City();
					JSONObject ob=ja.getJSONObject(i);
					city.setId(ob.getInt("ID"));
					city.setProvince(ob.getString("province"));
					city.setCity(ob.getString("city"));
					city.setCounty(ob.getString("county"));
					city.setEnName(ob.getString("EnName"));
					city.setSEnName(ob.getString("SimplyEnName"));
                    coolWeatherDB.SaveCity(city);
									}
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
				return false;
			}
			
		}
		
		return false;
	}
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			try {
				JSONArray ja=new JSONArray(response);
				for(int i=0;i<ja.length();i++){
					Province province=new Province();
					JSONObject ob=ja.getJSONObject(i);
					province.setId(ob.getInt("ID"));
					province.setProvince(ob.getString("province"));
							
					coolWeatherDB.SaveProvince(province);
									}
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
				return false;
			}
			
		}
		
		return false;
	}
}
