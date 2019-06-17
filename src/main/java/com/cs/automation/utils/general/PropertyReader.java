package com.cs.automation.utils.general;

import java.util.ResourceBundle;

public class PropertyReader {

    private static ResourceBundle prop;

    public static String getProperty(String key) {
        prop = ResourceBundle.getBundle("config");
        return prop.getString(key);
    }

    /**
     * @author mallikarjun
     * @param key 
     * @return returns the value for provided key, first it will check into environment variable , if present will return from environment variable
     * 			else will read from config file and return it
     * @exception key not found, if key is not availabel in config.properties 
     * **/
    public static String readEnvOrConfigProperty(String key) {
        // first pref for env, next for config file
        String value = System.getProperty(key);
        if (value == null || value.trim().length() == 0) {
            value = PropertyReader.getProperty(key);
        }
        return value;

    }

    private PropertyReader() {

    }
}
