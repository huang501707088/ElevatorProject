package com.huada.serialcommunication;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * 
 * 
 * 2016.11.23  16.38 跟梁杰调通了以下接口  版本V1.0
 * 	private native  JniReturnData getReadheadCode(JniReturnData returnData,String port);
	private native  JniReturnData sendReadheadCode(JniReturnData returnData,String port,int codeLenth,byte[] code);
	private native  JniReturnData getSignin(JniReturnData returnData,String port);
	private native  JniReturnData sendCPUCode(JniReturnData returnData,String port,int codeLenth,byte[] code);
	private native  JniReturnData getCPUCode(JniReturnData returnData,String port);
 * 
 * 
 * @author HuangYq
 *
 */


public class SerialCommunication {

	static {
		System.loadLibrary("SerialCommunication");
	}
	private native  JniReturnData getReadheadCode(JniReturnData returnData,String port);
	private native  JniReturnData sendReadheadCode(JniReturnData returnData,String port,int codeLenth,byte[] code);
	
	private native  JniReturnData getSignin(JniReturnData returnData,String port);
	
	private native  JniReturnData sendCPUCode(JniReturnData returnData,String port,int codeLenth,byte[] code);
	private native  JniReturnData getCPUCode(JniReturnData returnData,String port);
	
	private native  JniReturnData downloadSM1(JniReturnData returnData,String port,int keyType,int keyLength,byte[] key);
	private native  JniReturnData downloadKey(JniReturnData returnData,String port,int keyType,int keyLength,byte[] key);
	
	private native  JniReturnData ReadIDCard(JniReturnData returnData,String port);
	private native  JniReturnData TypeAPowerOn(JniReturnData returnData,String port );
	private native  JniReturnData TypeBPowerOn(JniReturnData returnData,String port );
	private native  JniReturnData ICCReaderPrePowerOn(JniReturnData returnData,String port,byte ICCSlotNo);
	private native  JniReturnData ICCReaderGetStatus(JniReturnData returnData,String port,byte ICCSlotNo);
	private native  JniReturnData ICCReaderApplication(JniReturnData returnData,String port,byte ICCSlotNo,int lengthofAPDU,byte[] APDU);
	private native  JniReturnData PICCReaderApplication(JniReturnData returnData,String port,int lengthofAPDU,byte[] APDU);
	private native  JniReturnData readMagCard(JniReturnData returnData,String port,int outTime,int track);
	private native byte[] HdosIdUnpack(byte[] inputImpBuffer,String pkName);

	
	/**
	 * 函数名称：Rcard(int ctime, int track, int[] rlen, byte[] getdata);
	 * 函数功能：读取磁条卡
	 * 入口参数：1、ctime：设置刷卡超时时间、单位为s
	 				   2、track：第几磁道 参数1-3
	 * 出口参数：返回值小于0则读取失败，否则读取成功。
					   1、rlen[0]：磁条数据长度
					   2、getdata[]：磁条数据
	 */
	public int readMagCard(String port,int outTime,int track,byte[] response){
		
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=readMagCard(returnData,port,outTime, track);
		re=returnData.result;
		if(returnData.result<0){//读磁条卡失败  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.receveDataLen);
		System.arraycopy(receive, 0, response, 0, returnData.receveDataLen);	
		return returnData.receveDataLen;
	}
	
	/**
	 * 函数名称：PICCReaderApplication(String port ,int lenthofAPDU, byte[] commandAPDU,
			byte[] response)
	 * 函数功能：Type a/b执行apdu命令
	 * 入口参数：
		 			 1、int Lenth_of_Command_APDU：APDU命令长度
		 			 2、byte[] Command_APDU：APDU命令
	 * 出口参数：返回值小于0则执行APDU命令失败 大于0则执行APDU命令成功返回值为执行APDU命令的数据长度
	  					byte[]Response：执行APDU命令的数据
	 */
	public int PICCReaderApplication(String port ,int lenthofAPDU, byte[] commandAPDU,
			byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=PICCReaderApplication(returnData,port,lenthofAPDU,commandAPDU);
		re=returnData.result;
		if(returnData.result<0){//读身份证出错  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	/**
		mainKey
	 */
	public int downloadKey(String port ,int keyType,int keyLenth, byte[] key,
			byte[] response) {
		JniReturnData returnData =new JniReturnData();
		returnData=downloadKey(returnData,port,keyType,keyLenth,key);
		return returnData.result;
	}
	public int downloadSM1(String port ,int keyType,int keyLenth, byte[] key,
			byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=downloadSM1(returnData,port,keyType,keyLenth,key);
		re=returnData.result;
		if(returnData.result<0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	public int sendReadheadCode(String port ,int codeLenth, byte[] code,
			byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=sendReadheadCode(returnData,port,codeLenth,code);
		re=returnData.result;
		if(returnData.result<=0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	public int getReadheadCode(String port ,byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=getReadheadCode(returnData,port);
		re=returnData.result;
		if(returnData.result<=0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	public int getSignin(String port ,byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=getSignin(returnData,port);
		re=returnData.result;
		if(returnData.result<=0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}

	public int sendCPUCode(String port ,int codeLenth, byte[] code,
			byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=sendCPUCode(returnData,port,codeLenth,code);
		re=returnData.result;
		if(returnData.result<=0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	
	public int getCPUCode(String port ,byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=getCPUCode(returnData,port);
		re=returnData.result;
		if(returnData.result<=0){
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	/**
	 * 函数名称：ICC_Reader_Application(byte ICC_Slot_No,int Lenth_of_Command_APDU, byte[]Command_APDU, byte[] Response_APDU)
	 * 函数功能：IC卡执行apdu命令
	 * 入口参数：
		 			 1、int Lenth_of_Command_APDU：APDU命令长度
		 			 2、byte[] Command_APDU：APDU命令
		 			 3、ICC_Slot_No：IC卡卡座
	  					ICC_Slot_No=0x01 大卡座
	  					ICC_Slot_No=0x11 PASM1
	  					ICC_Slot_No=0x12 PASM2
	  					ICC_Slot_No=0x13 PASM3
	  					ICC_Slot_No=0x14 PASM4
	 * 出口参数：返回值小于0则执行APDU命令失败 大于0则执行APDU命令成		功返回值为执行APDU命令的数据长度
	  	byte[]Response：执行APDU命令的数据
	 */
	public int ICCReaderApplication(String port,byte ICCSlotNo, int lenthofAPDU, byte[] commandAPDU,
			byte[] response) {
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=ICCReaderApplication(returnData,port,ICCSlotNo, lenthofAPDU,commandAPDU);
		re=returnData.result;
		if(returnData.result<0){//读身份证出错  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	public int ICRunAPDU(String port,String commandString,byte[] response){
		int ret=0;
    	byte[] apdu = new byte[commandString.length() / 2];
		apdu = HexString2Bytes(commandString);
		byte ICCSlotNo=0x01;
		ret=ICCReaderApplication(port,ICCSlotNo,commandString.length() / 2,apdu, response);
		if(ret<1)return -1;
		byte[] temp = new byte[ret];
		System.arraycopy(response, 0, temp, 0, ret);	
		
		return ret;
		
	}
	/*还有问题*/
	public  Map<String,String> readSocialSecurityCard(String port){
		
		Map<String, String> ret = new HashMap<String, String>();
		byte[] recBuffer = new byte[512];
		int re=0;
		 re =ICCPrePowerOn(port, (byte) 0x01, recBuffer);
		 if(re<0)
			 {
			 	ret.put("result", "-1");
			 	return ret;
			 }
		ICRunAPDU(port,"00a404000f7378312e73682ec9e7bbe1b1a3d5cf",recBuffer);
		ICRunAPDU(port,"00a4020002ef05",recBuffer);
		
		//读取社保卡卡号
		re=ICRunAPDU(port,"00b207040B",recBuffer);
		
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		ret.put("cardNo", new String(recBuffer, 2,9));
		
		//选择EF06文件 
		ICRunAPDU(port,"00a4000002ef06",recBuffer);
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		//读取社会保障号码
		re=ICRunAPDU(port,"00B2080014",recBuffer);
		
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		ret.put("SocialSecurityCardNo", new String(recBuffer, 2,18));
//		for(int i=2;i<20;i++)
//			 System.out.println("recBuffer["+i+"]= "+Integer.toHexString(recBuffer[i]&0xff));
		
		
		re=ICRunAPDU(port,"00B2090020",recBuffer);
		
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		try {
				ret.put("cardName", new String(recBuffer, 2, findValidChar(recBuffer, 2, 30)-2, "GBK"));
		} catch (UnsupportedEncodingException e) {

				}

		//性别
		re=ICRunAPDU(port,"00B20A0003",recBuffer);
		
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		if((byte)0x31==recBuffer[2]) ret.put("cardSex", "男");
		else if((byte)0x32==recBuffer[2]) ret.put("cardSex", "女");
		else ret.put("cardSex", "其他");

		//民族
		re=ICRunAPDU(port,"00B20B0003",recBuffer);
		int nation = ((recBuffer[2]&0xf0)>>>4)*10+(recBuffer[2]&0x0f);
		String m_nation = null;
		switch (nation) {
		case 01:
			m_nation = "汉";
			break;
		case 02:
			m_nation = "蒙古";
			break;
		case 03:
			m_nation = "回";
			break;
		case 04:
			m_nation = "藏";
			break;
		case 05:
			m_nation = "维吾尔";
			break;
		case 06:
			m_nation = "苗";
			break;
		case 07:
			m_nation = "彝";
			break;
		case 8:
			m_nation = "壮";
			break;
		case 9:
			m_nation = "布依";
			break;
		case 10:
			m_nation = "朝鲜";
			break;
		case 11:
			m_nation = "满";
			break;
		case 12:
			m_nation = "侗";
			break;
		case 13:
			m_nation = "瑶";
			break;
		case 14:
			m_nation = "白";
			break;
		case 15:
			m_nation = "土家";
			break;
		case 16:
			m_nation = "哈尼";
			break;
		case 17:
			m_nation = "哈萨克";
			break;
		case 18:
			m_nation = "傣";
			break;
		case 19:
			m_nation = "黎";
			break;
		case 20:
			m_nation = "傈僳";
			break;
		case 21:
			m_nation = "佤";
			break;
		case 22:
			m_nation = "畲";
			break;
		case 23:
			m_nation = "高山";
			break;
		case 24:
			m_nation = "拉祜";
			break;
		case 25:
			m_nation = "水";
			break;
		case 26:
			m_nation = "东乡";
			break;
		case 27:
			m_nation = "纳西";
			break;
		case 28:
			m_nation = "景颇";
			break;
		case 29:
			m_nation = "柯尔克孜";
			break;
		case 30:
			m_nation = "土";
			break;
		case 31:
			m_nation = "达斡尔";
			break;
		case 32:
			m_nation = "仫佬";
			break;
		case 33:
			m_nation = "羌";
			break;
		case 34:
			m_nation = "布朗";
			break;
		case 35:
			m_nation = "撒拉";
			break;
		case 36:
			m_nation = "毛南";
			break;
		case 37:
			m_nation = "仡佬";
			break;
		case 38:
			m_nation = "锡伯";
			break;
		case 39:
			m_nation = "阿昌";
			break;
		case 40:
			m_nation = "普米";
			break;
		case 41:
			m_nation = "塔吉克";
			break;
		case 42:
			m_nation = "怒";
			break;
		case 43:
			m_nation = "乌孜别克";
			break;
		case 44:
			m_nation = "俄罗斯";
			break;
		case 45:
			m_nation = "鄂温克";
			break;
		case 46:
			m_nation = "德昂";
			break;
		case 47:
			m_nation = "保安";
			break;
		case 48:
			m_nation = "裕固";
			break;
		case 49:
			m_nation = "京";
			break;
		case 50:
			m_nation = "塔塔尔";
			break;
		case 51:
			m_nation = "独龙";
			break;
		case 52:
			m_nation = "鄂伦春";
			break;
		case 53:
			m_nation = "赫哲";
			break;
		case 54:
			m_nation = "门巴";
			break;
		case 55:
			m_nation = "珞巴";
			break;
		case 56:
			m_nation = "基诺";
			break;

		default:
			break;

		}
		ret.put("nation",m_nation);//CFMLY
		
		//读取出生日期
		re=ICRunAPDU(port,"00B20D0006",recBuffer);
		
		 if(re<0)
		 {
		 	ret.put("result", "-1");
		 	return ret;
		 }
		byte [] year=new byte[4];
		year[0]=(byte) (((recBuffer[2]<0? recBuffer[2]+256:recBuffer[2])>>4)+0x30);
		year[1]=(byte) (((recBuffer[2]<0? recBuffer[2]+256:recBuffer[2])&0x0f)+0x30);
		year[2]=(byte) (((recBuffer[3]<0? recBuffer[3]+256:recBuffer[3])>>4)+0x30);
		year[3]=(byte) (((recBuffer[3]<0? recBuffer[3]+256:recBuffer[3])&0x0f)+0x30);
		
		byte [] moth=new byte[2];
		moth[0]=(byte) (((recBuffer[4]<0? recBuffer[4]+256:recBuffer[4])>>4)+0x30);
		moth[1]=(byte) (((recBuffer[4]<0? recBuffer[4]+256:recBuffer[4])&0x0f)+0x30);;
		
		byte [] date=new byte[2];
		date[0]=(byte) (((recBuffer[5]<0? recBuffer[5]+256:recBuffer[5])>>4)+0x30);
		date[1]=(byte) (((recBuffer[5]<0? recBuffer[5]+256:recBuffer[5])&0x0f)+0x30);
		ret.put("birthday", new String(year)+"年"+
				new String(moth)+"月"+
				new String(date)+"日");
		

		ret.put("result", "0");
		return ret;
		

		
	}
	private int findValidChar(byte[] recBuffer, int i, int j) {
		// TODO Auto-generated method stub
		int k=0;
		for(k=i;k<j;k++)
		{
			if(recBuffer[k]==0x00) break;
		}
		return k;
	}
	
	/**
	 * 函数名称：PICC_Reader_PowerOnTypeA(byte[] Response)
	 * 函数功能：TypeA上电
	 * 入口参数：byte[]Response
	 * 出口参数：返回值大于0则上电成功返回值为TypeA卡片上电复位的数据长度
	  					byte[]Response：TypeA卡片上电复位的数据
	 */
	public int PICCTypeAPowerOn(String port,byte[] response)
	{

		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=TypeAPowerOn(returnData,port);
		re=returnData.result;
		if(returnData.result<0){//读身份证出错  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	/**
	 * 函数名称：PICC_Reader_PowerOnTypeB(byte[] Response)
	 * 函数功能：TypeA上电
	 * 入口参数：byte[]Response
	 * 出口参数：返回值大于0则上电成功返回值为TypeA卡片上电复位的数据长度
	  					byte[]Response：TypeA卡片上电复位的数据
	 */
	public int PICCTypeBPowerOn(String port,byte[] response)
	{
		
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=TypeBPowerOn(returnData,port);
		re=returnData.result;
		if(returnData.result<0){//读身份证出错  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	/**
	 * 函数名称：ICC_Reader_pre_PowerOn(byte ICC_Slot_No,byte[] Response);
	 * 函数功能：IC卡下电
	 * 入口参数：ICC_Slot_No：IC卡卡座
	  					ICC_Slot_No=0x01 大卡座
	  					ICC_Slot_No=0x11 PASM1
	  					ICC_Slot_No=0x12 PASM2
	  					ICC_Slot_No=0x13 PASM3
	  					ICC_Slot_No=0x14 PASM4
						byte[] Response = new byte[512];

	 * 出口参数：返回值小于0则下电失败 大于0则上电成功返回值为卡片上电复位的数据长度
	  					byte[]Response：卡片上电复位的数据
	 */
	public int ICCPrePowerOn(String port,byte ICCSlotNo,byte[] response)//冷复位
	{
		int re=0;
		JniReturnData returnData =new JniReturnData();
		returnData=ICCReaderPrePowerOn(returnData,port,ICCSlotNo);
		re=returnData.result;
		if(returnData.result<0){//读身份证出错  程序返回
			return re;
		}
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		System.arraycopy(receive, 0, response, 0, re);	
		return re;
	}
	/**
	 * 函数名称：ICCReaderGetStatus(byte ICC_Slot_No,byte[] Response);
	 * 函数功能：获取卡座状态
	 * 入口参数：ICC_Slot_No：IC卡卡座
	  					ICC_Slot_No=0x01 大卡座
	  					ICC_Slot_No=0x11 PASM1
	  					ICC_Slot_No=0x12 PASM2
	  					ICC_Slot_No=0x13 PASM3
	  					ICC_Slot_No=0x14 PASM4
						byte[] Response = new byte[512];

	 * 出口参数：
	 */
	public int ICCReaderGetStatus(String port,byte ICCSlotNo,byte[] response)//冷复位
	{
		JniReturnData returnData =new JniReturnData();
		returnData=ICCReaderGetStatus(returnData,port,ICCSlotNo);
		return returnData.result;
	}
	
	
	
	public IDCardInfo readIDCard(String port,String pkName){
		IDCardInfo idCardInfo =new IDCardInfo();
		JniReturnData returnData =new JniReturnData();
		returnData=ReadIDCard(returnData,port);
		idCardInfo.Result=returnData.result;
		System.out.println("idCardInfo.Result="+idCardInfo.Result);
		if(returnData.result<0){//读身份证出错  程序返回
			return idCardInfo;
		}
		

		try {
			
		byte[] receive=null;
		receive=strToHex(returnData.receveData,returnData.result);
		
		byte[] name = new byte[32];
		name[0] = (byte) 0xff;
		name[1] = (byte) 0xfe;
		System.arraycopy(receive, 0, name, 2, 30);	
		idCardInfo.Name= new String(name, "Unicode");
		
	byte[] sex = new byte[2];
		
		System.arraycopy(receive, 30, sex, 0, 2);
		byte[] sexout = null;
		if (sex[0] == 0x31) {
			idCardInfo.SexL="男";
		} else if (sex[0] == 0x32) {
			idCardInfo.SexL="女";
		} else {
			idCardInfo.SexL="其他";
		}

		byte[] nation = new byte[4];
		byte[] nationasc = new byte[2];
		byte[] nationout = null;
		System.arraycopy(receive, 32, nation, 0, nation.length);
		for (int i = 0; i < nationasc.length; i++) {
			nationasc[i] = nation[2 * i];
		}
		int na = Integer.parseInt(new String(nationasc));
		String m_nation = null;
		switch (na) {
		case 01:
			m_nation = "汉";
			break;
		case 02:
			m_nation = "蒙古";
			break;
		case 03:
			m_nation = "回";
			break;
		case 04:
			m_nation = "藏";
			break;
		case 05:
			m_nation = "维吾尔";
			break;
		case 06:
			m_nation = "苗";
			break;
		case 07:
			m_nation = "彝";
			break;
		case 8:
			m_nation = "壮";
			break;
		case 9:
			m_nation = "布依";
			break;
		case 10:
			m_nation = "朝鲜";
			break;
		case 11:
			m_nation = "满";
			break;
		case 12:
			m_nation = "侗";
			break;
		case 13:
			m_nation = "瑶";
			break;
		case 14:
			m_nation = "白";
			break;
		case 15:
			m_nation = "土家";
			break;
		case 16:
			m_nation = "哈尼";
			break;
		case 17:
			m_nation = "哈萨克";
			break;
		case 18:
			m_nation = "傣";
			break;
		case 19:
			m_nation = "黎";
			break;
		case 20:
			m_nation = "傈僳";
			break;
		case 21:
			m_nation = "佤";
			break;
		case 22:
			m_nation = "畲";
			break;
		case 23:
			m_nation = "高山";
			break;
		case 24:
			m_nation = "拉祜";
			break;
		case 25:
			m_nation = "水";
			break;
		case 26:
			m_nation = "东乡";
			break;
		case 27:
			m_nation = "纳西";
			break;
		case 28:
			m_nation = "景颇";
			break;
		case 29:
			m_nation = "柯尔克孜";
			break;
		case 30:
			m_nation = "土";
			break;
		case 31:
			m_nation = "达斡尔";
			break;
		case 32:
			m_nation = "仫佬";
			break;
		case 33:
			m_nation = "羌";
			break;
		case 34:
			m_nation = "布朗";
			break;
		case 35:
			m_nation = "撒拉";
			break;
		case 36:
			m_nation = "毛南";
			break;
		case 37:
			m_nation = "仡佬";
			break;
		case 38:
			m_nation = "锡伯";
			break;
		case 39:
			m_nation = "阿昌";
			break;
		case 40:
			m_nation = "普米";
			break;
		case 41:
			m_nation = "塔吉克";
			break;
		case 42:
			m_nation = "怒";
			break;
		case 43:
			m_nation = "乌孜别克";
			break;
		case 44:
			m_nation = "俄罗斯";
			break;
		case 45:
			m_nation = "鄂温克";
			break;
		case 46:
			m_nation = "德昂";
			break;
		case 47:
			m_nation = "保安";
			break;
		case 48:
			m_nation = "裕固";
			break;
		case 49:
			m_nation = "京";
			break;
		case 50:
			m_nation = "塔塔尔";
			break;
		case 51:
			m_nation = "独龙";
			break;
		case 52:
			m_nation = "鄂伦春";
			break;
		case 53:
			m_nation = "赫哲";
			break;
		case 54:
			m_nation = "门巴";
			break;
		case 55:
			m_nation = "珞巴";
			break;
		case 56:
			m_nation = "基诺";
			break;

		default:
			break;

		}
		idCardInfo.NationL=m_nation;
		byte[] birth = new byte[16];
		byte[] birthasc = new byte[8];
		System.arraycopy(receive, 36, birth, 0, birth.length);
		for (int i = 0; i < birthasc.length; i++) {
			birthasc[i] = birth[2 * i];
		}
		
		idCardInfo.BornL=new String(birthasc);


		byte[] address = new byte[72];
		address[0] = (byte) 0xff;
		address[1] = (byte) 0xfe;
		System.arraycopy(receive, 52, address, 2, address.length - 2);
		idCardInfo.Address=new String(address, "Unicode");

		byte[] IDNo = new byte[36];
		byte[] IDNoasc = new byte[18];
		byte[] IDNoout = null;
		System.arraycopy(receive, 122, IDNo, 0, IDNo.length);
		for (int i = 0; i < IDNoasc.length; i++) {
			IDNoasc[i] = IDNo[2 * i];
		}
		IDNoout = new String(IDNoasc).getBytes("Unicode");
		idCardInfo.CardNo=new String(IDNoout, "Unicode");

		byte[] Department = new byte[32];
		Department[0] = (byte) 0xff;
		Department[1] = (byte) 0xfe;
		System.arraycopy(receive, 158, Department, 2, Department.length - 2);
		idCardInfo.Police=new String(Department, "Unicode");
		byte[] EffectDate = new byte[16];
		byte[] EffectDateasc = new byte[8];
		byte[] EffectDateout = null;
		System.arraycopy(receive, 188, EffectDate, 0, EffectDate.length);
		for (int i = 0; i < EffectDateasc.length; i++) {
			EffectDateasc[i] = EffectDate[2 * i];
		}
//		EffectDateout = new String(EffectDateasc).getBytes("Unicode");
//		idCardInfo.EffectDate=new String(EffectDateout, "Unicode");
		idCardInfo.EffectDate=new String(EffectDateasc);

		byte[] ExpireDate = new byte[16];
		byte[] ExpireDateasc = new byte[8];
		byte[] ExpireDateout = null;
		System.arraycopy(receive, 204, ExpireDate, 0, ExpireDate.length);
		for (int i = 0; i < ExpireDateasc.length; i++) {
			ExpireDateasc[i] = ExpireDate[2 * i];
		}
//		ExpireDateout = new String(ExpireDateasc).getBytes("Unicode");
//		idCardInfo.ExpireDate=new String(ExpireDateout, "Unicode");;			
		idCardInfo.ExpireDate=new String(ExpireDateasc);
		
		
		byte[] tupian=new byte[1025];
		byte[] tupianShow=null;
		Arrays.fill(tupian, (byte) 0x00);
		System.arraycopy(receive, 256, tupian, 0,1024);
		tupianShow= HdosIdUnpack(tupian,pkName);
		if(tupianShow!=null){
			byte tmp;
			int i=0;
			while(i<38556/2){
				tmp=tupianShow[i];
				tupianShow[i]=tupianShow[38555-i];
				tupianShow[38555-i]=tmp;
				i++;
			}
			for(int row=0;row<126;row++){
				for(int col=0;col<102*3/2;col++){
					tmp=tupianShow[col+row*102*3];
					tupianShow[col+row*102*3]=tupianShow[102*3-1-col+row*102*3];
					tupianShow[102*3-1-col+row*102*3]=tmp;
				
				}
			}
			
			idCardInfo.bitmap=new byte[38556];
			System.arraycopy(tupianShow, 0, idCardInfo.bitmap, 0,38556);
	
		}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idCardInfo;
	}
	
	
	public int[] convertByteToColor(byte[] data){
		int size = data.length;
		if (size == 0){
			return null;
		}
		
		
		// 理论上data的长度应该是3的倍数，这里做个兼容
		int arg = 0;
		if (size % 3 != 0){
			arg = 1;
		}
		
		int []color = new int[size / 3 + arg];
		
		
		if (arg == 0){									//  正好是3的倍数
			for(int i = 0; i < color.length; ++i){
		
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
						   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
						   (data[i * 3 + 2] & 0x000000FF ) | 
						    0xFF000000;
			}
		}else{										// 不是3的倍数
			for(int i = 0; i < color.length - 1; ++i){
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
				   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
				   (data[i * 3 + 2] & 0x000000FF ) | 
				    0xFF000000;
			}
			
			color[color.length - 1] = 0xFF000000;					// 最后一个像素用黑色填充
		}

		return color;
	}
	
	public byte  chartoint(byte c)
	{
		switch (c)
		{
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'A':
		case 'a':
			return 10;
		case 'B':
		case 'b':
			return 11;
		case 'C':
		case 'c':
			return 12;
		case 'D':
		case 'd':
			return 13;
		case 'E':
		case 'e':
			return 14;
		case 'F':
		case 'f':
			return 15;
		default:
			break;
		}
		return 0;
	}
	public byte[] strToHex(String indata,int len){
		byte[] result = new byte[len];
		byte[] buf=indata.getBytes();
		for(int i=0;i<len;i++){
		//	System.out.println("buf:"+buf[i]);
			result[i]=(byte)( (chartoint(buf[i*2])<<4)+chartoint(buf[i*2+1]));
//			System.out.println("chartoint(buf[*2])<<4["+i*2+" "+(chartoint(buf[i*2])<<4)+" ");
//			System.out.println("chartoint(buf[*2])<<4["+i*2+1+" "+chartoint(buf[i*2+1])+" ");
//			System.out.println("result["+i+"]:"+result[i]+" ");
		}
		
		return result;
	}
	
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
}
