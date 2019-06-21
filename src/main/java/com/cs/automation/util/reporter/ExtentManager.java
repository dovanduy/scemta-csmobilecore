package com.cs.automation.util.reporter;

import java.io.File;

import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {
	 
    private static ExtentReports extent;
 
    public synchronized static ExtentReports getReporter(){
        if(extent == null){
			// Set HTML reporting file location
			String workingDir = System.getProperty("user.dir");
			extent = new ExtentReports(workingDir + File.separator + "Reports" + File.separator + "extent-report.html",
					true);
			extent.loadConfig(new File(workingDir + File.separator + "src" + File.separator + "main" + File.separator
					+ "resources" + File.separator + "extent-config.xml"));
        }
        return extent;
    }

}
