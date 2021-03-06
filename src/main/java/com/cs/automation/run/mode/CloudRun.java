package com.cs.automation.run.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.cs.automation.appium.device.DeviceManager;
import com.cs.automation.run.cloudrun.mode.BrowserStackCloudRun;
import com.cs.automation.run.cloudrun.mode.SauceLabCloudRun;
import com.cs.automation.utils.enums.ECloudRunType;
import com.cs.automation.utils.enums.ERunModeType;
import com.cs.automation.utils.general.Constants;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class CloudRun implements IRunMode {
	protected static ResourceBundle cloudResource;
	private static int deviceCount;
	protected static ResourceBundle resources;
//	protected static ConcurrentHashMap<String, List<String>> devicesNameMap =
//			new ConcurrentHashMap<String, List<String>>();
//	protected static ConcurrentHashMap<String, List<String>> deviceNamePlatformVersionMap =
//			new ConcurrentHashMap<String, List<String>>();

	/**
	 * deviceMapping is a map in which the key is deviceId with value as true/false when test is
	 * running on device value is false, if value is true it is available to execute test make value
	 * = false when test get started executing on device and value = true when test get executed
	 */
	public static ArrayList<String> deviceList = new ArrayList<String>();

	static {
		cloudResource = ResourceBundle.getBundle("cloud");
		resources = ResourceBundle.getBundle("config");
		init(CloudRun.getCloudRunMode());
		System.out.println("inside static block***************");
		String[] andrDeviceNames = cloudResource.getString(Constants.ANDROID_DEVICES).split(",");
		String[] iosDeviceNames = cloudResource.getString(Constants.IOS_DEVICES).split(",");
//		String[] platformVersions = cloudResource.getString("PLATFORM_VERSION").split(",");

		/*
        if (PropertyReader.getProperty(Constants.APP_TYPE).equalsIgnoreCase("android")
                || PropertyReader.getProperty(Constants.APP_TYPE).equalsIgnoreCase("both")) {
            for (int i = 0; i < cloudResource.getString(Constants.PLATFORM_VERSION).split(",").length; i++) {
                devicesArrayList.add(cloudResource.getString(Constants.DEVICE_NAME).split(",")[i]);
            }
        }
		 */

//		System.err.println("deviceNames:"+deviceNames.toString());
//		
//		for (String device : deviceNames) {
//			System.err.println("deviceName:"+device);
//		}
//		
//		
//		System.err.println("platformVersions:"+platformVersions.toString());
//		
//		for (String version : platformVersions) {
//			System.err.println("version:"+version);
//		}
//		
		if(andrDeviceNames.length > 0)
		for (final String device : andrDeviceNames) {
			deviceList.add(device);
		}
		if(iosDeviceNames.length > 0)
		for (final String device : iosDeviceNames) {
			deviceList.add(device);
		}
//		for (String deviceName : deviceNames) {
//			if (devicesNameMap.get(deviceName) == null) {
//				List<String> platformVersionList = new ArrayList<String>();
//				int count = 0;
//				for (int i = Arrays.asList(deviceNames).indexOf(deviceName) + 1; i < deviceNames.length; i++) {
//					System.out.println(deviceNames[i] + "------------------" + deviceName + "   "
//							+ count);
//					if (deviceName.equals(deviceNames[i])) {
//						count++;
//						if (count == 1) {
//							platformVersionList.add(platformVersions[Arrays.asList(deviceNames)
//							                                         .indexOf(deviceName)]);
//						}
//						platformVersionList.add(platformVersions[i]);
//					} else if (i == deviceNames.length - 1) {
//						System.out.println("inside else if" + deviceNames.length);
//						platformVersionList.add(platformVersions[Arrays.asList(deviceNames)
//						                                         .indexOf(deviceName)]);
//					}
//				}
//				devicesNameMap.put(deviceName, platformVersionList);
//				System.out.println("List of platform version is:" + platformVersionList);
//			}
//		}
	}

	private static CloudRun cloudRunObject;

	private static ECloudRunType getCloudRunMode() {
		String runMode = readEnvOrConfigProperty("CLOUD_RUN");
		if (runMode.equalsIgnoreCase("SAUCELAB")) {
			return ECloudRunType.SAUCE_LAB;
		}else if(runMode.equalsIgnoreCase("BROWSER_STACK")) {
			return ECloudRunType.BROWSER_STACK;
		}
		return null;
	}

	private static void init(ECloudRunType type) {
		switch (type) {
		case SAUCE_LAB:
			cloudRunObject = new SauceLabCloudRun();
			break;
		case BROWSER_STACK:
			cloudRunObject = new BrowserStackCloudRun();
			break;
		default:
			cloudRunObject = null;
			break;
		}
	}

	private static String readEnvOrConfigProperty(String key) {
		String value = System.getProperty(key);
		if (value == null || value.trim().length() == 0) {
			value = resources.getString(key);
		}
		return value;

	}

	public CloudRun() {
	}

	@Override
	public void executeAfterRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeBeforeRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAvailableDeviceToRunTest() {
		return cloudRunObject.getAvailableDeviceToRunTest();
	}

	@Override
	public ArrayList<String> getConnectedDevicesList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDevicesList() {
		return DeviceManager.getSelectedDevices(ERunModeType.CLOUD_RUN);
	}

	@Override
	public AppiumDriver<MobileElement> getDriverForDevice(String deviceId, String device_type) {
		return cloudRunObject.getDriverForDevice(deviceId, device_type);
	}

	@Override
	public int getTotalNoOfDevicesForRun() {
		deviceCount = cloudResource.getString(Constants.PLATFORM_VERSION).split(",").length;
		return deviceCount;
	}

	@Override
	public void onSuiteFinish() {
		cloudRunObject.onSuiteFinish();
	}

	@Override
	public void onSuiteStarted() {
		cloudRunObject.onSuiteStarted();
	}

	@Override
	public void setDeviceAsFree(String deviceId) {
		cloudRunObject.setDeviceAsFree(deviceId);
	}

	@Override
	public void setDeviceId(String deviceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startAppiumServersForDevices(List<String> devices) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopAppiumServersForDevices(List<String> devices) {
		// TODO Auto-generated method stub
		
	}

}
