package com.huada.connectinternet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ConnectInterner {
	
	public Map<String,String> SyncDevInfo(String keyAddr){
	
		HttpURLConnection conn=null;
		
		Map<String, String> ret = new HashMap<String, String>();
	try {
		 conn=(HttpURLConnection) new URL(keyAddr).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		if(conn.getResponseCode()==200){
			
			
			// 获取响应的输入流对象
			InputStream is = conn.getInputStream();

			// 创建字节输出流对象
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int len = 0;
			// 定义缓冲区
			byte buffer[] = new byte[2048];
			// 按照缓冲区的大小，循环读取
			while ((len = is.read(buffer)) != -1) {
				// 根据读取的长度写入到os对象中
				os.write(buffer, 0, len);
			}
			// 释放资源
			is.close();
			os.close();
			// 返回字符串
			String result = new String(os.toByteArray());
			System.out.println("result =="+result);
			JSONTokener jsonTokener=new JSONTokener(result);
			JSONObject resultJson=(JSONObject) jsonTokener.nextValue();
			JSONObject head=resultJson.getJSONObject("head");
			String code=head.getString("code");
			String msg=head.getString("msg");
			
			JSONObject body=resultJson.getJSONObject("body");
			String tradeId=body.getString("tradeId");
			String Version=body.getString("Version");
			ret.put("code", code);
			ret.put("msg", msg);
			ret.put("tradeId", tradeId);
			ret.put("Version", Version);
			ret.put("result", result);

			if(code.equals(0000)){
				ret.put("InstallFlag", body.getString("InstallFlag"));
				ret.put("LiftRegCode", body.getString("LiftRegCode"));
				ret.put("LiftHelpCode", body.getString("LiftHelpCode"));
				ret.put("CPUCode", body.getString("CPUCode"));
				ret.put("NFCCode", body.getString("NFCCode"));
				ret.put("RFICCardNo", body.getString("RFICCardNo"));
				ret.put("QRCode", body.getString("QRCode"));
				ret.put("TermNo", body.getString("TermNo"));
				ret.put("Longitude", body.getString("Longitude"));
				ret.put("Dimension", body.getString("Dimension"));
				ret.put("AdServerUrl", body.getString("AdServerUrl"));
				ret.put("adManager", body.getString("adManager"));
				ret.put("LiftAddress", body.getString("LiftAddress"));	
			}
			else{
				conn.disconnect();
				return null;
			}

			
		

		}else{
			System.out.println("Connect Error");
		}
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}finally{
		if(conn!=null){
			conn.disconnect();
			return ret;
		}
	}
	return ret;
	}


}
