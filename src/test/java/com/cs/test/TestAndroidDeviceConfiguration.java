package com.cs.test;

import org.testng.annotations.Test;

import com.cs.automation.appium.device.AndroidDeviceConfiguration;

public class TestAndroidDeviceConfiguration {
	
	@Test
	public void test() {
		AndroidDeviceConfiguration androidConfig = new AndroidDeviceConfiguration();
		String deviceModel = androidConfig.getDeviceModel("ZY2242HQ5H");
		System.out.println(" DeviceModel :: "+deviceModel);
	}

}
