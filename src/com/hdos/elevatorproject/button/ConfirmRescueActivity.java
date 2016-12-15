package com.hdos.elevatorproject.button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.hdos.elevatorproject.MainActivity;
import com.hdos.elevatorproject.R;
import com.hdos.elevatorproject.RescueActivity;
import com.hdos.elevatorproject.button.TriggerRescueActivity.mBroadcastReceiver;
import com.hdos.elevatorproject.common.Constants;
import com.hdos.elevatorproject.common.Tools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmRescueActivity extends Activity {
	private TextView tvCountDown;
	private myCountDownTimer mCoundDownTimer;
	private ButtonThread button;
	private mBroadcastReceiver myBroadcastReceiver;
	
	
	/*take photo*/
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private boolean isBackOpened = false;
	private Camera mCamera;
	private String LocalHelpCode=null;
	
	private String PicturePathToUpload=null;
	/**
	 * 定义图片保存的路径和图片的名字
	 */
	//public final static String PHOTO_PATH = "/mnt/sdcard/usr/data/rescuepic";
	public final static String PHOTO_PATH = Environment.getExternalStorageDirectory()+"/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_rescue);
		tvCountDown = (TextView) findViewById(R.id.tvCountDown);

		mCoundDownTimer = new myCountDownTimer(32000, 1000);
		mCoundDownTimer.start();
		
		myBroadcastReceiver= new mBroadcastReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(CheckButtonThread.BtnPress);
		filter.setPriority(100);
		registerReceiver(myBroadcastReceiver, filter);
		initLocalData();
		initPhoto();
		takePhoto();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirm_rescue, menu);
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
		if(myBroadcastReceiver!=null){
			unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver=null;
		}
	}

	private void initPhoto(){
		mSurfaceView = (SurfaceView) findViewById(R.id.back_surfaceview);
		mHolder = mSurfaceView.getHolder();
		
	}
	class myCountDownTimer extends CountDownTimer {

		public myCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			if (millisUntilFinished < 31000) {
				String str=null;
//				if(millisUntilFinished < 10000)
//					str="再按报警救援键三秒，启动救援通话(0%d秒后退出)";
//				else str="再按报警救援键三秒，启动救援通话(%d秒后退出)";
				if(millisUntilFinished < 10000)
					str="确认困梯，请再按报警键;不按键，0%d秒后退出报警";
				else str="确认困梯，请再按报警键;不按键，%d秒后退出报警";
				
				tvCountDown.setText(String.format(str, millisUntilFinished / 1000 ));
			}
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			finish();
			//backToFirst();
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
			System.out.println("Confirm Rescue  Button Detect Start");
			while (isRunning) {
				value = readButton();
				// System.out.println("GPIO Value=" + value);
				if (value ==  Constants.ButtonOn ) {
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
									Intent intent = new Intent(context,
											RescueActivity.class);
									startActivity(intent);
									
//									Intent intent =new Intent(this,RescueActivity.class);
//									int requestCode=RESCUE_ATY;
//									startActivityForResult(intent, requestCode);
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
					System.out.println("Confirm Rescue ButtonThread Finish");
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
	

	
	class mBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle=arg1.getExtras();
			Toast.makeText(getApplicationContext(), "ConfirmRescueActivity Get Button,Duration="+bundle.getString("Duration"), Toast.LENGTH_SHORT).show();
			abortBroadcast();
			System.out.println("ConfirmRescueActivity Get Button,Duration="+bundle.getString("Duration"));
			//if("long".equals(bundle.getString("Duration"))){
				mCoundDownTimer.cancel();
				Intent intent = new Intent(ConfirmRescueActivity.this,
						RescueActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("PICPATH", PicturePathToUpload);
				startActivity(intent);
			//}
		}
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		clearReceiver();
	}
	
	void setReceiver(){
		myBroadcastReceiver= new mBroadcastReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(CheckButtonThread.BtnPress);
		filter.setPriority(10);
		registerReceiver(myBroadcastReceiver, filter);
	}
	void clearReceiver(){
		if(myBroadcastReceiver!=null){
			unregisterReceiver(myBroadcastReceiver);
			myBroadcastReceiver=null;
		}
	}
	
	
	
	private void takePhoto() {

		mCamera = Camera.open();

		if (isBackOpened == false && mCamera != null) {

			try {
				// 这里的myCamera为已经初始化的Camera对象
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				e.printStackTrace();
				// 如果出错立刻进行处理，停止预览照片
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			// -------------- 如果成功开始实时预览
			mCamera.startPreview();

			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO 空实现

				}
			});
			isBackOpened = true;
			// 拍照

			mCamera.takePicture(null, null, new PictureCallback() {

				@Override
				public void onPictureTaken(final byte[] data, final Camera camera) {
					// 将得到的照片进行90°旋转，使其竖直
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					Matrix matrix = new Matrix();
					matrix.preRotate(0);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					// 创建并保存图片文件
					File mFile = new File(PHOTO_PATH);
					if (!mFile.exists()) {
						mFile.mkdirs();
					}

					final String fileName = getPhotoFileName();
					final File pictureFile = new File(PHOTO_PATH, fileName);

					try {
						if (!pictureFile.exists())
							pictureFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						FileOutputStream fos = new FileOutputStream(pictureFile);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
						bitmap.recycle();
						fos.close();

						scannerByReceiver(ConfirmRescueActivity.this, pictureFile.getAbsolutePath());
						Log.i(getLocalClassName(), "拍摄成功！");
					} catch (Exception error) {
						Log.e(getLocalClassName(), "拍摄失败");
						error.printStackTrace();
					} finally {
						camera.stopPreview();
						camera.release();
						Log.e(getLocalClassName(), "Finish:" +PicturePathToUpload);
						//finish();

					}

				}
			});
		}
	}
	
	private void initLocalData() {
		
		
		
		// Real local File For Installation Data
		if (Tools.getTerminalData() != null) {
			String str = Tools.getTerminalData();// 文件存在、读取解析
			if (str != null) {
				JSONObject resultJson;
				try {
					resultJson = new JSONObject(str);

					JSONObject head = resultJson.getJSONObject("head");
					JSONObject body = resultJson.getJSONObject("body");
					LocalHelpCode = body.getString("LiftHelpCode");
					// etBarcodeNo.setText(body.getString("QRCode"));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	
	
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		PicturePathToUpload=PHOTO_PATH+LocalHelpCode+"-"+dateFormat.format(date) + ".jpg";
		return LocalHelpCode+"-"+dateFormat.format(date) + ".jpg";
	}

	/** 扫描更新图库图片 **/
	private void scannerByReceiver(Context context, String path) {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
	}
	
}
