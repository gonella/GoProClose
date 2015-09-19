package com.github.gonella.goproclose.util;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Wifi helper to find strongest wiki signal existing.
 */
public class WiFiScanner {
	
	private static final String TAG = WiFiScanner.class.getSimpleName();

	public static String scanWifi(WifiManager wifi) {
		Log.d(TAG, "Scanning WIFIs...");
		List<ScanResult> results = wifi.getScanResults();
		ScanResult bestSignal = null;
		for (ScanResult result : results) {
			
			Log.d(TAG,"Scan SSID ["+result.SSID+"] - "+result.level);
			if (bestSignal == null
					|| WifiManager.compareSignalLevel(bestSignal.level,
							result.level) < 0){
				bestSignal = result;
			}
		}

		String message = String.format("%s networks found. %s is the strongest.", results.size(),bestSignal.SSID);

		Log.d(TAG, "onReceive() message: " + message);
		
		return message;
	}

	
}