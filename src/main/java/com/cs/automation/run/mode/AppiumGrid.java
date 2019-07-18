package com.cs.automation.run.mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.cs.automation.appium.device.DeviceManager;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.appium.AvailablePorts;
import com.cs.automation.utils.enums.ERunModeType;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

import bsh.This;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class AppiumGrid implements IRunMode {
	
	private static List<String> devices = new ArrayList<String>();
	
	/**
	 * deviceMapping is a map in which the key is deviceId with value as true/false when test is
	 * running on device value is false, if value is true it is available to execute test make value
	 * = false when test get started executing on device and value = true when test get executed
	 */
	private static ConcurrentHashMap<String, Boolean> deviceMapping =
			new ConcurrentHashMap<String, Boolean>();

	/**
	 * static block to get the available devices
	 * @Info executes as soon as class get load into memory
	 */

	static {
		refreshDeviceList();
	}

	private static synchronized String getNextAvailableDeviceId() {
		// final ConcurrentHashMap.KeySetView<String, Boolean> devices = deviceMapping.keySet();
		Set<String> devices = deviceMapping.keySet();
		for (final String device : devices) {
			if (deviceMapping.get(device) == true) {
				deviceMapping.put(device, false);
				return device;
			}
		}
		return null;
	}


	/**
	 * @author mallikarjun
	 * @see This method checks on which os (windows ,mac or linux) the excution will going on,
	 * 		According to that, it will check for connected devices.
	 * 		Also, will add the connected devices in list
	 * 
	 * 
	 * **/
	private static void refreshDeviceList() {
		/*
		 * devices.clear(); deviceMapping.clear();
		 * 
		 * try {
		 * 
		 * devices = DeviceManager.getSelectedDevices();
		 * 
		 * for (final String device : devices) { deviceMapping.put(device, true); }
		 * System.out.println(deviceMapping); } catch (final Exception e) {
		 * e.printStackTrace(); System.out.println("Failed to initialize framework"); }
		 */
	}
	
	
	//***************************************
	
	
	private ReportLogServiceImpl report = new ReportLogServiceImpl(AppiumGrid.class);
	private AppiumDriver<MobileElement> driver;

	@Override
	public void executeAfterRun() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeBeforeRun() {
		// TODO Auto-generated method stub
		
	}


	private String device_udid;
	
	/**
	 * @return returns the deviceId of the next available device
	 */
	@Override
	public String getAvailableDeviceToRunTest() {
		device_udid = getNextAvailableDeviceId();
		return device_udid;
	}

	@Override
	public List<String> getConnectedDevicesList() {
		refreshDeviceList();
		return devices;
	}
	
	
	@Override
	public List<String> getDevicesList() {
		return DeviceManager.getSelectedDevices(ERunModeType.APPIUM_GRID);
	}

	@Override
	public AppiumDriver<MobileElement> getDriverForDevice(String deviceId, String device_type) {
		String appiumUrl = PropertyReader.readEnvOrConfigProperty("appiumurl");
		URL url = null;
		try {
			url = new URL(appiumUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
		if (device_type.equalsIgnoreCase("ios")) {
			//ios -- is selected
			driver = new IOSDriver<>(url, iosCaps(deviceId));
		}else {
			//android -- is selected
			driver = new AndroidDriver<>(url, androidCaps(deviceId));
		}
		
		return driver;
	
	}

	@Override
	public int getTotalNoOfDevicesForRun() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void onSuiteFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuiteStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDeviceAsFree(String deviceId) {
		// TODO Auto-generated method stub
		
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
	
	
	/**
	 * @return returns the desired capabilities for android web
	 */

	private synchronized DesiredCapabilities androidCaps(String deviceId) {
		DesiredCapabilities androidCapabilities = new DesiredCapabilities();
		androidCapabilities.setCapability(MobileCapabilityType.UDID, deviceId);
		androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		//androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, PropertyReader.readEnvOrConfigProperty(Constants.PLATFORM_NAME));
		//androidCapabilities.setCapability(MobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		//androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, PropertyReader.readEnvOrConfigProperty(Constants.PLATFORM_VERSION));
		androidCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, PropertyReader.readEnvOrConfigProperty(Constants.NEW_COMMAND_TIMEOUT));
		
		try {
			androidCapabilities = addExtraCapsFromFile(androidCapabilities, Constants.ANDROID_CAPS_PROP_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return androidCapabilities; 
	}
	
	/**
	 * @author mallikarjun
	 * @param caps - desired capabilities
	 * @param fileName - .property file name (to add extra capabilities) 
	 * @return returns desired capabilities with added extra capabilities from .properties file
	 * **/
	private DesiredCapabilities addExtraCapsFromFile(DesiredCapabilities caps,String fileName) throws IOException {
		String androidCapsPropFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator +  fileName;
		File anCapsFile = new File(androidCapsPropFilePath);
		if (anCapsFile.exists()) {
			InputStream input = new FileInputStream(anCapsFile);
			Properties prop = new Properties();
			prop.load(input);
			Enumeration<?> keys = prop.propertyNames();
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				String value = prop.getProperty(name);
				caps.setCapability(name, value);
			}
			return caps;
		}else
			return caps;
	}
	
	/**
	 * @author
	 * @return capabilities for ios real device
	 **/
	private synchronized DesiredCapabilities iosCaps(String udid) {

		DesiredCapabilities iOSCapabilities = new DesiredCapabilities();
		System.out.println("Setting iOS Desired Capabilities:");

		iOSCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, ""); // For real device
		iOSCapabilities.setCapability(MobileCapabilityType.UDID,udid);
		//iOSCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, PropertyReader.readEnvOrConfigProperty(Constants.AUTOMATION_NAME));
		iOSCapabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true);
		iOSCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader.readEnvOrConfigProperty(Constants.FULL_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader.readEnvOrConfigProperty(Constants.NO_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone"); // For real device
		//iOSCapabilities.setCapability(IOSMobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		
		
		//added in v0.1.3 _____by Suraj //
		AvailablePorts ap = new AvailablePorts();
		int wdaPort = 8100;
		try {
			wdaPort = ap.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		iOSCapabilities.setCapability("wdaLocalPort",wdaPort );

		try {
			iOSCapabilities = addExtraCapsFromFile(iOSCapabilities, Constants.IOS_CAPS_PROP_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return iOSCapabilities;
	}

}
