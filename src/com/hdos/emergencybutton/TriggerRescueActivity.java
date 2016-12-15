package com.hdos.emergencybutton;

import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.hdos.elevatorproject.R;
import com.hdos.elevatorproject.button.ConfirmRescueActivity;
import com.hdos.elevatorproject.common.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TriggerRescueActivity extends Activity {

	private TextView tvCountDown;
	private myCountDownTimer mCoundDownTimer;
	private ButtonThread button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trigger_rescue);
		tvCountDown = (TextView) findViewById(R.id.tvCountDown);

		mCoundDownTimer = new myCountDownTimer(12000, 1000);
		mCoundDownTimer.start();
		System.out.println("Trigger Timer start");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		startButtonDetect();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopButtonDetect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trigger_rescue, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class myCountDownTimer extends CountDownTimer {

		public myCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			if(millisUntilFinished<11000){
				String str=null;
				if(millisUntilFinished < 10000)
					str="再按报警救援键三秒，启动救援通话(0%d秒后退出)";
				
				else str="再按报警救援键三秒，启动救援通话(%d秒后退出)";
				
				tvCountDown.setText(String.format(str, millisUntilFinished / 1000 ));
			}
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			System.out.println("Trigger Timer stop");
			finish();
		}

	}

	class ButtonThread extends Thread {
		private final String mPh22Path = "/sys/class/gpio_sw/PH22/data";
		boolean isRunning = true;
		FileReader localFileReader;
		private Context context;

		public ButtonThread(Context context) {
			this.context = context;
		}

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
			int lastValue = 0;
			System.out.println("Trigger Rescue  Button Detect Start");
			while (isRunning) {
				value = readButton();
				// System.out.println("GPIO Value=" + value);
				if (value ==  Constants.ButtonOn) {
					try {
						Thread.sleep(10);
						value = readButton();
						if (value ==  Constants.ButtonOn) {
							lastValue =  Constants.ButtonOn;
							long startTime = System.currentTimeMillis();
							long endTime = startTime;
							do {
								value = readButton();
								endTime = System.currentTimeMillis();
								Thread.sleep(50);
								if (endTime - startTime >= 3000) {

									mCoundDownTimer.cancel();
									System.out.println("Trigger Timer cancel");
									Intent intent = new Intent(context,
											ConfirmRescueActivity.class);
									startActivity(intent);
									break;
								}
							} while (value ==  Constants.ButtonOn);
							endTime = System.currentTimeMillis();
							System.out.println("Time=" + (endTime - startTime));
						} else {// 抖动

						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				} else {
					lastValue = value;
				}

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					System.out.println("Trigger Rescue ButtonThread Finish");
					isRunning = false;
					return;
				}

			}

		}
	}

	public void startButtonDetect() {
		if (button == null) {
			button = new ButtonThread(this);
			button.start();
		}
	}

	public void stopButtonDetect() {
		if (button != null) {
			button.isRunning = false;
			button.interrupt();
			button = null;
		}
	}
}
