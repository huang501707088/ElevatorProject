package com.hdos.elevatorproject;

import java.io.IOException;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatOutParam;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class FileActivity extends Activity implements AnyChatBaseEvent,AnyChatObjectEvent,AnyChatTransDataEvent,AnyChatUserInfoEvent{

	private AnyChatCoreSDK anychatcore=null;
	private AnyChatOutParam anychatOutPara=null;
	private int FileRoomId=4;
	private int FriendId=-142812;
	
	private int mTastID = 0;					// 任务id
	private ProgressDialog mProgressDialog;		// 发送文件进度框
	
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		//AnyChatOutParam mAnyChatOutParam=new AnyChatOutParam();
		@Override
		public void run() {
			try {
				int returnFlag = anychatcore.QueryTransTaskInfo(-1,
								mTastID,
								AnyChatDefine.BRAC_TRANSTASK_PROGRESS,
								anychatOutPara);
				System.out.println("returnFlag="+returnFlag);
				if (returnFlag == 0){
					handleProgressDlg();
					mProgressDialog.setProgress(anychatOutPara.GetIntValue());
				}
				else {
					handler.removeCallbacks(runnable);
					return;
				}

				if (anychatOutPara.GetIntValue() == 100) {
					mProgressDialog.dismiss();
					handler.removeCallbacks(runnable);
					return;
				} 

				handler.postDelayed(runnable, 500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@SuppressWarnings("deprecation")
	private void handleProgressDlg(){
		if (mProgressDialog == null){
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setMessage("正在传送");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (anychatOutPara != null){
						anychatcore.CancelTransTask(-1, mTastID);
						handler.removeCallbacks(runnable);
						mProgressDialog.cancel();
						mProgressDialog = null;
					}
				}
			});
		}
		
		mProgressDialog.show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		anychatcore=AnyChatCoreSDK.getInstance(null);
		anychatOutPara=new AnyChatOutParam();
		
		anychatcore.SetBaseEvent(this);
		anychatcore.SetObjectEvent(this);
		anychatcore.SetUserInfoEvent(this);
		anychatcore.SetTransDataEvent(this);
		
		anychatcore.EnterRoom(FileRoomId, "");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file, menu);
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
		anychatcore.LeaveRoom(FileRoomId);
		setResult(69);
		finish();
	}
	
	public void Transfer(View view){
//		String path=getResources().getResourceEntryName(R.raw.test_photo);
//		System.out.println("Path="+path);
//		String[] strs=null;
//		try {
//			 strs=getResources().getAssets().list("");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(null!=strs){
//			for(int i=0;i<strs.length;i++){
//				System.out.println(String.format("strs[%d]=%s", i,strs[i]));
//			}
//		}else{
//			System.out.println("null!=strs");
//		}
		int ret=anychatcore.TransFile(FriendId, "/storage/emulated/0/Tencent/QQfile_recv/test_photo.png", 0, 0, 0, this.anychatOutPara);
		mTastID = anychatOutPara.GetIntValue();
		handler.postDelayed(runnable, 500); //每隔1s执行
		Toast("Done,TastID="+mTastID+"\nret="+ret);
		

	}

	@Override
	public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
		// TODO Auto-generated method stub
		//String str_format="FileActivity,%s.%s=%s,%s=%s";
		//System.out.println(String.format(str_format,"UserInfoUpdate","dwUserId",dwUserId,"dwType",dwType ));
	}

	@Override
	public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatTransFile(int dwUserid, String FileName,
			String TempFilePath, int dwFileLength, int wParam, int lParam,
			int dwTaskId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
		// TODO Auto-generated method stub
		
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
		
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		if(!bEnter){
			Toast(""+dwUserId+"leave Room");
		}else{
			Toast(""+dwUserId+"Enter Room");
		}
		
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}
	
	private void Toast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}
