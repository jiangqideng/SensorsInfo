package com.example.test_sensor;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private SensorManager sensorManager;
	private List<Sensor> sensors;
	private ListView listView;
	private TextView data1,data6;
	private int sensor_num = -1;
	private SensorEventListener listener;
	private Button button1;
	private Button button2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listView = (ListView) findViewById(R.id.listView1);
		data1 = (TextView) findViewById(R.id.data1);
		data6 = (TextView) findViewById(R.id.data6);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		
		sensors  =  sensorManager.getSensorList(Sensor.TYPE_ALL);
		MyAdapter adapter = new MyAdapter(this, sensors);	
		listView.setAdapter(adapter);

		if (sensor_num==-1) {
			sensor_num=0;
			listener=new SensorEventListener() {			
				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub	
						data6.setText(
								"  Name:\t" + event.sensor.getName()
								+"\n  Power:\t" + String.valueOf(event.sensor.getPower())
								+"\n  Resolution:\t" + String.valueOf(event.sensor.getResolution())
								+"\n  MinDelay:\t" + event.sensor.getMinDelay()
								+"\n  MaximumRange:\t" + event.sensor.getMaximumRange()
								);
						data1.setText("");
						for (int j = 0; j < event.values.length; j++) {
							data1.setText(data1.getText()+"  values["+j+"]:\t"+String.valueOf(event.values[j])+"\n");
	
						}
				}	
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
				}
			};
			sensorManager.registerListener(listener, sensors.get(sensor_num), SensorManager.SENSOR_DELAY_NORMAL);
		}
		//下面为listview的点击事件,用来改变一个信号值sensor_num
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				sensorManager.unregisterListener(listener);
				sensor_num=arg2;
				listener=new SensorEventListener() {			
					@Override
					public void onSensorChanged(SensorEvent event) {
						// TODO Auto-generated method stub	
							data6.setText(
									"  Name:\t" + event.sensor.getName()
									+"\n  Power:\t" + String.valueOf(event.sensor.getPower())
									+"\n  Resolution:\t" + String.valueOf(event.sensor.getResolution())
									+"\n  MinDelay:\t" + event.sensor.getMinDelay()
									+"\n  MaximumRange:\t" + event.sensor.getMaximumRange()
									);
							data1.setText("");
							for (int j = 0; j < event.values.length; j++) {
								data1.setText(data1.getText()+"  values["+j+"]:\t"+String.valueOf(event.values[j])+"\n");

							}
					}	
					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						// TODO Auto-generated method stub
					}
				};
				sensorManager.registerListener(listener, sensors.get(sensor_num), SensorManager.SENSOR_DELAY_NORMAL);
			}
		});
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RtChartsActivity.class);
				intent.putExtra("sensorName", sensors.get(sensor_num).getType());
				
				startActivity(intent);
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CollectActivity.class);
				intent.putExtra("sensorName", sensors.get(sensor_num).getType());
				startActivity(intent);
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		sensorManager.unregisterListener(listener);
		super.onPause();
	}
	
	

}
