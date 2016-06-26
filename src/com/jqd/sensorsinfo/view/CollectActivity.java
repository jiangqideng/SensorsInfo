package com.jqd.sensorsinfo.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jqd.sensorsinfo.file.SaveData;
import com.jqd.sensorsinfo.model.CollectModel;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午9:04:11
 * @description 数据采集模块的activity
 */
public class CollectActivity extends Activity {

	private TextView collecTextView1;
	private TextView collecTextView2;
	private Button startButton;
	private Button stopButton;
	private Button saveButton;
	private EditText editText1;
	private boolean isStart = false;
	private float[] buf0 = new float[10000];
	private float[] buf1 = new float[10000];
	private float[] buf2 = new float[10000];
	private int index = 0;
	private CollectModel collectModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);

		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		collecTextView1 = (TextView) findViewById(R.id.collectText1);
		collecTextView2 = (TextView) findViewById(R.id.collectText2);
		editText1 = (EditText) findViewById(R.id.editText1);

		// 得到mainActivity中选择的sensor
		Intent intent = this.getIntent();
		int sensorName = intent.getIntExtra("sensorName",
				Sensor.TYPE_ACCELEROMETER);
		collectModel = new CollectModel();
		collectModel.initial(this, sensorName);

		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isStart = true;
				index = 0;
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isStart = false;
			}
		});
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				collectModel.unregisterSensors();
				String fileNameString = editText1.getText() + " ";
				SaveData.getInstance().save(buf0, buf1, buf2, index,
						fileNameString, CollectActivity.this);
				Toast.makeText(CollectActivity.this, "Saved", Toast.LENGTH_LONG)
						.show();
				index = 0;
				collectModel.registerSensors();
			}
		});

	}

	public void updateView(SensorEvent event) {
		collecTextView1.setText("  Name:\t" + event.sensor.getName()
				+ "\n  Power:\t" + String.valueOf(event.sensor.getPower())
				+ "\n  Resolution:\t"
				+ String.valueOf(event.sensor.getResolution())
				+ "\n  MinDelay:\t" + event.sensor.getMinDelay()
				+ "\n  MaximumRange:\t" + event.sensor.getMaximumRange()
				+ "\n  采集状态:\t" + isStart);
		collecTextView2.setText("");
		for (int j = 0; j < event.values.length; j++) {
			collecTextView2.setText(collecTextView2.getText() + "  values[" + j
					+ "]:\t" + String.valueOf(event.values[j]) + "\n");
		}
		if (isStart) {
			collecTextView2.setBackgroundColor(Color.BLUE);
		} else {
			collecTextView2.setBackgroundColor(Color.GRAY);
		}
		if (isStart) {
			if (index >= 10000) {
				Toast.makeText(CollectActivity.this, "缓冲区已满！",
						Toast.LENGTH_SHORT).show();
			} else {
				buf0[index] = event.values[0];
				buf1[index] = event.values[1];
				buf2[index] = event.values[2];
				index++;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collect, menu);
		return true;
	}

}
