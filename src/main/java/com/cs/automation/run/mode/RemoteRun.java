package com.cs.automation.run.mode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.cs.automation.util.reporter.ReportLogService;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class RemoteRun implements IRunMode {

    private static int deviceCount;
    private static ReportLogService report = new ReportLogServiceImpl(RemoteRun.class);
    private static Map<String, String> remoteAppiumUrlMap = new HashMap<String, String>();
    private static ConcurrentHashMap<String, Boolean> deviceMapping =
            new ConcurrentHashMap<String, Boolean>();
    private static ArrayList<String> devices = new ArrayList<String>();
    private static Map<String, String> baseProjectPathMap = new HashMap<String, String>();

    static {
        refreshDevices();
    }

    /**
     * @return returns the next available device from the deviceMapping deviceMapping is map where
     *         key is deviceId and value is Boolean (i.e. true/false) false - describes the device
     *         is busy in some test running
     **/
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

    private static void refreshDevices() {
		/*
		 * try { RemoteClientConnector.startConnectionForProvidedIP(); } catch
		 * (Exception e) { report.error("Error in starting connection");
		 * e.printStackTrace(); } deviceCount =
		 * RemoteClientConnector.getTotalNoOfDevicesForRun(); devices =
		 * RemoteClientConnector.getRemoteDevices(); remoteAppiumUrlMap =
		 * RemoteClientConnector.getRemoteAppiumUrls(); baseProjectPathMap =
		 * RemoteClientConnector.getBasePathMap();
		 * 
		 * for (final String device : devices) { deviceMapping.put(device, true); }
		 */
    }

    /**
     * @return returns the desired capabilities for provided device
     * @Info
     **/
    private synchronized DesiredCapabilities androidNative(String deviceId) {
        System.out.println("Setting Android Desired Capabilities:");
        final DesiredCapabilities androidCapabilities = new DesiredCapabilities();
        androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
        androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.X");
        androidCapabilities.setCapability("browserName", "");
        androidCapabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, PropertyReader
                .getProperty(Constants.APPIUM_VERSION));
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
        androidCapabilities.setCapability(MobileCapabilityType.APP, getAppPath(deviceId));
        androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, PropertyReader
                .getProperty(Constants.APP_PACKAGE));
        androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, PropertyReader
                .getProperty(Constants.APP_ACTIVITY));
        if (PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY) != null) {
            androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY,
                    PropertyReader.getProperty(Constants.APP_WAIT_ACTIVITY));
        }
        return androidCapabilities;
    }

    @Override
    public void executeAfterRun() {
        // TODO Auto-generated method stub

    }

    @Override
    public void executeBeforeRun() {
        // TODO Auto-generated method stub

    }

    /**
     * @return returns the app path (i.e. This method reads the application name from config prop
     *         and will get the path up to applications folder, which is at root of project and
     *         returns the whole path)
     */
    private String getAppPath(String deviceId) {
        String app_path =
                baseProjectPathMap.get(deviceId.split("##")[0]) + "/applications/"
                        + PropertyReader.getProperty("APP_NAME");
        System.out.println("Printing app path for device " + deviceId + "path ===" + app_path);
        return app_path;
    }

    /**
     * @return returns the next available device
     */
    @Override
    public String getAvailableDeviceToRunTest() {
        // TODO Auto-generated method stub
        return getNextAvailableDeviceId();
    }

    @Override
    public ArrayList<String> getConnectedDevicesList() {
		/* RemoteClientConnector.clearAllLists(); */
        refreshDevices();
        return devices;
    }

    /**
     * @return returns the deviceName in format (ipaddress+devcieId)
     ***/
    protected String getDeviceName(String ipAdress, String deviceId) {
        return ipAdress + deviceId;
    }

    @Override
    public ArrayList<String> getDevicesList() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return returns the driver for provided deviceId
     */
    @Override
    public synchronized AppiumDriver<MobileElement> getDriverForDevice(String deviceId) {
        // TODO Auto-generated method stub
        String remoteAddress = "http://" + remoteAppiumUrlMap.get(deviceId) + "/wd/hub";
        System.out.println("printing address for device  " + deviceId + "  Address "
                + remoteAddress);
        try {
            AppiumDriver<MobileElement> driver =
                    new AndroidDriver<>(new URL(remoteAddress), androidNative(deviceId));
            System.out.println("Returning driver.....");
            return driver;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return returns the total no of devices
     **/
    @Override
    public int getTotalNoOfDevicesForRun() {
        return deviceCount;
    }

    /**
     * @Info called on the suite end
     */
    @Override
    public void onSuiteFinish() {
    }

    /**
     * @Info called on the suite start
     */
    @Override
    public void onSuiteStarted() {
    }

    /**
     * @Info after running the test on the device it should get free i.e. its value in deviceMapping
     *       as true true defines device is available to run the test (i.e. not busy)
     **/
    @Override
    public void setDeviceAsFree(String deviceId) {
        deviceMapping.put(deviceId, true);

    }

    @Override
    public void setDeviceId(String deviceId) {
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
