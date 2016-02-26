package com.example.test_sensor;

import java.util.List;
import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{
	
	private Context context;
	
//	private String[] ss;
	private List<Sensor> list;

//	public MyAdapter(Context context, String[] ss) {
//		super();
//		this.context = context;
//		this.ss = ss;
//	}

	public MyAdapter(MainActivity context, List<Sensor> list) {
		// TODO Auto-generated constructor stub
		super();
		this.context = context;
		this.list = list;
	}

	//getcount  获取数据的个数
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	//getView  需要构建一个View对象来显示数据源中的数据
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
			Sensor sensor = list.get(position);
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup group = (ViewGroup) inflater.inflate(R.layout.sensors_name, null);
			TextView textView1 = (TextView) group.findViewById(R.id.textView1);
			TextView textView2 = (TextView) group.findViewById(R.id.textView2);
			
			textView1.setText(String.valueOf(position+1));
			textView2.setText(sensor.getName());
			
		return group;
	}

	
	
}