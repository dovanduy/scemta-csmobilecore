package com.atmecs.falcon.automation.utils.threadsTestNG;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class DriverInfo {

    private AppiumDriver<MobileElement> driver;
    private String deviceId;
    private String deviceType;

    /**
     * @author suraj
     * @param driver, deviceId (current device id for current thread
     * **/
    public DriverInfo(AppiumDriver<MobileElement> driver, String deviceId) {
        // TODO Auto-generated constructor stub
        this.driver = driver;
        this.deviceId = deviceId;
        if (driver.toString().split(":")[0].trim().equals("AndroidDriver")) {
            this.deviceType = "android";
        } else {
            this.deviceType = "ios";
        }

    }

    
    /**
     * @author suraj
     * @return returs device Id as String
     * */
    public String getDeviceId() {
        return this.deviceId;
    }

    /**
     * @author suraj
     * @return deviceType as String (android or ios)
     * **/
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * @author suraj
     * @return Appium driver instance
     * **/
    public AppiumDriver<MobileElement> getDriver() {
        return this.driver;
    }

}
