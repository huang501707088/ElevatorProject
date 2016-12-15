package com.hdos.elevatorproject;

import java.io.File;

import android.os.Environment;

public class Constant {
	
	public final static String baseUrl = "http://14.17.77.85:6112";
	
	public static final File baseCache = new File(
			Environment.getExternalStorageDirectory() + "/jyzd");
	
	/** 设置服务器超时时间*/
    public static final int SERVER_TIME_OUT = 30000;
    
    public static final int TIME_OUT_HANDLE = 1;//连接服务器超时
    
    public static final int REGIST_TOKEN_HANDLER = 2;//注册推送

}
