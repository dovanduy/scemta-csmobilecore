package com.cs.automation.util.reporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.Utils;

import com.cs.automation.util.error.ErrorUtil;
import com.cs.automation.utils.threadsTestNG.ThreadPool;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportListener implements ITestListener, IInvokedMethodListener {

	@Override
	public void onTestStart(ITestResult result) {
		String testName = getClassName(result.getInstanceName());
		ExtentTest test = ExtentTestManager.startTest(testName, "");
	}

	private String getClassName(String instanceName) {
		String[] arr = instanceName.split("\\.");
		String className = (arr.length > 0) ? arr[arr.length - 1] : instanceName;
		return className;
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// Extentreports log operation for passed tests.
		ExtentTestManager.getTest().log(LogStatus.PASS, "Test passed on : " + result.getMethod().getMethodName());
//		for (String step : Reporter.getOutput(result)) {
//			addStepToExtentReport(step);
//		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentTestManager.getTest().log(LogStatus.FAIL, "Test failed on : " + result.getMethod().getMethodName());
//		for (String step : Reporter.getOutput(result)) 
//			addStepToExtentReport(step);
		// commented below screenshot capture as we already taking screenshot on verify
		// fail
		// String imagePath = takesScreenShot(result.getMethod().getMethodName());
		// ExtentTestManager.getTest().log(LogStatus.FAIL,
		// ExtentTestManager.getTest().addScreenCapture(imagePath));
		String stackTrace = ExceptionUtils.getStackTrace(result.getThrowable());
		ExtentTestManager.getTest().log(LogStatus.ERROR, "<pre>stackTrace: " + stackTrace + "</pre>");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped on : " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onFinish(ITestContext context) {
		ExtentTestManager.endTest();
		ExtentManager.getReporter().flush();
	}

	private void addStepToExtentReport(String step) {
		if (step.contains("PASS"))
			ExtentTestManager.getTest().log(LogStatus.PASS, step);
		else if (step.contains("FAIL"))
			ExtentTestManager.getTest().log(LogStatus.FAIL, step);
		else
			ExtentTestManager.getTest().log(LogStatus.INFO, step);
	}

	public String takesScreenShot(String methodName) {
		String filePath = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "images"
				+ File.separator;
		File srcFile = ((TakesScreenshot) ThreadPool.getDriverInfo().getDriver()).getScreenshotAs(OutputType.FILE);
		String imagePath = filePath + methodName + ".png";
		try {
			FileUtils.copyFile(srcFile, new File(imagePath));
			System.out.println("Screenshot taken for method: " + methodName);
		} catch (IOException e) {
			System.err.println("Error in capturing screenshot");
			e.printStackTrace();
		}
		return "images" + File.separator + methodName + ".png";
	}

	public static void captureFailureScreenShot(String methodName, String stepNumber, String message) {
		String filePath = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + "images"
				+ File.separator;
		File srcFile = ((TakesScreenshot) ThreadPool.getDriverInfo().getDriver()).getScreenshotAs(OutputType.FILE);
		if (stepNumber == null)
			stepNumber = "step";
		String fileName = methodName + "-" + stepNumber + ".png";
		String imagePath = filePath + fileName;
		try {
			FileUtils.copyFile(srcFile, new File(imagePath));
			System.out.println("Screenshot taken for method: " + methodName + "-" + stepNumber);
			ExtentTestManager.getTest().log(LogStatus.FAIL,
					ExtentTestManager.getTest().addScreenCapture("images" + File.separator + fileName));
		} catch (IOException e) {
			System.err.println("Error in capturing screenshot");
			e.printStackTrace();
		} catch (Exception e) {
		}
		try {
			ExtentTestManager.getTest().log(LogStatus.FAIL, "Step " + stepNumber + ": FAIL : " + message);
		} catch (Exception e) {
		}
	}

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult result) {
		Reporter.setCurrentTestResult(result);

		if (method.isTestMethod()) {
			@SuppressWarnings("unchecked")
			List<Throwable> verificationFailures = ErrorUtil.getVerificationFailures();

			// if there are verification failures...
			if (verificationFailures.size() != 0) {
				// set the test to failed
				result.setStatus(ITestResult.FAILURE);

				// if there is an assertion failure add it to
				// verificationFailures
				if (result.getThrowable() != null) {
					verificationFailures.add(result.getThrowable());
				}

				int size = verificationFailures.size();
				// if there's only one failure just set that
				if (size == 1) {
					result.setThrowable(verificationFailures.get(0));
				} else {
					// create a failure message with all failures and stack
					// traces (except last failure)
					StringBuffer failureMessage = new StringBuffer("Multiple failures (").append(size).append("):nn");
					for (int i = 0; i < size - 1; i++) {
						failureMessage.append("Failure ").append(i + 1).append(" of ").append(size).append(":n");
						Throwable t = verificationFailures.get(i);
						String fullStackTrace = Utils.stackTrace(t, false)[1];
						failureMessage.append(fullStackTrace).append("nn");
					}

					// final failure
					Throwable last = verificationFailures.get(size - 1);
					failureMessage.append("Failure ").append(size).append(" of ").append(size).append(":n");
					failureMessage.append(last.toString());

					// set merged throwable
					Throwable merged = new Throwable(failureMessage.toString());
					merged.setStackTrace(last.getStackTrace());

					result.setThrowable(merged);

				}
			}

		}
	}
}
