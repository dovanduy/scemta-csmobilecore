package com.cs.automation.appium.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.cs.automation.run.mode.RunModeFactory;
import com.cs.automation.utils.enums.ERunModeType;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class DeviceManager {
	
	/**
	 * @author mallikarjun
	 * @return 	true - If user has provided the devices in config.properties
	 * 			false - If user has not provided the devices in config.properties
	 */
	public static boolean isUserProvidedDevices() {
		String platform = getSelectedPlatform();
		if (platform.equalsIgnoreCase(Constants.ANDROID)) {
			String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
			if (androidDevices.trim().isEmpty())
				return false;
			else
				return true;
		}else if (platform.equalsIgnoreCase(Constants.IOS)) {
			String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
			if (iosDevices.trim().isEmpty())
				return false;
			else
				return true;
		}else
			return false;
	}
	
	/**
	 * @author mallikarjun
	 * @return selectedAppType - returns the APP_TYPE provided in config.properties (ANDROID/IOS)
	 */
	public static String getSelectedPlatform() {
		String platform = PropertyReader.readEnvOrConfigProperty(Constants.APP_TYPE);
		return platform;
	}
	
	
	
	/**
	 * @author mallikarjun
	 * @return listOfDevices - 	returns the list contains the id of selected devices
	 * 							List of devices provided in config.properties
	 * 							If no devices provided are provided in config.properties then this will read the connected devices
	 */
	public static List<String> getSelectedDevices() {
		ERunModeType eRunMode = RunModeFactory.getRunModeType();
		switch (eRunMode) {
		case LOCAL_RUN:
			return getDevicesListForLocalRunMode();
		case REMOTE_APPIUM:
			return getDevicesListForLocalRunMode();
		case CLOUD_RUN:
			return getDevicesListForCloudRunMode();
		default:
			return null;
		}
		
	}
	
	
	/**
	 * @author mallikarjun
	 * @return listOFDevices - list of connected devices to local machine
	 * @see This method will read the user provided devices first (i.e. comma separated values provided in config.properties)
	 * 		For Android - the values provided for key 'AndroidDevices'
	 * 		For IOS - the values provided for key 'IOSDevices'
	 * 		If no values provided for the above keys, then this method will read the connected devices.
	 */
	private static List<String> getDevicesListForLocalRunMode() {
		List<String> devicesList = new ArrayList<>();
		
		if (isUserProvidedDevices()) {
			if (isPlatformIsAndroid()) {
				String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				devicesList.addAll(Arrays.asList(androidDevicesArr));
			}else if(isPlatformIsIOS()) {
				String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				devicesList.addAll(Arrays.asList(iosDevicesArr));
			}
		}else {
			if (isPlatformIsAndroid()) {
				AndroidDeviceConfiguration androidDeviceConfiguration = new AndroidDeviceConfiguration();
				try {
					devicesList.addAll(androidDeviceConfiguration.getDeviceSerial());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return devicesList;
			}else if(isPlatformIsIOS()) {
				IOSDeviceConfiguration iosDeviceConfiguration = new IOSDeviceConfiguration();
				if (iosDeviceConfiguration.getIOSUDID() != null ) 
					devicesList.addAll(iosDeviceConfiguration.getIOSUDID());
				if (devicesList.isEmpty()) 
					devicesList.add(Constants.IOS_SIMULATOR);
				return devicesList;
			}
		}
		return devicesList;
	
	}
	
	/**
	 * @author mallikarjun
	 * @return listOfDevices - provided in cloud.properties
	 */
	private static List<String> getDevicesListForCloudRunMode() {
		ResourceBundle cloudResource = ResourceBundle.getBundle("cloud");
		String deviceNames = cloudResource.getString("DEVICE_NAME");
		List<String> devicesToRun = Arrays.asList(deviceNames.split(","));
		return devicesToRun;
	}
	
	/**
	 * @author mallikarjun
	 * @return 	true - if selected APP_TYPE is Android (In config.properties)
	 * 			false - if selected APP_TYPE is not Android 
	 */
	public static boolean isPlatformIsAndroid() {
		if (getSelectedPlatform().equalsIgnoreCase(Constants.ANDROID)) {
			return true;
		}else
			return false;
	}
	
	
	/**
	 * @author mallikarjun
	 * @return	true - If selected APP_TYPE is IOS (In config.properties)
	 * 			false - If selected APP_TYPE is not IOS
	 */
	public static boolean isPlatformIsIOS() {
		if (getSelectedPlatform().equalsIgnoreCase(Constants.IOS)) {
			return true;
		}else
			return false;
	}
}
