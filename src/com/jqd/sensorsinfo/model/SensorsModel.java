package com.jqd.sensorsinfo.model;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jqd.sensorsinfo.view.MainActivity;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午7:38:17
 * @description 控制传感器。注册、注销，获取响应事件
 */
public class SensorsModel {
	private SensorManager sensorManager;
	private List<Sensor> sensors; //记录设备中所包含的传感器
	private SensorEventListener listener;
	private int sensor_num; //当前传感器在列表中的位置
	private MainActivity activity;

	//初始
	public void sensorsInitial(final MainActivity aActivity) {
		this.activity = aActivity;
		sensorManager = (SensorManager) aActivity
				.getSystemService(Context.SENSOR_SERVICE);
		sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		sensor_num = 0;
		listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				aActivity.updateData(event);
			}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		registerSensors();
	}

	//改变当前传感器
	public void changeCurSensor(int newIdx) {
		unregisterSensors();
		sensor_num = newIdx;
		listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				activity.updateData(event);
			}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		registerSensors();
	}

	//传感器注册
	public void registerSensors() {
		sensorManager.registerListener(listener, sensors.get(sensor_num),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	//传感器注销
	public void unregisterSensors() {
		sensorManager.unregisterListener(listener);
	}

	//返回Sensor的列表
	public List<Sensor> getSensors() {
		return sensors;
	}

	//返回当前传感器的位置
	public int getSensor_num() {
		return sensor_num;
	}
}
