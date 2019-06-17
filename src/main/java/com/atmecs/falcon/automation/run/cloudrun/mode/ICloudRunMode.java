package com.atmecs.falcon.automation.run.cloudrun.mode;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface ICloudRunMode {

    /**
     *
     */
    void configureCloud();

    /**
     * @return DesiredCapabilities
     */
    DesiredCapabilities setCapabilities();

    /**
     * To upload application on Cloud Storage
     */
    void uploadApplication();
    

}
