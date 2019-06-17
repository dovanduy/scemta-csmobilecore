package com.cs.automation.appium.device;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.cs.automation.utils.appium.AvailablePorts;
import com.cs.automation.utils.appium.CommandPrompt;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

public class IOSDeviceConfiguration {

	private static ConcurrentHashMap<String, Integer> appiumServerProcess =
			new ConcurrentHashMap<>();
	private CommandPrompt commandPrompt = new CommandPrompt();
	private AvailablePorts ap = new AvailablePorts();
	private HashMap<String, String> deviceMap = new HashMap<String, String>();
	private Process p1;


	public boolean checkiOSDevice(String UDID) throws Exception {
		try {
			String getIOSDeviceID = commandPrompt.runCommand("idevice_id --list");
			boolean checkDeviceExists = getIOSDeviceID.contains(UDID);
			if (checkDeviceExists) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public void destroyIOSWebKitProxy(String device_id) throws IOException, InterruptedException {
		Thread.sleep(3000);
		if (getIOSUDID() != null) { // added for IOS simulator
			if (appiumServerProcess.get(device_id) != -1) {
				String process =
						"pgrep -P " + appiumServerProcess.get(Thread.currentThread().getId());
				Process p2 = Runtime.getRuntime().exec(process);
				BufferedReader r = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				String command = "kill -9 " + r.readLine();
				System.out.println("Kills webkit proxy");
				System.out.println("******************" + command);
				Runtime.getRuntime().exec(command);
			}
		}
	}

	public String getDeviceName(String udid) throws InterruptedException, IOException {
		String deviceName =
				commandPrompt.runCommand("idevicename --udid " + udid).replace("\\W", "_");
		return deviceName;
	}


	public ArrayList<String> getIOSUDID() {
		ArrayList<String> deviceUDIDiOS = new ArrayList<String>();
		try {
			String getIOSDeviceID = commandPrompt.runCommand("idevice_id --list");
			if (getIOSDeviceID == null || getIOSDeviceID.equalsIgnoreCase("")
					|| getIOSDeviceID.isEmpty()) {
				return null;
			} else {
				String[] lines = getIOSDeviceID.split("\n");
				for (int i = 0; i < lines.length; i++) {
					lines[i] = lines[i].replaceAll("\\s+", "");
					deviceUDIDiOS.add(lines[i]);
				}
				return deviceUDIDiOS;
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}



	public int getPid(Process process) {

		try {
			Class<?> cProcessImpl = process.getClass();
			Field fPid = cProcessImpl.getDeclaredField("pid");
			if (!fPid.isAccessible()) {
				fPid.setAccessible(true);
			}
			return fPid.getInt(process);
		} catch (Exception e) {
			return -1;
		}
	}


	public HashMap<String, String> setIOSWebKitProxyPorts(String device_udid) throws Exception {
		try {
			int webkitproxyport = ap.getPort();
			deviceMap.put(device_udid, Integer.toString(webkitproxyport));
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return deviceMap;
	}

	public String startIOSWebKit(String udid) throws IOException, InterruptedException {
		String serverPath = PropertyReader.getProperty(Constants.APPIUM_JS_PATH);
		File file = new File(serverPath);
		File curentPath = new File(file.getParent());
		System.out.println(curentPath);
		file = new File(curentPath + "");
		String ios_web_lit_proxy_runner =
				file.getCanonicalPath() + "/ios-webkit-debug-proxy-launcher.js";
		String webkitRunner =
				ios_web_lit_proxy_runner + " -c " + udid + ":" + deviceMap.get(udid) + " -d";
		System.out.println(webkitRunner);
		p1 = Runtime.getRuntime().exec(webkitRunner);
		System.out.println("WebKit Proxy is started on device " + udid + " and with port number "
				+ deviceMap.get(udid) + " and in thread " + Thread.currentThread().getId());
		// Add the Process ID to hashMap, which would be needed to kill IOSwebProxywhen required
		appiumServerProcess.put(udid, getPid(p1));
		System.out.println("Process ID's:" + appiumServerProcess);
		Thread.sleep(1000);
		return deviceMap.get(udid);

	}


}
