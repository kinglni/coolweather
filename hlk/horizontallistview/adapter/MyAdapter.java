package com.hlk.horizontallistview.adapter;

import java.util.List;
import java.util.Map;




import com.example.coolweather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	
	private Context mContext ;
	private List<Map<String,Object>> mList;
	
	public MyAdapter(Context context ,List<Map<String,Object>> list){
		this.mContext = context;
		this.mList = list;//自定义用于导入环境和数据。
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}//这个应该是用于判断是不是到最后了，决定能不能更新视图。

	@Override
	public Object getItem(int position) {
		return mList.get(position);   
	}//这个应该是获取下一个数据并用于载入视图。

	@Override
	public long getItemId(int position) {
		return position;//id就是position。
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holderView = null;
		View view  = convertView;
		
		if(view == null ){
			holderView = new HolderView();
			view = LayoutInflater.from(mContext).inflate(R.layout.weather_reflash, parent, false);
			
			holderView.data =(TextView) view.findViewById(R.id.data);
			holderView.weather_img = (ImageView) view.findViewById(R.id.weather_img);
			holderView.temp_max =(TextView) view.findViewById(R.id.temp_max);
			holderView.temp_min =(TextView) view.findViewById(R.id.temp_min);
			holderView.infomation =(TextView) view.findViewById(R.id.infomation);
			
			view.setTag(holderView);
		}else{
			holderView = (HolderView) view.getTag();
		}
		
		holderView.imageView.setImageResource((Integer) mList.get(position).get("pic"));
		holderView.textView.setText((String) mList.get(position).get("page"));
		//在载入的时候写。
		return view;
	}
	
	class HolderView{
		TextView data;//日期
		ImageView weather_img;//气候图片
		TextView temp_max;
		TextView temp_min;
		TextView infomation;
	}//自定义的数据结构。
}
