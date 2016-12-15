package com.hdos.elevatorproject.button;

import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;



public class CheckButtonThread extends Thread {


		private final String mPh22Path = "/sys/class/gpio_sw/PH22/data";
		boolean isRunning = true;
		FileReader localFileReader;
	//	public static String BtnShortPress="com.hdos.elevator.buttondetect.short";
	//	public static String BtnLongPress="com.hdos.elevator.buttondetect.long";
		public static String BtnPress="com.hdos.elevator.buttondetect.press";
		public CheckButtonThread(Context thisContext, Class nextClass,
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
				localFileReader = new FileReader(mPh22Path);
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
			int value = 0;
			int lastValue=0;
			System.out.println("Button Detect Start");
			while (isRunning) {
				value = readButton();
				//System.out.println("GPIO Value=" + value);
				if (value == Constants.ButtonOn && lastValue== Constants.ButtonOff) {
					try {

						Thread.sleep(10);
						value = readButton();
						if (value ==  Constants.ButtonOn) {			
							lastValue =  Constants.ButtonOn;
							long startTime=System.currentTimeMillis();
							long endTime=startTime;
							do{
								value=readButton();
								 endTime=System.currentTimeMillis();
								 Thread.sleep(50);
								 if(endTime-startTime>=3000){
										Intent intent =new Intent();
										intent.setAction(BtnPress);
										intent.putExtra("Duration", "long");
										ThisContext.sendOrderedBroadcast(intent, null);
										break;
									}
							}while(value==Constants.ButtonOn);
						
							Intent intent =new Intent();
							intent.setAction(BtnPress);
							intent.putExtra("Duration", "short");
							ThisContext.sendOrderedBroadcast(intent, null);
							
						
							
						} else {//抖动
								
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}else{
					lastValue=value;
				}

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					System.out.println("ButtonThread Finish");
					isRunning = false;
					return;
				}

			}

		}
	
}
