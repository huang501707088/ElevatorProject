package com.hdos.elevatorproject;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					
						for (int i = 0; i <5; i++) {
							Toast.makeText(TestActivity.this, "i="+i, Toast.LENGTH_SHORT).show();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
						finish();
					}
				});
			}
		}).start();
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	Toast.makeText(this, "onDestory", Toast.LENGTH_SHORT).show();
    }
}
