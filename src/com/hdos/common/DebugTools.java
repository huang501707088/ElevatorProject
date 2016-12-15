package com.hdos.common;

public class DebugTools {
	static public void SystemOut(String str){
		if(str.length()>1000){
			String subString="";
			for(int i=0;i<(str.length()/1000)+1;i++){
				subString=str.substring(0+i*1000, (i*1000+1000)>str.length()?str.length():(i*1000+1000));
				System.out.println(subString);
			}
		}else{
			System.out.println(str);
		}
	
	}
	public static StringBuffer byteArrayOutput(byte[] buffer, int length) {
		StringBuffer outBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			outBuffer.append(Integer.toHexString(buffer[i] & 0xff).length() == 1 ? "0"
							+ Integer.toHexString(buffer[i] & 0xff)
							: Integer.toHexString(buffer[i] & 0xff));
			outBuffer.append(" ");
		}
		return outBuffer;

	}
	public static StringBuffer byteArrayOutputWithIndex(byte[] buffer, int length) {
		StringBuffer outBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			outBuffer.append(Integer.toHexString(buffer[i] & 0xff).length() == 1 ? "0"
							+ Integer.toHexString(buffer[i] & 0xff)
							: Integer.toHexString(buffer[i] & 0xff));
			outBuffer.append(String.format("[%s]", i));
			outBuffer.append("  ");
		}
		return outBuffer;

	}
	
	public static String byte2Hex(byte[] input){
		if(input==null) return "null";
		return byte2Hex(input, 0, input.length);
	}
	public static String byte2Hex(byte[] input,final int ioffset,final int isize){
		if(input==null) return "null";
		StringBuilder sb = new StringBuilder();
		int i;
		for(int d=ioffset; d<ioffset+isize; d++){
			i = input[d];
			if(i<0) i+=256;
			if(i<16) sb.append("0");
			sb.append(Integer.toString(i, 16));
		}
		return sb.toString().toUpperCase();
	}

}
