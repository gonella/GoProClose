package com.github.gonella.goproclose.service;

import java.util.List;
import java.util.Set;

import org.apache.http.conn.HttpHostConnectException;
import org.gopro.main.GoProApi;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.gonella.goproclose.NotificationHelper;
import com.github.gonella.goproclose.PreferenceHelper;
import com.github.gonella.goproclose.util.WiFiScanner;

/**
 *  Class responsible to detect wifi/bluetooth state to start gopro process
 */
public class GoProAndroidService extends Service {
	private static final String TAG = GoProAndroidService.class.getSimpleName();

	public static String KEY_GOPRO_WIFI_SSID = "KEY_GOPRO_WIFI_SSID";
	public static String KEY_GOPRO_WIFI_PASSWORD = "KEY_GOPRO_WIFI_PASSWORD";
	public static String KEY_BLUETOOTH_NAME = "KEY_BLUETOOTH_NAME";
	public static String KEY_BLUETOOTH_PASSWORD = "KEY_BLUETOOTH_PASSWORD";
	
	public static String GOPRO_WIFI_SSID = null;
	public static String GOPRO_WIFI_PASSWORD = null;
	public static String BLUETOOTH_NAME = null;
	public static String BLUETOOTH_PASSWORD = null;

	private GoProApi gopro;
	private GoProAndroidService service;

	private PreferenceHelper preference;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		service = this;
		Log.d(TAG, "Starting service ["+TAG+"]");
		
		SharedPreferences settings = getSharedPreferences(PreferenceHelper.PREFS_NAME, 0);
		preference = new PreferenceHelper(settings);
		
		GOPRO_WIFI_SSID=preference.readWifiSSID();
		GOPRO_WIFI_PASSWORD=preference.readWifiPassword();
		
		BLUETOOTH_NAME=preference.readBluetoothName();
		BLUETOOTH_PASSWORD=preference.readBluetoothPassword();
				
		
		// Register the BroadcastReceiver
		IntentFilter filterBluetoothConnected = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);		
		registerReceiver(mReceiverDetectBluetoothConnected, filterBluetoothConnected);
		
		IntentFilter filterBluetoothDisconnected = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mReceiverDetectBluetoothDisconnected, filterBluetoothDisconnected);

		if(isBluetoothEnabled()){
			discoveryBluetoothDevices();
		}
		else{
			NotificationHelper.generateNotification(this, "The bluetooth is not enabled.");
		}
		
		if(!isWifiEnabled()){
			NotificationHelper.generateNotification(this, "The wifi is not enabled.");
		}
				
		return Service.START_NOT_STICKY;
	}

	private void discoveryBluetoothDevices() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				String deviceStr = device.getName() + "\n"+ device.getAddress();
				Log.d(TAG, "FOUND: " + deviceStr);
			}
		}
	}
	
	public void disoveryWifiDevices(){
		// List available networks
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
			Log.d(TAG, config.SSID);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private WifiManager connectToGoWifi() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wc = buildWifiGoPro();
		int res = wifi.addNetwork(wc);
		Log.d(TAG, "Adding - Wifi GoPro ["+GOPRO_WIFI_SSID+" = " + res+"]");
		boolean b = wifi.enableNetwork(res, true);
		Log.d(TAG, "Enable - Wifi GoPro ["+GOPRO_WIFI_SSID+" = " + b+"]");

		return wifi;
	}
	
	private boolean waitForSSIDIsConnected(String ssid) throws Exception{
		
		if(ssid==null || (ssid!=null && ssid.isEmpty())){
			throw new Exception("The ssid is empyt. Please check it");
		}
		
		boolean result=false;
		
		int timeout=0;

		for (int i = 0; i < GoProApi._RETRY_OPERATION; i++) {

			try{
				logGoProInfo("Waiting until SSID["+ssid+"] is connected...");
		
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			    String ssidFound = networkInfo.getExtraInfo();
				Log.d(TAG,ssidFound);
				if(ssidFound.contains(ssid) && networkInfo.isConnectedOrConnecting() ) {
			       result=true;
			       break;
			    }
			
			}catch(Exception e){
				logGoProError("Fail to check if gopro is ready. Let try again. Waiting time ["+GoProApi._POLLINGTIME+"]",e);				
			}

			Thread.sleep(GoProApi._POLLINGTIME);

			timeout++;
		}
		if(timeout==GoProApi._RETRY_OPERATION){
			throw new Exception("The wait has timeout[waitForSSIDIsConnected], check if the go pro is working correctly.");
		}
	
		return result;
	}
	
	private WifiConfiguration buildWifiGoPro() {
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + GOPRO_WIFI_SSID + "\"";
		wc.preSharedKey = "\"" + GOPRO_WIFI_PASSWORD + "\"";
		wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		return wc;
	}
	
	final BroadcastReceiver mReceiverDetectBluetoothConnected = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			//toasMessage("ACTION : " + action);
			toasMessage("Bluetooth["+BLUETOOTH_NAME+"] connected");
			// When discovery starts
			//if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) 
			//BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			try{
				
				connectToGoWifi();

				waitForSSIDIsConnected(GOPRO_WIFI_SSID);

				logGoProInfo("Starting record...");
				
				getGoPro().powerOnAndStartRecord();
				
				logGoProInfo("STARTED - DONE");
				
			}
			catch(HttpHostConnectException e){
				logGoProError("It was not possible to connect to gopro",e);
			}
			catch(Exception e){
				logGoProError("Fail to start",e);
			}	
		}
	};

	final BroadcastReceiver mReceiverDetectBluetoothDisconnected = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			//toasMessage("ACTION : " + action);
			toasMessage("Bluetooth["+BLUETOOTH_NAME+"] disconnected");
			
			try{
				
				logGoProInfo("Stopping record...");
				
				getGoPro().stopRecordAndPowerOff();
				
				logGoProInfo("STOPPED - DONE");
			}catch(Exception e){
				logGoProError("Fail to stop",e);
			}	
		}
	};
	
	public void checkingStrongestWifi(){
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifi.startScan();
		String message = WiFiScanner.scanWifi(wifi);
		toasMessage(message);
	}

	
	public void toasMessage(String message){
		Log.d(TAG, message);
		Toast.makeText(service, message, Toast.LENGTH_LONG).show();
	}
	
	public GoProApi getGoPro(){		
		if(gopro==null){
			gopro = new GoProApi(GOPRO_WIFI_PASSWORD);
		}
		return gopro;
	}
	
	public void logGoProInfo(String message){
		Log.d(TAG, "GoPro["+GOPRO_WIFI_SSID+"] - "+message);
	}
	
	public void logGoProError(String message,Exception e){
		Log.e(TAG, "GoPro["+GOPRO_WIFI_SSID+"] - "+message,e);
	}
	
	@Override
	public void onDestroy() {

		unregisterReceiver(mReceiverDetectBluetoothConnected);
		unregisterReceiver(mReceiverDetectBluetoothDisconnected);
	}
	
	private boolean isBluetoothEnabled(){
	
		boolean result=false;
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			result=false;
		} else {
		    if (!mBluetoothAdapter.isEnabled()) {
		        // Bluetooth is not enable :)
		    	result=false;
		    }else{
		    	
		    	Log.d(TAG,mBluetoothAdapter.getName());
		    	
		    	result=true;
		    }
		}
		
		return result;
	}
	
	private boolean isWifiEnabled(){
		
		boolean result=false;
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    result=true; 
		}
		else{
			result=false;
		}
		return result;
	}
}