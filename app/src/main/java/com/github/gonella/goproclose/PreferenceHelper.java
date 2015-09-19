package com.github.gonella.goproclose;

import android.content.SharedPreferences;
import android.util.Log;

import com.github.gonella.goproclose.service.GoProAndroidService;

/**
 * Place to store all settings customize by the customer. Also some default common settings.
 */
public class PreferenceHelper {

	//Some detault values
	public static String GOPRO_WIFI_SSID = "GoGoGoGo";
	public static String GOPRO_WIFI_PASSWORD = "goprt4231";
	public static String BLUETOOTH_NAME = "Enjoy";
	public static String BLUETOOTH_PASSWORD = "0000";

	private static final String TAG = PreferenceHelper.class.getSimpleName();
	private SharedPreferences settings;
	public static final String PREFS_NAME = "PREFS_GOPROCLOSE";

	public PreferenceHelper(SharedPreferences settings){
		this.settings = settings;		
	}
	

	public void writeValue(String key,String value){
		Log.d(TAG,"Writing key ["+key+"] - value ["+value+"]");
		// We need an Editor object to make preference changes. All objects are from android.context.Context
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString(key,value);
	    // Commit the edits!
	    editor.commit();
	}

	public String readValue(String key){
	    return settings.getString(key, null);	    
	}
	
	public void writeWifiSSID(String value){
		writeValue(GoProAndroidService.KEY_GOPRO_WIFI_SSID, value);
	}
	public void writeWifiPassword(String value){
		writeValue(GoProAndroidService.KEY_GOPRO_WIFI_PASSWORD, value);
	}
	public void writeBluetoothName(String value){
		writeValue(GoProAndroidService.KEY_BLUETOOTH_NAME, value);
	}
	public void writeBluetoothPassword(String value){
		writeValue(GoProAndroidService.KEY_BLUETOOTH_PASSWORD, value);
	}
	
	public String readWifiSSID(){
		String readValue = readValue(GoProAndroidService.KEY_GOPRO_WIFI_SSID);
		
		if(readValue==null || (readValue!=null && readValue.isEmpty())){
			return GOPRO_WIFI_SSID;
		}
		
		return readValue;
	}
	public String readWifiPassword(){
		String readValue = readValue(GoProAndroidService.KEY_GOPRO_WIFI_PASSWORD);
		
		if(readValue==null || (readValue!=null && readValue.isEmpty())){
			return GOPRO_WIFI_PASSWORD;
		}
		return readValue;
	}
	public String readBluetoothName(){
		String readValue = readValue(GoProAndroidService.KEY_BLUETOOTH_NAME);
		
		if(readValue==null || (readValue!=null && readValue.isEmpty())){
			return BLUETOOTH_NAME;
		}
		
		return readValue;
	}
	public String readBluetoothPassword(){
		String readValue = readValue(GoProAndroidService.KEY_BLUETOOTH_PASSWORD);
		
		if(readValue==null || (readValue!=null && readValue.isEmpty())){
			return BLUETOOTH_PASSWORD;
		}
		
		return readValue;
	}
}
