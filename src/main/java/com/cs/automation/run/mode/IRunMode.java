package com.cs.automation.run.mode;

import java.util.List;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

/**
 * Use for running the framework as per configuration property set
 * @author mallikarjun
 * @since 5 Aug 2016
 */
public interface IRunMode {
    /**
     * @Purpose perform something after run
     **/
    void executeAfterRun();

    /**
     * @Purpose perform some operations before running all things
     **/
    void executeBeforeRun();

    /**
     * @return the deviceId (i.e. next available device)
     */
    String getAvailableDeviceToRunTest();

    /**
     * @Purpose to get the connected deviceList
     */
    List<String> getConnectedDevicesList();

    /**
     * @return returns the arraylist of connected devices
     **/
    List<String> getDevicesList();

    /**
     * @param device_type 
     * @return returns a driver for provided deviceId
     * @Info This will create the driver for the provided device and will return it
     */
    AppiumDriver<MobileElement> getDriverForDevice(String deviceId, String device_type);

    /**
     * Provides the no of devices on which tests to be run.
     * @return
     */
    int getTotalNoOfDevicesForRun();

    /**
     * will be called on suite ended (write a code in this method which you want to execute on suite
     * finish)
     */
    void onSuiteFinish();

    /**
     * will be called when suite get started (write a code in this method which you want to execute
     * before suite started)
     */
    void onSuiteStarted();

    /**
     * Set the device as free after test has been executed
     */
    void setDeviceAsFree(String deviceId);

    /**
     * @Purpose to set the deviceId
     */
    void setDeviceId(String deviceId);
    
    /*
     * @Purpose to start the appium servers for provided devices
     * **/
    void startAppiumServersForDevices(List<String> devices);
    
    /*
     * @Purpose to start the appium servers for provided devices
     * **/
    void stopAppiumServersForDevices(List<String> devices);
    
    
    
}