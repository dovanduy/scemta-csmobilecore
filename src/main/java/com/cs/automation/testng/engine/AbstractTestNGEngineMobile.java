package com.cs.automation.testng.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

public abstract class AbstractTestNGEngineMobile {
    protected List<XmlSuite> mainSuiteFileList = new ArrayList<XmlSuite>();
    SortedSet<String> listOfSelectedClients = new TreeSet<String>();
    SortedSet<String> listOfDesiredModules = new TreeSet<String>();
    SortedSet<String> listOfDesiredGroups = new TreeSet<String>();
    SortedSet<String> listOfExcludeGroups = new TreeSet<String>();
    List<XmlSuite> suitesToRun = new ArrayList<XmlSuite>();
    SortedSet<String> listOfBrowserEnvValues = new TreeSet<String>();
    Map<String, SortedSet<String>> devicesMap = new HashMap<String, SortedSet<String>>();
    
    /**
     * Purpose Build suites for each client and for each browser. The implemented class would use
     * the super class attributes
     * @return the list of newly created XMLSuites
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    protected List<XmlSuite> buildSuites() throws ParserConfigurationException, SAXException,
            IOException {
        return suitesToRun;
    }

    /**
     * Purpose: Builds suites on runtime for each client for each suite in the Parent suite list
     * @param clientsSelected Set of comma separated values of the System property
     * @param parentSuiteList List of Xml Suites identified from the parent suite
     * @return the List of Xml Suites created on runtime for each client for each suite
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    protected abstract List<XmlSuite> buildSuites(SortedSet<String> listOfSelectedClients,
            List<XmlSuite> mainSuiteFileList) throws ParserConfigurationException, SAXException,
            IOException;

    /**
     * Purpose: Gets the Set of String values of the
     * @param browserEnvParamName
     * @return
     */
    protected SortedSet<String> getBroswerEnvParamValues(String browserEnvParamName) {
        return listOfBrowserEnvValues;
    }
    protected Map<String, SortedSet<String>> getDevicesEnvParamValues() {
        return devicesMap;
    }
    /**
     * Purpose: Gets the Set of String values of the System property which defined as part of the
     * command line
     * @param envModuleName is the System property name
     * @return the Sorted Set of the module names Note: Splits the value of System property with
     *         comma and sets as Set of values
     * @throws Exception
     */
    protected SortedSet<String> getEnvModuleValues(String envModuleName) throws Exception {
        return listOfDesiredModules;
    }
    /**
     * Purpose: Gets the Set of String values of the System property which defined as part of the
     * command line
     * @param envGroupName is the System property name
     * @return the Sorted Set of the group names Note: Splits the value of System property with
     *         comma and sets as Set of values
     * @throws Exception
     */
    protected SortedSet<String> getEnvGroupValues(String envGroupName) throws Exception {
        return listOfDesiredGroups;
    }
    /**
     * Purpose: Gets the Set of String values of the System property which defined as part of the
     * command line
     * @param envGroupName is the System property name
     * @return the Sorted Set of the group names Note: Splits the value of System property with
     *         comma and sets as Set of values
     * @throws Exception
     */
    protected SortedSet<String> getEnvExcludeGroupValues(String envGroupName) throws Exception {
        return listOfExcludeGroups;
    }    

    /**
     * Purpose: Gets the Set of String values of the System property which defined as part of the
     * command line
     * @param envParameterName is the System property name
     * @return the Sorted Set of the client names Note: Splits the value of System property with
     *         comma and sets as Set of values
     */
    protected abstract SortedSet<String> getEnvParamValues(String envParameterName);

    public final List<XmlSuite> getSuitesToRunFor(String mainSuiteFileName, String envParamName)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        listOfSelectedClients = getEnvParamValues(envParamName);
        suitesToRun = buildSuites(listOfSelectedClients, mainSuiteFileList);
        return suitesToRun;

    }

    public final List<XmlSuite> getSuitesToRunFor(String mainSuiteFileName, String envParamName,
            String devicesEnvParam) throws ParserConfigurationException, SAXException, IOException,
            TransformerException {
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        listOfSelectedClients = getEnvParamValues(envParamName);
        listOfBrowserEnvValues = getBroswerEnvParamValues(devicesEnvParam);
        suitesToRun = buildSuites();
        return suitesToRun;

    }

    /**
     * Purpose is to get the desired suites to run for TestNG, Note : Only used with
     * DESIRED_SUITE_FOR_GIVEN_MODULES, DESIRED_SUITE_FOR_GIVEN_CLIENT_BASED_MODULES
     * @param mainSuiteFileName
     * @param envParamClientName
     * @param envParamDesiredModules
     * @return
     * @throws Exception
     */
    public final List<XmlSuite> getSuitesToRunFor(String mainSuiteFileName,
            String envParamClientName, String envParamDesiredModules, String deviceEnvParams)
            throws Exception {					//TODO:    change logic here
        listOfSelectedClients = getEnvParamValues(envParamClientName);
        listOfDesiredModules = getEnvModuleValues(envParamDesiredModules);
        listOfBrowserEnvValues = getBroswerEnvParamValues(deviceEnvParams);
        devicesMap = getDevicesEnvParamValues();
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        return buildSuites(listOfSelectedClients, mainSuiteFileList);
    }
    /**
     * Purpose is to get the desired suites to run for TestNG, Note : Only used with
     * DESIRED_SUITE_FOR_GIVEN_MODULES, DESIRED_SUITE_FOR_GIVEN_CLIENT_BASED_MODULES, Grouping tests
     * @param mainSuiteFileName
     * @param envParamClientName
     * @param envParamDesiredModules
     * @param envParamDesiredGroups
     * @return
     * @throws Exception
     */
    public final List<XmlSuite> getSuitesToRunFor(String mainSuiteFileName,
            String envParamClientName, String envParamDesiredModules, String deviceEnvParams, String envParamDesiredGroups)
            throws Exception {
        listOfSelectedClients = getEnvParamValues(envParamClientName);
        listOfDesiredModules = getEnvModuleValues(envParamDesiredModules);
        listOfBrowserEnvValues = getBroswerEnvParamValues(deviceEnvParams);
        devicesMap = getDevicesEnvParamValues();
        listOfDesiredGroups = getEnvGroupValues(envParamDesiredGroups);
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        return buildSuites(listOfSelectedClients, mainSuiteFileList);
    }
    /**
     * Purpose is to get the desired suites to run for TestNG, Note : Only used with
     * DESIRED_SUITE_FOR_GIVEN_MODULES, DESIRED_SUITE_FOR_GIVEN_CLIENT_BASED_MODULES, Grouping tests
     * @param mainSuiteFileName
     * @param envParamClientName
     * @param envParamDesiredModules
     * @param envParamDesiredGroups - include groups
     * @param envParamExcludeGroups - exclude groups
     * @return
     * @throws Exception
     */
    public final List<XmlSuite> getSuitesToRunFor(String mainSuiteFileName,
            String envParamClientName, String envParamDesiredModules, String deviceEnvParams, String envParamDesiredGroups, String envParamExcludeGroups)
            throws Exception {
        listOfSelectedClients = getEnvParamValues(envParamClientName);
        listOfDesiredModules = getEnvModuleValues(envParamDesiredModules);
        listOfBrowserEnvValues = getBroswerEnvParamValues(deviceEnvParams);
        listOfDesiredGroups = getEnvGroupValues(envParamDesiredGroups);
        listOfExcludeGroups = getEnvExcludeGroupValues(envParamExcludeGroups);
        devicesMap = getDevicesEnvParamValues();
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        return buildSuites(listOfSelectedClients, mainSuiteFileList);
    }
    /**
     * Purpose is to get the desired suites to run for TestNG, Note : Only used with
     * DESIRED_SUITE_FOR_GIVEN_CLIENT_BASED_MODULES Templates for testing WebServices (Soap, Rest)
     * @param mainSuiteFileName
     * @param envParamClientName
     * @param envParamDesiredModules
     * @return
     * @throws Exception
     */
    public final List<XmlSuite> getSuitesToRunForWebServices(String mainSuiteFileName,
            String envParamClientName, String envParamDesiredModules) throws Exception {
        listOfSelectedClients = getEnvParamValues(envParamClientName);
        listOfDesiredModules = getEnvModuleValues(envParamDesiredModules);
        mainSuiteFileList = parseMainSuite(mainSuiteFileName);
        return buildSuites(listOfSelectedClients, mainSuiteFileList);
    }

    /**
     * Purpose: Parses main suite file
     * @param filename is the existing Xml Suite file name with path
     * @return List of XmlSuites defined under the main suite file passed as parameter
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    protected abstract List<XmlSuite> parseMainSuite(String filename)
            throws ParserConfigurationException, SAXException, IOException, TransformerException;



}
