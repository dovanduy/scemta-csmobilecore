package com.cs.automation.run.cloudrun.mode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.io.IOException;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.cs.automation.run.mode.CloudRun;
import com.cs.automation.utils.appium.CommandPrompt;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class AmazonCloudRun extends CloudRun implements ICloudRunMode {
    private static String runType;
    private static CommandPrompt command;

    static {
        runType = getAmazonRunType();
        if (runType.equalsIgnoreCase("cli")) {
            command = new CommandPrompt();
            try {
                String output = command.runCommand("aws");
                if (output.contains("aws help") && output.contains("aws <command> help")
                        && output.contains("aws <command> <subcommand> help")) {

                } else {
                    System.out
                            .println("Install aws cli from URL:http://docs.aws.amazon.com/cli/latest/userguide/installing.html");
                    System.exit(0);
                }
            } catch (InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (runType.equalsIgnoreCase("amazon")) {
        }
    }

    private static String getAmazonRunType() {
        if (PropertyReader.getProperty(Constants.AMAZON_RUN_TYPE).equalsIgnoreCase("CLI")) {
            return "cli";
        } else if (PropertyReader.getProperty(Constants.AMAZON_RUN_TYPE).equalsIgnoreCase("API")) {
            return "api";
        }
        return null;
    }

    @Override
    public void configureCloud() {

    }

    @Override
    public String getAvailableDeviceToRunTest() {
        return null;
    }

    @Override
    public AppiumDriver<MobileElement> getDriverForDevice(String deviceId) {
        return null;
    }

    @Override
    public int getTotalNoOfDevicesForRun() {
        return super.getTotalNoOfDevicesForRun();
    }

    @Override
    public void onSuiteFinish() {
    }

    @Override
    public void onSuiteStarted() {
        configureCloud();
    }

    @Override
    public DesiredCapabilities setCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDeviceAsFree(String deviceId) {
        // TODO Auto-generated method stub
        super.setDeviceAsFree(deviceId);
    }

    @Override
    public void uploadApplication() {
        // TODO Auto-generated method stub

    }

}
