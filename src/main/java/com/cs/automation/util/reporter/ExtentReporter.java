/**
* @author kaushik.vijay
**/
package com.cs.automation.util.reporter;

import org.slf4j.Logger;
import org.testng.Reporter;

import com.cs.automation.util.logging.LogManager;
import com.relevantcodes.extentreports.LogStatus;
/**
 * ReportLogServiceImpl Class contains all type of log services. 
 * And all the methods provide information for log level and reporter level as well
 * which helps user to debug the tests easily even log level will off
 * @author kaushik.vijay
 *
 */
public class ExtentReporter implements ReportLogService {

	 Logger logger = null;
	
	 /**
	 * Added a constructor with custom class-name as parameter
	 * In this method, creating a logManager instance with logger
	 * @param classname
	 */
	public ExtentReporter(Class<?> className){
		logger =  LogManager.getInstance().getLogger(className);
	}
	
	/**
	 * Debug level helps developer/script developers to debug application.
	 * This method accepts one argument as string for custom message
	 * It provides log information of debug level along with reporter log information
	 * @param message
	 */
	public void debug(String message){
		logger.debug(message);
		Reporter.log(message);
		ExtentTestManager.getTest().log(LogStatus.INFO, message);
	}
	
	/**
	 * Info level gives the progress and chosen state information. 
	 * This method accepts one argument as string for custom message
	 * It provides log information of info level along with reporter log information
	 * @param message
	 */
	public void info(String message){
		logger.info(message);
		Reporter.log(message);
		ExtentTestManager.getTest().log(LogStatus.INFO, message);
	}

	/**
	 * Warning level gives a warning about an unexpected event to the user.
	 * This method accepts one argument as string for custom message
	 * It provides reporter log information of warn level along with log information
	 * @param message
	 */
	public void warning(String message){
		logger.warn(message);
		Reporter.log(message);
		ExtentTestManager.getTest().log(LogStatus.WARNING, message);
	}
	
	/**
	 * Error level gives information about a serious error which needs to be addressed and may result in unstable state.
	 * This method accepts one argument as string for custom message
	 * It provides reporter log information of error level along with log information
	 * @param message 
	 */
	public void error(String message){
		logger.error(message);
		Reporter.log(message);
		ExtentTestManager.getTest().log(LogStatus.ERROR, message);
	}
}
