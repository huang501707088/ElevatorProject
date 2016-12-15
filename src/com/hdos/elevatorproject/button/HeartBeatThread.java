package com.hdos.elevatorproject.button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hdos.elevatorproject.common.HttpClientUtil2;
import com.softwinner.update.ProcCpuInfo;

public class HeartBeatThread extends Thread {
	
	
	boolean isRunning = true;
	public int PowerFlag=1;
	public void setPowerFlag(int PowerFlag){
		this.PowerFlag=PowerFlag;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			while(isRunning){
			Thread.sleep(30000);
			 final List<NameValuePair> params = new ArrayList<NameValuePair>();
 	        params.add(new BasicNameValuePair("param", "post"));
 	        params.add(new BasicNameValuePair("PowerFlag", PowerFlag+""));
 	        params.add(new BasicNameValuePair("Version","1.0.0"));
 	        params.add(new BasicNameValuePair("tradeId", "Heartbeat"));
 	        params.add(new BasicNameValuePair("CPUCode",ProcCpuInfo.getChipIDHex()));
 	        params.add(new BasicNameValuePair("LastMotion","0"));
 	        Date date=new Date();
 	        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
 	        params.add(new BasicNameValuePair("LastTime", sdf.format(date)));
					
						  String result = HttpClientUtil2.getBaseResult(params);
						  System.out.println("Result="+result);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("HeartBeatThread Finish");
			isRunning = false;
			return;
		}
	}
}
