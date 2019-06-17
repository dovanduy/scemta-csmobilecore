package com.cs.automation.appium.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.cs.automation.run.mode.IRunMode;
import com.cs.automation.util.reporter.JIRARestIssueCreation;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.enums.ERunModeType;
import com.cs.automation.utils.general.PropertyReader;
import com.cs.automation.utils.threadsTestNG.ImageUploadClient;
import com.cs.automation.utils.threadsTestNG.ThreadPool;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class AppiumParallelTest extends TestListenerAdapter implements ITestListener,
ISuiteListener {

	public static IRunMode runMode;
	private StringBuffer reportInfo = null;


	/**
	 * @return RunModeTye (Enum -> LOCAL,REMOTE,CLOUD)
	 * @see 
	 **/
	public static ERunModeType getRunModeType() {
		String runMode = PropertyReader.readEnvOrConfigProperty("RUN_MODE");
		if (runMode.equalsIgnoreCase("LOCAL")) {
			return ERunModeType.LOCAL_RUN;
		} else if (runMode.equalsIgnoreCase("REMOTE")) {
			return ERunModeType.REMOTE_RUN;
		} else if (runMode.equalsIgnoreCase("CLOUD")) {
			return ERunModeType.CLOUD_RUN;
		} else if(runMode.equalsIgnoreCase("REMOTEAPPIUM")){
			return ERunModeType.REMOTE_APPIUM;
		}else {
			return ERunModeType.LOCAL_RUN;
		}
	}

	private AppiumDriver<MobileElement> driver;
	public String device_udid;
	private ReportLogServiceImpl report = new ReportLogServiceImpl(AppiumParallelTest.class);
	private String summary;
	private String description;


	/**
	 * @param test_result , AppiumDriver, desc - steps upto the failure
	 * @see	will create a jira defect
	 * 		At 10th_May_2017, desc argument added to provide the steps in the jira issue
	 * 
	 * ***/
	private void createJIRABug(ITestResult result, AppiumDriver<MobileElement> driver,String desc) {
		JIRARestIssueCreation jiraIssueCreation = new JIRARestIssueCreation();
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		if (PropertyReader.readEnvOrConfigProperty("automatic.bug.creation").equalsIgnoreCase("true")) {
			summary =
					"Bug in Test " + result.getName().split(" ")[0] + " on "
							+ ThreadPool.getDriverInfo().getDeviceType() + " device :"
							+ ThreadPool.getDriverInfo().getDeviceId();

			description = "Steps:\n"+desc +"\nError is:\n"+ result.getThrowable().getMessage();
			
			System.out.println("*************--summary--************"+summary);
			System.out.println("*************--desc--************"+description);

			String response = jiraIssueCreation.createBugAndAttach(summary, description, file);

			System.out.println("Response " + response);
			report.info("Response " + response);
			report.info("You can see jira defect here: "+getJiraIssueUrl());
		} else {
			System.out
			.println("For automatic bug creation in jira set automatic.bug.creation property as true in config.properties file!!!");
		}
	}


	/** 
	 * @author mallikarjun
	 * @return JiraIssueUrl as String
	 * @see will read the jira.baseurl and jira.projectkey from jira.properties file
	 * **/
	private String getJiraIssueUrl() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("jira");
		String baseUrl = resourceBundle.getString("jira.baseurl");
		String projectKey = resourceBundle.getString("jira.projectkey");
		String url = baseUrl +"/projects/" + projectKey + "/issues";
		return url;
	}

	/**
	 * This method is for ending the appium session
	 **/
	protected synchronized void endAppiumSession() throws InterruptedException, IOException {
		
	}

	/**
	 * @see will set the device as free to excecute the next script
	 * */
	protected void freeDevice(String device_udid) {
		runMode.setDeviceAsFree(device_udid);
	}


	/**
	 * @return AppiumDriver instance
	 * */
	protected AppiumDriver<MobileElement> getDriver() {
		return driver;
	}


	/**
	 * @author mallikarjun 
	 * @return returns AppiumDriver for selected runMode (e.g. local,remote or cloud)
	 * 
	 * */
	protected AppiumDriver<MobileElement> getDriverForRun() {
		driver = runMode.getDriverForDevice(device_udid);
		return driver;
	}
 
	/**
	 * @return throwable error as String
	 * **/
	protected String getStackTrace(Throwable t) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * @author CS
	 * @param methodname
	 * @Description: get the next available device from device map devicemap is a map in which the
	 *               device value is true if its free else it is false for busy device (i.e. test is
	 *               running on that device)
	 **/
	protected void initiateDevice(String methodName) throws Exception {
//		device_udid = runMode.getAvailableDeviceToRunTest();
		System.out.println("Printing the Next Available device : " + device_udid);
	}

	@Override
	public void onFinish(ISuite suite) {
		runMode.onSuiteFinish();
	}

	/**
	 * The following methods are to start and close Appium Server OnStart (i.e. BeforeSuite) --
	 * Appium Server get started for all available devices OnFinish (i.e. AfterSuite) -- Appium
	 * Server get closed for all devices (for which appium server was started in onStart)
	 **/
	@Override
	public void onStart(ISuite suite) {
		runMode.onSuiteStarted();
	}

	/**
	 * overided methods called on testFailure
	 **/
	@Override
	public void onTestFailure(ITestResult result) {
		AppiumDriver<MobileElement> dvr;
		try {
			dvr = ((AppiumParallelTest) result.getInstance()).getDriver();
			String descForJiraBug = ((AppiumParallelTest) result.getInstance()).getReportInfo();
			System.out.println("******-report info found--***"+descForJiraBug);
			createJIRABug(result, dvr,descForJiraBug);
			if (PropertyReader.readEnvOrConfigProperty("upload.result").equals("true"))
				reportServerImageUpload(dvr);

		} catch (IOException e) {
			e.printStackTrace();
			report.info("Failed to upload failure image " + e.getMessage());
		}

	}

	@Override
	public void onTestStart(ITestResult result) {
		final Object currentClass = result.getInstance();
		driver = ((AppiumParallelTest) currentClass).getDriver();
	}

	/**
	 * @author mallikarjun
	 * @Description: Take a screenshsot for failure and upload to the report server
	 * @exception IOException
	 **/
	private void reportServerImageUpload(AppiumDriver<MobileElement> driver) throws IOException {
		final String uploadUrl = PropertyReader.readEnvOrConfigProperty("testimage.uploadurl");		
		ImageUploadClient testImageUploadClient = new ImageUploadClient(uploadUrl);
		report.info("Uploading Screenshot for failure");
		try {
			File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileInputStream fis = new FileInputStream(file);

			// content type can be image/png or image/jpeg
			testImageUploadClient.upload("image/png", fis);
			System.out.println("Printing summary -> " + summary + "desc -> " + description);
		} catch (Exception e) {
			e.printStackTrace();
			report.error("Unknown error : " + e.getMessage());
		}
	}


	/**
	 * @author mallikarjun
	 * @info will capture the screenshot and will upload it to server (Not on failure, you can call it at anypoint where you want screenshot)
	 * 
	 * **/
	public void captureScreenShotAndUploadToServer(AppiumDriver<MobileElement> driver) { 
		try {
			reportServerImageUpload(driver);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * @author mallikarjun
	 * @info
	 * **/
	public void writeToReport(String info){
		report.info(info);
		reportInfo.append(info+"\n");
	}


	/**
	 * @author mallikarjun
	 * @info report 
	 * **/
	protected void initializeReportInfo() {
		reportInfo = null;
		reportInfo = new StringBuffer();
	}

	/**
	 * @author mallikarjun
	 * 
	 * **/
	private String getReportInfo() {
		return reportInfo.toString();
	}


}
