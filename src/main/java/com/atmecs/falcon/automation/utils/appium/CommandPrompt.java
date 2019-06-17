package com.atmecs.falcon.automation.utils.appium;

/**
 * Command Prompt - this class contains method to run windows and mac commands
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandPrompt {

    private static Process p;
    ProcessBuilder builder;

    /**
     * @author suraj
     * @param command - 
     * @return command output as String 
     * */
    public String runCommand(String command) throws InterruptedException, IOException {
        p = Runtime.getRuntime().exec(command);
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        String allLine = "";
        // int i = 1;
        while ((line = r.readLine()) != null) {
            /*
             * if (line.isEmpty()) { break; }
             */
            allLine = allLine + "" + line + "\n";
            if (line.contains("Console LogLevel: debug") && line.contains("Complete")) {
                break;
                // i++;
            }
        }
        return allLine;

    }
//
//    private String runCommandThruProcessBuilder(String command) throws InterruptedException,
//            IOException {
//        List<String> commands = new ArrayList<String>();
//        commands.add("/bin/sh");
//        commands.add("-c");
//        commands.add(command);
//        ProcessBuilder builder = new ProcessBuilder(commands);
//        // Map<String, String> environ = builder.environment();
//
//        final Process process = builder.start();
//        InputStream is = process.getInputStream();
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
//        String line;
//        String allLine = "";
//        while ((line = br.readLine()) != null) {
//            allLine = allLine + "" + line + "\n";
//            System.out.println(allLine);
//        }
//        return allLine.split(":")[1].replace("\n", "").trim();
//    }
}