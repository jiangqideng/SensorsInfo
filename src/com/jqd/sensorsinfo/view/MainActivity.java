package com.jqd.sensorsinfo.view;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jqd.sensorsinfo.model.SensorsModel;
import com.jqd.sensorsinfo.thirdparty.RtChartsActivity;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午7:28:58
 * @description 主界面，显示一个传感器名字的list，和当前选中的传感器的详细信息
 */
public class MainActivity extends Activity {

	private ListView listView; // 显示设备中包含的传感器列表
	private TextView data1, data6; // 分别显示当前传感器本身的信息，以及这个传感器测得的数据
	private Button button1; // 查看实时曲线
	private Button button2; // 采集数据
	private SensorsModel sensorsModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView1);
		data1 = (TextView) findViewById(R.id.data1);
		data6 = (TextView) findViewById(R.id.data6);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);

		sensorsModel = new SensorsModel();
		sensorsModel.sensorsInitial(this);
		SensorListAdapter adapter = new SensorListAdapter(this, sensorsModel.getSensors()); // 为listview设置显示内容的adapter
		listView.setAdapter(adapter);

		// listview的点击事件,用来改变一个model中的信号值sensor_num, 即当前旋转的传感器
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				sensorsModel.changeCurSensor(arg2);
			}
		});

		// Button1的点击，查看实时曲线，跳转到RtChartsActivity
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RtChartsActivity.class);
				//把当前传感器的信息通过intent直接传过去
				intent.putExtra(
						"sensorName",
						sensorsModel.getSensors()
								.get(sensorsModel.getSensor_num()).getType());
				startActivity(intent);
			}
		});
		
		// Button1的点击，采集数据，跳转到CollectActivity
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CollectActivity.class);
				intent.putExtra(
						"sensorName",
						sensorsModel.getSensors()
								.get(sensorsModel.getSensor_num()).getType());
				startActivity(intent);
			}
		});
	}

	//离开这个activity时注销传感器的使用
	protected void onPause() {
		sensorsModel.unregisterSensors();
		super.onPause();
	}

	//更新当前传感器信息和数据的显示
	public void updateData(SensorEvent event) {
		data6.setText("  Name:\t" + event.sensor.getName() + "\n  Power:\t"
				+ String.valueOf(event.sensor.getPower()) + "\n  Resolution:\t"
				+ String.valueOf(event.sensor.getResolution())
				+ "\n  MinDelay:\t" + event.sensor.getMinDelay()
				+ "\n  MaximumRange:\t" + event.sensor.getMaximumRange());
		data1.setText("");
		for (int j = 0; j < event.values.length; j++) {
			data1.setText(data1.getText() + "  values[" + j + "]:\t"
					+ String.valueOf(event.values[j]) + "\n");

		}
	}
}
