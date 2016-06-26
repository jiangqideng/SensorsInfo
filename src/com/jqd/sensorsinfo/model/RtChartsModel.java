package com.jqd.sensorsinfo.model;

import java.util.Date;

import com.jqd.sensorsinfo.thirdparty.RtChartsActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午8:27:36
 * @description 获取传感器数据，简单处理，然后每次要更新的时候开一个线程去更新图表
 */
public class RtChartsModel {

	private Handler handler;
	private SensorManager sensorManager;
	private Sensor sensor;
	private int ADD = 10;// 每次更新图表加入的数据个数,决定异步线程中的工作量.必须保证没新建一个线程时之前那个已经完成
	private int add_j = 0;
	private int[] addY = new int[ADD];
	private long[] addX = new long[ADD];
	private int valueNumber = 0;
	private RtChartsActivity activity;

	public void initial(final RtChartsActivity activity, int sensorName) {
		this.activity = activity;
		sensorManager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(sensorName);
		activity.updateTitle("传感器型号: " + sensor.getName() + "\n" + "value[" + valueNumber + "]:");
		handler = new Handler() {
			public void handleMessage(Message msg) {
				// 刷新图表
				activity.updateChart();
				super.handleMessage(msg);
			}
		};
	}

	// 传感器注册
	public void registerSensors() {
		sensorManager.registerListener(new SensorEventListener() {
			@Override
			public void onSensorChanged(final SensorEvent event) {
				// 新建一个线程，这里面执行和UI相关的一些事情
				new Thread(new Runnable() {
					@Override
					public void run() {
						addX[add_j] = new Date().getTime();
						addY[add_j] = (int) (event.values[valueNumber] * 5 - 50);
						add_j++;
						if (add_j >= ADD) {
							add_j = 0;
							Message message = new Message();
							// message.what = 200;
							handler.sendMessage(message);
						}
					}
				}).start();
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void changeValueNumber() {
		valueNumber++;
		if (valueNumber >= 3) {
			valueNumber = 0;
		}
		activity.updateTitle("传感器型号: " + sensor.getName() + "\n" + "value[" + valueNumber + "]:");
	}
	
	public int getADD() {
		return ADD;
	}

	public int getAdd_j() {
		return add_j;
	}

	public int[] getAddY() {
		return addY;
	}

	public long[] getAddX() {
		return addX;
	}
}
