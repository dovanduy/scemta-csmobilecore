package com.cs.automation.run.cloudrun.mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.cs.automation.run.mode.CloudRun;
import com.cs.automation.util.reporter.ReportLogService;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class BrowserStackCloudRun extends CloudRun {


	static Properties cloudProperties = null;
	ReportLogService report = new ReportLogServiceImpl(BrowserStackCloudRun.class);
	private Map<String, String> deviceVersionsMap = new HashMap<String, String>();

	private static synchronized String getNextAvailableDeviceId() {
		for (final String device : deviceList) {
			deviceList.remove(device);
			return device;
		}
		return null;
	}

	// private static Properties prop = new Properties();
	private AppiumDriver<MobileElement> driver;

	private Properties getCloudPropertiesInstance() {
		if (cloudProperties == null) {

			cloudProperties = new Properties();
			String fileName = "cloud.properties";
			String cloudPropertyPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + fileName;
			try {
				cloudProperties.load(new FileInputStream(new File(cloudPropertyPath)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cloudProperties;
	}


	/**
	 * @return returns the deviceId of the next available device
	 */
	@Override
	public String getAvailableDeviceToRunTest() {
		return getNextAvailableDeviceId();

	}

	/**
	 * @author mallikarjun
	 * @return deviceNames - provided comma separated device names in cloud.properties
	 */
	private String getDeviceNames() {
		return getCloudPropertyValue("DEVICE_NAME");
	}
	
	/**
	 * @author mallikarjun
	 * @return deviceVersions - provided comma separated device Versions in cloud.properties
	 */
	private String getDeviceVersions() {
		return getCloudPropertyValue("PLATFORM_VERSION");
	}
	
	/**
	 * @author mallikarjun
	 * @return deviceVersionMap - the map with key as deviceId and value as corresponding os_version provided in cloud.properties
	 */
	private Map<String, String> getDeviceVersionsMap() {
		if (deviceVersionsMap.isEmpty()) {
			List<String> deviceNames = Arrays.asList(getDeviceNames().split(","));
			List<String> deviceVersions = Arrays.asList(getDeviceVersions().split(","));
			
			for (int deviceIndex = 0; deviceIndex < deviceNames.size(); deviceIndex++) {
				deviceVersionsMap.put(deviceNames.get(deviceIndex), deviceVersions.get(deviceIndex));
			}
		}
		
		return deviceVersionsMap;
	}
	

	/**
	 * @author mallikarjun
	 * @return driver - returns the driver instance for selected platform (Android/IOS) with browserstack capabilities
	 */
	@Override
	public AppiumDriver<MobileElement> getDriverForDevice(String deviceId, String device_type) {

		if (device_type.equalsIgnoreCase("ios")) {
			try {
				driver =
						new IOSDriver<MobileElement>(new URL(cloudResource.getString("URL")),
								getIOSCaps(deviceId));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}else {
			try {
				driver =
						new AndroidDriver<MobileElement>(new URL(cloudResource.getString("URL")),
								getAndroidCaps(deviceId));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return driver;
	}

	
	protected String getPlatformVesrion() {
		Set<String> devices = devicesNameMap.keySet();
		for (final String device : devices) {
			String platformVersion = null;
			if (devicesNameMap.get(device).size() != 0) {
				platformVersion = devicesNameMap.get(device).get(0);
				devicesNameMap.get(device).remove(0);
			}
			System.out.println("-------------- Platform version is :" + platformVersion);
			return platformVersion;
		}
		return null;
	}

	@Override
	public int getTotalNoOfDevicesForRun() {
		return super.getTotalNoOfDevicesForRun();
	}

	@Override
	public void onSuiteFinish() {
		driver.quit();
	}

	@Override
	public void onSuiteStarted() {

	}


	@Override
	public void setDeviceAsFree(String deviceId) {
	}



	private DesiredCapabilities getAndroidCaps(String deviceId) {
		System.out.println("Setting Android Desired Capabilities:");
		String browserName = PropertyReader.readEnvOrConfigProperty("BROWSER_NAME");
		
		if (browserName != null ) {
			if (browserName.length() > 0) {
				report.info("Running test on android browser :: "+ browserName);
				return getAndroidWebCaps(deviceId);
			}
		}

		report.info("Running test for android native app");
		return getAndroidNativeCaps(deviceId);	
	}
	
	private DesiredCapabilities getAndroidNativeCaps(String deviceId) {
		final DesiredCapabilities androidCapabilities = new DesiredCapabilities();
		androidCapabilities.setCapability("device", deviceId);
		androidCapabilities.setCapability("realMobile", "true");
		androidCapabilities.setCapability("os_version",
				getPlatformVersionForDevice(deviceId));

		androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		androidCapabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, PropertyReader.readEnvOrConfigProperty("APPIUM_VERSION"));
		androidCapabilities.setCapability(AndroidMobileCapabilityType.DONT_STOP_APP_ON_RESET,
				PropertyReader.getProperty(Constants.DONT_STOP_APP_ON_RESET));
		androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_PACKAGE,
				PropertyReader.getProperty(Constants.APP_WAIT_PACKAGE));
		androidCapabilities.setCapability(MobileCapabilityType.LOCALE, PropertyReader
				.getProperty("LOCALE"));
		androidCapabilities.setCapability(MobileCapabilityType.LANGUAGE, PropertyReader
				.getProperty(Constants.LANGUAGE));
		androidCapabilities.setCapability(MobileCapabilityType.ORIENTATION, PropertyReader
				.getProperty(Constants.ORIENTATION));
		androidCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, PropertyReader
				.getProperty(Constants.NEW_COMMAND_TIMEOUT));
		androidCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader
				.getProperty(Constants.NO_RESET));
		androidCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader
				.getProperty(Constants.FULL_RESET));
		androidCapabilities.setCapability(MobileCapabilityType.APP, PropertyReader.readEnvOrConfigProperty("ANDROID_APP_PATH"));
		androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, PropertyReader
				.readEnvOrConfigProperty(Constants.APP_PACKAGE));
		androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, PropertyReader
				.readEnvOrConfigProperty(Constants.APP_ACTIVITY));
		if (PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY) != null) {
			androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,
					PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY));
		}
		
		return androidCapabilities;
	}
	
	private DesiredCapabilities getAndroidWebCaps(String deviceId) {
		System.out.println("Setting Android Desired Capabilities:");

		final DesiredCapabilities androidCapabilities = new DesiredCapabilities();
		androidCapabilities.setCapability("device", deviceId);
		androidCapabilities.setCapability("realMobile", "true");
		androidCapabilities.setCapability("os_version",
				getPlatformVersionForDevice(deviceId));

		androidCapabilities.setCapability("browserName", PropertyReader.readEnvOrConfigProperty("BROWSER_NAME"));

		androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		androidCapabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, PropertyReader.readEnvOrConfigProperty("APPIUM_VERSION"));

		androidCapabilities.setCapability(MobileCapabilityType.ORIENTATION, PropertyReader
				.getProperty(Constants.ORIENTATION));
		androidCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader
				.getProperty(Constants.NO_RESET));
		androidCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader
				.getProperty(Constants.FULL_RESET));

		return androidCapabilities;
	}


	private DesiredCapabilities getIOSCaps(String deviceId) {
		final DesiredCapabilities iOSCapabilities = new DesiredCapabilities();

		iOSCapabilities.setCapability("device", deviceId);
		iOSCapabilities.setCapability("realMobile", "true");
		iOSCapabilities.setCapability("os_version",getPlatformVersionForDevice(deviceId));

		iOSCapabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, PropertyReader.readEnvOrConfigProperty(Constants.BUNDLE_ID));
		iOSCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, PropertyReader.readEnvOrConfigProperty(Constants.AUTOMATION_NAME));
		iOSCapabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true);
		iOSCapabilities.setCapability(MobileCapabilityType.FULL_RESET, PropertyReader.getProperty(Constants.FULL_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.NO_RESET, PropertyReader.getProperty(Constants.NO_RESET));
		iOSCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone"); // For real device
		iOSCapabilities.setCapability(IOSMobileCapabilityType.BROWSER_NAME, PropertyReader.readEnvOrConfigProperty(Constants.BROWSER_NAME));

		return iOSCapabilities;

	}

	private String getCloudPropertyValue(String key) {
		String value = System.getProperty(key);
		if (value == null || value.trim().length() == 0) {
			value = getCloudPropertiesInstance().getProperty(key);
		}
		return value;
	}

	/**
	 * @author mallikarjun
	 * @param deviceId - the device id of mobile device (ios/android)
	 * @return platformVersion - the platformVersion provided for given device in cloud.properties
	 * **/
	private String getPlatformVersionForDevice(String deviceId) {
		return getDeviceVersionsMap().get(deviceId);
	}

}
