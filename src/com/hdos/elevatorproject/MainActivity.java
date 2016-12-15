package com.hdos.elevatorproject;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.softwinner.update.ProcCpuInfo;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends InstrumentedActivity {
	
	
	public static boolean isForeground = false;
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private Context context;//������
	private Button btnQrcode;//��ɶ�ά�밴ť
	private ImageView qrcodeImageView = null;  
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_jpush);
		context = this;
	    //JPushInterface.setDebugMode(true);
		initView();
	    JPushInterface.init(this);
	    initData();
	    initEvent();
	    registerMessageReceiver();  // used for receive msg
	}
	
	
	private void initEvent() {
		// TODO Auto-generated method stub
		btnQrcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "��ɶ�ά��", Toast.LENGTH_LONG).show();
				/*if (!Constant.baseCache.exists()) {
					Constant.baseCache.mkdirs();
				}*/
				 try {  
		                //��ɶ�ά��ͼƬ����һ�������Ƕ�ά������ݣ��ڶ�������������ͼƬ�ı߳�����λ������  
					 //��ɶ�ά�룬Ȼ��Ϊ��ά������logo
				        Bitmap qrcodeBitmap= EncodingUtils.createQRCode("1234567890,0b020580574948488065778416516609", 800, 800,null);
		                //Bitmap  = EncodingUtils.(context, 400);  
		                qrcodeImageView.setImageBitmap(qrcodeBitmap);  
		            } catch (Exception e) {  
		                // TODO Auto-generated catch block  
		                e.printStackTrace();  
		            }  
				String path=Constant.baseCache.getPath(); 
				Toast.makeText(context, path+"", Toast.LENGTH_LONG).show();

			}
		});
	}


	private void initView() {
		// TODO Auto-generated method stub
		btnQrcode = (Button) findViewById(R.id.btn_qrcode);
		qrcodeImageView = (ImageView) findViewById(R.id.img_qrcode);
		
	}


	private void initData() {
		// TODO Auto-generated method stub
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
                    handler.sendEmptyMessage(Constant.TIME_OUT_HANDLE);
                } else {
                    handler.sendMessage(handler.obtainMessage(Constant.REGIST_TOKEN_HANDLER, result));
                }
            }
        }).start();
		
	}
	
	 /**
     * 处理访问网络的返回结果
     */
    private Handler handler = new Handler() {
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


	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}


	protected void handlerResigt(String resRegist) {
		// TODO Auto-generated method stub
		Toast.makeText(context, resRegist, Toast.LENGTH_LONG).show();
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}
	
	
	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
              String messge = intent.getStringExtra(KEY_MESSAGE);
              String extras = intent.getStringExtra(KEY_EXTRAS);
              StringBuilder showMsg = new StringBuilder();
              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
              
              if (!ExampleUtil.isEmpty(extras)) {
            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
              }
              //setCostomMsg(showMsg.toString());
              //通过msg来判断如何操作
              Toast.makeText(context, showMsg.toString(), Toast.LENGTH_LONG).show();
              System.out.println("Get JPush:"+showMsg.toString());
              //
			}
		}
	}
	
	
	
	
}
