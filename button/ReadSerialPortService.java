package com.hdos.elevatorproject.button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Locale;

import com.example.testport.GpioMonitorThread;
import com.example.testport.http.PostHelper;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;
import android_serialport_api.SerialPort;

/**
 * 后台持续运行读取串口数据服务,监听供电状态变化，紧急按键
 * 
 * 当前串口配置为[9600,N,8,1]，请从PC或其它设备往本机串口(5V,RX,TX,GND)发送数据
 * 
 * @author Jack
 *
 */
public class ReadSerialPortService extends Service {

	protected Application mApplication;

	protected SerialPort mSerialPort;

	protected OutputStream mOutputStream;

	private InputStream mInputStream;

	private ReadThread mReadThread;

	private Handler mHandler = new Handler();

	private GpioMonitorThread mGpioMonitorThread;

	private CheckButtonThread checkButtonThread=null;
	private mBroadcastReceiver myBroadcastReceiver=null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

//		try {
//
//			mSerialPort = new SerialPort(new File("/dev/ttyS5"), 9600, 0);
//			mOutputStream = mSerialPort.getOutputStream();
//			mInputStream = mSerialPort.getInputStream();
//
//			/* Create a receiving thread */
//			mReadThread = new ReadThread();
//			mReadThread.start();
//			mGpioMonitorThread = new GpioMonitorThread(getApplicationContext());
//			mGpioMonitorThread.start();
//		} catch (SecurityException e) {
//			DisplayError(R.string.error_security);
//		} catch (IOException e) {
//			DisplayError(R.string.error_unknown);
//		} catch (InvalidParameterException e) {
//			DisplayError(R.string.error_configuration);
//		} catch (UnsatisfiedLinkError error) {
//			// TODO jni异常，由于编译库文件原因，手机上会有此异常
//		}
		myBroadcastReceiver= new mBroadcastReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(CheckButtonThread.BtnPress);
		filter.setPriority(0);
		registerReceiver(myBroadcastReceiver, filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Read serial port Server is Starting!", Toast.LENGTH_SHORT).show();
		System.out.println("Read serial port Server is Starting!");
		checkButtonThread=new CheckButtonThread(this, TriggerRescueActivity.class,mHandler);
		checkButtonThread.start();

		return Service.START_STICKY;
	}

	private class ReadThread extends Thread {

		private boolean isExit = false;

		@Override
		public void run() {
			super.run();
			while (!isExit) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
					}

					try {
						sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		public void exit() {
			isExit = true;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		if (mReadThread != null) {
//			mReadThread.exit();
//			mReadThread.interrupt();
//		}
//
//		if (mGpioMonitorThread != null) {
//			mGpioMonitorThread.exit();
//			mGpioMonitorThread.interrupt();
//
//		}
//
//		if (mSerialPort != null)
//			mSerialPort.close();
//
//		mSerialPort = null;
		if (checkButtonThread != null) {
		//	checkButtonThread.exit();
			checkButtonThread.interrupt();
			checkButtonThread=null;
		}
		if(myBroadcastReceiver!=null){
			unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver=null;
		}
		
		Toast.makeText(this, "Read serial port Server is onDestroy!", Toast.LENGTH_SHORT).show();
	}

	private void DisplayError(int resourceId) {
		Toast.makeText(getApplicationContext(), "读取串口数据错误:" + getResources().getString(resourceId), Toast.LENGTH_LONG).show();
	}

	// 转换HEX为字符串
	private String changeHexToString(final byte[] buffer, final int size) {
		StringBuffer strbuf = new StringBuffer("");
		if (buffer == null || buffer.length <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			int temp = buffer[i] & 0xFF;
			String strTemp = Integer.toHexString(temp);
			if (strTemp.length() < 2) {
				strbuf.append(0);
			}
			strbuf.append(strTemp);
		}
		return strbuf.toString();
	}

	// 接收数据
	protected void onDataReceived(final byte[] buffer, final int size) {
		final String curentResultStr = changeHexToString(buffer, size).toUpperCase(Locale.getDefault());
		if (TextUtils.isEmpty(curentResultStr))
			return;

		mHandler.post(new Runnable() {

			@Override
			public void run() {

				String deviceNo = "100039";

				Toast.makeText(getApplicationContext(), "Read /dev/ttyS5 PortData : " + curentResultStr + " deviceNo:" + deviceNo + " \n 长度:" + curentResultStr.length(), Toast.LENGTH_SHORT).show();

				PostHelper.postElevatorMaintenanceInfo(getApplicationContext(), deviceNo, curentResultStr);

			}
		});
	}

	class mBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle=arg1.getExtras();
			Toast.makeText(getApplicationContext(), "Service Get Button,duration="+bundle.getString("Duration"), Toast.LENGTH_SHORT).show();
			System.out.println("Service Get Button");
			//Intent intent= new Intent(ReadSerialPortService.this,TriggerRescueActivity.class);
		//	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//	startActivity(intent);
		}
		
	}
}
