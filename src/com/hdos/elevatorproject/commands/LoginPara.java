package com.hdos.elevatorproject.commands;

public class LoginPara {

	public String userName="";
	public String passWord="";
	
	public String ipAddr="";
	public int port=0;
	public LoginPara(String userName, String passWord, String ipAddr,
			int port) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.ipAddr = ipAddr;
		this.port = port;
	}

}
