package com.cs.automation.run.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.cs.automation.run.cloudrun.mode.BrowserStackCloudRun;
import com.cs.automation.run.cloudrun.mode.SauceLabCloudRun;
import com.cs.automation.utils.enums.ECloudRunType;
import com.cs.automation.utils.general.Constants;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class TotalRun implements IRunMode {
	protected static ResourceBundle cloudResource;
	private static int deviceCount;
	protected static ResourceBundle resources;
	/**
	 * deviceMapping is a map in which the key is deviceId with value as true/false when test is
	 * running on device value is false, if value is true it is available to execute test make value
	 * = false when test get started executing on device and value = true when test get executed
	 */
//	public static ArrayList<String> deviceList = new ArrayList<String>();

	static {
		cloudResource = ResourceBundle.getBundle("cloud");
		resources = ResourceBundle.getBundle("config");
		init(TotalRun.getCloudRunMode());
		System.out.println("inside static block of RUN ALL ******");

	}

	private static IRunMode cloudRunObject;
	private static IRunMode gridRunObject = new AppiumGrid();

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

	public TotalRun() {
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
		return null;	// cloudRunObject.getAvailableDeviceToRunTest();
	}

	@Override
	public ArrayList<String> getConnectedDevicesList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getDevicesList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppiumDriver<MobileElement> getDriverForDevice(String deviceId, String device_type) {

		if(cloudRunObject.getDevicesList().contains(deviceId))
			return cloudRunObject.getDriverForDevice(deviceId, device_type);
		else
			return gridRunObject.getDriverForDevice(deviceId, device_type);
	}

	@Override
	public int getTotalNoOfDevicesForRun() {
		deviceCount = cloudRunObject.getDevicesList().size() + gridRunObject.getDevicesList().size();
		return deviceCount;
	}

	@Override
	public void onSuiteFinish() {
		cloudRunObject.onSuiteFinish();
		gridRunObject.onSuiteFinish();
	}

	@Override
	public void onSuiteStarted() {
		cloudRunObject.onSuiteStarted();
		gridRunObject.onSuiteStarted();
	}

	@Override
	public void setDeviceAsFree(String deviceId) {
		if(cloudRunObject.getDevicesList().contains(deviceId))
			cloudRunObject.setDeviceAsFree(deviceId);
		else
			gridRunObject.setDeviceAsFree(deviceId);
		
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
