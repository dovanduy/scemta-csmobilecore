package com.atmecs.falcon.automation.run.cloudrun.mode;


public class CloudDeviceCapabilities {
    private String deviceName;
    private String platformVersion;

    public CloudDeviceCapabilities(String deviceName, String platformVersion) {
        this.deviceName = deviceName;
        this.platformVersion = platformVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

}
