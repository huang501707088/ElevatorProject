package com.hdos.elevatorproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatOutParam;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;
import com.hdos.elevatorproject.RescueActivity.MyCountdown;
import com.hdos.elevatorproject.RescueActivity.RescueThread;
import com.hdos.elevatorproject.RescueActivity.mBroadcastReceiver;
import com.hdos.elevatorproject.button.CheckButtonThread;
import com.hdos.elevatorproject.common.MD5;
import com.hdos.elevatorproject.common.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class RemoteCameraActivity extends Activity  implements AnyChatBaseEvent,
AnyChatObjectEvent, AnyChatTransDataEvent, AnyChatUserInfoEvent{




	private AnyChatCoreSDK anychat = null;
	private AnyChatOutParam anychatOutPara;

	// LoginPara loginPara;

	boolean isConnect = false;
	boolean isLogin = false;
	boolean isEnterRoom = false;
	boolean isFileTrans = false;

	boolean is9001 = false;
	String Msg9001 = "";

	boolean is1000 = false;
	String Msg1000 = "";

	boolean is4000 = false;
	String Msg4000 = "";

	boolean is9100 = false;
	String Msg9100 = "";

	private int ConnectTryTimes = 3;
	private int ConnectFailTimes = 0;
	// private int ConnectWaitingTime=1000000;//ms
	private int LoginTryTimes = 3;
	private int LoginFailTimes = 0;
	private int LoginWaitingTime = 1000000;// ms
	private int EnterRoomTryTimes = 3;
	private int EnterRoomTimes = 0;
	private int EnterRoomTime = 1000000;// ms
	private int FileTransTryTimes = 0;
	private int FileTransWaitingTime = 10000;// ms

	private RemoteCameraThread remoteCameraThread = null;

	private int myUserId = -1;

	private int RoomId = 1;

	private int FriendId_File = 1;
	private int FileTransTastID = -1;
	private ProgressDialog mProgressDialog; // 发送文件进度框

	private int FriendId_TransBuff = 0;

	Properties pro;

	// video
	//private SurfaceView localSurfaceView;
	//private SurfaceView remoteSurfaceView;

	// videoView
	//private VideoView VideoPlayer;

	// TextView
	//private TextView videoMsg;

	private mBroadcastReceiver myBroadcastReceiver;
	public final int MSG_SHOW = 0x00;
	public final int MSG_QUIT = 0x01;

	// local Para
	private String LocalRegisterCode = null;
	private String LocalRFIDNo = null;
	private String LocalLongitude = null;
	private String LocalLatitude = null;
	private String LocalHelpCode = null;
	private String LocalPicturePath = null;
	private String LocalServer = null;
	private int LocalPort = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_rescue);
		initAnychat();
		initLocalAndRemoteCamera();
//		initVideoPlayer();
//		initView();
//		initEvents();
//		initLocalData();
		processStart();

	}

	private void processStart() {
		// TODO Auto-generated method stub
		remoteCameraThread = new RemoteCameraThread();
		remoteCameraThread.start();
		
	}

	private void initEvents() {
		// Add one receiver to abort the boardcast
		myBroadcastReceiver = new mBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CheckButtonThread.BtnPress);
		filter.setPriority(1000);
		registerReceiver(myBroadcastReceiver, filter);
	}

	private void initView() {
		// TODO Auto-generated method stub
		// TextView--videoMsg
		//videoMsg = (TextView) findViewById(R.id.videoMsg);
	}

//	private void initVideoPlayer() {
//		// TODO Auto-generated method stub
//		// VideoView
//		//VideoPlayer = (VideoView) findViewById(R.id.videoView);
//		String path = "android.resource://" + getPackageName() + "/"
//				+ R.raw.video;
//		final Uri uri = Uri.parse(path);
//		MediaController mediaController = new MediaController(this);
//		mediaController.setVisibility(View.INVISIBLE);
//		VideoPlayer.setMediaController(mediaController);
//		VideoPlayer.setVideoURI(uri);
//		VideoPlayer.start();
//		VideoPlayer.requestFocus();
//
//		VideoPlayer.setOnCompletionListener(new OnCompletionListener() {
//
//			@Override
//			public void onCompletion(MediaPlayer arg0) {
//				// TODO Auto-generated method stub
//				VideoPlayer.setVideoURI(uri);
//				VideoPlayer.start();
//			}
//		});
//	}

	private void initLocalAndRemoteCamera() {
		// 5.1
		//localSurfaceView = (SurfaceView) findViewById(R.id.localSV);
		//remoteSurfaceView = (SurfaceView) findViewById(R.id.remoteSV);

		// 5.2
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, 1);

		// 5.3
		anychat.mSensorHelper.InitSensor(this);
		AnyChatCoreSDK.mCameraHelper.SetContext(this);
//		localSurfaceView.getHolder().setType(
//				SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		localSurfaceView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
//		localSurfaceView.setZOrderOnTop(true);
		localVideoOpen(true);
	}

	private void initAnychat() {
		anychat = AnyChatCoreSDK.getInstance(null);
		anychatOutPara = new AnyChatOutParam();
		anychat.SetBaseEvent(this);
		anychat.SetObjectEvent(this);
		anychat.SetTransDataEvent(this);
		anychat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
	}

	private void initLocalData() {
		// TODO Auto-generated method stub
		// Read Properity for Development Data

		pro = new Properties();
		InputStream is;
		try {
			is = this.getAssets().open("config.properties");
			pro.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stopRescureThread();
		//	VideoPlayer.pause();
			showToast("Load Properity Files Fail");
			handler.postDelayed(exitRunable, 5000);

		}
		
		
		
		//Use Property Config
		LocalServer=pro.getProperty("Server");
		LocalPort=Integer.valueOf(pro.getProperty("Port"));
		
		
		
		// Real local File For Installation Data
		if (Tools.getTerminalData() != null) {
			String str = Tools.getTerminalData();// 文件存在、读取解析
			if (str != null) {
				JSONObject resultJson;
				try {
					resultJson = new JSONObject(str);

					JSONObject head = resultJson.getJSONObject("head");
					JSONObject body = resultJson.getJSONObject("body");

					// etCPUNo.setText(body.getString("CPUCode"));
					// etTerminalNo.setText(body.getString("TermNo"));
					LocalRegisterCode = body.getString("LiftRegCode");
					LocalRFIDNo = body.getString("RFICCardNo");
					LocalLongitude = body.getString("Longitude");
					// etNFCCode.setText(body.getString("NFCCode"));
					// etInstallFlag.setText(body.getString("InstallFlagName"));
					LocalHelpCode = body.getString("LiftHelpCode");
					// etBarcodeNo.setText(body.getString("QRCode"));
					LocalLatitude = body.getString("Dimension");
					// tvLiftAddress.setText(body.getString("LiftAddress"));
					// tvVersion.setText(body.getString("Version"));
					
					if(body.getString("AnychatIP")!=null){
						LocalServer=body.getString("AnychatIP");
					}
					if(body.getString("AnychatPort")!=null){
						LocalPort=Integer.valueOf(body.getString("AnychatPort"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		LocalPicturePath = getPicturePath();
		
//		System.out.println("LocalSever="+LocalServer);
//		System.out.println("LocalPort="+LocalPort);
	
		
		
	}

	private String getPicturePath() {
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String TimeNow = sdf.format(date);
		String PictureName = LocalHelpCode + "-" + TimeNow + ".jpg";

		deepFile("test.jpg");
		makeFile(PictureName);
		return PictureName;
	}

	private void makeFile(String pictureName) {
		// TODO Auto-generated method stub
		FileInputStream srcFIS = null;
		FileOutputStream destFOS = null;
		String srcPath = Environment.getExternalStorageDirectory() + "/"
				+ "test.jpg";
		String destPath = Environment.getExternalStorageDirectory() + "/"
				+ pictureName;

		try {
			srcFIS = new FileInputStream(srcPath);

			destFOS = new FileOutputStream(destPath);

			byte[] buffer = new byte[1024];
			int count = 0;
			while (true) {
				count++;
				int len = srcFIS.read(buffer);
				if (len == -1) {
					break;
				}
				destFOS.write(buffer, 0, len);
			}
			srcFIS.close();
			destFOS.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (srcFIS != null) {
				try {
					srcFIS.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (destFOS != null) {
				try {
					destFOS.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rescue, menu);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		myFinish();
		Toast.makeText(this, "RemoteCameraOnDestroy",Toast.LENGTH_SHORT).show();	
	}

	/* AnyChat Functions */
	@Override
	public void OnAnyChatTransFile(int dwUserid, String FileName,
			String TempFilePath, int dwFileLength, int wParam, int lParam,
			int dwTaskId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
		// TODO Auto-generated method stub
		try {
			String ReceiveBuffer = new String(lpBuf);
			System.out.println("OnAnyChatTransBuffer,dwUserid=" + dwUserid
					+ ";lpBuf=" + ReceiveBuffer + ";dwLen=" + dwLen);
			android.util.Log.i("SendAndReceive", "userId=" + dwUserid
					+ "Receive=" + ReceiveBuffer);
			String[] strs = Tools.parse(ReceiveBuffer);
			switch (Integer.valueOf(strs[0])) {
			case 9001:
				is9001 = true;
				Msg9001 = ReceiveBuffer;
				break;
			case 1000:
				is1000 = true;
				Msg1000 = ReceiveBuffer;
				break;
			case 4000:
				is4000 = true;
				Msg4000 = ReceiveBuffer;
				break;
			case 9100:
				is9100 = true;
				Msg9100 = ReceiveBuffer;
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen,
			int wparam, int lparam, int taskid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId,
			int dwEventType, int dwParam1, int dwParam2, int dwParam3,
			int dwParam4, String strParam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub
		if (bSuccess) {
			isConnect = true;
		} else {
			isConnect = false;
			Log("Connect Fail");
			// ConnectFailTimes++;//这里不采用多次尝试。即一次失败就退出
			// if(ConnectFailTimes==Tools.getIntProperity(pro.get("ConnectTryTimes"))){
			stopRescureThread();
			failAndQuit("连接服务器失败",
					Tools.getIntProperity(pro.get("LoginFailQuitTime")));
			// }
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		if (dwErrorCode == 0) {
			isLogin = true;
			Log("Login Success,myUserId=" + dwUserId);
			this.myUserId = dwUserId;
		} else {
			isLogin = false;
			Log("Login Fail,Reason=" + Tools.errorToStr(dwErrorCode));
			stopRescureThread();
			failAndQuit("登录失败，原因＝" + Tools.errorToStr(dwErrorCode),
					Tools.getIntProperity(pro.get("LoginFailQuitTime")));

		}
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub

		if (dwErrorCode == 0) {
			isEnterRoom = true;
			Log("EnterRoom Success,RoomId=" + dwRoomId);
		} else {
			isLogin = false;
			Log("EnterRoom Fail,Error Code=" + dwErrorCode);
			stopRescureThread();
			failAndQuit("进入房间失败，原因＝" + Tools.errorToStr(dwErrorCode),
					Tools.getIntProperity(pro.get("EnterRoomFailQuitTime")));
		}

	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
		// TODO Auto-generated method stub

	}

	/* Elevator Code */

	class RemoteCameraThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

//			Message msg = new Message();
//			msg.what = MSG_SHOW;
//			msg.obj = "救援通话请求中......";
//			handler.sendMessage(msg);
			// Connect
			anychat.Connect(pro.getProperty("Server"),
					Integer.valueOf(pro.getProperty("Port")));
			int i = 0;
			int TryTime = Tools.getIntProperity(pro
					.getProperty("ConnectWaitingTime")) / 500;
			while (!isConnect && i < TryTime) {
				Log("Connecting...");
//				msg = new Message();
//				msg.what = MSG_SHOW;
//				msg.obj = "连接服务器中......";
//				handler.sendMessage(msg);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log("Cancel Connect");
					return;
				}
				i++;
			}
			if (i == TryTime) {
//				failAndQuit("连接服务器超时......",
//						Tools.getIntProperity(pro.get("ConnectFailQuitTime")));
				Log("连接服务器超时......");
				return;
			}

			// Login
			anychat.Login(pro.getProperty("Username"),
					MD5.encode(pro.getProperty("Username")));
			i = 0;
			TryTime = Tools
					.getIntProperity(pro.getProperty("LoginWaitingTime")) / 500;
			while (!isLogin && i < TryTime) {
				Log("Login...");
//				msg = new Message();
//				msg.what = MSG_SHOW;
//				msg.obj = "登录中......";
//				handler.sendMessage(msg);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log("Cancel Login");
					return;
				}
				i++;
			}
			if (i == TryTime) {
//				failAndQuit("登录超时......",
//						Tools.getIntProperity(pro.get("LoginFailQuitTime")));
				Log("登录超时......");
				return;
			}

			// EnterRoom
			anychat.EnterRoom(RoomId, "");
			i = 0;
			TryTime = Tools.getIntProperity(pro
					.getProperty("EnterRoomWaitingTime")) / 500;
			while (!isEnterRoom && i < TryTime) {
				Log("EnterRoom...");
//				msg = new Message();
//				msg.what = MSG_SHOW;
//				msg.obj = "进入房间中......";
//				handler.sendMessage(msg);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log("Cancel EnterRoom");
					return;
				}
				i++;
			}
			if (i == TryTime) {
//				failAndQuit("进入房间超时......",
//						Tools.getIntProperity(pro.get("EnterRoomFailQuitTime")));
				Log("进入房间超时......");
				return;
			}
			localVideoOpen(true);
			
			/*
			// String str =
			// Tools.pack(9000,System.currentTimeMillis(),pro.get("Username"),pro.get("RegisterCode"),123,123,123,0,0);
			String str = Tools.pack(9000, System.currentTimeMillis(),
					LocalHelpCode, LocalRegisterCode, LocalRFIDNo,
					LocalLongitude, LocalLatitude, 0, 0);

			// byte[] buffer = str.getBytes();
			// anychat.TransBuffer(FriendId_TransBuff, buffer, buffer.length);
			System.out.println("Send(FriendId_TransBuff, str)=" + str);
			Send(FriendId_TransBuff, str);
			Log("Send=" + str);
			is9001 = false;
			Msg9001 = "";
			boolean is9001_OK = false;
			String[] strs = null;
			TryTime = Tools.getIntProperity(pro.getProperty("9000WaitingTime")) / 500;
			i = 0;
			while (!is9001_OK) {
				while (!is9001 && i < TryTime) {
					Log("Waiting 9001...");
					updateScreenText("请求终端通讯......");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						Log("Cancel 9001");
						return;
					}
					i++;
				}
				if (i == TryTime) {
					failAndQuit("请求终端通讯超时......",
							Tools.getIntProperity(pro.get("9000FailQuitTime")));
					return;
				}
				strs = Tools.parse(Msg9001);
				if ("OK".equals(strs[2])) {
					Log("9001_OK");
					is9001_OK = true;
					continue;
				} else {
					failAndQuit("请求视频通话失败:" + strs[3] + "请重试或者使用其他方式求助",
							Tools.getIntProperity(pro.get("9000FailQuitTime")));
					return;
				}
			}
			is1000 = false;
			TryTime = Tools.getIntProperity(pro.getProperty("1000WaitingTime")) / 500;
			i = 0;
			while (!is1000 && i < TryTime) {
				Log("Waiting 1000...");
				updateScreenText("等待话务员任务......");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log("Cancel 1000");
					return;
				}
				i++;
			}
			if (i == TryTime) {
				failAndQuit("等待话务员任务超时",
						Tools.getIntProperity(pro.get("1000FailQuitTime")));
				return;
			}
			// Get Command 1000
			// send 1001;

			strs = Tools.parse(Msg1000);
			String batchNo = strs[1];
			int contractId = Integer.valueOf(strs[5]);

			Send(contractId, Tools.pack("1001", batchNo, "OK", null));

			localVideoOpen(true);
			//bindRemoteVideo(contractId);
			remoteVideoOpen(contractId, true);
			//pauseVideo();
			is9100 = false;
			while (!is9100) {
				Log("Waiting 9100...");
				updateScreenText("");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log("Cancel 9100");
					localVideoOpen(false);
					remoteVideoOpen(contractId, false);
					return;
				}
				if (is4000) {// 有4000信息到
					strs = Tools.parse(Msg4000);
					batchNo = strs[1];
					int onOrOff = Integer.valueOf(strs[2]);
					if (onOrOff == 0) {
						remoteVideoOpen(contractId, false);
						updateScreenText("话务员暂停通话。。。。。。");
						//resumeVideo();
					} else {
						remoteVideoOpen(contractId, true);
						updateScreenText("");
						//pauseVideo();
					}
					Send(contractId, Tools.pack("4001", batchNo, "OK", null));

					is4000 = false;
					Msg4000 = "";
				}

			}
			//pauseVideo();
			strs = Tools.parse(Msg9100);
			batchNo = strs[1];
			Send(contractId, Tools.pack("9101", batchNo, "OK", null));
			*/
			localVideoOpen(false);
			//remoteVideoOpen(contractId, false);
			Log("RemoteCameraThread Finish");
			//failAndQuit("通话结束", 10000);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW:
				//videoMsg.setText((CharSequence) msg.obj);
				break;
			case MSG_QUIT:
				new MyCountdown((Integer) msg.obj, 1000).start();
				break;
			default:
				break;
			}

		};
	};

	class MyCountdown extends CountDownTimer {
		private String orinalMsg = "";

		public MyCountdown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			//this.orinalMsg = videoMsg.getText().toString();
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			finish();
			// myFinish();
			// finish();

		}

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub

			//videoMsg.setText(orinalMsg + ",\n" + arg0 / 1000 + "秒后退出");
		}
	}

	Runnable runnable = new Runnable() {
		// AnyChatOutParam mAnyChatOutParam=new AnyChatOutParam();
		@Override
		public void run() {
			try {
				int returnFlag = anychat.QueryTransTaskInfo(-1,
						FileTransTastID, AnyChatDefine.BRAC_TRANSTASK_PROGRESS,
						anychatOutPara);
				Log("returnFlag=" + returnFlag + "\nanychatOutPara="
						+ anychatOutPara.GetIntValue());
				if (returnFlag == 0) {
					handleProgressDlg();
					mProgressDialog.setProgress(anychatOutPara.GetIntValue());
				} else {
					handler.removeCallbacks(runnable);

					// Elevator
					isFileTrans = false;
					if (remoteCameraThread != null) {
						remoteCameraThread.interrupt();
						remoteCameraThread = null;
					}

					return;
				}

				if (anychatOutPara.GetIntValue() == 100) {
					mProgressDialog.dismiss();
					handler.removeCallbacks(runnable);

					// Elevator
					isFileTrans = true;
					return;
				}

				handler.postDelayed(runnable, 500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	Runnable exitRunable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			finish();
		}
	};

	@SuppressWarnings("deprecation")
	private void handleProgressDlg() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setMessage("正在传送");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (anychatOutPara != null) {
								if (remoteCameraThread != null) {
									remoteCameraThread.interrupt();
								}

								anychat.CancelTransTask(-1, FileTransTastID);
								handler.removeCallbacks(runnable);
								mProgressDialog.cancel();
								mProgressDialog = null;
							}
						}
					});
		}

		mProgressDialog.show();
	}

	private void updateScreenText(String str) {
		Message msg = new Message();
		msg.what = MSG_SHOW;
		msg.obj = str;
		handler.sendMessage(msg);
	}

	private void Log(String str) {
		System.out.println(str);

	}

	private void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

	}

	private void stopRescureThread() {
		if (remoteCameraThread != null) {// After onDestroy of RescueActivity,this
			// method can still be called
			remoteCameraThread.interrupt();
		}
	}

	private void failAndQuit(String ErrorMsg, int DelayTime) {
		Message msg = new Message();
		msg.what = MSG_SHOW;
		msg.obj = ErrorMsg;
		handler.sendMessage(msg);

		msg = new Message();
		msg.what = MSG_QUIT;
		msg.obj = DelayTime;
		handler.sendMessage(msg);
	}

	private void Send(int userid, String str) {
		byte[] buf = str.getBytes();
		int len = buf.length;
		anychat.TransBuffer(userid, buf, len);
		android.util.Log
				.i("SendAndReceive", "userId=" + userid + "Send=" + str);
	}

//	private void pauseVideo() {
//		VideoPlayer.pause();
//	}
//
//	private void resumeVideo() {
//		VideoPlayer.start();
//	}

	private void localVideoOpen(boolean open) {
		if (open) {

			anychat.UserCameraControl(-1, 1);
			anychat.UserSpeakControl(-1, 1);
		} else {
			anychat.UserCameraControl(-1, 0);
			anychat.UserSpeakControl(-1, 0);

		}
	}

	private void remoteVideoOpen(int userId, boolean open) {
		if (open) {

			anychat.UserCameraControl(userId, 1);
			anychat.UserSpeakControl(userId, 1);
		} else {
			anychat.UserCameraControl(userId, 0);
			anychat.UserSpeakControl(userId, 0);

		}
	}

//	private void bindRemoteVideo(int userId) {
//		int index = anychat.mVideoHelper.bindVideo(remoteSurfaceView
//				.getHolder());
//		anychat.mVideoHelper.SetVideoUser(index, userId);
//	}

	public void deepFile(String path) {
		File file = null;
		try {
			file = new File(Environment.getExternalStorageDirectory() + "/"
					+ path);
			System.out.println(Environment.getExternalStorageDirectory());
			if (file.exists()) {
				return;
			}
			FileOutputStream fos = new FileOutputStream(file);
			InputStream is = this.getAssets().open(path);
			byte[] buffer = new byte[1024];
			int count = 0;
			while (true) {
				count++;
				int len = is.read(buffer);
				if (len == -1) {
					break;
				}
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void myFinish() {
		if (remoteCameraThread != null) {
			remoteCameraThread.interrupt();
			remoteCameraThread = null;
		}

		localVideoOpen(false);
		anychat.LeaveRoom(RoomId);
		anychat.Logout();
		anychat.Release();
		if (myBroadcastReceiver != null) {
			unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver = null;
		}

	}

	class mBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle = arg1.getExtras();
			Toast.makeText(
					getApplicationContext(),
					"RescueActivity Get Button,Duration="
							+ bundle.getString("Duration"), Toast.LENGTH_SHORT)
					.show();
			abortBroadcast();
			System.out.println("RescueActivity Get Button,Duration="
					+ bundle.getString("Duration"));
		}

	}
}
