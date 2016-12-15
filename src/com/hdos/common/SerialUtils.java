package com.hdos.common;

import java.util.ArrayList;


public class SerialUtils {
	
	public static int ERROR_HEAD =-11;
	public static int ERROR_RECV= -12;
	public static int ERROR_XOR =-13;
	public static int ERROR_TAIL =-14;
	public static int ERROR_SEND =-15;
	public static int ERROR_PARAMENT =-16;
	public static int ERROR_HANDLE =-17;
	public static int ERROR_TIME =-18;
	public static int ERROR_LEN =-19;
	public static int OK_DLL=0;
	
	/*public int IFD_BadCommand -29	未找到该命令*/
	public static  int IFD_TypeA_Find_Error= -28;	/*TYPE A寻卡错误*/
	public static int IFD_TypeA_MoreCard_Error= -27;	/*TYPE A检测到多张卡片*/
	public static  int IFD_TypeA_Anticoll_Error =-26;	/*TYPE A防碰撞错误*/
	public static int IFD_TypeA_Select_Error= -25;	/*TYPE A选卡错误*/
	public static  int IFD_TypeA_RATS_Error =-24	;/*TYPE A RATS错误*/
	public static int IFD_M1_Auth_Error =-23;	/*Mifare one Auth错误*/
	public static int IFD_TypeB_Find_Error =-22;	/*TYPE B寻卡错误*/
	public static int IFD_TypeB_attrib_Error =-21	;/*TYPE B attrib错误*/
	public static  int IFD_SendAgain= -40  ;         /*如果返回此错误，需重发数据*/

	/*状态码*/
	public static  int IFD_OK=	0	;				 /*执行成功*/
	public static  int IFD_ICC_TypeError=	-1	;	 /*卡片类型不对*/
	public static  int IFD_ICC_NoExist	=-2	;		 /*无卡*/
	public static  int IFD_ICC_NoPower	=-3	;		 /*有卡未上电*/
	public static  int IFD_ICC_NoResponse=	-4	  ;   /*卡片无应答*/
	public static  int IFD_ConnectError=	-11	  ;   /*读卡器连接错*/
	public static  int IFD_UnConnected	=-12	 ;        /*未建立连接(没有执行打开设备函数)*/
	public static  int IFD_BadCommand	=-13	 ;        /*(动态库)不支持该命令*/
	public static  int IFD_ParameterError=	-14	;     /*(发给动态库的)命令参数错*/
	public static  int IFD_CheckSumError	=-15	  ;   /*信息校验和出错*/

	public int JR_OK= 0 ;    /*执行成功*/
	public static int formatrecv(byte[] data1, int recvdlen, byte[] response) {

		int i;
		int recvlen = 0;
		
		/* 判断帧头 */
		if (data1[0] != (byte)0xFA || data1[1] != (byte)0xFB || data1[2] != (byte)0xFC
				|| data1[3] != (byte)0xFD)
			return ERROR_HEAD;
		recvlen += 4;

	/*以下因为长度超过128会变成负数而更改的 2015/9/15日  黄有清*/	
		int length1;
		int length2;
		/* 长度 */
		
		length1=data1[recvlen]<0? data1[recvlen]+256:data1[recvlen];
		length2=data1[recvlen+1]<0? data1[recvlen+1]+256:data1[recvlen+1];
		
		/*********************更改完毕*****************/
		long len = length1+length2*256;
		if (len < 4)
			return ERROR_LEN;
		recvlen += 2;/* 6 */

		/* 状态位 */
		if (data1[recvlen] != 0) {
			switch (data1[recvlen]) {
			case (byte) 0xfe:
				return IFD_BadCommand;
			case (byte) 0xfd:
				return IFD_TypeA_Find_Error;
			case (byte) 0xfc:
				return IFD_TypeA_MoreCard_Error;
			case (byte) 0xfb:
				return IFD_TypeA_Anticoll_Error;
			case (byte) 0xfa:
				return IFD_TypeA_Select_Error;
			case (byte) 0xf9:
				return IFD_TypeA_RATS_Error;
			case (byte) 0xf8:
				return IFD_M1_Auth_Error;
			case (byte) 0xf7:
				return IFD_TypeB_Find_Error;
			case (byte) 0xf6:
				return IFD_TypeB_attrib_Error;
			case (byte) 0xA7:
				return IFD_SendAgain;
			default:
				return ERROR_RECV;
			}
		}
		recvlen += 1;/* 7 */

		/* 有效数据内容 */
		/* long i; */
		for (i = 0; i < len - 4; i++) {
			response[i] = data1[recvlen + i];
		}
		recvlen += (len - 4);

		/* 判断异或校验位 */

		byte xxor;
		xxor = 0x00;
		for (i = 4; i < len + 4 - 1; i++) {
			xxor ^= data1[i];
		}
		if (data1[(int) (4 + len - 1)] != xxor){
			DebugTools.SystemOut("data1[(int) (4 + len - 1)]"+data1[(int) (4 + len - 1)]);
	//		return ERROR_XOR;
		}
		
		recvlen += 1;

		/* 判断帧尾 */
		if (data1[recvlen] != (byte)0xBB){
			DebugTools.SystemOut("data1[recvlen]="+data1[recvlen]);
			DebugTools.SystemOut("recvlen="+recvlen);
			return ERROR_TAIL;
		}
		/* return OK_DLL; */
		return (int) (len - 4); /* 返回数据长度 */
	}
	
	
	public static int formatrecvRandomFlag(byte[] data1, int recvdlen, byte[] response) {

		int i;
		int recvlen = 0;
		
		/* 判断帧头 */
		if (data1[0] != (byte)0xFA || data1[1] != (byte)0xFB || data1[2] != (byte)0xFC
				|| data1[3] != (byte)0xFD)
			return ERROR_HEAD;
		recvlen += 4;

	/*以下因为长度超过128会变成负数而更改的 2015/9/15日  黄有清*/	
		int length1;
		int length2;
		/* 长度 */
		
		length1=data1[recvlen]<0? data1[recvlen]+256:data1[recvlen];
		length2=data1[recvlen+1]<0? data1[recvlen+1]+256:data1[recvlen+1];
		
		/*********************更改完毕*****************/
		long len = length1+length2*256;
		if (len < 4)
			return ERROR_LEN;
		recvlen += 2;/* 6 */

		/* 状态位 */
		//data1[recvlen];
	
		recvlen += 1;/* 7 */

		/* 有效数据内容 */
		/* long i; */
		for (i = 0; i < len - 4; i++) {
			response[i] = data1[recvlen + i];
		}
		recvlen += (len - 4);

		/* 判断异或校验位 */

		byte xxor;
		xxor = 0x00;
		for (i = 4; i < len + 4 - 1; i++) {
			xxor ^= data1[i];
		}
		if (data1[(int) (4 + len - 1)] != xxor){
			DebugTools.SystemOut("data1[(int) (4 + len - 1)]"+data1[(int) (4 + len - 1)]);
	//		return ERROR_XOR;
		}
		
		recvlen += 1;

		/* 判断帧尾 */
		if (data1[recvlen] != (byte)0xBB){
			DebugTools.SystemOut("data1[recvlen]="+data1[recvlen]);
			DebugTools.SystemOut("recvlen="+recvlen);
			return ERROR_TAIL;
		}
		/* return OK_DLL; */
		return (int) (len - 4); /* 返回数据长度 */
	}
	
	
	
	public static int formatsend(byte[] sd,byte flag, byte[] data, int len)
	{
		int lenflag = 0;
		sd[0] = (byte) 0xFA;
		sd[1] = (byte) 0xFB;
		sd[2] = (byte) 0xFC;
		sd[3] = (byte) 0xFD;
		lenflag += 4;

		sd[lenflag] = (byte) ((len+4)%256);
		sd[lenflag+1] = (byte) ((len+4)/256);

		lenflag += 2;

		/*填充标志位*/
		sd[lenflag] = flag;
		lenflag += 1;

		/*填充数据域*/
		int i;
		for(i=0; i<len; i++)
			sd[lenflag+i] = data[i];
		lenflag += len;

		byte xxor = (byte)0x00;
		/*计算异或校验*/
		for(i=4; i<len+3+4; i++)
		{
			xxor ^= sd[i];
		}
		sd[lenflag] = xxor;
		lenflag += 1;

		/*填充帧尾*/
		sd[lenflag] = (byte) 0xBB;
		lenflag+=1;
		return lenflag;
	}
	
	public static byte[] formatsend(byte flag, byte[] data)
	{
		int len=0;
		if(data!=null){
			len=data.length;
		}
		byte[] sd=new byte[len+9];
		int lenflag = 0;
		sd[0] = (byte) 0xFA;
		sd[1] = (byte) 0xFB;
		sd[2] = (byte) 0xFC;
		sd[3] = (byte) 0xFD;
		lenflag += 4;

		sd[lenflag] = (byte) ((len+4)%256);
		sd[lenflag+1] = (byte) ((len+4)/256);

		lenflag += 2;

		/*填充标志位*/
		sd[lenflag] = flag;
		lenflag += 1;

		/*填充数据域*/
		int i;
		for(i=0; i<len; i++)
			sd[lenflag+i] = data[i];
		lenflag += len;

		byte xxor = (byte)0x00;
		/*计算异或校验*/
		for(i=4; i<len+3+4; i++)
		{
			xxor ^= sd[i];
		}
		sd[lenflag] = xxor;
		lenflag += 1;

		/*填充帧尾*/
		sd[lenflag] = (byte) 0xBB;
		lenflag+=1;
		return sd;
	}
	public static byte[] formatsend(int flag, byte[] data){
		return formatsend((byte)flag, data);
	}
	
}
