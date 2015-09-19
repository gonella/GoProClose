package org.gopro.main;

import org.gopro.core.GoProHelper;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.ENCameraBoss;
import org.gopro.core.model.ENCameraPowerStatus;
import org.gopro.core.model.ENCameraReady;

public class GoProApi {
	
	
	public static final int _POLLINGTIME = 4000;
	public static final int _RETRY_OPERATION = 3;

	public static void main(String[] args) throws Exception {
			
		try{
			System.out.println("Go Pro Starting....");
			GoProApi gopro=new GoProApi("goprt4231");
			
			/*try{
				
				BacPacStatus bacpacStatus = gopro.getHelper().getBacpacStatus();
				int cameraReady = bacpacStatus.getCameraReady();				
				System.out.println("Camera ready ? "+cameraReady);
				System.out.println("Camera getCameraPower ? "+bacpacStatus.getCameraPower());
				
			}catch(ConnectTimeoutException e){
				System.out.println("Fail to connect to GO PRO. Check if the wifi is on.");
				System.exit(0);
			}*/
			
			gopro.powerOnAndStartRecord();	
			
			System.out.println("==== DONE ====");
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	public void powerOnAndStartRecord() throws Exception {
		boolean goproIsReady=false;
		
		try{
			verifyIfGoProIsPowerOn();
				
			verifyIfGoProIsReadyToRecord();

			goproIsReady=true;
		}catch(Exception e){
			System.out.println("The gopro is not poweron. Let try agin power on. ["+e.getMessage()+"]");
			goproIsReady = powerAndWaitUntilIsReady();
		}
		
		if(goproIsReady){
			
			waitUntilIsReadyToReceiveCmd();
			
			System.out.println("Starting record");
			startRecord();
			
			System.out.println("Started.");
		}
	}
	
	public void stopRecordAndPowerOff() throws Exception {
		
		System.out.println("Stopping record");
		stopRecord();
		System.out.println("Stopped.");
			
		Thread.sleep(1000);
			
		System.out.println("Power Off Go Pro");
		powerOff();
		
		System.out.println("Power Off.");
	}

	private BacPacStatus verifyIfGoProIsReadyToRecord()
			throws Exception {
		
		System.out.println("Verifying if go pro is ready...");
		BacPacStatus bacpacStatus = getHelper().getBacpacStatus();
		int cameraReady = bacpacStatus.getCameraReady();				
		System.out.println("Camera ready 		? "+cameraReady);		
		
		if(ENCameraReady.READY.getCode()!=cameraReady){
			throw new Exception("The go pro is not ready. Check if it is power on.");
		}
		
		return bacpacStatus;
	}
	
	private BacPacStatus verifyIfGoProIsPowerOn() throws Exception {
		
		System.out.println("Verifying if go pro is power on...");
		
		BacPacStatus bacpacStatus = getHelper().getBacpacStatus();
		
		int cameraPower = bacpacStatus.getCameraPower();
		System.out.println("Camera power ? "+cameraPower);		
		if(ENCameraPowerStatus.POWERON.getCode()!=cameraPower){
			throw new Exception("The go pro is not power on.");
		}		
		return bacpacStatus;
	}
	
	public boolean powerAndWaitUntilIsReady() throws Exception{
		
		boolean result=false;
		
		System.out.println("Sending power on to gopro");
		powerOn();

		int timeout=0;

		for (int i = 0; i < _RETRY_OPERATION; i++) {

			try{
				verifyIfGoProIsReadyToRecord();
				
				result=true;
				
				break;
			}catch(Exception e){
				System.out.println("Fail to check if gopro is ready. Let try again. Waiting time ["+_POLLINGTIME+"]");				
			}

			Thread.sleep(_POLLINGTIME);

			timeout++;
		}
		if(timeout==_RETRY_OPERATION){
			throw new Exception("The wait has timeout[waitUntilIsBOSSReady], check if the go pro is working correctly.");
		}
		
		
		return result;
	}
	
	public boolean waitUntilIsReadyToReceiveCmd() throws Exception{
		
		boolean result=false;
	
		int timeout=0;
		for (int i = 0; i < _RETRY_OPERATION; i++) {

			try{
				verifyIfGoProIsReadyToReceiveCmd();
				
				result=true;				
				break;								
			}catch(Exception e){
				System.out.println("Fail to check if gopro is ready. Let try again. Waiting time ["+_POLLINGTIME+"]");				
			}
			Thread.sleep(_POLLINGTIME);

			timeout++;
		}
		
		if(timeout==_RETRY_OPERATION){
			throw new Exception("The wait has timeout[waitUntilIsReadyToReceiveCmd], check if the go pro is working correctly.");
		}
		
		return result;
	}

	private BacPacStatus verifyIfGoProIsReadyToReceiveCmd() throws Exception {
		System.out.println("Verifying if go pro is ready to receive command(BOSS ready)...");

		BacPacStatus bacpacStatus = getHelper().getBacpacStatus();
		
		int bossReady = bacpacStatus.getBOSSReady();				
				
		System.out.println("Camera BOSSReady :"+bossReady);
		
		if(ENCameraBoss.READY_TO_RECEIVE_CMD.getCode()!=bossReady){
			throw new Exception("The go pro is not ready to receive url cmd(i.e start record).");
		}
		
		return bacpacStatus;
	}
	
	private GoProHelper helper;

	public GoProApi(String password){
		setHelper(new GoProHelper("10.5.5.9", 80,password));

	}

	public GoProHelper getHelper() {
		return helper;
	}

	public void setHelper(GoProHelper helper) {
		this.helper = helper;
	}
	
	public void startRecord() throws Exception{
		getHelper().startRecord();
	}
	
	public void stopRecord() throws Exception{
		getHelper().stopRecord();
	}
	
	public void powerOn() throws Exception{
		getHelper().turnOnCamera();
	}
	
	public void powerOff() throws Exception{
		getHelper().turnOffCamera();
	}
	
	
}