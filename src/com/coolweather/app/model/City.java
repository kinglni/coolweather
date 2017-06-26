package com.coolweather.app.model;

public class City {
private int id;
private String province;
private String city;
private String county;
private String enName;
private String sEnName;
public int getId(){
	return id;
	}
public  void setId(int x){
	id=x;
}
public String getProvince(){
	return province;
	}
public  void setProvince(String p){
	province=p;
		}
public String getCity(){
	return city;
	}
public  void setCity(String c){
	city=c;
		}
public String getCounty(){
	return county;
	}
public  void setCounty(String c){ 
	county=c;
		}
public String getEnName(){
	return enName;
	}
public  void setEnName(String e){
	enName=e;
		}
public String getSEnName(){
			return sEnName;
		}
	public  void setSEnName(String se){
		sEnName=se;
			}

}
