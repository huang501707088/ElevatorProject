package com.hdos.elevatorproject.common;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TerminalSystem {
	public static String getCPUCode(Context context){
		return ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
	}
}
