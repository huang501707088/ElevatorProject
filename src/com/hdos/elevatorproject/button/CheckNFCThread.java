
  package com.hdos.elevatorproject.button;
 

import java.io.FileReader;
import java.io.IOException;

import com.hdos.common.DebugTools;
import com.huada.serialcommunication.SerialCommunication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class CheckNFCThread extends Thread {

	private final String mPh23Path = "/sys/class/gpio_sw/PH23/data";
	boolean isRunning = true;
	FileReader localFileReader;
	public static String NFCDetach = "com.hdos.elevator.nfc.nfcdetach";

	public CheckNFCThread(Context thisContext, Class nextClass,
			Handler mHandler) {
		super();
		ThisContext = thisContext;
		NextClass = nextClass;
		this.mHandler = mHandler;
		serialCommunication=new SerialCommunication();
	}

	private Context ThisContext;
	private Class NextClass;
	private Handler mHandler;
	private SerialCommunication serialCommunication;
	private String port="/dev/ttyS5";

	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int value = 0;
			int lastValue = 0;
			byte[] response=new byte[2014];
			System.out.println("NFC Detect Start");
			while (isRunning) {
				value=0;
				value =  serialCommunication.getSignin(port,response);
				//System.out.println("GPIO Value=" + value);
				if (value >0) {//刷卡成功

					
					
						Intent intent=new Intent();
						intent.setAction(NFCDetach);
							intent.putExtra("CardNo", DebugTools.byte2Hex(response,0,value));
							ThisContext.sendOrderedBroadcast(intent, null);
			
				} 

				Thread.sleep(300);

			}
		} catch (InterruptedException e) {
			System.out.println("CheckNFCThread Finish");
			isRunning = false;
			return;
		}
	}

}
