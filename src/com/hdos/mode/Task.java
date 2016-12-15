package com.hdos.mode;

import java.io.Serializable;

public class  Task implements Serializable{
	private String LiftAddress;//安装地址
	private String InstallFlag;//安装标志
	private String LiftRegCode;//电梯注册码
	private String LiftHelpCode;//电梯救援码
	private String CPUCode;//cpu码
	private String NFCCode;//NFC读头码
	private String RFICCardNo;//RFIC码
	private String QRCode;//二维码
	private String TermNo;//终端编号
	private String Longitude;//经度
	private String Dimension;//纬度
	 
    
   
	public String getLiftRegCode() {
		return LiftRegCode;
	}
	public void setLiftRegCode(String liftRegCode) {
		LiftRegCode = liftRegCode;
	}
	public String getLiftHelpCode() {
		return LiftHelpCode;
	}
	public void setLiftHelpCode(String liftHelpCode) {
		LiftHelpCode = liftHelpCode;
	}
	public String getNFCCode() {
		return NFCCode;
	}
	public void setNFCCode(String nFCCode) {
		NFCCode = nFCCode;
	}
	public String getCPUCode() {
		return CPUCode;
	}
	public void setCPUCode(String cPUCode) {
		CPUCode = cPUCode;
	}
	public String getRFICCardNo() {
		return RFICCardNo;
	}
	public void setRFICCardNo(String rFICCardNo) {
		RFICCardNo = rFICCardNo;
	}
	public String getQRCode() {
		return QRCode;
	}
	public void setQRCode(String qRCode) {
		QRCode = qRCode;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public String getDimension() {
		return Dimension;
	}
	public void setDimension(String dimension) {
		Dimension = dimension;
	}
	public String getLiftAddress() {
		return LiftAddress;
	}
	public void setLiftAddress(String liftAddress) {
		LiftAddress = liftAddress;
	}
	public String getTermNo() {
		return TermNo;
	}
	public void setTermNo(String termNo) {
		TermNo = termNo;
	}
	public String getInstallFlag() {
		return InstallFlag;
	}
	public void setInstallFlag(String installFlag) {
		InstallFlag = installFlag;
	}
    
	
    
}
