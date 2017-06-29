package com.coolweather.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;//默认初始化值为0（LEVEL_PROVINCE）；
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		queryProvinces();
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {if(currentLevel==LEVEL_PROVINCE){
						selectedProvince=provinceList.get(index);
						queryCities();
					}else if(currentLevel==LEVEL_CITY){
						selectedCity=cityList.get(index);
						queryProvinces();
						
					}
				// TODO Auto-generated method stub
				
			}

		});
	}

	protected void queryCities() {
		// TODO Auto-generated method stub
		cityList=coolWeatherDB.loadCity(selectedProvince.getProvince());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCity());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvince());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvince(),"city");
		}
				
	}
	protected void queryProvinces() {
		// TODO Auto-generated method stub
		
		provinceList=coolWeatherDB.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvince());
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText("中国");
				currentLevel=LEVEL_PROVINCE;
			}
		}else{
		queryFromServer(null,"province");
	}
	
}
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(currentLevel==LEVEL_PROVINCE){
			address="http://10.0.2.2/all_city.json";}else{
				address="http://10.0.2.2/province.json";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
			@Override
			public void onFinish(String response){
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvinceResponse(coolWeatherDB,response);
				}else if("city".equals(type)){
					result=Utility.handleCityResponse(coolWeatherDB,response);
				}
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",Toast.LENGTH_SHORT);
					}
				});
			}
		});	}
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog(){
		// TODO Auto-generated method stub
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed(){
		if(currentLevel==LEVEL_CITY){
			queryCities();
		}else{
			finish();
		}
	}
}
//test