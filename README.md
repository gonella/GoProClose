# Introduction
This is an android application to make a gopro camera as a car camera, which allow the customer start record process automatically when driver starts the car. The motivation is customer should not be concern about the camera, it should be a automate process to start.  

![GoProClose - Workflow](https://github.com/gonella/GoProClose/blob/master/doc/GoProCloseWorkFlow.png "GoProClose - Workflow")

##Steps:

1. Customer starts the ignition which enable bluetooth device. This is important to know to start looking for wifi signal(it is a expensive operation). 
2. Once bluetooth signal is detected GoProClose app starts searching for gopro wifi signal. 
3. GoProClose app tries to connect to gopro wifi an send power on command to GoPro. After that, it send another wifi command, starting the record process. 
4. GoPro starts recording with a previous configuration done. 
5. (Future) - Once customer turned off the car, bluetooth signal is off and android app is able to detected and send a power off/stop record command to gopro. 

##Requirements
 1. Car must have a bluetooth device. 
 2. GoPro must enable wifi standby option. 
 3. 

# Futuro work
 1. Integrate with framework GoProJavaApi, some maven dependency must be incorporated.
 2. UI improvements, giving more customize options to the customer. 
 2. Improve some timeout values, some start/stop record process should be accurated.
 3. Download recording files from GoPro into Android smartphone. 
