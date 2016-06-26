package com.jqd.sensorsinfo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Environment;

import com.jqd.sensorsinfo.view.CollectActivity;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-25 下午9:01:27
 * @description 存储采集的数据到系统目录下
 */
public class SaveData {

	private volatile static SaveData saveData = null;

	public static SaveData getInstance() {
		if (saveData == null) {
			synchronized (SaveData.class) {
				if (saveData == null) {
					saveData = new SaveData();
				}
			}
		}
		return saveData;
	}

	// 输入数据，存为文件
	public void save(float[] buf0, float[] buf1, float[] buf2, int index,
			String fileName, CollectActivity activity) {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File directory = new File(sdCard.getAbsolutePath()
					+ "/CIPS-DataCollect");
			directory.mkdirs();
			File file = new File(directory, fileName + "value0.txt");
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			for (int i = 0; i < index; i++) {
				osw.write(String.valueOf(buf0[i]) + " ");
			}
			osw.flush();
			osw.close();
			
			file = new File(directory, fileName + "value1.txt");
			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut);
			for (int i = 0; i < index; i++) {
				osw.write(String.valueOf(buf1[i]) + " ");
			}
			osw.flush();
			osw.close();
			
			file = new File(directory, fileName + "value2.txt");
			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut);
			for (int i = 0; i < index; i++) {
				osw.write(String.valueOf(buf2[i]) + " ");
			}
			osw.flush();
			osw.close();
			
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
}
