package com.jqd.sensorsinfo.thirdparty;

import java.util.Date;
import java.util.Random;
import java.util.Timer;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jqd.sensorsinfo.model.RtChartsModel;
import com.jqd.sensorsinfo.view.MainActivity;
import com.jqd.sensorsinfo.view.R;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午8:25:36
 * @description 改了改别人的代码，使用这个绘制图标的包
 */
public class RtChartsActivity extends Activity {

	private RtChartsModel rtChartsModel;
	private Timer timer = new Timer();
	private GraphicalView chart;
	private Button goBackButton;
	private Button changeNumberButton;
	private TextView rtInfoTitle;

	// 曲线数量
	private static final int SERIES_NR = 1;
	private static final String TAG = "message";
	private TimeSeries series1;
	private XYMultipleSeriesDataset dataset1;
	final private int LENGTH = 500;// 图表中总共显示的数据个数

	// 需要显示的时间数据
	Date[] xcache = new Date[LENGTH];
	/** 数据 */
	int[] ycache = new int[LENGTH];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rt_charts);
		
		rtInfoTitle = (TextView) findViewById(R.id.myview);
		// 得到mainActivity中选择的sensor
		Intent intent = this.getIntent();
		int sensorName = intent.getIntExtra("sensorName",
				Sensor.TYPE_ACCELEROMETER);
		rtChartsModel = new RtChartsModel();
		rtChartsModel.initial(RtChartsActivity.this, sensorName);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
		// 生成图表
		chart = ChartFactory.getTimeChartView(this, getDateDemoDataset(),
				getDemoRenderer(), "hh:mm:ss");
		layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT, 380));

		//更改value维度
		changeNumberButton = (Button) findViewById(R.id.changeNnmberButton);
		changeNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rtChartsModel.changeValueNumber();
			}
		});
		
		// 点击时间，返回
		goBackButton = (Button) findViewById(R.id.goBackButton);
		goBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(RtChartsActivity.this, "ceshiview", 1).show();
				Intent intent = new Intent();
				intent.setClass(RtChartsActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		rtChartsModel.registerSensors();
	}

	// 设定图表样式
	private XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setChartTitle("实时曲线");// 标题
		renderer.setChartTitleTextSize(20);
		renderer.setXTitle("时间"); // x轴说明
		renderer.setAxisTitleTextSize(16);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsTextSize(15); // 数轴刻度字体大小
		renderer.setLabelsColor(Color.BLACK);
		renderer.setLegendTextSize(15); // 曲线说明
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setShowLegend(false);
		renderer.setMargins(new int[] { 20, 30, 100, 0 });// 图表位置 上 左 下 右
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		r.setChartValuesTextSize(15);
		r.setChartValuesSpacing(3);
		r.setPointStyle(PointStyle.POINT);
		r.setFillBelowLine(true);
		r.setFillBelowLineColor(Color.WHITE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setPanEnabled(false, false);
		renderer.setShowGrid(true);
		renderer.setYAxisMax(50);
		renderer.setYAxisMin(-30);
		renderer.setInScroll(true); // 调整大小
		return renderer;
	}

	public void updateTitle(String titleString) {
		rtInfoTitle.setText(titleString);
	}
	
	public void updateChart() {
		int length = series1.getItemCount();
		if (length >= LENGTH)
			length = LENGTH;
		// 将前面的点放入缓存
		for (int i = 0; i < length; i++) {
			xcache[i] = new Date((long) series1.getX(i));
			ycache[i] = (int) series1.getY(i);
		}
		series1.clear();
		for (int j = rtChartsModel.getADD() - 1; j >= 0; j--) {
			series1.add(new Date(rtChartsModel.getAddX()[j]),
					rtChartsModel.getAddY()[j]);
		}
		for (int k = 0; k < length; k++) {
			series1.add(xcache[k], ycache[k]);
		}
		// 在数据集中添加新的点集
		dataset1.removeSeries(series1);
		dataset1.addSeries(series1);
		// 曲线更新
		chart.invalidate();
	}

	// 数据对象
	private XYMultipleSeriesDataset getDateDemoDataset() {
		dataset1 = new XYMultipleSeriesDataset();
		final int nr = LENGTH + 1;
		long value = new Date().getTime();
		Random r = new Random();
		// 常量SERIES_NR为曲线的条数，自行设置。
		for (int i = 0; i < SERIES_NR; i++) {
			series1 = new TimeSeries("Demo series " + (i + 1));
			for (int k = 0; k < nr; k++) {
				// 这里设置初始的曲线，nr是初始数据的个数，都给他设置为0了。
				series1.add(new Date(value - k * 100), 0);// 原来的代码中这里写成value+k*1000了，怪怪的
			}
			dataset1.addSeries(series1);
		}
		Log.i(TAG, dataset1.toString());
		return dataset1;
	}

	@Override
	public void onDestroy() {
		// 当结束程序时关掉Timer
		timer.cancel();
		super.onDestroy();
	};
}
