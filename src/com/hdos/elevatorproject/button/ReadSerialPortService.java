package com.hdos.elevatorproject.button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.hdos.elevatorproject.Constant;
import com.hdos.elevatorproject.ExampleUtil;
import com.hdos.elevatorproject.HttpClientUtil;
import com.hdos.elevatorproject.RemoteCheckActivity;
import com.hdos.elevatorproject.RemoteCommunActivity;
import com.hdos.elevatorproject.RescueActivity;
import com.hdos.elevatorproject.StringUtils;
import com.hdos.elevatorproject.TestActivity;
import com.hdos.elevatorproject.button.CheckButtonThread;
import com.hdos.elevatorproject.button.TriggerRescueActivity;
import com.hdos.elevatorproject.common.HttpClientUtil2;
import com.hdos.elevatorproject.common.TerminalSystem;
import com.softwinner.update.ProcCpuInfo;

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

/**
 * 后台持续运行读取串口数据服务,监听供电状态变化，紧急按键
 * 
 * 当前串口配置为[9600,N,8,1]，请从PC或其它设备往本机串口(5V,RX,TX,GND)发送数据
 * 
 * @author Jack
 *
 */
public class ReadSerialPortService extends Service {
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private Handler mHandler = new Handler(){
		 public void handleMessage(android.os.Message msg) {
	            switch (msg.what) {
	                case Constant.TIME_OUT_HANDLE:
	                    //if (progressDialog.isShowing())
	                      //  progressDialog.dismiss();
	                    //prompConnectService("连接服务器超时");
	                    break;
	                /*case Constant.GET_MODI_PWD_HANDLER:
	                    if (progressDialog.isShowing())
	                        progressDialog.dismiss();
	                    String resModify = (String) msg.obj;
	                    //处理登录逻辑
	                    handlerModify(resModify);
	                    break;*/
	                case Constant.REGIST_TOKEN_HANDLER:
	                    //if (progressDialog.isShowing())
	                      //  progressDialog.dismiss();
	                    String resRegist = (String) msg.obj;
	                    //处理登录逻辑
	                    handlerResigt(resRegist);
	                    break;
	                default:
	                    break;
	            }
	        }
	};
	protected void handlerResigt(String resRegist) {
		// TODO Auto-generated method stub
		Toast.makeText(this, resRegist, Toast.LENGTH_LONG).show();
	}

	
	private CheckButtonThread checkButtonThread=null;
	private CheckPowerThread checkPowerThread=null;
	private CheckNFCThread checkNFCThread=null;
	private HeartBeatThread heartBeatThread=null;
	private mBroadcastReceiver myBroadcastReceiver=null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		initReceiverAndThread();
			
				
				initJPush();
				initToken();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Read serial port Server is Starting!", Toast.LENGTH_SHORT).show();

		System.out.println("Read serial port Server is Starting!");
		
		return Service.START_STICKY;
	}

	private void initJPush() {
	  JPushInterface.init(this);
	}
	private void initToken(){
	  String rid = JPushInterface.getRegistrationID(getApplicationContext());
	  if (!rid.isEmpty()) {
		  //Toast.makeText(this, "RegId:"+rid, Toast.LENGTH_SHORT).show();
		  System.out.println("RegId:"+rid);
		  	//注册推送
		  registToken(rid);
	  } else {
		  Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
	  }
	}
	/**
	 * 注册token
	 */
	private void registToken(String rid) {
		// TODO Auto-generated method stub
		//cpu code
		//String udid =  ExampleUtil.getImei(getApplicationContext(), "");
		String udid =ProcCpuInfo.getChipIDHex();
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("param", "post"));
        params.add(new BasicNameValuePair("tradeId", "PushTokenReg"));
        params.add(new BasicNameValuePair("Version", "1.0.0"));
        params.add(new BasicNameValuePair("CPUCode", udid));
        params.add(new BasicNameValuePair("PushType", "1"));
        params.add(new BasicNameValuePair("PushToken", rid));
     

        
        //if (!progressDialog.isShowing())
          //  progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpClientUtil.getBaseResult(params);
                if (StringUtils.isEmpty(result)) {
                	mHandler.sendEmptyMessage(Constant.TIME_OUT_HANDLE);
                } else {
                	mHandler.sendMessage(mHandler.obtainMessage(Constant.REGIST_TOKEN_HANDLER, result));
                }
            }
        }).start();
		
	}
	private void initReceiverAndThread(){
		if(checkButtonThread==null){
			checkButtonThread=new CheckButtonThread(this, TriggerRescueActivity.class,mHandler);
			checkButtonThread.start();
		}
		if(checkPowerThread==null){
			checkPowerThread=new CheckPowerThread(this, TriggerRescueActivity.class,mHandler);
			checkPowerThread.start();
		}
		if(checkNFCThread==null){
		checkNFCThread=new CheckNFCThread(this, TriggerRescueActivity.class,mHandler);
		checkNFCThread.start();
		}
		if(heartBeatThread==null){
				heartBeatThread=new HeartBeatThread();
			heartBeatThread.start();
		}
		if(myBroadcastReceiver==null){
			myBroadcastReceiver= new mBroadcastReceiver();
			IntentFilter filter=new IntentFilter();
			filter.addAction(CheckButtonThread.BtnPress);
			filter.addAction(CheckPowerThread.PowerState);
			filter.addAction(CheckNFCThread.NFCDetach);
			filter.addAction(MESSAGE_RECEIVED_ACTION);
			filter.setPriority(0);
			registerReceiver(myBroadcastReceiver, filter);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uninitReceiverAndThread();
	
		
		Toast.makeText(this, "Read serial port Server is onDestroy!", Toast.LENGTH_SHORT).show();
	}

	public void uninitReceiverAndThread(){
		if(myBroadcastReceiver!=null){
			unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver=null;
		}
		if (checkButtonThread != null) {
			checkButtonThread.interrupt();
			checkButtonThread=null;
		}

		if (checkPowerThread != null) {
			checkPowerThread.interrupt();
			checkPowerThread=null;
			}
		if (checkNFCThread != null) {
			checkNFCThread.interrupt();
			checkNFCThread=null;
		}
		if (heartBeatThread != null) {
			heartBeatThread.interrupt();
			heartBeatThread=null;
		}
		
		
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


	class mBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle=arg1.getExtras();
			String action=arg1.getAction();
			if(CheckButtonThread.BtnPress.equals(action)){//按键按下
				Toast.makeText(getApplicationContext(), "Service Get Button,duration="+bundle.getString("Duration"), Toast.LENGTH_SHORT).show();
				System.out.println("Service Get Button");
				if("long".equals(bundle.getString("Duration"))){
				Intent intent= new Intent(ReadSerialPortService.this,ConfirmRescueActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				}
			}else if(CheckPowerThread.PowerState.equals(action)){//电源切换
				Toast.makeText(getApplicationContext(), "Power Mode="+bundle.getString("PowerState"), Toast.LENGTH_SHORT).show();
				System.out.println("Service Get Power");
				 final List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	        params.add(new BasicNameValuePair("param", "post"));
	    	        params.add(new BasicNameValuePair("tradeId", "powerChange"));
	    	        params.add(new BasicNameValuePair("Version","1.0.0"));
	    	        params.add(new BasicNameValuePair("CPUCode",ProcCpuInfo.getChipIDHex()));
	    	        String ChangeFlag="";
	    	        if("Adapter".equals(bundle.getString("PowerState"))){
	    	        	ChangeFlag="2";
	    	        	heartBeatThread.setPowerFlag(1);
	    	        }else{
	    	        	ChangeFlag="1";
	    	        	heartBeatThread.setPowerFlag(2);
	    	        }
	    	        params.add(new BasicNameValuePair("ChangeFlag", ChangeFlag));
	    	        Date date=new Date();
	    	        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	    	        params.add(new BasicNameValuePair("LastTime", sdf.format(date)));
	    	        params.add(new BasicNameValuePair("LastMotion","0"));
	    	        new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							  String result = HttpClientUtil2.getBaseResult(params);
							  System.out.println("Result="+result);
						}
					}).start();
			}else if(CheckNFCThread.NFCDetach.equals(action)){//NFC刷卡
				Toast.makeText(getApplicationContext(), "Get NFC,CardNo="+bundle.getString("CardNo"), Toast.LENGTH_SHORT).show();
				System.out.println("Get NFC");
				 final List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	        params.add(new BasicNameValuePair("param", "post"));
	    	        params.add(new BasicNameValuePair("tradeId", "nfcSingnIn"));
	    	        params.add(new BasicNameValuePair("Version","1.0.0"));
//	    	       params.add(new BasicNameValuePair("CPUCode", TerminalSystem.getCPUCode(ReadSerialPortService.this)));
	    	       params.add(new BasicNameValuePair("CPUCode",ProcCpuInfo.getChipIDHex()));
	    	 
	    	        params.add(new BasicNameValuePair("NFCCardNo", bundle.getString("CardNo")));

	    	        new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							  String result = HttpClientUtil2.getBaseResult(params);
							  System.out.println("Result="+result);
						}
					}).start();
			}else if(ReadSerialPortService.MESSAGE_RECEIVED_ACTION.equals(action)){//JPush 
				  String messge = arg1.getStringExtra(KEY_MESSAGE);
	              String extras = arg1.getStringExtra(KEY_EXTRAS);
	              StringBuilder showMsg = new StringBuilder();
	              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
	              
	              if (!ExampleUtil.isEmpty(extras)) {
	            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
	              }
	              //setCostomMsg(showMsg.toString());
	              //通过msg来判断如何操作
	              Toast.makeText(ReadSerialPortService.this, showMsg.toString(), Toast.LENGTH_LONG).show();
	              //System.out.println("JPUSH="+ showMsg.toString());
	              System.out.println("JPUSH="+messge);
	  			JSONObject resultJson;
	  			String RemoteMoniNo=null;
	  			String MoniType=null;
					try {
						resultJson = new JSONObject(messge);
						RemoteMoniNo=resultJson.getString("RemoteMoniNo");
						MoniType=resultJson.getString("MoniType");
						System.out.println("JPUSH  RemoteMoniNo="+ RemoteMoniNo+"\nMoniType="+MoniType);
						if("1".equals(MoniType)){//Remote Check
							Toast.makeText(ReadSerialPortService.this, "远程查看", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(ReadSerialPortService.this,
									RemoteCheckActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
			                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
						}else if("2".equals(MoniType)){//Remote Commun
							Toast.makeText(ReadSerialPortService.this, "远程通话", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(ReadSerialPortService.this,
									RemoteCommunActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
			                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
	              
	              /*
	             // Toast.makeText(ReadSerialPortService.this, "收到救援指令", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(ReadSerialPortService.this,
						RescueActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				*/
			}
		
		}
		
	}
	
//	/**
//     * 处理访问网络的返回结果
//     */
//    private Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//            
//                case Constant.TIME_OUT_HANDLE:
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    prompConnectService("连接服务器超时");
//                    break;
//                case Constant.GET_MODI_PWD_HANDLER:
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    String resModify = (String) msg.obj;
//                    //处理登录逻辑
//                    handlerModify(resModify);
//                    break;
//                case Constant.GET_USER_LOGOUT_HANDLER:
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    String resLogout = (String) msg.obj;
//                    //处理登录逻辑
//                    handlerLogout(resLogout);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
}
