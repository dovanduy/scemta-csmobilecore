package com.cs.automation.run.cloudrun.mode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.cs.automation.run.mode.CloudRun;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class SauceLabCloudRun extends CloudRun implements ICloudRunMode {

    private static synchronized String getNextAvailableDeviceId() {
        for (final String device : deviceList) {
            deviceList.remove(device);
            return device;
        }
        return null;
    }

    // private static Properties prop = new Properties();
    private AppiumDriver<MobileElement> driver;

    @Override
    public void configureCloud() {
        // TODO Auto-generated method stub

    }

    /**
     * @return returns the deviceId of the next available device
     */
    @Override
    public String getAvailableDeviceToRunTest() {
        return getNextAvailableDeviceId();

    }

    private String getDeviceName() {
        Set<String> devices = devicesNameMap.keySet();
        for (final String device : devices) {
            System.out.println("---------------" + devicesNameMap.get(device).size());
            if (devicesNameMap.get(device).size() != 0) {
                System.out.println("-------------- Device Name is :" + device);
                return device;
            } else {
                devicesNameMap.remove(device);
            }
        }
        return null;
    }

    @Override
    public AppiumDriver<MobileElement> getDriverForDevice(String deviceId) {
        try {
            driver =
                    new AndroidDriver<MobileElement>(new URL(cloudResource.getString("URL")),
                            setCapabilities());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    private String getPlatformVesrion() {
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
    public synchronized DesiredCapabilities setCapabilities() {
        System.out.println("Setting Android Desired Capabilities:");
        final DesiredCapabilities androidCapabilities = new DesiredCapabilities();
        androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, getDeviceName());
        androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
                getPlatformVesrion());
        androidCapabilities.setCapability("browserName", PropertyReader.readEnvOrConfigProperty("BROWSER_NAME"));

        androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        androidCapabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.5.3");
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

    @Override
    public void setDeviceAsFree(String deviceId) {
    }

    @Override
    public void uploadApplication() {
        // TODO Auto-generated method stub

    }

}
