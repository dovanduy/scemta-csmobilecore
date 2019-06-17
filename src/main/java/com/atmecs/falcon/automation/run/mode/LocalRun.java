package com.atmecs.falcon.automation.run.mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.atmecs.falcon.automation.appium.device.AndroidDeviceConfiguration;
import com.atmecs.falcon.automation.appium.device.DeviceManager;
import com.atmecs.falcon.automation.appium.device.IOSDeviceConfiguration;
import com.atmecs.falcon.automation.appium.manager.AppiumManager;
import com.atmecs.falcon.automation.appium.manager.AppiumParallelTest;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.atmecs.falcon.automation.utils.appium.AvailablePorts;
import com.atmecs.falcon.automation.utils.general.Constants;
import com.atmecs.falcon.automation.utils.general.PropertyReader;

import bsh.This;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class LocalRun implements IRunMode {
	private static List<String> devices = new ArrayList<String>();
	private static IOSDeviceConfiguration iosDevice = new IOSDeviceConfiguration();
	private static AndroidDeviceConfiguration androidDevice = new AndroidDeviceConfiguration();

	/**
	 * appiumManagerMap contains key is deviceId and value is AppiumManager object So, we can get
	 * AppiumManager object to get url
	 */
	private static ConcurrentHashMap<String, AppiumManager> appiumManagerMap =
			new ConcurrentHashMap<String, AppiumManager>();

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
	 * @author suraj
	 * @see This method checks on which os (windows ,mac or linux) the excution will going on,
	 * 		According to that, it will check for connected devices.
	 * 		Also, will add the connected devices in list
	 * 
	 * 
	 * **/
	private static void refreshDeviceList() {
		devices.clear();
		deviceMapping.clear();
		
		try {
			
			devices = DeviceManager.getSelectedDevices();
			
			for (final String device : devices) {
				deviceMapping.put(device, true);
			}
			System.out.println(deviceMapping);
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("Failed to initialize framework");
		}

	}

	/***
	 * This methods for starting the appium server at the start
	 ***/

	private static AppiumServiceBuilder startAppiumServerForDevice(String device_id)
			throws Exception {

		AppiumManager appiumMan = new AppiumManager();

		if (device_id == null) {
			System.out.println("No devices are free to run test or Failed to run test");
			return null;
		}
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			if (iosDevice.checkiOSDevice(device_id)) {
				iosDevice.setIOSWebKitProxyPorts(device_id);
				iosDevice.getDeviceName(device_id).replace(" ", "_");
			} else if (device_id.equalsIgnoreCase("iossimulator")) {
				System.out.println("The device is " + device_id);
			}
			if (!iosDevice.checkiOSDevice(device_id) && !device_id.equalsIgnoreCase("iossimulator")) {
				androidDevice.getDeviceModel(device_id);
			}
		} else {
			androidDevice.getDeviceModel(device_id);
		}
		appiumManagerMap.put(device_id, appiumMan);
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			if (iosDevice.checkiOSDevice(device_id)) {
				//String webKitPort = iosDevice.startIOSWebKit(device_id);
				String webKitPort = "";
				return appiumMan.appiumServerForIOS(device_id, webKitPort);
			} else if (device_id.equalsIgnoreCase("iossimulator")) {
				return appiumMan.appiumServerForIOSSimulator(device_id);
			}
			if (!iosDevice.checkiOSDevice(device_id) && !device_id.equalsIgnoreCase("iossimulator")) {
				return appiumMan.appiumServerForAndroid(device_id);
			}
		} else {
			return appiumMan.appiumServerForAndroid(device_id);
		}
		return null;
	}

	private ReportLogServiceImpl report = new ReportLogServiceImpl(AppiumParallelTest.class);
	private AppiumDriver<MobileElement> driver;
	private AppiumManager appiumMan = new AppiumManager();

	private String device_udid;

	private synchronized DesiredCapabilities androidNative(String deviceId) {

		System.out.println("Setting Android Desired Capabilities:");
		DesiredCapabilities androidCapabilities = new DesiredCapabilities();
		androidCapabilities.setCapability(MobileCapabilityType.UDID, deviceId);
		androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.X");
		androidCapabilities.setCapability("browserName", "");
		androidCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader
				.readEnvOrConfigProperty(Constants.NO_RESET));
		androidCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader
				.readEnvOrConfigProperty(Constants.FULL_RESET));
		androidCapabilities.setCapability(MobileCapabilityType.APP, getAppPath());
		androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, PropertyReader
				.readEnvOrConfigProperty(Constants.APP_PACKAGE));
		androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, PropertyReader
				.readEnvOrConfigProperty(Constants.APP_ACTIVITY));
		androidCapabilities.setCapability(MobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		if (PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY) != null) {
			androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,
					PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY));
		}
		
		try {
			androidCapabilities = addExtraCapsFromFile(androidCapabilities, Constants.ANDROID_CAPS_PROP_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return androidCapabilities;
	}

	/**
	 * @return returns the desired capabilities for android web
	 */

	private synchronized DesiredCapabilities androidWeb(String deviceId) {
		DesiredCapabilities androidCapabilities = new DesiredCapabilities();
		androidCapabilities.setCapability(MobileCapabilityType.UDID, deviceId);
		androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, PropertyReader.readEnvOrConfigProperty(Constants.DEVICE_NAME));
		androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, PropertyReader.readEnvOrConfigProperty(Constants.PLATFORM_NAME));
		androidCapabilities.setCapability(MobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, PropertyReader.readEnvOrConfigProperty(Constants.PLATFORM_VERSION));
		androidCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, PropertyReader.readEnvOrConfigProperty(Constants.NEW_COMMAND_TIMEOUT));
		
		try {
			androidCapabilities = addExtraCapsFromFile(androidCapabilities, Constants.ANDROID_CAPS_PROP_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return androidCapabilities; 
	}

	@Override
	public void executeAfterRun() {
		stopAppiumServers();
	}

	@Override
	public void executeBeforeRun() {
		startAppiumServers();
	}

	/**
	 * Makes the provided device into the map as free (i.e. value = true) So that next test can use
	 * that device
	 */
	private void freeDevice(String deviceId) {
		deviceMapping.put(deviceId, true);
	}

	/**
	 * @return returns the app path (i.e. This method reads the application name from config prop
	 *         and will get the path up to applications folder, which is at root of project and
	 *         returns the whole path)
	 */
	private String getAppPath() {
		String appName = PropertyReader.readEnvOrConfigProperty("APP_NAME");
		String app_path = "";
		if(appName.trim().length() > 0 && appName != null){
			app_path =
					System.getProperty("user.dir") + File.separator + "applications" + File.separator
							+ appName;
		}
		return app_path;
	}

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
		return devices;
	}

	/**
	 * Initiate the driver with desired capabilities and appium url
	 */
	@Override
	public AppiumDriver<MobileElement> getDriverForDevice(String deviceId) {
		try {
			driver = initiateDriverForDevice(deviceId);
			return driver;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @return returns the total no of devices
	 */
	@Override
	public int getTotalNoOfDevicesForRun() {
		return devices.size();
	}

	/**
	 * Just to highlight something important in console
	 */
	private void highLightInConsole(String msg) {
		System.out.println("=======================================================");
		System.out.println(msg);
		System.out.println("=======================================================");
	}

	/**
	 * @author
	 * @param method name
	 * @return driver with the device capabilities
	 **/
	private synchronized AppiumDriver<MobileElement> initiateDriverForDevice(String deviceId) {
		appiumMan = appiumManagerMap.get(deviceId);
		if (PropertyReader.readEnvOrConfigProperty(Constants.APP_TYPE).equalsIgnoreCase("web")) {
			driver = new AndroidDriver<>(appiumMan.getAppiumUrl(), androidWeb(deviceId));
		} else {
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				try {
					if (iosDevice.checkiOSDevice(deviceId)) {
						driver = new IOSDriver<>(appiumMan.getAppiumUrl(), iosNative(deviceId));
					} else if ((PropertyReader.readEnvOrConfigProperty(Constants.APP_TYPE).equalsIgnoreCase(
							"IOS") || PropertyReader.readEnvOrConfigProperty("APP_TYPE").equalsIgnoreCase(
									"both"))
							&& deviceId.equalsIgnoreCase("iossimulator")) {
						driver = new IOSDriver<>(appiumMan.getAppiumUrl(), iosNativeSimulator());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// TODO: check for parallel run
				try {
					if (!iosDevice.checkiOSDevice(deviceId)
							&& !deviceId.equalsIgnoreCase("iossimulator")) {
						try {
							driver = new AndroidDriver<>(appiumMan.getAppiumUrl(), androidNative(deviceId));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				driver = new AndroidDriver<>(appiumMan.getAppiumUrl(), androidNative(deviceId));
			}
		}
		return driver;
	}

	/**
	 * @author
	 * @return capabilities for ios real device
	 **/
	private synchronized DesiredCapabilities iosNative(String udid) {

		DesiredCapabilities iOSCapabilities = new DesiredCapabilities();
		System.out.println("Setting iOS Desired Capabilities:");

		iOSCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, ""); // For real device
		iOSCapabilities.setCapability(MobileCapabilityType.UDID,udid);
		iOSCapabilities.setCapability(MobileCapabilityType.APP, getAppPath());
		iOSCapabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, PropertyReader.readEnvOrConfigProperty(Constants.BUNDLE_ID));
		iOSCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, PropertyReader.readEnvOrConfigProperty(Constants.AUTOMATION_NAME));
		iOSCapabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true);
		iOSCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader.readEnvOrConfigProperty(Constants.FULL_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader.readEnvOrConfigProperty(Constants.NO_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone"); // For real device
		iOSCapabilities.setCapability(IOSMobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		
		
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

	/**
	 * @author
	 * @return capabilities for ios simulator
	 **/
	private synchronized DesiredCapabilities iosNativeSimulator() {
		System.out.println("Setting iOS Simulator Desired Capabilities:");
		DesiredCapabilities iOSCapabilities = new DesiredCapabilities();
		iOSCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, PropertyReader.readEnvOrConfigProperty(Constants.PLATFORM_VERSION));
		iOSCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, PropertyReader.readEnvOrConfigProperty(Constants.AUTOMATION_NAME)); 
		iOSCapabilities.setCapability(MobileCapabilityType.APP, getAppPath());
		iOSCapabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, PropertyReader.readEnvOrConfigProperty(Constants.BUNDLE_ID));
		iOSCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, PropertyReader.readEnvOrConfigProperty(Constants.DEVICE_NAME));
		iOSCapabilities.setCapability(IOSMobileCapabilityType.NATIVE_INSTRUMENTS_LIB, true);
		iOSCapabilities.setCapability(IOSMobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));
		try {
			iOSCapabilities = addExtraCapsFromFile(iOSCapabilities, Constants.IOS_CAPS_PROP_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iOSCapabilities;
	}

	/**
	 */
	@Override
	public void onSuiteFinish() {
		//		stopAppiumServers();
	}

	/**
	 * Starting Appium Server for available devices on Local machine
	 **/
	@Override
	public void onSuiteStarted() {
		//		startAppiumServers();
	}

	/**
	 * set the current device as free, so it can be used in next test
	 */
	@Override
	public void setDeviceAsFree(String deviceId) {
		freeDevice(deviceId);
	}

	@Override
	public void setDeviceId(String deviceId) {
		device_udid = deviceId;
	}

	/**
	 * @author suraj
	 * @DateOfCreation 4thOct2016
	 **/
	private void startAppiumServers() {
		for (String device : devices) {
			try {
				highLightInConsole("Starting Appium Server for Device: " + device);
				report.info("Starting Appium server for device : " + device);
				startAppiumServerForDevice(device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author suraj
	 * @DateOfCreation 4thOct2016
	 */
	private void stopAppiumServers() {
		for (String device : devices) {
			highLightInConsole("Closing Appium Server for Device: " + device);
			report.info("Closing Appium server for device " + device);
			AppiumManager manager = appiumManagerMap.get(device);
			manager.destroyAppiumNode();
		}
	}
	
	
	/**
	 * @author suraj
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


	@Override
	public void startAppiumServersForDevices(List<String> devices) {
		startAppiumServers();
	}


	@Override
	public void stopAppiumServersForDevices(List<String> devices) {
		stopAppiumServers();
	}

}
