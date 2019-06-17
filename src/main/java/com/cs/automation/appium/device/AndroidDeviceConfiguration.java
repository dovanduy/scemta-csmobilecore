package com.cs.automation.appium.device;

import java.io.IOException;
import java.util.ArrayList;

import com.cs.automation.utils.appium.CommandPrompt;

public class AndroidDeviceConfiguration {

    private CommandPrompt cmd = new CommandPrompt();
 //   private Map<String, String> devices = new HashMap<String, String>();
    
    ArrayList<String> deviceModel = new ArrayList<String>();

    /**
     * This method clears the app data only for android
     * @throws IOException
     * @throws InterruptedException
     */
    public void clearAppData(String deviceID, String app_package) throws InterruptedException,
            IOException {
        cmd.runCommand("adb -s " + deviceID + " shell pm clear " + app_package);
    }

    /**
     * This method will close the running app
     * @throws IOException
     * @throws InterruptedException
     */
    public void closeRunningApp(String deviceID, String app_package) throws InterruptedException,
            IOException {
        cmd.runCommand("adb -s " + deviceID + " shell am force-stop " + app_package);
    }

    /**
     * This method gets the device OS API Level
     */
    public String deviceOS(String deviceID) {
        String deviceOSLevel = null;
        try {
            deviceOSLevel =
                    cmd.runCommand("adb -s " + deviceID + " shell getprop ro.build.version.sdk")
                            .replaceAll("\\W", "");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return deviceOSLevel;

    }

    /**
     * This method gets the device model name
     */
    public String getDeviceModel(String deviceID) {
        String deviceModelName = null;
        String brand = null;
        String deviceModel = null;
        try {
            deviceModelName =
                    cmd.runCommand("adb -s " + deviceID + " shell getprop ro.product.model")
                            .replaceAll("\\W", "");

            brand = cmd.runCommand("adb -s " + deviceID + " shell getprop ro.product.brand");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        deviceModel = deviceModelName.concat("_" + brand);

        return deviceModel.trim();
    }

    public ArrayList<String> getDeviceSerial() throws Exception {

    	ArrayList<String> deviceSerail = new ArrayList<String>();
        startADB(); // start adb service
        final String output = cmd.runCommand("adb devices");
        final String[] lines = output.split("\n");

        if (lines.length <= 1) {
            System.out.println("No Device Connected");
            // stopADB();
            return null;
        } else {
            for (int i = 1; i < lines.length; i++) {
                lines[i] = lines[i].replaceAll("\\s+", "");

                if (lines[i].contains("device")) {
                    lines[i] = lines[i].replaceAll("device", "");
                    final String deviceID = lines[i];
                    deviceSerail.add(deviceID);
                } else if (lines[i].contains("unauthorized")) {
                    lines[i] = lines[i].replaceAll("unauthorized", "");
                } else if (lines[i].contains("offline")) {
                    lines[i] = lines[i].replaceAll("offline", "");
                }
            }
            return deviceSerail;
        }
    }

    /**
     * This method start adb server
     */
    private void startADB() throws Exception {
        final String output = cmd.runCommand("adb start-server");
        final String[] lines = output.split("\n");
        if (lines[0].contains("internal or external command")) {
            System.out.println("Please set ANDROID_HOME in your system variables");
        }
    }

    /**
     * This method stop adb server
     */
    protected void stopADB() throws Exception {
        cmd.runCommand("adb kill-server");
    }
}
