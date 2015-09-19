package org.gopro.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.BackPack;
import org.gopro.core.model.CamFields;

public class GoProHelper {
	private static final String CAMERA_CN 	= "/camera/cn";
	private static final String CAMERA_SX 	= "/camera/sx";
	private static final String CAMERA_HS2 	= "/camera/hs";
	private static final String BACPAC_SD 	= "/bacpac/sd";
	private static final String CAMERA_CV 	= "/camera/cv";
	private static final String CAMERA_DL 	= "/camera/DL";
	private static final String CAMERA_DA 	= "/camera/DA";
	private static final String BACPAC_WI 	= "/bacpac/WI";
	private static final String BACPAC_SE 	= "/bacpac/se";
	private static final String BACPAC_CV 	= "/bacpac/cv";
	private static final String CAMERA_UP 	= "/camera/UP";
	private static final String CAMERA_HS 	= "/camera/HS";
	private static final String CAMERA_DM 	= "/camera/DM";
	private static final String BACPAC_SH 	= "/bacpac/SH";
	private static final String BACPAC_PW 	= "/bacpac/PW";
	private static final String CAMERA_CM 	= "/camera/CM";
	
	public static final boolean LOGGING_ENABLED = false;
	private static final int SOCKET_OPERATION_TIMEOUT = 10000;
	private static final String TAG = GoProHelper.class.getSimpleName();
	// GoProCamera mCamera;
	private String mCameraAddress = null;
	private final DefaultHttpClient mClient = newInstance();
	private String ipAddress;
	private Integer port;
	private String password;

	public GoProHelper() {
	}

	public GoProHelper(String ipAddress, Integer port, String password) {
		this();
		this.setIpAddress(ipAddress);
		this.setPort(port);
		this.setPassword(password);
		// this.mCamera = paramGoProCamera;
		this.mCameraAddress = ("http://" + ipAddress + ":" + port);
	}

	private void hexDump(byte[] paramArrayOfByte, String paramString) {
	}

	private boolean passFail(byte[] paramArrayOfByte) {
		boolean bool = false;
		if (paramArrayOfByte != null) {
			int i = paramArrayOfByte.length;
			bool = false;
			if (i > 0) {
				int j = paramArrayOfByte[0];
				bool = false;
				if (j == 0)
					bool = true;
			}
		}
		return bool;
	}

	private boolean sendCommand(String paramString) {
		try {
			sendGET(this.mCameraAddress + paramString + "?t=" + this.getToken());
			return true;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean deleteFilesOnSd() {
		return sendCommand(CAMERA_DA);
	}

	public boolean deleteLastFileOnSd() {
		return sendCommand(CAMERA_DL);
	}

	public int fromBoolean(boolean paramBoolean) {
		if (paramBoolean)
			return 1;
		return 0;
	}

	public String getBacPacPassword() {
		try {
			GoProProtocolParser localGoProProtocolParser = new GoProProtocolParser(
					sendGET(this.mCameraAddress + BACPAC_SD));
			byte[] arrayOfByte = new byte[1];
			arrayOfByte[0] = localGoProProtocolParser.extractByte();
			boolean bool = passFail(arrayOfByte);
			Object localObject = null;
			if (bool) {
				String str = localGoProProtocolParser.extractString();
				localObject = str;
			}
			return (String) localObject;
		} catch (Exception localException) {
		}
		return null;
	}

	public BackPack getBackPackInfo() throws Exception {
		BackPack localBackPack = new BackPack();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte = sendGET("http://" + this.getIpAddress()+ BACPAC_CV);
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK){
				return null;
			}
		} catch (Exception localException) {
			throw new Exception("Fail to get backpack info", localException);
		}
		localBackPack.setVersion(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setModel(localGoProProtocolParser.extractUnsignedByte());
		localBackPack.setId(localGoProProtocolParser
				.extractFixedLengthString(2));
		localBackPack.setBootLoaderMajor(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setBootLoaderMinor(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setBootLoaderBuild(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setRevision(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setMajorversion(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setMinorversion(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setBuildversion(localGoProProtocolParser
				.extractUnsignedByte());
		localBackPack.setWifimac(localGoProProtocolParser
				.extractFixedLengthString(6));
		localBackPack.setSSID(localGoProProtocolParser.extractString());
		return localBackPack;
	}

	public BacPacStatus getBacpacStatus() throws Exception {
		BacPacStatus localBacPacStatus = new BacPacStatus();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress + BACPAC_SE+"?t="
					+ this.getToken());
			hexDump(arrayOfByte, "BacPac SE");
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
				return null;
		} catch (Exception localException) {
			throw localException;
		}
		localBacPacStatus.setBacPacBattery(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setWifiMode(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setBlueToothMode(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setRSSI(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setShutterStatus(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setAutoPowerOff(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setBlueToothAudioChannel(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setFileServer(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraPower(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraI2CError(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraReady(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraModel(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraProtocolVersion(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setCameraAttached(localGoProProtocolParser
				.extractUnsignedByte());
		localBacPacStatus.setBOSSReady(localGoProProtocolParser
				.extractUnsignedByte());
		return localBacPacStatus;
	}

	public int getCameraHLSSegment() {
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress +CAMERA_HS2+"?t="
					+ this.getToken());
			return new GoProProtocolParser(arrayOfByte).extractUnsignedByte();
		} catch (Exception localException) {
		}
		return -1;
	}

	public CamFields getCameraInfo() {
		CamFields localCamFields = new CamFields();
		GoProProtocolParser localGoProProtocolParser;
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress + CAMERA_CV+"?t="
					+ this.getToken());
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			if (localGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
				return null;
		} catch (Exception localException) {
			return null;
		}
		localCamFields.setProtocol(localGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setModel(localGoProProtocolParser.extractUnsignedByte());
		localCamFields.setVersion(localGoProProtocolParser.extractString());
		localCamFields.setCamname(localGoProProtocolParser.extractString());
		return localCamFields;
	}

	public String getCameraNameCN() {
		String str = this.getIpAddress();
		byte[] arrayOfByte;
		try {
			arrayOfByte = sendGET(this.mCameraAddress + CAMERA_CN+"?t="
					+ this.getToken());
			if ((arrayOfByte == null) || (arrayOfByte.length == 0)
					|| (arrayOfByte[0] == 1))
				return str;
		} catch (Exception localException) {
			return str;
		}
		int i = arrayOfByte[1];
		int j = 0;
		for (int k = 2;; k++) {
			if (j >= i)
				return str;
			if (k < arrayOfByte.length)
				str = str + (char) arrayOfByte[k];
			j++;
		}
	}

	public CamFields getCameraSettings() throws Exception {
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress + "/camera/se"+"?t="
					+ this.getToken());
			return getCameraSettings(new GoProProtocolParser(arrayOfByte));
		} catch (Exception localException) {
			throw new Exception("Fail to get camera settings",localException);
		}
	}

	public CamFields getCameraSettings(
			GoProProtocolParser paramGoProProtocolParser) {
		CamFields localCamFields = new CamFields();
		if (paramGoProProtocolParser.extractResultCode() != GoProProtocolParser.RESULT_IS_OK)
			return null;
		localCamFields.setMode(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setMicrophoneMode(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setOndefault(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setExposure(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setTimeLapse(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setAutopower(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setFieldOfView(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setPhotoResolution(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields
				.setVidres(paramGoProProtocolParser.extractUnsignedByte());
		localCamFields.setAudioinput(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setPlaymode(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields
				.setPlaybackPos(paramGoProProtocolParser.extractInteger());
		localCamFields.setBeepSound(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setLedblink(paramGoProProtocolParser
				.extractUnsignedByte());
		int i = paramGoProProtocolParser.extractByte();
		localCamFields.setPreviewActive(true);
		localCamFields.setBattery(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setUsbMode(paramGoProProtocolParser
				.extractUnsignedByte());
		localCamFields.setPhotosAvailable(paramGoProProtocolParser
				.extractShort());
		localCamFields.setPhotosOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setVideoAvailable(paramGoProProtocolParser
				.extractShort());
		localCamFields.setVideoOncard(paramGoProProtocolParser.extractShort());
		localCamFields.setShutter(paramGoProProtocolParser
				.extractUnsignedByte());

		return localCamFields;
	}

	public CamFields getCameraSettingsExtended() {
		GoProProtocolParser localGoProProtocolParser;
		CamFields localCamFields;
		try {
			byte[] arrayOfByte = sendGET(this.mCameraAddress + CAMERA_SX+"?t="
					+ this.getToken());
			localGoProProtocolParser = new GoProProtocolParser(arrayOfByte);
			localCamFields = getCameraSettings(localGoProProtocolParser);
			if (localCamFields == null)
				return localCamFields;
		} catch (Exception localException) {
			return null;
		}
		HashMap localHashMap = new HashMap();
		localCamFields.setSettingsBag(localHashMap);
		localHashMap.put("camera/SS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/BU",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/CS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/WB",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/BR",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/PN",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/LO",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/PS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/BX",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/TS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("video_loop_counter",
				Long.valueOf(localGoProProtocolParser.extractInteger()));
		localHashMap.put("external_battery",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		int i = localGoProProtocolParser.extractByte();
		localHashMap.put("bombie_attached", Integer.valueOf(i & 0x8));
		localHashMap.put("lcd_attached", Integer.valueOf(i & 0x4));
		localHashMap.put("is_boradcasting", Integer.valueOf(i & 0x2));
		localHashMap.put("is_uploading", Integer.valueOf(i & 0x1));
		localHashMap.put("camera/LV",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/LN",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/LS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/VV",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		localHashMap.put("camera/FS",
				Short.valueOf(localGoProProtocolParser.extractUnsignedByte()));
		return localCamFields;
	}

	public DefaultHttpClient newInstance() {
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(localBasicHttpParams,
				HttpVersion.HTTP_1_1);
		HttpProtocolParams
				.setContentCharset(localBasicHttpParams, "ISO-8859-1");
		HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
		HttpConnectionParams.setStaleCheckingEnabled(localBasicHttpParams,
				false);
		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 10000);
		HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
		HttpConnectionParams.setSocketBufferSize(localBasicHttpParams, 8192);
		SchemeRegistry localSchemeRegistry = new SchemeRegistry();
		localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ConnManagerParams.setMaxTotalConnections(localBasicHttpParams, 1);
		return new DefaultHttpClient(new ThreadSafeClientConnManager(
				localBasicHttpParams, localSchemeRegistry),
				localBasicHttpParams);
	}

	public boolean sendCommand(String paramString, int paramInt) throws Exception {
		StringBuilder localStringBuilder = new StringBuilder("%");
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(paramInt);
		return sendCommand(paramString, String.format("%02x", arrayOfObject));
	}

	public boolean sendCommand(String paramString1, String paramString2) throws Exception {
		if (!paramString1.startsWith("/")){
			paramString1 = "/" + paramString1;
		}
		sendGET(this.mCameraAddress + paramString1 + "?t="
				+ this.getToken() + "&p=" + paramString2);
		return true;
		
	}

	public byte[] sendGET(String paramString) throws Exception {
		return sendGET(paramString, this.mClient);
	}

	public byte[] sendGET(String paramString,
			DefaultHttpClient paramDefaultHttpClient) throws Exception {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		HttpResponse localHttpResponse;
		try {
			System.out.println("Cmd :"+paramString);
			
			System.setProperty("http.keepAlive", "true");
			HttpGet localHttpGet = new HttpGet(paramString);
			localHttpResponse = paramDefaultHttpClient.execute(localHttpGet);
			int statusCode = localHttpResponse.getStatusLine().getStatusCode();
			if (statusCode >= 400) {
				localHttpGet.abort();
				throw new IOException("Fail to send GET - HTTP error code = ["+statusCode+"]");
			}
		} catch (Exception localException) {
			throw localException;
		}
		int j = (int) localHttpResponse.getEntity().getContentLength();
		if (j <= 0)
			j = 128;
		InputStream localInputStream = localHttpResponse.getEntity()
				.getContent();
		byte[] arrayOfByte = new byte[j];
		while (true) {
			if (localInputStream.read(arrayOfByte, 0, arrayOfByte.length) == -1) {
				localByteArrayOutputStream.flush();
				return localByteArrayOutputStream.toByteArray();
			}
			localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
		}
	}

	public boolean setBacPacWifiMode(int paramInt) throws Exception {
		return sendCommand(BACPAC_WI, paramInt);
	}
	
	public boolean setBackPackPowerCamera(boolean paramBoolean) throws Exception {
		return sendCommand(BACPAC_PW, fromBoolean(paramBoolean));
	}

	public boolean setCamAutoPowerOff(int paramInt) throws Exception {
		return sendCommand("/camera/AO", paramInt);
	}

	public boolean setCamDateTime(String paramString) {
		try {
			boolean bool = passFail(sendGET(this.mCameraAddress
					+ "/camera/TM?t=" + this.getToken() + "&p=" + paramString));
			return bool;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean setCamDefaultMode(int paramInt) throws Exception {
		return sendCommand(CAMERA_DM, paramInt);
	}

	public boolean setCamExposure(int paramInt) throws Exception {
		return sendCommand("/camera/EX", paramInt);
	}

	public boolean setCamFov(int paramInt) throws Exception {
		return sendCommand("/camera/FV", paramInt);
	}

	public boolean setCamLEDBlink(int paramInt) throws Exception {
		return sendCommand("/camera/LB", paramInt);
	}

	public boolean setCamLivePreview(boolean paramBoolean) throws Exception {
		if (paramBoolean)
			;
		for (int i = 2;; i = 0)
			return sendCommand("/camera/PV", i);
	}

	public boolean setCamLocate(boolean paramBoolean) throws Exception {
		return sendCommand("/camera/LL", fromBoolean(paramBoolean));
	}

	public boolean setCamMode(int paramInt) throws Exception {
		return sendCommand(CAMERA_CM, paramInt);
	}

	public boolean setCamNtscPal(boolean paramBoolean) throws Exception {
		if (paramBoolean)
			;
		for (int i = 0;; i = 1)
			return sendCommand("/camera/VM", i);
	}

	public boolean setCamOnScreenDisplay(int paramInt) throws Exception {
		return sendCommand("/camera/DS", paramInt);
	}

	public boolean setCamPhotoResolution(int paramInt) throws Exception {
		return sendCommand("/camera/PR", paramInt);
	}

	public boolean setCamProtune(int paramInt) throws Exception {
		return sendCommand("/camera/PT", paramInt);
	}

	public boolean setCamShutter(boolean paramBoolean) throws Exception {
		return sendCommand(BACPAC_SH, fromBoolean(paramBoolean));
	}

	public boolean setCamSound(int paramInt) throws Exception {
		return sendCommand("/camera/BS", paramInt);
	}

	public boolean setCamTimeLapseTI(String paramString) throws Exception {
		return sendCommand("/camera/TI", paramString);
	}

	public boolean setCamUpDown(int paramInt) throws Exception {
		return sendCommand(CAMERA_UP, paramInt);
	}

	public boolean setCamVideoResolution(int paramInt) throws Exception {
		return sendCommand("/camera/VR", paramInt);
	}

	public boolean setCameraHLSSegment(int paramInt) throws Exception {
		return sendCommand(CAMERA_HS, paramInt);
	}

	public boolean setCameraName(String paramString) {
		if ((paramString.length() > 31) || (paramString.length() == 0))
			return false;
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = Integer.valueOf(paramString.length());
		arrayOfObject[1] = paramString;
		String str = URLEncoder.encode(String.format("%x%s", arrayOfObject)
				.replaceAll("\\s+", "%20"));
		try {
			boolean bool = passFail(sendGET(this.mCameraAddress
					+ "/camera/CN?t=" + this.getToken() + "&p=%0" + str));
			return bool;
		} catch (Exception localException) {
		}
		return false;
	}

	public boolean toBoolean(int paramInt) {
		return paramInt != 0;
	}

	public String getToken() {
		return getPassword();
	}

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	private Integer getPort() {
		return port;
	}

	private void setPort(Integer port) {
		this.port = port;
	}

	private String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}
	
	public boolean startRecord() throws Exception{
		return sendCommand(BACPAC_SH,"%01");
	}
	public boolean stopRecord() throws Exception{
		return sendCommand(BACPAC_SH,"%00");
	}

	public boolean turnOnCamera() throws Exception{
		return sendCommand(BACPAC_PW,"%01");
	}
	public boolean turnOffCamera() throws Exception{
		return sendCommand(BACPAC_PW,"%00");
	}
	public boolean changeModeCamera() throws Exception{
		return sendCommand(BACPAC_PW,"%02");
	}

	public boolean modeCamera() throws Exception{
		return sendCommand(CAMERA_CM,"%00");
	}
	public boolean modePhoto() throws Exception{
		return sendCommand(CAMERA_CM,"%01");
	}
	public boolean modeBurst() throws Exception{
		return sendCommand(CAMERA_CM,"%02");
	}
	
	/*public boolean timelapse1(){
		return sendCommand("/camera/CM","%03");
	}
	public boolean timelapse2(){
		return sendCommand("/camera/CM","%04");
	}
	*/
}