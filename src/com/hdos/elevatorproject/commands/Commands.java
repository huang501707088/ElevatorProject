package com.hdos.elevatorproject.commands;
import java.util.regex.*;

public class Commands {

	private final String HEAD="*[;";
	private final String END=";]";
	
	private final static String resp="*[;9001;01;20161026095907;]";
	
	
	//4.2.1
	public static String terminalRequestCom(){
		return null;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(resp.replaceAll("*[]", ""));
	}
}
