package com.cs.automation.appium.manager;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.threadsTestNG.DriverInfo;
import com.cs.automation.utils.threadsTestNG.ThreadPool;

public class UserBaseTest extends AppiumParallelTest {

	public AppiumDriver<MobileElement> driver;
	public ReportLogServiceImpl report = new ReportLogServiceImpl(UserBaseTest.class);

	@BeforeClass(alwaysRun = true)
	@Parameters({"device_udid","device_type"})
	public void beforeClass(String deviceId, String deviceType) throws Exception {
		highLightInLogs("Before Class : Initiating device");
		String className = getClass().getSimpleName();
		device_udid = deviceId; 
		device_type = deviceType;
		initiateDevice(className);
	}


	@BeforeMethod(alwaysRun = true)
	public void startApp(Method name) throws Exception {
		highLightInLogs("Before Method");
		this.driver = getDriverForRun();
		DriverInfo driverInfo = new DriverInfo(driver, device_udid);
		ThreadPool.setDriverInfo(driverInfo);
		initializeReportInfo();
	}



	@AfterMethod(alwaysRun = true)
	public void killServer(ITestResult result) throws InterruptedException, IOException {
		highLightInLogs("After Method: "+result.getMethod().getMethodName()+" with status: "+getStatus(result));
		highLightInLogs("Start Time: "+new Date(result.getStartMillis()).toString());
		highLightInLogs("End Time: "+new Date(result.getEndMillis()).toString());
		getDriver().quit();
		ThreadPool.clear();

	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws InterruptedException, IOException {
		endAppiumSession();
		highLightInLogs("set to free the device "+device_udid);
		freeDevice(device_udid);
	}


	@Override
	public AppiumDriver<MobileElement> getDriver() {
		return driver;
	}


	private void highLightInLogs(String msg) {
		System.out.println("=========================================="+msg);
	}


	private String getStatus(ITestResult result) {
		String status = "";
		switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			status = "PASS";
			break;

		case ITestResult.FAILURE:
			status = "FAIL";
			break;

		case ITestResult.SKIP:
			status = "SKIP BLOCKED";
			break;
		default:
			status = "Invalid status";
		}
		return status;
	}
}
