package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
public static final String DB_Name="cool_weather";
public static final int VERSION=1;
private SQLiteDatabase db;
private static CoolWeatherDB coolWeatherDB;

private CoolWeatherDB(Context context){
	CoolWeatherOpenHelper dbHelper=new 
			CoolWeatherOpenHelper(context,DB_Name,null,VERSION);
	db=dbHelper.getWritableDatabase() ;
	
}

public synchronized static CoolWeatherDB getInstance(Context context){
	if(coolWeatherDB==null){
		coolWeatherDB= new CoolWeatherDB(context);
	}
	
	return coolWeatherDB;
	
}
public void SaveCity(City city){
	if(city!=null){
		ContentValues cv=new ContentValues();
		//cv.put("id",city.getId());这句话会引起 constraint failed
		cv.put("province",city.getProvince());
		cv.put("city",city.getCity());
		cv.put("county",city.getCounty());
		cv.put("enName",city.getEnName());
		cv.put("SEnName",city.getSEnName());
		db.insert("city",null,cv);
		
	}
	
}

public void SaveProvince(Province province){
	if(province!=null){
		ContentValues cv=new ContentValues();
		//cv.put("id",province.getId());这句话会引起 constraint failed
		cv.put("province",province.getProvince());

		db.insert("province",null,cv);
			}
	}
public List<Province> loadProvince(){
	
	List<Province> list=new ArrayList<Province>();
	Cursor cursor=db.query("province",null,null,null,null,null,null);
	if(cursor.moveToFirst()){
		do{
			Province province=new Province();
		province.setId(cursor.getInt(cursor.getColumnIndex("id")));
		System.out.println(cursor.getInt(cursor.getColumnIndex("id")));
		province.setProvince(cursor.getString(cursor.getColumnIndex("province")));
		list.add(province);
		}while(cursor.moveToNext());
	}
		return list;
	}

public List<City> loadCity(String provinceName){
	
	List<City> list=new ArrayList<City>();
	Cursor cursor=db.query("city", null, "province=?",new String[]{String.valueOf(provinceName)},null,null,null);
	if(cursor.moveToFirst()){
		do{City city=new City();
		city.setId(cursor.getInt(cursor.getColumnIndex("id")));
		city.setProvince(cursor.getString(cursor.getColumnIndex("province")));
		city.setCity(cursor.getString(cursor.getColumnIndex("city")));
		city.setCounty(cursor.getString(cursor.getColumnIndex("county")));
		city.setEnName(cursor.getString(cursor.getColumnIndex("enName")));
		city.setSEnName(cursor.getString(cursor.getColumnIndex("sEnName")));
		list.add(city);
		}while(cursor.moveToNext());
	}
		return list;
	}

}
