package com.hdos.elevatorproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class CountdownActivity extends Activity {

	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countdown);
		Properties pro=new Properties();
		InputStream is;
		try {
			 is=this.getAssets().open("config.properties");
			 pro.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(pro.get("time"));
		System.out.println(pro.get("SS"));
		textView=(TextView) findViewById(R.id.TextView);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stubß
				
				handler.sendEmptyMessage(1);
			}
		}).start();
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			new MyCountdown(5000, 1000).start();
			
			
		};
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.countdown, menu);
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
	
	class MyCountdown extends CountDownTimer{
		public MyCountdown(long millisInFuture, long countDownInterval) {
	
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			textView.setText("Finish");
		}

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
			
			textView.setText(arg0/1000+"s Remaining");
		}
	}
}
