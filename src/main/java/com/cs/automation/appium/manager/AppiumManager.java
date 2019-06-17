package com.cs.automation.appium.manager;

import java.io.File;
import java.net.URL;

import com.cs.automation.utils.appium.AvailablePorts;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

/**
 * Appium Manager - this class contains method to start and stops appium server To execute the tests
 * from eclipse, you need to set PATH as /usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin in run
 * configuration
 */
public class AppiumManager {

    private AvailablePorts ap = new AvailablePorts();
    private AppiumDriverLocalService appiumDriverLocalService;


    /**
     * @author mallikarjun
     * start appium with auto generated ports : appium port, chrome port, bootstrap port and device
     * UDID
     */
    public AppiumServiceBuilder appiumServerForAndroid(String deviceID) throws Exception {
        System.out.println("Starting Appium Server Android");
        System.out.println(deviceID);
        final int port = ap.getPort();
        final int bootstrapPort = ap.getPort();
        final int selendroidPort = ap.getPort();
        final String ipAddress = "127.0.0.1";
        
        final String appiumLogLevel = PropertyReader.readEnvOrConfigProperty(Constants.APPIUM_LOG_LEVEL);
        
        final AppiumServiceBuilder builder =
        new AppiumServiceBuilder().withAppiumJS(
                new File(getAppiumJSPath()))
                .withArgument(GeneralServerFlag.LOG_LEVEL, appiumLogLevel)
                                                .withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER,Integer.toString(bootstrapPort))
                                                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                                                .withArgument(AndroidServerFlag.SUPPRESS_ADB_KILL_SERVER)
                                                .withArgument(AndroidServerFlag.SELENDROID_PORT,Integer.toString(selendroidPort))
                                                .usingPort(port)
                                                .withIPAddress(ipAddress);
        /* and so on */
        appiumDriverLocalService = builder.build();
        appiumDriverLocalService.start();
        return builder;

    }

    /***
     * @author mallikarjun
     * @param device_ID, webkit port
     * @return AppiumServiceBuilder instance for ios device
     * */
    public AppiumServiceBuilder appiumServerForIOS(String deviceID, String webKitPort)
            throws Exception {
        System.out.println("Starting Appium Server IOS");
        final File classPathRoot = new File(System.getProperty("user.dir"));
        final int port = ap.getPort();
        
        final String appiumLogLevel = PropertyReader.readEnvOrConfigProperty(Constants.APPIUM_LOG_LEVEL);
        
        final AppiumServiceBuilder builder =
        new AppiumServiceBuilder().withAppiumJS(
                new File(getAppiumJSPath()))
                .withArgument(GeneralServerFlag.LOG_LEVEL, appiumLogLevel)
                							.withArgument(GeneralServerFlag.LOG_LEVEL, "debug").withArgument(
                                                                GeneralServerFlag.TEMP_DIRECTORY,
                                                                new File(String.valueOf(classPathRoot)).getAbsolutePath()
                                                                + "/temp/" + "tmp_" + port).withArgument(
                                                                        GeneralServerFlag.SESSION_OVERRIDE).usingPort(port);

        appiumDriverLocalService = builder.build();
        appiumDriverLocalService.start();
        return builder;
    }

    
    /**
     * @author mallikarjun
     * @param deviceId (here deviceID is ios simulator)
     * @return AppiumServiceBuilder instance for ios simulator
     * **/
    public AppiumServiceBuilder appiumServerForIOSSimulator(String deviceID) throws Exception {
        System.out.println("Starting Appium Server IOS " + deviceID);
        File classPathRoot = new File(System.getProperty("user.dir"));
        final int port = ap.getPort();
        
        final String appiumLogLevel = PropertyReader.readEnvOrConfigProperty(Constants.APPIUM_LOG_LEVEL);
        
        AppiumServiceBuilder builder =
                new AppiumServiceBuilder().withAppiumJS(
                        new File(getAppiumJSPath()))
                        .withArgument(GeneralServerFlag.LOG_LEVEL, appiumLogLevel).withArgument(
                                                GeneralServerFlag.LOG_LEVEL, "debug").withArgument(
                                                        GeneralServerFlag.TEMP_DIRECTORY,
                                                        new File(String.valueOf(classPathRoot)).getAbsolutePath()
                                                        + "/temp/" + "tmp_" + port).withArgument(
                                                                GeneralServerFlag.SESSION_OVERRIDE).usingPort(port);

        /* and so on */
        ;
        appiumDriverLocalService = builder.build();
        appiumDriverLocalService.start();
        return builder;

    }

    /***
     * @author mallikarjun
     * @see stops the appium server node (should be called after run to kill the AppiumService)
     * */
    public void destroyAppiumNode() {
        appiumDriverLocalService.stop();
        System.out.println("Appium is shutting down!!!!!!!!!");
        if (appiumDriverLocalService.isRunning()) {
            System.out.println("AppiumServer didn't shut... Trying to quit again....");
            appiumDriverLocalService.stop();
        }
    }

    
    /**
     * @author mallikarjun
     * @return appium server url for current instance
     * 
     * */
    public URL getAppiumUrl() {
        return appiumDriverLocalService.getUrl();
    }
    
    private String getAppiumJSPath() throws Exception {
    		String appiumJSPath = System.getenv(Constants.APPIUM_JS_PATH);
    		String appiumJsPathFromConfigFile = PropertyReader.readEnvOrConfigProperty(Constants.APPIUM_JS_PATH);
    		if (appiumJSPath != null) {
			return appiumJSPath;	
		}else if (appiumJsPathFromConfigFile != null) {
			return appiumJsPathFromConfigFile;
		}else
			throw new Exception("Please set APPIUM_JS_PATH env variable");
    }
    
}
