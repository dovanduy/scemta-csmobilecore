package com.cs.automation.util.reporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.cs.automation.utils.threadsTestNG.ThreadPool;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportListener implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		String testName = getClassName(result.getInstanceName());
		ExtentTest test = ExtentTestManager.startTest(testName, "");
	}

	private String getClassName(String instanceName) {
		String[] arr = instanceName.split("\\.");
		String className = (arr.length > 0) ? arr[arr.length -1] : instanceName;
		return className;
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		//Extentreports log operation for passed tests.
		ExtentTestManager.getTest().log(LogStatus.PASS, "Test passed on : " + result.getMethod().getMethodName());
		for (String step : Reporter.getOutput(result)) {
			addStepToExtentReport(step);
		}
	}
	@Override
	public void onTestFailure(ITestResult result) {		
		ExtentTestManager.getTest().log(LogStatus.FAIL, "Test failed on : " + result.getMethod().getMethodName());
		for (String step : Reporter.getOutput(result)) 
			addStepToExtentReport(step);
		
		String imagePath = takesScreenShot(result.getMethod().getMethodName());
		String stackTrace = ExceptionUtils.getStackTrace(result.getThrowable());
		ExtentTestManager.getTest().log(LogStatus.FAIL, ExtentTestManager.getTest().addScreenCapture(imagePath));
		ExtentTestManager.getTest().log(LogStatus.ERROR,"<pre>stackTrace: "+stackTrace+"</pre>");
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
		if(step.contains("PASS"))
			ExtentTestManager.getTest().log(LogStatus.PASS, step);
		else if(step.contains("FAIL"))
			ExtentTestManager.getTest().log(LogStatus.FAIL, step);
		else
			ExtentTestManager.getTest().log(LogStatus.INFO, step);
	}
	
	public String takesScreenShot(String methodName) {
		String filePath = System.getProperty("user.dir")+File.separator +"Reports"+File.separator +"images"+File.separator;
		File srcFile = ((TakesScreenshot) ThreadPool.getDriverInfo().getDriver()).getScreenshotAs(OutputType.FILE);
		String imagePath = filePath + methodName + ".png";
		try {
			FileUtils.copyFile(srcFile, new File(imagePath));
			System.out.println("Screenshot taken for method: "+methodName);
		} catch (IOException e) {
			System.err.println("Error in capturing screenshot");
			e.printStackTrace();
		}
		return "images"+File.separator+methodName+".png";
	}
	
	public static void captureFailureScreenShot(String methodName, String stepNumber) {
		String filePath = System.getProperty("user.dir")+File.separator +"Reports"+File.separator +"images"+File.separator;
		File srcFile = ((TakesScreenshot) ThreadPool.getDriverInfo().getDriver()).getScreenshotAs(OutputType.FILE);
		if(stepNumber == null)
			stepNumber = "step";
		String fileName = methodName +  "-" + stepNumber + ".png";
		String imagePath = filePath + fileName;
		try {
			FileUtils.copyFile(srcFile, new File(imagePath));
			System.out.println("Screenshot taken for method: "+methodName+  "-" + stepNumber);
		} catch (IOException e) {
			System.err.println("Error in capturing screenshot");
			e.printStackTrace();
		}
		ExtentTestManager.getTest().log(LogStatus.FAIL, ExtentTestManager.getTest().addScreenCapture("images"+File.separator + fileName));
	}
}
