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
	
	/*public int IFD_BadCommand -29	δ�ҵ�������*/
	public static  int IFD_TypeA_Find_Error= -28;	/*TYPE AѰ������*/
	public static int IFD_TypeA_MoreCard_Error= -27;	/*TYPE A��⵽���ſ�Ƭ*/
	public static  int IFD_TypeA_Anticoll_Error =-26;	/*TYPE A����ײ����*/
	public static int IFD_TypeA_Select_Error= -25;	/*TYPE Aѡ������*/
	public static  int IFD_TypeA_RATS_Error =-24	;/*TYPE A RATS����*/
	public static int IFD_M1_Auth_Error =-23;	/*Mifare one Auth����*/
	public static int IFD_TypeB_Find_Error =-22;	/*TYPE BѰ������*/
	public static int IFD_TypeB_attrib_Error =-21	;/*TYPE B attrib����*/
	public static  int IFD_SendAgain= -40  ;         /*������ش˴������ط�����*/

	/*״̬��*/
	public static  int IFD_OK=	0	;				 /*ִ�гɹ�*/
	public static  int IFD_ICC_TypeError=	-1	;	 /*��Ƭ���Ͳ���*/
	public static  int IFD_ICC_NoExist	=-2	;		 /*�޿�*/
	public static  int IFD_ICC_NoPower	=-3	;		 /*�п�δ�ϵ�*/
	public static  int IFD_ICC_NoResponse=	-4	  ;   /*��Ƭ��Ӧ��*/
	public static  int IFD_ConnectError=	-11	  ;   /*���������Ӵ�*/
	public static  int IFD_UnConnected	=-12	 ;        /*δ��������(û��ִ�д��豸����)*/
	public static  int IFD_BadCommand	=-13	 ;        /*(��̬��)��֧�ָ�����*/
	public static  int IFD_ParameterError=	-14	;     /*(������̬���)���������*/
	public static  int IFD_CheckSumError	=-15	  ;   /*��ϢУ��ͳ���*/

	public int JR_OK= 0 ;    /*ִ�гɹ�*/
	public static int formatrecv(byte[] data1, int recvdlen, byte[] response) {

		int i;
		int recvlen = 0;
		
		/* �ж�֡ͷ */
		if (data1[0] != (byte)0xFA || data1[1] != (byte)0xFB || data1[2] != (byte)0xFC
				|| data1[3] != (byte)0xFD)
			return ERROR_HEAD;
		recvlen += 4;

	/*������Ϊ���ȳ���128���ɸ��������ĵ� 2015/9/15��  ������*/	
		int length1;
		int length2;
		/* ���� */
		
		length1=data1[recvlen]<0? data1[recvlen]+256:data1[recvlen];
		length2=data1[recvlen+1]<0? data1[recvlen+1]+256:data1[recvlen+1];
		
		/*********************�������*****************/
		long len = length1+length2*256;
		if (len < 4)
			return ERROR_LEN;
		recvlen += 2;/* 6 */

		/* ״̬λ */
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

		/* ��Ч�������� */
		/* long i; */
		for (i = 0; i < len - 4; i++) {
			response[i] = data1[recvlen + i];
		}
		recvlen += (len - 4);

		/* �ж����У��λ */

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

		/* �ж�֡β */
		if (data1[recvlen] != (byte)0xBB){
			DebugTools.SystemOut("data1[recvlen]="+data1[recvlen]);
			DebugTools.SystemOut("recvlen="+recvlen);
			return ERROR_TAIL;
		}
		/* return OK_DLL; */
		return (int) (len - 4); /* �������ݳ��� */
	}
	
	
	public static int formatrecvRandomFlag(byte[] data1, int recvdlen, byte[] response) {

		int i;
		int recvlen = 0;
		
		/* �ж�֡ͷ */
		if (data1[0] != (byte)0xFA || data1[1] != (byte)0xFB || data1[2] != (byte)0xFC
				|| data1[3] != (byte)0xFD)
			return ERROR_HEAD;
		recvlen += 4;

	/*������Ϊ���ȳ���128���ɸ��������ĵ� 2015/9/15��  ������*/	
		int length1;
		int length2;
		/* ���� */
		
		length1=data1[recvlen]<0? data1[recvlen]+256:data1[recvlen];
		length2=data1[recvlen+1]<0? data1[recvlen+1]+256:data1[recvlen+1];
		
		/*********************�������*****************/
		long len = length1+length2*256;
		if (len < 4)
			return ERROR_LEN;
		recvlen += 2;/* 6 */

		/* ״̬λ */
		//data1[recvlen];
	
		recvlen += 1;/* 7 */

		/* ��Ч�������� */
		/* long i; */
		for (i = 0; i < len - 4; i++) {
			response[i] = data1[recvlen + i];
		}
		recvlen += (len - 4);

		/* �ж����У��λ */

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

		/* �ж�֡β */
		if (data1[recvlen] != (byte)0xBB){
			DebugTools.SystemOut("data1[recvlen]="+data1[recvlen]);
			DebugTools.SystemOut("recvlen="+recvlen);
			return ERROR_TAIL;
		}
		/* return OK_DLL; */
		return (int) (len - 4); /* �������ݳ��� */
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

		/*����־λ*/
		sd[lenflag] = flag;
		lenflag += 1;

		/*���������*/
		int i;
		for(i=0; i<len; i++)
			sd[lenflag+i] = data[i];
		lenflag += len;

		byte xxor = (byte)0x00;
		/*�������У��*/
		for(i=4; i<len+3+4; i++)
		{
			xxor ^= sd[i];
		}
		sd[lenflag] = xxor;
		lenflag += 1;

		/*���֡β*/
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

		/*����־λ*/
		sd[lenflag] = flag;
		lenflag += 1;

		/*���������*/
		int i;
		for(i=0; i<len; i++)
			sd[lenflag+i] = data[i];
		lenflag += len;

		byte xxor = (byte)0x00;
		/*�������У��*/
		for(i=4; i<len+3+4; i++)
		{
			xxor ^= sd[i];
		}
		sd[lenflag] = xxor;
		lenflag += 1;

		/*���֡β*/
		sd[lenflag] = (byte) 0xBB;
		lenflag+=1;
		return sd;
	}
	public static byte[] formatsend(int flag, byte[] data){
		return formatsend((byte)flag, data);
	}
	
}
