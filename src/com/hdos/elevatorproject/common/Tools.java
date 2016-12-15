package com.hdos.elevatorproject.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Environment;

public class Tools {
	public static String[] parse(String str) {
		String replace_Str = str.replace("*[;", "").replace(";]", "");
		String[] out = replace_Str.split(";");
		return out;
	}

	
public static String pack(Object ...args){
	StringBuffer buffer=new StringBuffer();
	buffer.append("*[;");
	for(int i=0;i<args.length;i++){
		if(args[i]!=null){
			buffer.append(args[i]);
		}
		buffer.append(";");
	}
		buffer.append("]");
		return buffer+"";
	}


public static String errorToStr(int errCode){
	String str="";
	switch(errCode){
		//连接部分
		case 100:
			str="连接服务器超时";
			break;
		case 101:
			str="与服务器的连接中断";
			break;
		case 102:
			str="连接服务器认证失败";
			break;
		case 103:
			str="域名解析失败";
			break;
		case 104:
			str="超过授权用户数";
			break;
		case 105:
			str="服务器功能受限制";
			break;
		case 106:
			str="只能在内网使用";
			break;	
		case 107:
			str="版本太旧，不允许连接";
			break;
		case 109:
			str="嵌入式设备连接限制（没有授权）";
			break;
			
		//登陆部分
		case 200:
			str="只能在内网使用";
			break;
		case 201:
			str="该用户已登录";
			break;
		case 202:
			str="帐户已被暂时锁定";
			break;
		case 203:
			str="IP地址已被暂时锁定";
			break;
		case 204:
			str="游客登录被禁止（登录时没有输入密码）";
			break;
		case 205:
			str="无效的用户ID（用户不存在）";
			break;
		case 206:
			str="与业务服务器连接失败，认证功能失效";
			break;
		case 207:
			str="业务服务器执行任务超时";
			break;
		case 208:
			str="没有登录";
			break;
		case 209:
			str="该用户在其它计算机上登录";
			break;
			
		//进入房间
		case 300:
			str="房间已被锁住，禁止进入";
			break;
		case 301:
			str="房间密码错误，禁止进入";
			break;
		case 302:
			str="房间已满员，不能进入";
			break;
		case 303:
			str="房间不存在";
			break;
		case 304:
			str="房间服务时间已到期";
			break;
		case 305:
			str="房主拒绝进入";
			break;
		case 306:
			str="房主不在，不能进入房间";
			break;
		case 307:
			str="不能进入房间";
			break;
		case 308:
			str="已经在房间里面了，本次进入房间请求忽略";
			break;
			
		//私聊
		case 401:
			str="用户已经离开房间";
			break;
		case 402:
			str="用户拒绝了私聊邀请";
			break;
		case 403:
			str="不允许与该用户私聊，或是用户禁止私聊";
			break;
	
		case 420:
			str="私聊请求ID号错误，或请求不存在";
			break;
		case 421:
			str="已经在私聊列表中";
			break;
		case 431:
			str="私聊请求超时";
			break;
		case 432:
			str="对方正在私聊中，繁忙状态";
			break;
		case 433:
			str="对方用户关闭私聊";
			break;
		case 434:
			str="用户自己关闭私聊";
			break;
		case 435:
			str="私聊请求被取消";
			break;
			
		//Mic控制权
		case 500:
			str="说话时间太长，请休息一下";
			break;
		case 501:
			str="有高级别用户需要发言，请休息一下";
			break;
			
		//修改昵称
		case 600:
			str="该昵称已被使用，请换用其它的昵称";
			break;
		
		//传输部分
		case 700:
			str="创建任务失败";
			break;
		case 701:
			str="没有该任务，或是任务已完成";
			break;
		case 710:
			str="打开文件出错";
			break;
		case 711:
			str="文件长度为0";
			break;
		case 712:
			str="文件长度太大";
			break;
		case 713:
			str="读文件出错";
			break;
		
		//录像部分
		case 720:
			str="没有录像任务";
			break;
		case 721:
			str="创建录像任务失败";
			break;
		
		//SDK警告
		case 800:
			str="与服务器的UDP通信异常，流媒体服务将不能正常工作";
			break;
		case 801:
			str="SDK加载brMiscUtil.dll动态库失败，部分功能将失效";
			break;
		case 802:
			str="SDK加载brMediaUtil.dll动态库失败，部分功能将失效";
			break;
	
		default:
			str=errCode+"";
			break;
	}
	return str;
}

	public  static int getIntProperity(Object obj){
		return Integer.valueOf((String) obj);
	}
	
	
	
	
public static boolean saveTerminalData(String data ) {
		
		try {
			
//			String path ="/data/data/com.hdos.elevatorproject/Run.txt";
			String path =Environment.getExternalStorageDirectory()+ "/data.txt";
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			
			fos.write(data.getBytes());
			
			fos.flush();
			
			fos.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getTerminalData() {
		FileInputStream fis =null;
		try {
			String path =Environment.getExternalStorageDirectory()+ "/data.txt";
			 fis = new FileInputStream(path);
			
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			
			StringBuffer sb = new StringBuffer();
			String  data = "";
			while((data = reader.readLine()) != null)
			{
				sb.append(data);
			}
				fis.close();
				return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		return null;
	}
	
	public static String stringToJstring(Map ret){
		return "{"+"\n"
         +  "\"head\" : " + "{"+"\n"
         +  "\"code\" : "
         + "\""
         + ret.get("code")
         + "\","+"\n"
         + "\"msg\" :"
         + " \""
         + ret.get("msg")
         + "\""+"\n"
         + "},"+"\n"
         
         +  "\"body\" : {"+"\n"
         +  "\"tradeId\" : "
         + "\""
         + ret.get("tradeId")
         + "\","+"\n"
         + "\"Version\" : "
         + "\""
         + ret.get("Version")
         + "\""+"\n"
         + "}"+"\n"
         +   "}";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//"*[;9000;70413463;5234658974;123;123;123;0;0;]";
		System.out.println(pack(9000,70413463,"5234658974",123,123,null,0,0));
	}

}
