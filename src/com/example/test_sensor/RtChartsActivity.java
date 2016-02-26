package com.example.test_sensor;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RtChartsActivity extends Activity {
	
	private Timer timer = new Timer();
    private GraphicalView chart;
    private Button goBackButton;
    private TimerTask task;
    private int ADD = 10;//每次更新图表加入的数据个数,决定异步线程中的工作量.必须保证没新建一个线程时之前那个已经完成
    private long X;
    private int Y;
    private int add_j=0;
    private int [] addY = new int[ADD];
	private long [] addX = new long[ADD];
	/**曲线数量*/
    private static final int SERIES_NR=1;
    private static final String TAG = "message";
    private TimeSeries series1;
    private XYMultipleSeriesDataset dataset1;
    private Handler handler;
    private Random random=new Random();
    final private int LENGTH=500;//图表中总共显示的数据个数
    
    /**时间数据*/
    Date[] xcache = new Date[LENGTH];
	/**数据*/
    int[] ycache = new int[LENGTH];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rt_charts);
		
		//得到mainActivity中选择的sensor
		Intent intent = this.getIntent();
		int sensorName = intent.getIntExtra("sensorName", Sensor.TYPE_ACCELEROMETER);
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(sensorName);
		
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        //生成图表
		chart = ChartFactory.getTimeChartView(this, getDateDemoDataset(), getDemoRenderer(), "hh:mm:ss");
		layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,380));
		//为TextView添加事件
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
		
		sensorManager.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(final SensorEvent event) {
				//新建一个线程，去执行update的非UI操作	
				new Thread(new Runnable() {
					@Override
					public void run() {
						addX[add_j]=new Date().getTime();
						addY[add_j]=(int) (event.values[2]*5-50);
						add_j++;
						if (add_j>=ADD) {
							add_j=0;
							Message message = new Message();
	//		        	    message.what = 200;
			        	    handler.sendMessage(message);
						}
					}
				}).start();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		}, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		
//据观察： NORMAL约3Hz  UI约9Hz  	GAME约25Hz  FASTEST约50Hz
		handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		//刷新图表 		
        		updateChart();
        		super.handleMessage(msg);
        	}
        };
//        task = new TimerTask() {
//        	@Override
//        	public void run() {
//        		Message message = new Message();
////        	    message.what = 200;
//        	    handler.sendMessage(message);
//        	}
//        };
////        timer.schedule(task, 2*1000,1000);//两秒之后开始执行，每秒执行一次
//        timer.schedule(task, 1000, 200);
        
//        //再加一级异步缓存
//        new Thread(new Runnable() {
//			@Override
//			public void run() {
//				long date_temp = 0;
//				while (true) {
//					if (date_temp!=addX[0]) {
//						date_temp=addX[0];
//						
//
//					}
//				}
//			}
//		}).start();
        
        
        
	}
	
		private void updateChart() {
		    int length = series1.getItemCount();
		    if(length>=LENGTH) length = LENGTH;
		    //将前面的点放入缓存
			for (int i = 0; i < length; i++) {
				xcache[i] =  new Date((long)series1.getX(i));
				ycache[i] = (int) series1.getY(i);
			}
		    
			series1.clear();
			for (int j = ADD-1; j >=0; j--) {
				series1.add(new Date(addX[j]), addY[j]);
			}
			for (int k = 0; k < length; k++) {
	    		series1.add(xcache[k], ycache[k]);
	    	}
			//在数据集中添加新的点集
			dataset1.removeSeries(series1);
			dataset1.addSeries(series1);
			
			
			//曲线更新
			chart.invalidate();
		}
	   
	/**
	 * 设定如表样式
	 * @return
	 */
	   private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setChartTitle("实时曲线");//标题
		    renderer.setChartTitleTextSize(20);
		    renderer.setXTitle("时间");    //x轴说明
		    renderer.setAxisTitleTextSize(16);
		    renderer.setAxesColor(Color.BLACK);
		    renderer.setLabelsTextSize(15);    //数轴刻度字体大小
		    renderer.setLabelsColor(Color.BLACK);
		    renderer.setLegendTextSize(15);    //曲线说明
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0,Color.BLACK);
		    renderer.setShowLegend(false);
		    renderer.setMargins(new int[] {20, 30, 100, 0});//图表位置 上 左 下 右
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
		    renderer.setPanEnabled(false,false);
		    renderer.setShowGrid(true);
		    renderer.setYAxisMax(50);
		    renderer.setYAxisMin(-30);
		    renderer.setInScroll(true);  //调整大小
		    return renderer;
		  }
	   /**
	    * 数据对象
	    * @return
	    */
	   private XYMultipleSeriesDataset getDateDemoDataset() {
		    dataset1 = new XYMultipleSeriesDataset();
		    final int nr = LENGTH+1;
		    long value = new Date().getTime();
		    Random r = new Random();
//常量SERIES_NR为曲线的条数，自行设置。		    
		    for (int i = 0; i < SERIES_NR; i++) {
		      series1 = new TimeSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
//这里设置初始的曲线，nr是初始数据的个数，都给他设置为0了。
		        series1.add(new Date(value-k*100), 0);//原来的代码中这里写成value+k*1000了，怪怪的
		      }
		      dataset1.addSeries(series1);
		    }
		    Log.i(TAG, dataset1.toString());
		    return dataset1;
		  }
	    @Override
	    public void onDestroy() {
	    	//当结束程序时关掉Timer
	    	timer.cancel();
	    	super.onDestroy();
	    };
}
