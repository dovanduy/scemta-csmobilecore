package com.cs.automation.appium.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.cs.automation.run.mode.RunModeFactory;
import com.cs.automation.utils.enums.ERunModeType;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class DeviceManager {

	/**
	 * @author mallikarjun
	 * @return true - If user has provided the devices in config.properties false -
	 *         If user has not provided the devices in config.properties
	 */
	public static boolean isUserProvidedDevices() {
		ERunModeType runModeType = RunModeFactory.getRunModeType();
		String platform = getSelectedPlatform();
		if (runModeType == ERunModeType.RUN_IN_ALL && platform.equalsIgnoreCase(Constants.BOTH)) {
			return (isAndroidDevicesProvided() || isIosDevicesProvided()
					|| isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.ANDROID_DEVICES)
					|| isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.IOS_DEVICES));
		} else if (runModeType == ERunModeType.RUN_IN_ALL && platform.equalsIgnoreCase(Constants.ANDROID)) {
			return (isAndroidDevicesProvided()
					|| isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.ANDROID_DEVICES));
		} else if (runModeType == ERunModeType.RUN_IN_ALL && platform.equalsIgnoreCase(Constants.IOS)) {
			return (isIosDevicesProvided()
					|| isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.IOS_DEVICES));
		} else if (runModeType == ERunModeType.CLOUD_RUN && platform.equalsIgnoreCase(Constants.BOTH)) {
			return (isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.ANDROID_DEVICES)
					|| isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.IOS_DEVICES));
		} else if (runModeType == ERunModeType.CLOUD_RUN && platform.equalsIgnoreCase(Constants.ANDROID)) {
			return isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.ANDROID_DEVICES);
		} else if (runModeType == ERunModeType.CLOUD_RUN && platform.equalsIgnoreCase(Constants.IOS)) {
			return isDevicesProvided(ResourceBundle.getBundle("cloud"), Constants.IOS_DEVICES);
		}
		if (runModeType == ERunModeType.APPIUM_GRID && platform.equalsIgnoreCase(Constants.BOTH)) {
			return (isAndroidDevicesProvided() || isIosDevicesProvided());
		}
		if (platform.equalsIgnoreCase(Constants.ANDROID)) {
			return isAndroidDevicesProvided();
		} else if (platform.equalsIgnoreCase(Constants.IOS)) {
			return isIosDevicesProvided();
		} else
			return false;
	}

	private static boolean isAndroidDevicesProvided() {
		String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
		if (androidDevices.trim().isEmpty())
			return false;
		else
			return true;
	}

	private static boolean isIosDevicesProvided() {
		String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
		if (iosDevices.trim().isEmpty())
			return false;
		else
			return true;
	}

	private static boolean isDevicesProvided(ResourceBundle bundle, String devicesKey) {
		String iosDevices = bundle.getString(devicesKey);
		if (iosDevices.trim().isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @author mallikarjun
	 * @return selectedAppType - returns the APP_TYPE provided in config.properties
	 *         (ANDROID/IOS)
	 */
	public static String getSelectedPlatform() {
		String platform = PropertyReader.readEnvOrConfigProperty(Constants.APP_TYPE);
		return platform;
	}

	/**
	 * @author mallikarjun
	 * @return listOfDevices - returns the list contains the id of selected devices
	 *         List of devices provided in config.properties If no devices provided
	 *         are provided in config.properties then this will read the connected
	 *         devices
	 */
	public static List<String> getSelectedDevices() {
		ERunModeType eRunMode = RunModeFactory.getRunModeType();
		return getSelectedDevices(eRunMode);

	}

	public static List<String> getSelectedDevices(ERunModeType eRunMode) {
		switch (eRunMode) {
		case LOCAL_RUN:
			return getDevicesListForLocalRunMode();
		case REMOTE_APPIUM:
			return getDevicesListForLocalRunMode();
		case APPIUM_GRID:
			return getDevicesListForGridRunMode();
		case CLOUD_RUN:
			return getDevicesListForCloudRunMode();
		case RUN_IN_ALL:
			return getDevicesListForTotalRunMode();
		default:
			return null;
		}

	}

	private static List<String> getDevicesListForTotalRunMode() {
		List<String> fullList = getDevicesListForGridRunMode();
		fullList.addAll(getDevicesListForCloudRunMode());
		System.out.println("Full List" + fullList.toString());
		return fullList;
	}

	public static Map<String, List<String>> getSelectedDevicesForGrid() {
		ERunModeType eRunMode = RunModeFactory.getRunModeType();
		switch (eRunMode) {
		case LOCAL_RUN:
			return getDevicesMapForLocalRunMode();
		case REMOTE_APPIUM:
			return getDevicesMapForLocalRunMode();
		case APPIUM_GRID:
			return getDevicesMapForGridRunMode();
		case CLOUD_RUN:
			return getDevicesMapForCloudRunMode();
		case RUN_IN_ALL:
			return getDevicesMapForTotalRunMode();
		default:
			return null;
		}

	}

	/**
	 * @author mallikarjun
	 * @return listOFDevices - list of connected devices to local machine
	 * @see This method will read the user provided devices first (i.e. comma
	 *      separated values provided in config.properties) For Android - the values
	 *      provided for key 'AndroidDevices' For IOS - the values provided for key
	 *      'IOSDevices' If no values provided for the above keys, then this method
	 *      will read the connected devices.
	 */
	private static List<String> getDevicesListForLocalRunMode() {
		List<String> devicesList = new ArrayList<>();
		if (isUserProvidedDevices()) {
			if (isPlatformIsAndroid()) {
				String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				devicesList.addAll(Arrays.asList(androidDevicesArr));
			} else if (isPlatformIsIOS()) {
				String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				devicesList.addAll(Arrays.asList(iosDevicesArr));
			}
		} else {
			if (isPlatformIsAndroid()) {
				AndroidDeviceConfiguration androidDeviceConfiguration = new AndroidDeviceConfiguration();
				try {
					devicesList.addAll(androidDeviceConfiguration.getDeviceSerial());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return devicesList;
			} else if (isPlatformIsIOS()) {
				IOSDeviceConfiguration iosDeviceConfiguration = new IOSDeviceConfiguration();
				if (iosDeviceConfiguration.getIOSUDID() != null)
					devicesList.addAll(iosDeviceConfiguration.getIOSUDID());
				if (devicesList.isEmpty())
					devicesList.add(Constants.IOS_SIMULATOR);
				return devicesList;
			}
		}
		return devicesList;

	}

	private static Map<String, List<String>> getDevicesMapForLocalRunMode() {
		Map<String, List<String>> devicesMap = new HashMap<String, List<String>>();
		List<String> devicesList = new ArrayList<>();
		if (isUserProvidedDevices()) {
			if (isPlatformIsAndroid()) {
				String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				devicesList.addAll(Arrays.asList(androidDevicesArr));
				devicesMap.put(Constants.ANDROID_DEVICES, devicesList);
			} else if (isPlatformIsIOS()) {
				String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				devicesList.addAll(Arrays.asList(iosDevicesArr));
				devicesMap.put(Constants.IOS_DEVICES, devicesList);
			}
		} else {
			if (isPlatformIsAndroid()) {
				AndroidDeviceConfiguration androidDeviceConfiguration = new AndroidDeviceConfiguration();
				try {
					devicesList.addAll(androidDeviceConfiguration.getDeviceSerial());
					devicesMap.put(Constants.ANDROID_DEVICES, devicesList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (isPlatformIsIOS()) {
				IOSDeviceConfiguration iosDeviceConfiguration = new IOSDeviceConfiguration();
				if (iosDeviceConfiguration.getIOSUDID() != null)
					devicesList.addAll(iosDeviceConfiguration.getIOSUDID());
				if (devicesList.isEmpty())
					devicesList.add(Constants.IOS_SIMULATOR);
				devicesMap.put(Constants.IOS_DEVICES, devicesList);
			}
		}
		return devicesMap;

	}

	private static List<String> getDevicesListForGridRunMode() {
		List<String> devicesList = new ArrayList<>();
		if (isUserProvidedDevices()) {

			if (isPlatformIsAndroid()) {
				String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				devicesList.addAll(Arrays.asList(androidDevicesArr));
			} else if (isPlatformIsIOS()) {
				String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				devicesList.addAll(Arrays.asList(iosDevicesArr));
			} else if (isPlatformIsBOTH()) {
				if (isAndroidDevicesProvided()) {
					String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
					String[] androidDevicesArr = androidDevices.split(",");
					devicesList.addAll(Arrays.asList(androidDevicesArr));
				}
				if (isIosDevicesProvided()) {
					String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
					String[] iosDevicesArr = iosDevices.split(",");
					devicesList.addAll(Arrays.asList(iosDevicesArr));
				}
			}
		} else {
			return null;
		}
		return devicesList;
	}

	private static Map<String, List<String>> getDevicesMapForGridRunMode() {
		Map<String, List<String>> devicesMap = new HashMap<String, List<String>>();

		if (isUserProvidedDevices()) {
			List<String> androidDevicesList = new ArrayList<>();
			List<String> iosDevicesList = new ArrayList<>();
			if (isPlatformIsAndroid()) {
				String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				androidDevicesList.addAll(Arrays.asList(androidDevicesArr));
				devicesMap.put(Constants.ANDROID_DEVICES, androidDevicesList);
			} else if (isPlatformIsIOS()) {
				String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				iosDevicesList.addAll(Arrays.asList(iosDevicesArr));
				devicesMap.put(Constants.IOS_DEVICES, iosDevicesList);
			} else if (isPlatformIsBOTH()) {
				if (isAndroidDevicesProvided()) {
					String androidDevices = PropertyReader.readEnvOrConfigProperty(Constants.ANDROID_DEVICES);
					String[] androidDevicesArr = androidDevices.split(",");
					androidDevicesList.addAll(Arrays.asList(androidDevicesArr));
					devicesMap.put(Constants.ANDROID_DEVICES, androidDevicesList);
				}
				if (isIosDevicesProvided()) {
					String iosDevices = PropertyReader.readEnvOrConfigProperty(Constants.IOS_DEVICES);
					String[] iosDevicesArr = iosDevices.split(",");
					iosDevicesList.addAll(Arrays.asList(iosDevicesArr));
					devicesMap.put(Constants.IOS_DEVICES, iosDevicesList);
				}
			}
		} else {
			return null;
		}
		return devicesMap;
	}

	/**
	 * @author mallikarjun
	 * @return listOfDevices - provided in cloud.properties
	 */
	private static List<String> getDevicesListForCloudRunMode() {
		if (isUserProvidedDevices()) {
			ResourceBundle cloudResource = ResourceBundle.getBundle("cloud");
			List<String> devicesList = new ArrayList<>();
			if (isPlatformIsAndroid()) {
				String androidDevices = cloudResource.getString(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				devicesList.addAll(Arrays.asList(androidDevicesArr));
			} else if (isPlatformIsIOS()) {
				String iosDevices = cloudResource.getString(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				devicesList.addAll(Arrays.asList(iosDevicesArr));
			} else if (isPlatformIsBOTH()) {
				if (isDevicesProvided(cloudResource, Constants.ANDROID_DEVICES)) {
					String androidDevices = cloudResource.getString(Constants.ANDROID_DEVICES);
					String[] androidDevicesArr = androidDevices.split(",");
					devicesList.addAll(Arrays.asList(androidDevicesArr));
				}
				if (isDevicesProvided(cloudResource, Constants.IOS_DEVICES)) {
					String iosDevices = cloudResource.getString(Constants.IOS_DEVICES);
					String[] iosDevicesArr = iosDevices.split(",");
					devicesList.addAll(Arrays.asList(iosDevicesArr));
				}
			}
			return devicesList;
		} else {
			return null;
		}
	}

	private static Map<String, List<String>> getDevicesMapForCloudRunMode() {
		if (isUserProvidedDevices()) {
			ResourceBundle cloudResource = ResourceBundle.getBundle("cloud");
			Map<String, List<String>> devicesMap = new HashMap<String, List<String>>();
			List<String> androidDevicesList = new ArrayList<>();
			List<String> iosDevicesList = new ArrayList<>();
			if (isPlatformIsAndroid()) {
				String androidDevices = cloudResource.getString(Constants.ANDROID_DEVICES);
				String[] androidDevicesArr = androidDevices.split(",");
				androidDevicesList.addAll(Arrays.asList(androidDevicesArr));
				devicesMap.put(Constants.ANDROID_DEVICES, androidDevicesList);
			} else if (isPlatformIsIOS()) {
				String iosDevices = cloudResource.getString(Constants.IOS_DEVICES);
				String[] iosDevicesArr = iosDevices.split(",");
				iosDevicesList.addAll(Arrays.asList(iosDevicesArr));
				devicesMap.put(Constants.IOS_DEVICES, iosDevicesList);
			} else if (isPlatformIsBOTH()) {
				if (isDevicesProvided(cloudResource, Constants.ANDROID_DEVICES)) {
					String androidDevices = cloudResource.getString(Constants.ANDROID_DEVICES);
					String[] androidDevicesArr = androidDevices.split(",");
					androidDevicesList.addAll(Arrays.asList(androidDevicesArr));
					devicesMap.put(Constants.ANDROID_DEVICES, androidDevicesList);
				}
				if (isDevicesProvided(cloudResource, Constants.IOS_DEVICES)) {
					String iosDevices = cloudResource.getString(Constants.IOS_DEVICES);
					String[] iosDevicesArr = iosDevices.split(",");
					iosDevicesList.addAll(Arrays.asList(iosDevicesArr));
					devicesMap.put(Constants.IOS_DEVICES, iosDevicesList);
				}
			}

			return devicesMap;
		} else {
			return null;
		}
	}

	private static Map<String, List<String>> getDevicesMapForTotalRunMode() {
		if (isUserProvidedDevices()) {
			ResourceBundle cloudResource = ResourceBundle.getBundle("cloud");
			Map<String, List<String>> devicesMap = new HashMap<String, List<String>>();

			if (isPlatformIsAndroid()) {
				devicesMap.put(Constants.ANDROID_DEVICES, getDevicesFromAllResources(Constants.ANDROID_DEVICES));
			} else if (isPlatformIsIOS()) {
				devicesMap.put(Constants.IOS_DEVICES, getDevicesFromAllResources(Constants.IOS_DEVICES));
			} else if (isPlatformIsBOTH()) {
				if (isDevicesProvided(cloudResource, Constants.ANDROID_DEVICES) || isAndroidDevicesProvided()) {
					devicesMap.put(Constants.ANDROID_DEVICES, getDevicesFromAllResources(Constants.ANDROID_DEVICES));
				}
				if (isDevicesProvided(cloudResource, Constants.IOS_DEVICES) || isIosDevicesProvided()) {
					devicesMap.put(Constants.IOS_DEVICES, getDevicesFromAllResources(Constants.IOS_DEVICES));
				}
			}

			return devicesMap;
		} else {
			return null;
		}
	}

	private static List<String> getDevicesFromAllResources(String devicesKey) {
		ResourceBundle cloudResource = ResourceBundle.getBundle("cloud");
		List<String> devicesList = new ArrayList<>();
		String androidDevices = cloudResource.getString(devicesKey);
		if (androidDevices != null && androidDevices.trim().length() > 0) {
			String[] androidDevicesArr = androidDevices.split(",");
			if (androidDevicesArr.length > 0)
				devicesList.addAll(Arrays.asList(androidDevicesArr));
		}
		String androidDevices2 = PropertyReader.readEnvOrConfigProperty(devicesKey);
		if (androidDevices2 != null && androidDevices2.trim().length() > 0) {
			String[] androidDevicesArr2 = androidDevices2.split(",");
			if (androidDevicesArr2.length > 0)
				devicesList.addAll(Arrays.asList(androidDevicesArr2));
		}
		return devicesList;
	}

	/**
	 * @author mallikarjun
	 * @return true - if selected APP_TYPE is Android (In config.properties) false -
	 *         if selected APP_TYPE is not Android
	 */
	public static boolean isPlatformIsAndroid() {
		if (getSelectedPlatform().equalsIgnoreCase(Constants.ANDROID)) {
			return true;
		} else
			return false;
	}

	/**
	 * @author mallikarjun
	 * @return true - If selected APP_TYPE is IOS (In config.properties) false - If
	 *         selected APP_TYPE is not IOS
	 */
	public static boolean isPlatformIsIOS() {
		if (getSelectedPlatform().equalsIgnoreCase(Constants.IOS)) {
			return true;
		} else
			return false;
	}

	public static boolean isPlatformIsBOTH() {
		if (getSelectedPlatform().equalsIgnoreCase(Constants.BOTH)) {
			return true;
		} else
			return false;
	}
}
