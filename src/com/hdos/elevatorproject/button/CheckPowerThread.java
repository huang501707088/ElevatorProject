
  package com.hdos.elevatorproject.button;
 

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class CheckPowerThread extends Thread {

	private final String mPh23Path = "/sys/class/gpio_sw/PH23/data";
	boolean isRunning = true;
	FileReader localFileReader;
	public static String PowerState = "com.hdos.elevator.powerstate.change";

	public CheckPowerThread(Context thisContext, Class nextClass,
			Handler mHandler) {
		super();
		ThisContext = thisContext;
		NextClass = nextClass;
		this.mHandler = mHandler;
	}

	private Context ThisContext;
	private Class NextClass;
	private Handler mHandler;

	int readButton() {
		int returnValue = -1;
		try {
			localFileReader = new FileReader(mPh23Path);
			returnValue = localFileReader.read();
			localFileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return -1;
		}

		return returnValue;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int value = 0;
			int lastValue = 0;
			//System.out.println("PowerState Detect Start");
			while (isRunning) {
				value = readButton();
				//System.out.println("Power Value=" + value);
				if (value != lastValue) {

					Thread.sleep(1000);
					value = readButton();
					if (value != lastValue) {// 状态已经改变了
					
						Intent intent=new Intent();
						intent.setAction(PowerState);
						if(value==Constants.PowerAdapter){
							intent.putExtra("PowerState", "Adapter");
							ThisContext.sendOrderedBroadcast(intent, null);
						}else{
							intent.putExtra("PowerState", "Battery");
							ThisContext.sendOrderedBroadcast(intent, null);
						}
						lastValue=value;
					} else {// 抖动

					}
				} else {
					lastValue = value;
				}

				Thread.sleep(200);

			}
		} catch (InterruptedException e) {
			System.out.println("ButtonThread Finish");
			isRunning = false;
			return;
		}
	}

}
