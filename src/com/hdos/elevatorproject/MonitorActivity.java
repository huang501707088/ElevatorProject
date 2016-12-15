package com.hdos.elevatorproject;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import com.google.zxing.common.StringUtils;
import com.hdos.common.DebugTools;
import com.hdos.elevatorproject.button.CheckButtonThread;
import com.hdos.elevatorproject.button.CheckNFCThread;
import com.hdos.elevatorproject.button.ReadSerialPortService;
import com.hdos.elevatorproject.button.UpdateThread;
import com.hdos.elevatorproject.common.Constants;
import com.hdos.elevatorproject.common.FileUtil;
import com.hdos.elevatorproject.common.HttpClientUtil2;
import com.hdos.elevatorproject.common.JsonTool;
import com.hdos.elevatorproject.common.TerminalSystem;
import com.hdos.elevatorproject.common.Tools;
import com.hdos.mode.Task;
import com.huada.connectinternet.ConnectInterner;
import com.huada.serialcommunication.SerialCommunication;
import com.softwinner.update.ProcCpuInfo;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorActivity extends Activity {

	TextView etCPUNo, etTerminalNo, etElevatorRegiNo, etRFIDNo, etLongitude,
			etNFCCode, etInstallFlag, etRescueNo, etBarcodeNo, etLatitude;

	ImageView ivQRCode;
	/**************local var*****/
	String CPUNo, TerminalNo, ElevatorRegiNo, RFIDNo, Longitude,
	NFCCode, InstallFlag, RescueNo, BarcodeNo,tLatitude="";
	
	private TextView tvLiftAddress;//安装电梯地址
	private TextView tvVersion;//版本号
	private Context context;//上下文
	
	/*NFC*/
	private SerialCommunication serialCommunication;
	{
		serialCommunication= new SerialCommunication();
	}
	String port="/dev/ttyS5";
	String getNFCCodeFromReader(){
		byte[] response=new byte[128];
		int re= serialCommunication.getReadheadCode(port,response);
		if(re > 0){
			return new String(response);
		}
		else{
			return "";
		}
	}
	/*NFC end*/
	
	
	
	
	
	public String getCPUNo() {
		return CPUNo;
	}

	public void setCPUNo(String cPUNo) {
		String tmp=cPUNo;
		if(cPUNo.length()>10){
			tmp=cPUNo.substring(0, 16)+"\n"+cPUNo.substring(16,cPUNo.length());
		}
		CPUNo = tmp;
	}

	public String getTerminalNo() {
		return TerminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		TerminalNo = terminalNo;
	}

	public String getElevatorRegiNo() {
		return ElevatorRegiNo;
	}

	public void setElevatorRegiNo(String elevatorRegiNo) {
		ElevatorRegiNo = elevatorRegiNo;
	}

	public String getRFIDNo() {
		return RFIDNo;
	}

	public void setRFIDNo(String rFIDNo) {
		RFIDNo = rFIDNo;
	}

	public String getNFCCode() {
		return NFCCode;
	}

	public void setNFCCode(String nFCCode) {
		NFCCode = nFCCode;
	}

	public String getRescueNo() {
		return RescueNo;
	}

	public void setRescueNo(String rescueNo) {
		RescueNo = rescueNo;
	}

	public String getBarcodeNo() {
		return BarcodeNo;
	}

	public void setBarcodeNo(String barcodeNo) {
		BarcodeNo = barcodeNo;
	}

	/*******************/
	
	ProgressDialog dialog ;
	final int DIALOG_SHOW=3;
	final int DIALOG_DISMISS=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		this.context=this;
		initView();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				initData();
			}
		}).start();
		initService();
		//new UpdateThread().start();
		
		
	}
	
	//初始化信息
	private void initData() {
		// TODO Auto-generated method stub
		/* 获取CPU码 */
		handler.sendMessage(handler.obtainMessage(DIALOG_SHOW));
		runOnUiThread(new Runnable() {
			String cpuCode =ProcCpuInfo.getChipIDHex();
			String nfcCode = getNFCCodeFromReader();
			@Override
			public void run() {
				// TODO Auto-generated method stub
				etCPUNo.setText(cpuCode);
				etNFCCode.setText(nfcCode);
				/*NFC码 */
				//
				ivQRCode.setImageBitmap(EncodingUtils.createQRCode(nfcCode+","+cpuCode,500,500,null));
				
				
				if (Tools.getTerminalData() != null) {
					 String str= Tools.getTerminalData();//文件存在、读取解析
					 Toast.makeText(MonitorActivity.this, "本地数据:"+str, Toast.LENGTH_LONG).show();
					 JSONObject resultJson;
					 try {
						resultJson = new JSONObject(str);
				
					 JSONObject head=resultJson.getJSONObject("head");
					 JSONObject body=resultJson.getJSONObject("body");
					
					 String code=head.getString("code");
					 String msg=head.getString("msg");
					 String tradeId=body.getString("tradeId");
					 String Version=body.getString("Version");
					 
					 etCPUNo.setText(body.getString("CPUCode"));
					 etTerminalNo.setText(body.getString("TermNo"));
					 etElevatorRegiNo.setText(body.getString("LiftRegCode"));
					 etRFIDNo.setText(body.getString("RFICCardNo"));
					 etLongitude.setText(body.getString("Longitude"));
					 etNFCCode.setText(body.getString("NFCCode"));
					 etInstallFlag.setText(body.getString("InstallFlagName"));
					 etRescueNo.setText(body.getString("LiftHelpCode"));
					 etBarcodeNo.setText(body.getString("QRCode"));
					 etLatitude.setText(body.getString("Dimension"));
					 tvLiftAddress.setText(body.getString("LiftAddress"));
					 tvVersion.setText(body.getString("Version"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
				handler.sendMessage(handler.obtainMessage(DIALOG_DISMISS));
			}
		});
		
	}




	//初始化组件
	private void initView() {
		// TODO Auto-generated method stub
		etCPUNo = (TextView) findViewById(R.id.etCPUNo);
		etTerminalNo = (TextView) findViewById(R.id.etTerminalNo);
		etElevatorRegiNo = (TextView) findViewById(R.id.etElevatorRegiNo);
		etLongitude = (TextView) findViewById(R.id.etLongitude);
		etNFCCode = (TextView) findViewById(R.id.etNFCCode);
		etInstallFlag = (TextView) findViewById(R.id.etInstallFlag);
		etRescueNo = (TextView) findViewById(R.id.etRescueNo);
		etRFIDNo = (TextView) findViewById(R.id.etRFIDNo);
		etBarcodeNo = (TextView) findViewById(R.id.etBarcodeNo);
		etLatitude = (TextView) findViewById(R.id.etLatitude);
		ivQRCode=(ImageView) findViewById(R.id.ivQRCode);
		tvLiftAddress = (TextView) findViewById(R.id.tv_lift_address_value);
		tvVersion = (TextView) findViewById(R.id.tv_lift_version_value);
		
		dialog= new ProgressDialog(this);  
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条  
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消  
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条  
        dialog.setMessage("数据更新中,请稍后");  
	}
	/* 测试紧急救援 */
	public void btnTest(View view) {

		Intent intent=new Intent(this,RescueActivity.class);
		startActivity(intent);
	}

	/* 更新同步数据 */
	public void btnSynchronizeDeviceInformation(View view) {

		  //synchronizeDeviceInformation("0b020580574948488065778416516609");
		/* 1、同步设备信息 */
		/* 1.1、检查数据文件是否存在 */
//		if (Tools.getTerminalData() == null) {
			//同步文件
			//synchronizeDeviceInformation(ProcCpuInfo.getChipIDHex());
//			synchronizeDeviceInformation(ProcCpuInfo.getChipIDHex());
			String cpuCode=ProcCpuInfo.getChipIDHex();
			System.out.println("cpuCode="+cpuCode);
			System.out.println("ProcCpuInfo.getChipIDHex()="+ProcCpuInfo.getChipIDHex()+"\nlength="+cpuCode.length());
			synchronizeDeviceInformation(cpuCode);
			
//		}
//		else{
//			//获取读取本地文件
//			 String str= Tools.getTerminalData();//文件存在、读取解析
//			 Toast.makeText(this, str, Toast.LENGTH_LONG).show();
//			 JSONObject resultJson;
//			 try {
//			 resultJson = new JSONObject(str);
//			 JSONObject head=resultJson.getJSONObject("head");
//			 JSONObject body=resultJson.getJSONObject("body");
//			
//			 String code=head.getString("code");
//			 String msg=head.getString("msg");
//			 String tradeId=body.getString("tradeId");
//			 String Version=body.getString("Version");
//			 
//			 etCPUNo.setText(body.getString("CPUCode"));
//			 etTerminalNo.setText(body.getString("TermNo"));
//			 etElevatorRegiNo.setText(body.getString("LiftRegCode"));
//			 etRFIDNo.setText(body.getString("RFICCardNo"));
//			 etLongitude.setText(body.getString("Longitude"));
//			 etNFCCode.setText(body.getString("NFCCode"));
//			 etInstallFlag.setText(body.getString("InstallFlagName"));
//			 etRescueNo.setText(body.getString("LiftHelpCode"));
//			 etBarcodeNo.setText(body.getString("QRCode"));
//			 etLatitude.setText(body.getString("Dimension"));
//			 tvLiftAddress.setText(body.getString("LiftAddress"));
//			 tvVersion.setText(body.getString("Version"));
//		
//		 } catch (JSONException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//		
//		 }
		
	}

	public void synchronizeDeviceInformation(String imei) {
		
		final String addr = "http://14.17.77.85:6112/dtjyzdc/json.html?tradeId=queryTermInfoBindResult&Version=1.0.0&CPUCode="
				+ imei;
		System.out.println("imei="+addr);
		new Thread(new Runnable(

		) {
			@Override
			public void run() {
				
				String res = HttpClientUtil2.getGetBaseResult(addr);
				if(res != null){
					handler.sendMessage(handler.obtainMessage(2, res));
				}else{
					handler.sendEmptyMessage(1);
				}
				
				/* runOnUiThread( new Runnable() {
				 public void run() {
				 etCPUNo.setText(ret.get("CPUCode"));
				 etTerminalNo.setText(ret.get("TermNo"));
				 etElevatorRegiNo.setText(ret.get("LiftRegCode"));
				 etRFIDNo.setText(ret.get("RFICCardNo"));
				 etLongitude.setText(ret.get("Longitude"));
				 etNFCCode.setText(ret.get("NFCCode"));
				 etInstallFlag.setText(ret.get("InstallFlag"));
				 etRescueNo.setText(ret.get("LiftHelpCode"));
				 etBarcodeNo.setText(ret.get("QRCode"));
				 etLatitude.setText(ret.get("Dimension"));
				 }
				 });*/
				
			}
		}).start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitor, menu);
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
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				//
				String result = (String) msg.obj;
				handResult(result);
				break;
			case 1:
				Toast.makeText(context,"Update Error", Toast.LENGTH_LONG).show();
				break;
			case DIALOG_SHOW:
				dialog.show();
				break;
			case DIALOG_DISMISS:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 获得绑定信息
	 * @param result
	 */
	protected void handResult(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONObject head = jsonObject.getJSONObject("head");
			String code = head.getString("code");
			String msg = head.getString("msg");
			if("0000".equals(code)){
				JSONObject body = jsonObject.getJSONObject("body");
				 etCPUNo.setText(body.getString("CPUCode"));
				 etTerminalNo.setText(body.getString("TermNo"));
				 etElevatorRegiNo.setText(body.getString("LiftRegCode"));
				 etRFIDNo.setText(body.getString("RFICCardNo"));
				 etLongitude.setText(body.getString("Longitude"));
				 etNFCCode.setText(body.getString("NFCCode"));
				 etInstallFlag.setText(body.getString("InstallFlagName"));
				 etRescueNo.setText(body.getString("LiftHelpCode"));
				 etBarcodeNo.setText(body.getString("QRCode"));
				 etLatitude.setText(body.getString("Dimension"));
				 tvLiftAddress.setText(body.getString("LiftAddress"));
				 tvVersion.setText(body.getString("Version"));
				 Tools.saveTerminalData(result);
				
				 Toast.makeText(context,"NetWork Success,"+ body.toString(), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	
	/**
	 * 写本地文件
	 * @param result
	 */
	private void saveFile(String result) {
		// TODO Auto-generated method stub
		 try {
		      //      file.createNewFile();
		       FileUtil.writeFile("classAll.json",result,context);
		    } catch (IOException e) {
		            e.printStackTrace();
		}
		
	}
	/**
	 * 显示电梯信息
	 * @param task
	 */
	private void showInfo(Task task) {
		// TODO Auto-generated method stub
		
		
	}
	private void initService(){
		startService(new Intent(this,ReadSerialPortService.class));
	}
}
