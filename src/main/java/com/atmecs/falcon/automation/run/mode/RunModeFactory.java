package com.atmecs.falcon.automation.run.mode;

import com.atmecs.falcon.automation.utils.enums.ERunModeType;
import com.atmecs.falcon.automation.utils.general.PropertyReader;

public class RunModeFactory {

	/**
	 * @author suraj
	 * @return returns the runmode 
	 * **/
	public static IRunMode getRunMode(ERunModeType type) {
		IRunMode runMode;
		switch (type) {
			case LOCAL_RUN:
				runMode = new LocalRun();
				break;
			case REMOTE_RUN:
				runMode = new RemoteRun();
				break;
			case CLOUD_RUN:
				runMode = new CloudRun();
				break;
			case REMOTE_APPIUM:
				runMode = new RemoteAppium();
				break;
			default:
				runMode = null;
				break;
		}
		return runMode;
	}
	
	
	public static ERunModeType getRunModeType() {
		String runMode = PropertyReader.readEnvOrConfigProperty("RUN_MODE");
		if (runMode.equals("LOCAL")) {
			return ERunModeType.LOCAL_RUN;
		}else if (runMode.equals("REMOTE")) {
			return ERunModeType.REMOTE_RUN;
		} else if (runMode.equals("CLOUD")) {
			return ERunModeType.CLOUD_RUN;
		} else if (runMode.equals("REMOTEAPPIUM")) {
			return ERunModeType.REMOTE_APPIUM;
		} else {
			return ERunModeType.LOCAL_RUN;
		}
	}
}
