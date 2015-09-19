package com.github.gonella.goproclose;

import java.util.ArrayList;
import java.util.List;

import org.gopro.main.GoProApi;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

import com.github.gonella.goproclose.service.GoProAndroidService;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int POLLINGTIME = 10;
	
	private List<BluetoothDevice> tmpBtChecker = new ArrayList<BluetoothDevice>();
	private List<String> mArrayAdapter = new ArrayList<String>();

	private GoProApi gopro;

	private PreferenceHelper preference;

	
	// private WiFiScanReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    SharedPreferences settings = getSharedPreferences(PreferenceHelper.PREFS_NAME, 0);
	    preference = new PreferenceHelper(settings);

		//android.os.NetworkOnMainThreadException
		if( Build.VERSION.SDK_INT >= 9){
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		}
		
		// WifiManager wifi = connectWifi();

		/*
		 * // Register Broadcast Receiver if (receiver == null) { receiver = new
		 * WiFiScanReceiver(this); } registerReceiver(receiver, new
		 * IntentFilter( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		 */

		 Intent myIntentWithService = new Intent(this, GoProAndroidService.class);

		 startService(myIntentWithService);
		 /*
		 PendingIntent pintent = PendingIntent.getService(this, 0, myIntentWithService, 0);
		 AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		 alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),POLLINGTIME*1000, pintent);
		  */

		 
		 final Button button1 = (Button) findViewById(R.id.button3);
         button1.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 
            	
             }
         });
         
         final Button button2 = (Button) findViewById(R.id.button4);
         button2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 
            	 
             }
         });
		 
         final EditText editTextWifiSsid = (EditText)findViewById(R.id.editTextWifiSsid);
         editTextWifiSsid.append(preference.readWifiSSID());
         editTextWifiSsid.setOnFocusChangeListener(new OnFocusChangeListener() {
             public void onFocusChange(View v,boolean hasFocus) {           
            	 preference.writeWifiSSID(editTextWifiSsid.getText().toString());
             }
         });
         
        final  EditText editTextWifiPassword = (EditText)findViewById(R.id.editTextWifiPassword);
         editTextWifiPassword.append(preference.readWifiPassword());
         editTextWifiPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
             public void onFocusChange(View v,boolean hasFocus) {           
            	 preference.writeWifiPassword(editTextWifiPassword.getText().toString());
             }
         });
         
		final EditText editTextBluetoothName = (EditText)findViewById(R.id.editTextBluetoothName);
		editTextBluetoothName.append(preference.readBluetoothName());
		editTextBluetoothName.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v,boolean hasFocus) {            	 
            	preference.writeBluetoothName(editTextBluetoothName.getText().toString());
             }
         });
		
		final EditText editTextBluetoothPassword = (EditText)findViewById(R.id.editTextBluetoothPassword);
		editTextBluetoothPassword.append(preference.readBluetoothPassword());		         
		editTextBluetoothPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
             public void onFocusChange(View v,boolean hasFocus) {            	 
            	 preference.writeBluetoothPassword(editTextBluetoothPassword.getText().toString());
             }
         });
         
		 
		Log.d(TAG, "onCreate()");
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public GoProApi getGoPro(){		
		if(gopro==null){
			gopro = new GoProApi(GoProAndroidService.GOPRO_WIFI_PASSWORD);
		}
		return gopro;
	}

	public void logGoProInfo(String message){
		Log.d(TAG, "GoPro["+ GoProAndroidService.GOPRO_WIFI_SSID+"] - "+message);
	}
	
	public void logGoProError(String message,Exception e){
		Log.e(TAG, "GoPro["+ GoProAndroidService.GOPRO_WIFI_SSID+"] - "+message,e);
	}


	
}
