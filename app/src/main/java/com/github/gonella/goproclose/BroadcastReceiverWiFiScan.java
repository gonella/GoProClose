package com.github.gonella.goproclose;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * On development, another way to detect wifi signal
 */
public class BroadcastReceiverWiFiScan extends BroadcastReceiver {
	private static final String TAG = BroadcastReceiverWiFiScan.class.getSimpleName();
	MainActivity mainApp;

	public BroadcastReceiverWiFiScan(MainActivity wifiDemo) {
		super();
		this.mainApp = wifiDemo;
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		
		String scannWifi = scannWifi();
		
		generateNotification(c, scannWifi);
	}

	private String scannWifi() {
		Log.d(TAG, "Scanning WIFIs...");

		String message=null;
		/*WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		List<ScanResult> results = mainApp.wifi.getScanResults();
		ScanResult bestSignal = null;
		for (ScanResult result : results) {
			if (bestSignal == null
					|| WifiManager.compareSignalLevel(bestSignal.level,
							result.level) < 0){
				bestSignal = result;
			}
		}

		String message = String.format(
				"%s networks found. %s is the strongest.", results.size(),
				bestSignal.SSID);
		Toast.makeText(mainApp, message, Toast.LENGTH_LONG).show();

		Log.d(TAG, "onReceive() message: " + message);
*/		
		return message;
	}
	
	private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}