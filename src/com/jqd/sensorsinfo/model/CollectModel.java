package com.jqd.sensorsinfo.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jqd.sensorsinfo.view.CollectActivity;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午9:03:39
 * @description 采集数据模块的传感器处理
 */
public class CollectModel {
	private SensorManager sensorManager;
	private SensorEventListener listener;
	private Sensor sensor;
	private CollectActivity activity;
	
	public void initial(CollectActivity activity, int sensorName) {
		this.activity = activity;
		sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(sensorName);
		reListener();
		registerSensors();
	}
	
	public void reListener() {
		listener=new SensorEventListener() {			
			@Override
			public void onSensorChanged(SensorEvent event) {
				activity.updateView(event);
			}	
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}
	
	//传感器注销
	public void unregisterSensors() {
		sensorManager.unregisterListener(listener);
	}
	
	//传感器注册
	public void registerSensors() {
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

}
