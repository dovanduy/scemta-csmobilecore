package com.cs.automation.verifyresult;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;

import com.cs.automation.jiracloud.tm.JiraCloudManager;
import com.cs.automation.util.logging.LogManager;
import com.cs.automation.util.reporter.ExtentReportListener;
import com.cs.automation.util.reporter.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author mallikarjun.patnam
 * 
 *         The Verify Class provides the Tester to do Verification Operations on
 *         Selenium Web Elements.
 * 
 *         Implements IVerify Interface
 */
public class Verify extends VerifyAbstract {
	WebElement element;
	Logger logger = LogManager.getInstance().getLogger(Verify.class);

	/**
	 * These are the keys for JIRA Cloud
	 */
	String testCycleKey = null;

	String testCaseKey = null;
	
	String device_udid = null;

	public void setTestCycleKey(String testCycleKey) {
		this.testCycleKey = testCycleKey;
	}

	public void setTestCaseKey(String testCaseKey) {
		this.testCaseKey = testCaseKey;
	}

	public String getTestCycleKey() {
		return testCycleKey;
	}

	public String getTestCaseKey() {
		return testCaseKey;
	}
	public void setDeviceId(String deviceId) {
		this.device_udid = deviceId;
	}

	public String getDeviceId() {
		return device_udid;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cs.automation.ui.seleniuminterfaces.IVerify#verifyPageTitle(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean verifyPageTitle(String actual, String expected, String errorMessage) {
		boolean status = false;
		try {
			if (actual != null && expected != null) {

				status = verifyString(actual, expected, errorMessage);
				return status;
			} else
				throw new IllegalArgumentException("Invalid Arguments. Try with valid Arguments");
		} catch (Exception exception) {
			Reporter.log("Verifying Exception Message: " + exception.getMessage());
			logger.debug("Verifying Exception Message: " + exception.getMessage());
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cs.automation.ui.seleniuminterfaces.IVerify#verifyPageUrl(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean verifyPageUrl(String actual, String expected, String errorMessage) {

		boolean status = false;
		try {
			if (actual != null && expected != null) {

				status = verifyString(actual, expected, errorMessage);
				return status;
			} else
				throw new IllegalArgumentException("Invalid Arguments. Try with valid Arguments");
		} catch (Exception exception) {
			Reporter.log("Verifying Exception Message: " + exception.getMessage());
			logger.debug("Verifying Exception Message: " + exception.getMessage());
		}
		return status;

	}

	public boolean verifyBoolean(boolean actual, boolean expected, String message, String step, boolean updateToJira) {
		boolean result = verifyBoolean(actual, expected, message);
		if(!result)
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
		else
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS : " + message);
		if (updateToJira && testCycleKey != null)
			JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, result, message);
		return result;
	}

	public boolean verifyBooleanAndStopTest(boolean actual, boolean expected, String message, String step, boolean updateToJira) {
		try {
			Assert.assertEquals(actual, expected);
			logger.info("PASS : {} Actual value : {} is same as Expected value: {}", message, actual, expected);
			Reporter.log("PASS : " + message + ": " + "ACTUAL : " + actual + " 	" + "EXPECTED :" + expected);
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS : " + message);
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, true, message);
			return true;
		} catch (AssertionError assertionError) {
			Reporter.log("FAIL : " + message + ": " + assertionError.getMessage() + ": " + "ACTUAL : " + actual
					+ " 	" + "EXPECTED :" + expected);
			logger.error("FAIL : {} Actual value : {} is not same as Expected value: {}", assertionError.getMessage(),
					actual, expected);
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, false, message);
			Assert.fail(message);
			return false;
		}
	}

	public boolean verifyString(String actual, String expected, String message, String step, boolean updateToJira) {
		boolean result = verifyString(actual, expected, message);
		if(!result)
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
		else
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS : " + message);
		if (updateToJira && testCycleKey != null)
			JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, result, message);
		return result;
	}

	public boolean verifyStringAndStopTest(String actualValue, String expectedValue, String message,
			String step, boolean updateToJira) {
		boolean isverified = false;

		try {
			Assert.assertEquals(actualValue, expectedValue, message);
			Reporter.log(
					"PASS : " + message + ": " + "ACTUAL : " + actualValue + "    " + "EXPECTED :" + expectedValue);
			logger.info("PASS : " + message + ": " + "ACTUAL : " + actualValue + "    " + "EXPECTED :" + expectedValue);
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS : " + message);
			isverified = true;
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, true, message);
		} catch (AssertionError assertionError) {
			Reporter.log("FAIL     : " + message + ": " + assertionError.getMessage());
			logger.error("FAIL      : " + assertionError.getMessage());
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, false, message);
			Assert.fail(message);
		}
		return isverified;
	}

	/**
	 * This method will is used for verification of condition and take screenshot then update
	 * to JIRA step results.
	 * @param condition
	 * @param message message of verification/ JIRA step expected message
	 * @param step - JIRA Step number, used to take screenshot and log
	 * @param updateToJira whether to add into JIRA step results or not 
	 * (If this verification is JIRA step related then it should be true, otherwise false.
	 * @return
	 */
	public boolean verifyTrue(boolean condition, String message, String step, boolean updateToJira) {
		boolean result = verifyTrue(condition, message);
		if(!result)
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
		else
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS: " + message);
		if (updateToJira && testCycleKey != null)
			JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, result, message);
		return result;
	}

	public boolean verifyTrueAndStopTest(boolean condition, String message, String step, boolean updateToJira) {
		boolean result = false;

		try {
			Assert.assertTrue(condition);
			logger.info("PASS : {}", message);
			Reporter.log("PASS : " + message);
			ExtentTestManager.getTest().log(LogStatus.PASS, "Step "+step+": PASS : " + message);
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, true, message);
			result = true;
		} catch (AssertionError assertionError) {
			Reporter.log("FAIL : " + message + ": " + assertionError.getMessage());
			logger.error("FAIL : {}", assertionError.getMessage());
			ExtentReportListener.captureFailureScreenShot(testCaseKey, step, message, device_udid);
			if (updateToJira && testCycleKey != null)
				JiraCloudManager.buildTestStepResults(testCycleKey, testCaseKey, false, message);
			Assert.fail(message);
		}
		return result;
	}
}
