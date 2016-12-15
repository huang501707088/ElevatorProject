package com.hdos.elevatorproject;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatOutParam;
import com.hdos.elevatorproject.commands.LoginPara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class VideoActivity extends Activity implements AnyChatBaseEvent,AnyChatObjectEvent{

	Intent intent=null;
	SurfaceView localSurfaceView;
	SurfaceView remoteSurfaceView;
	
	
	private AnyChatCoreSDK anychatcore=null;
	private AnyChatOutParam anychatOutPara=null;
	
	private Button btnCamera;
	private LoginPara loginPara;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		intent=this.getIntent();
		
		
		btnCamera=(Button) findViewById(R.id.btnCamera);
		
		
		
		anychatcore=AnyChatCoreSDK.getInstance(null);
		
		
		anychatcore.SetBaseEvent(this);
		anychatcore.SetObjectEvent(this);
		//5.1
		localSurfaceView=(SurfaceView) findViewById(R.id.surface_local);
		remoteSurfaceView=(SurfaceView) findViewById(R.id.surface_remote);
		
		//5.2
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, 1);
		
		//5.3
		anychatcore.mSensorHelper.InitSensor(this);
		AnyChatCoreSDK.mCameraHelper.SetContext(this);
		localSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		localSurfaceView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
		
		
		 loginPara=new LoginPara("userName", "passWord", "demo.anychat.cn", 8906);
		 anychatcore.Connect(loginPara.ipAddr,loginPara.port);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
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
	
	public void Back(View view){
		setResult(69);
		anychatcore.UserCameraControl(-1, 0);
		anychatcore.UserSpeakControl(-1, 0);
		anychatcore.LeaveRoom(1);
		anychatcore.Logout();
		finish();
	}
	private boolean isCameraOpen=false;
	public void Camera(View view){
		if(!isCameraOpen){
			anychatcore.UserCameraControl(-1, 1);
			anychatcore.UserSpeakControl(-1, 1);
			btnCamera.setText("CamOn");
			isCameraOpen=true;
		}else{
			anychatcore.UserCameraControl(-1, 0);
			anychatcore.UserSpeakControl(-1, 0);
			btnCamera.setText("CamOff");
			isCameraOpen=false;
		}
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
		if(bSuccess){
			anychatcore.Login(loginPara.userName, loginPara.passWord);
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		if(dwErrorCode==0){
			anychatcore.EnterRoom(1, "");
		}else{
			System.out.println("Login Fail,errorCode="+dwErrorCode);
		}
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		if(dwErrorCode==0){
			int remodeUserId=-150659;
			System.out.println("EnterRoom success");
			int index=anychatcore.mVideoHelper.bindVideo(remoteSurfaceView.getHolder());
			anychatcore.mVideoHelper.SetVideoUser(index, remodeUserId);
			anychatcore.UserCameraControl(remodeUserId, 1);
			anychatcore.UserSpeakControl(remodeUserId, 1);
		}else{
			System.out.println("EnterRoom fail,ErrorCode="+dwErrorCode);
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
}
