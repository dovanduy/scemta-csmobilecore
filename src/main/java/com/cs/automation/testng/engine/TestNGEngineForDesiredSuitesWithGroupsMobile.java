package com.cs.automation.testng.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.cs.automation.appium.device.DeviceManager;
import com.cs.automation.util.parser.XMLReader;
import com.cs.automation.util.reporter.ReportLogService;
import com.cs.automation.util.reporter.ReportLogServiceImpl;
import com.cs.automation.utils.general.Constants;
import com.cs.automation.utils.general.PropertyReader;

/**
 * Purpose : is to execute the test scripts according to the module name
 * provided by the user. Module name must be similar to the Suite xml file
 * (child testng.xml) so this class will filter suites from the provided module
 * name and execute filtered suites only. If no suite found then it will not
 * execute any test scripts. Under the selected suites, it will execute only
 * selected groups (if provided) other wise it will execute all
 * 
 * @author mallikarjun.patnam
 * 
 */
public class TestNGEngineForDesiredSuitesWithGroupsMobile extends AbstractTestNGEngineMobile {

	private ReportLogService reporter = new ReportLogServiceImpl(TestNGEngineForDesiredSuitesWithGroupsMobile.class);
	private Parser mainSuiteParser;
	private List<XmlSuite> mainSuiteFileList = new ArrayList<XmlSuite>();
	private SortedSet<String> listOfSelectedClients = new TreeSet<String>();
	private String clients = null;
	private String modules = null;
	private String groups = null;
	protected String[] modulesArray = null;
	protected String[] includeGroupsArray = null;
	protected String[] excludeGroupsArray = null;
	private String[] clientsArray = null;
	private List<XmlSuite> suitesToRun = new ArrayList<XmlSuite>();
	private List<String> listSuiteFilesForVirtualTestng = new ArrayList<String>();
	private String parentSuiteName;
	private File virtualTestNGFile;
	private Map<String, String> mapToSubstitute = new HashMap<String, String>();

	@Override
	protected List<XmlSuite> buildSuites(SortedSet<String> listOfSelectedClients, List<XmlSuite> mainSuiteFileList)
			throws ParserConfigurationException, SAXException, IOException {
		reporter.info("INFO: Clients Found: " + listOfSelectedClients + "\n");
		reporter.info("INFO: Modules Selected: " + listOfDesiredModules + "\n");
		List<XmlSuite> childSuites = mainSuiteFileList;

		for (String client : listOfSelectedClients) {

//            for (String device_udid : listOfBrowserEnvValues) {			//Here listOfBrowserEnvValues = devices list

			mainSuiteFileList = getListOfXmlSuiteFromMainSuiteParser();
			try {
				XmlSuite parentSuite = mainSuiteFileList.get(0);
				childSuites = parentSuite.getChildSuites();

			} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
				reporter.debug(
						"Index for Parent Suite List is out of range :" + indexOutOfBoundsException.getMessage());
				throw indexOutOfBoundsException;
			}

			if (childSuites.isEmpty()) {
				reporter.info("INFO: No Suite Found for the provided Module Name: " + childSuites);
			} else if (childSuites.size() > 0) {

				for (XmlSuite child : childSuites) {
					String childSuiteName = child.getName();
					String clientNameForCurrentSuite = client + "-" + childSuiteName; // + "-" + device_udid
					child.setName(clientNameForCurrentSuite);
//                        child.setParameters(mapToSubstitute);

					List<XmlTest> listOfTests = child.getTests();
					List<XmlTest> listOfTestsToRun = new ArrayList<>();

					if (listOfTests.size() > 0) {
						XmlTest test = listOfTests.get(0);
						String oldTestName = test.getName();
						for (String key : devicesMap.keySet()) {
							SortedSet<String> devicesSet = devicesMap.get(key);
							String platform = key.equalsIgnoreCase(Constants.ANDROID_DEVICES) ? "android" : "ios";
							for (String device_udid : devicesSet) { 
								mapToSubstitute = new HashMap<String, String>();
								setParametersToMap(device_udid,platform);
								String testName = oldTestName + "-" +platform+ "-" + device_udid; 
								XmlTest testForDevice = new XmlTest(child);
								testForDevice.setClasses(test.getClasses());
								testForDevice.setName(testName);
								testForDevice.setParameters(mapToSubstitute);
								listOfTestsToRun.add(testForDevice);
							}
						}
					}

					child.setTests(listOfTestsToRun);
					String parallelMode = PropertyReader.readEnvOrConfigProperty("parallel");
					if (parallelMode != null && parallelMode.equalsIgnoreCase("true")) {
						child.setParallel(ParallelMode.TESTS);
					}
					reporter.info("INFO: New Suite Created with name: " + clientNameForCurrentSuite);
					reporter.info(child.toXml());
					suitesToRun.add(child);
				} // end of for loop childSuites
			}
//            }// end of loop listOfBrowserEnvValues

		} // end of for loop listOfSelectedClients

		// delete the virtual testng.xml file
		if (isFileValid(virtualTestNGFile.getName())) {
			virtualTestNGFile.delete();
		}
		return suitesToRun;
	}

	/**
	 * Purpose is to create new tesng.xml file with the desired suite files
	 * 
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	protected File createVirtualParentTestNGXml() throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Node rootElement = document.createElement("suite");
		document.appendChild(rootElement);
		Attr attrSuite = document.createAttribute("name");
		attrSuite.setTextContent(parentSuiteName);
		((Element) rootElement).setAttributeNode(attrSuite);

		// Suite Element of TestNG
		Element suiteFiles = document.createElement("suite-files");
		rootElement.appendChild(suiteFiles);
		/*
		 * Group feature START Author: mallikarjun.patnam
		 */
		Element groupforSuite = document.createElement("groups");
		Element runForGroup = document.createElement("run");
		groupforSuite.appendChild(runForGroup);
		boolean isIncludeGroupsAvailable = false;
		if (includeGroupsArray != null && includeGroupsArray.length > 0) {
			isIncludeGroupsAvailable = true;
			for (String group : includeGroupsArray) {
				Element include = document.createElement("include");
				Attr attrSuiteFile = document.createAttribute("name");
				attrSuiteFile.setNodeValue(group);
				include.setAttributeNode(attrSuiteFile);
				runForGroup.appendChild(include);
			}
		}
		if (excludeGroupsArray != null && excludeGroupsArray.length > 0)
			for (String group : excludeGroupsArray) {
				boolean addIt = true;
				if (isIncludeGroupsAvailable) {
					for (String inclgroup : includeGroupsArray) {
						if (group.equals(inclgroup)) {
							addIt = false;
							break;
						}
					}
				}
				if (!addIt)
					continue;
				Element exclude = document.createElement("exclude");
				Attr attrSuiteFile = document.createAttribute("name");
				attrSuiteFile.setNodeValue(group);
				exclude.setAttributeNode(attrSuiteFile);
				runForGroup.appendChild(exclude);
			}
		/* Group feature END */

		// SuiteFiles Elements of TestNG
		for (String suitepath : listSuiteFilesForVirtualTestng) {
			Element suiteFile = document.createElement("suite-file");
			Attr attrSuiteFile = document.createAttribute("path");
			attrSuiteFile.setNodeValue(suitepath);
			suiteFile.setAttributeNode(attrSuiteFile);
			// Adding Groups include in Suite - mallikarjun.patnam
			if (includeGroupsArray != null && includeGroupsArray.length > 0)
				suiteFile.appendChild(groupforSuite);
			else if (excludeGroupsArray != null && excludeGroupsArray.length > 0)
				suiteFile.appendChild(groupforSuite);
			suiteFiles.appendChild(suiteFile);
		}

		return transformToXmlFile(document);
	}

	@Override
	protected SortedSet<String> getBroswerEnvParamValues(String devicesEnvParamName) {
//		List<String> devicesList = new ArrayList<>();
//		String[] androidDevicesArr = devicesEnvParamName.split(",");
//		devicesList.addAll(Arrays.asList(androidDevicesArr));
		SortedSet<String> devicesSet = new TreeSet<>();
//		devicesSet.addAll(devicesList);	// this is from config, but not using
		devicesSet.addAll(DeviceManager.getSelectedDevices());
		return devicesSet;
	}
	protected Map<String, SortedSet<String>> getDevicesEnvParamValues() {
		Map<String, SortedSet<String>> devicesSortedMap = new HashMap<String, SortedSet<String>>();
		Map<String, List<String>> devicesListMap = DeviceManager.getSelectedDevicesForGrid();
		if (devicesListMap != null && (!devicesListMap.isEmpty())) {
			for (String key : devicesListMap.keySet()) {
				SortedSet<String> devicesSet = new TreeSet<>();
				devicesSet.addAll(devicesListMap.get(key));
				devicesSortedMap.put(key, devicesSet);
			}
		}
		return devicesSortedMap;
	}
	@Override
	protected SortedSet<String> getEnvModuleValues(String envModuleName) throws Exception {
		try {
			// modules = "module1";
			modules = envModuleName;
			if (modules == null || modules.equals(null) || modules.isEmpty()) {
				throw new NullPointerException("Env Module Parameter value cannot be null");
			}
			reporter.info(
					"INFO: The value found with Environment Module variable " + envModuleName + " is: " + modules);
			modulesArray = modules.split(",");
		} catch (NullPointerException nullPointerException) {
			reporter.debug("Environment Parameter cannot be null: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (PatternSyntaxException patternSyntaxException) {
			reporter.debug("Regular Expression Syntax Error" + patternSyntaxException.getMessage());
			throw patternSyntaxException;
		}

		catch (SecurityException securityException) {
			reporter.debug("Security Exception for Environment Variable  :" + securityException.getMessage());
			throw securityException;
		}

		try {
			Collections.addAll(listOfDesiredModules, modulesArray);
		} catch (UnsupportedOperationException unsupportedOperationException) {
			reporter.debug("Unsupported Add all collection Exception: " + unsupportedOperationException.getMessage());
			throw unsupportedOperationException;

		} catch (NullPointerException nullPointerException) {
			reporter.debug("Null elements cannot be added: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (IllegalArgumentException illegalAugumentException) {
			reporter.debug("Illegar property elment argument Exception: " + illegalAugumentException.getMessage());
			throw illegalAugumentException;
		}

		return listOfDesiredModules;
	}

	/*
	 * Author: mallikarjun.patnam (non-Javadoc)
	 * 
	 * @see
	 * com.cs.automation.util.main.AbstractTestNGEngine#getEnvGroupValues(java.lang.
	 * String)
	 */
	@Override
	protected SortedSet<String> getEnvGroupValues(String envGroupName) throws Exception {
		try {

			groups = envGroupName;
			if (groups == null || groups.equals(null) || groups.isEmpty()) {
				return listOfDesiredGroups;
			}
			reporter.info("INFO: The value found with Environment Group variable " + envGroupName + " is: " + groups);
			includeGroupsArray = groups.split(",");
		} catch (NullPointerException nullPointerException) {
			reporter.debug("Groups Environment Parameter cannot be null: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (PatternSyntaxException patternSyntaxException) {
			reporter.debug("Groups Regular Expression Syntax Error" + patternSyntaxException.getMessage());
			throw patternSyntaxException;
		}

		catch (SecurityException securityException) {
			reporter.debug("Security Exception for Environment Variable  :" + securityException.getMessage());
			throw securityException;
		}

		try {
			Collections.addAll(listOfDesiredGroups, includeGroupsArray);
		} catch (UnsupportedOperationException unsupportedOperationException) {
			reporter.debug(
					"Groups Unsupported Add all collection Exception: " + unsupportedOperationException.getMessage());
			throw unsupportedOperationException;

		} catch (NullPointerException nullPointerException) {
			reporter.debug("Null elements cannot be added: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (IllegalArgumentException illegalAugumentException) {
			reporter.debug("Illegar property elment argument Exception: " + illegalAugumentException.getMessage());
			throw illegalAugumentException;
		}

		return listOfDesiredGroups;
	}

	/*
	 * Author: mallikarjun.patnam (non-Javadoc)
	 * 
	 * @see
	 * com.cs.automation.util.main.AbstractTestNGEngine#getEnvGroupValues(java.lang.
	 * String)
	 */
	@Override
	protected SortedSet<String> getEnvExcludeGroupValues(String envGroupName) throws Exception {
		try {

			if (envGroupName == null || envGroupName.equals(null) || envGroupName.isEmpty()) {
				return listOfExcludeGroups;
			}
			reporter.info(
					"INFO: The value found with Environment Group variable " + envGroupName + " is: " + envGroupName);
			excludeGroupsArray = envGroupName.split(",");
		} catch (NullPointerException nullPointerException) {
			reporter.debug("Groups Environment Parameter cannot be null: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (PatternSyntaxException patternSyntaxException) {
			reporter.debug("Groups Regular Expression Syntax Error" + patternSyntaxException.getMessage());
			throw patternSyntaxException;
		}

		catch (SecurityException securityException) {
			reporter.debug("Security Exception for Environment Variable  :" + securityException.getMessage());
			throw securityException;
		}

		try {
			Collections.addAll(listOfExcludeGroups, excludeGroupsArray);
		} catch (UnsupportedOperationException unsupportedOperationException) {
			reporter.debug(
					"Groups Unsupported Add all collection Exception: " + unsupportedOperationException.getMessage());
			throw unsupportedOperationException;

		} catch (NullPointerException nullPointerException) {
			reporter.debug("Null elements cannot be added: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (IllegalArgumentException illegalAugumentException) {
			reporter.debug("Illegar property elment argument Exception: " + illegalAugumentException.getMessage());
			throw illegalAugumentException;
		}

		return listOfExcludeGroups;
	}

	@Override
	protected SortedSet<String> getEnvParamValues(String envParamName) {
		try {
			// clients = "cs";
			clients = envParamName;
			if (clients == null || clients.equals(null) || clients.isEmpty()) {
				throw new NullPointerException("Env Parameter value cannot be null");
			}
			reporter.info("INFO: The value found with Environment variable " + envParamName + " is: " + clients);
			clientsArray = clients.split(",");
		} catch (NullPointerException nullPointerException) {
			reporter.debug("Environment Parameter cannot be null: " + nullPointerException.getMessage());
			throw nullPointerException;

		} catch (PatternSyntaxException patternSyntaxException) {
			reporter.debug("Regular Expression Syntax Error" + patternSyntaxException.getMessage());
			throw patternSyntaxException;
		}

		catch (SecurityException securityException) {
			reporter.debug("Security Exception for Environment Variable  :" + securityException.getMessage());
			throw securityException;
		}

		try {
			Collections.addAll(listOfSelectedClients, clientsArray);
		} catch (UnsupportedOperationException unsupportedOperationException) {
			reporter.debug("Unsupported Add all collection Exception: " + unsupportedOperationException.getMessage());
			throw unsupportedOperationException;

		} catch (NullPointerException nullPointerException) {
			reporter.debug("Null elements cannot be added: " + nullPointerException.getMessage());
			throw nullPointerException;
		} catch (IllegalArgumentException illegalAugumentException) {
			reporter.debug("Illegar property elment argument Exception: " + illegalAugumentException.getMessage());
			throw illegalAugumentException;
		}

		return listOfSelectedClients;

	}

	/**
	 * Purpose is to return the List XmlSuite from the (Parser) mainSuiteParser
	 * object.
	 * 
	 * @return is List<XmlSuites> of XmlSuite.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private List<XmlSuite> getListOfXmlSuiteFromMainSuiteParser() throws IOException {
		try {
			mainSuiteFileList = mainSuiteParser.parseToList();
		} catch (IOException ioException) {
			reporter.debug("IO Exception occurred :" + ioException.getMessage());
			throw ioException;

		}
		return mainSuiteFileList;
	}

	/**
	 * Purpose: Validates the given filename exists or not
	 * 
	 * @param filename is the existing Xml Suite file
	 * @return true or false
	 * @throws FileNotFoundException if the file name passed as parameter is not
	 *                               found
	 */

	private boolean isFileValid(String filename) throws FileNotFoundException {

		if (filename == null) {
			reporter.debug("File name cannot be null: " + filename);
			throw new NullPointerException("File name cannot be null: " + filename);

		}
		if (!new File(filename).exists()) {
			reporter.debug("File does not exist on the path given " + filename);
			throw new FileNotFoundException("File does not exist at given location: " + filename);
		}
		return true;
	}

	/**
	 * Purpose is to parse the parent testng.xml file for getting all the tags
	 * inside it. It will add desired suite files to the List
	 * listSuiteFilesForVirtualTestng
	 * 
	 * @param mainSuitFilename parent testng file name
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected void parseAndApplyFilterOnParentTestNGXml(String mainSuitFilename)
			throws ParserConfigurationException, SAXException, IOException {
		XMLReader xmlReaderForMainSuiteFile = new XMLReader(new File(mainSuitFilename));
		Document mainDocument = xmlReaderForMainSuiteFile.getDocument();
		NodeList allNodesInDocument = mainDocument.getChildNodes();

		// this will read all the content of main testng.xml
		readAllNodes(allNodesInDocument);
	}

	@Override
	protected List<XmlSuite> parseMainSuite(String mainSuitFilename)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		if (isFileValid(mainSuitFilename)) {
			try {
				parseAndApplyFilterOnParentTestNGXml(mainSuitFilename);
				// Generate new xml with the desired suite path and pass the file name to TestNG
				// Parser Class
				virtualTestNGFile = createVirtualParentTestNGXml();
				mainSuitFilename = virtualTestNGFile.getName();
				mainSuiteParser = new Parser(mainSuitFilename);
				mainSuiteFileList = getListOfXmlSuiteFromMainSuiteParser();
			} catch (TransformerException transformerException) {
				reporter.debug(
						"TransformerException cannot transfrom into xml file " + transformerException.getMessage());
				throw transformerException;
			}

		}

		else {
			reporter.debug("Invalid File: " + mainSuitFilename);
			throw new IllegalArgumentException("Invalid File Argument: " + mainSuitFilename);

		}

		return mainSuiteFileList;
	}

	/**
	 * Purpose is to read add nodes(tags) available in testng.xml
	 * 
	 * @param allNodes is org.w3c.dom.NodeList
	 */
	private void readAllNodes(NodeList allNodes) {
		for (int nodeIndex = 0; nodeIndex < allNodes.getLength(); nodeIndex++) {
			Node rootElement = allNodes.item(nodeIndex);
			if (rootElement.getNodeType() == Node.ELEMENT_NODE) {
				readParentSuiteName(rootElement);
				readChildNodes(rootElement);
			}
		}
	}

	/**
	 * Purpose is to read all the child node available in <suite-files> tag present
	 * in testng.xml, apply filter only on desired suite and add them to a List.
	 * 
	 * @param nodeList is org.w3c.dom.NodeList
	 */
	private void readAndFilterDesiredSuiteFiles(NodeList nodeList) {
		listSuiteFilesForVirtualTestng.clear();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasAttributes()) {
					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int index = 0; index < nodeMap.getLength(); index++) {
						Node node = nodeMap.item(index);
						String suiteFilePath = node.getNodeValue();
						for (String moduleName : modulesArray) {
							String suiteFileName = suiteFilePath.substring(suiteFilePath.lastIndexOf("/") + 1,
									suiteFilePath.lastIndexOf("."));
							if (suiteFileName.equalsIgnoreCase(moduleName)) {
								if (!listSuiteFilesForVirtualTestng.contains(suiteFilePath)) {
									listSuiteFilesForVirtualTestng.add(suiteFilePath);
								}
							}
						}
					}
				}
				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					readAndFilterDesiredSuiteFiles(tempNode.getChildNodes());
				}
			}
		}

	}

	private void readChildNodes(Node rootElement) {
		if (rootElement.hasChildNodes()) {
			NodeList suiteFilesElement = rootElement.getChildNodes();
			for (int nodeIndex = 0; nodeIndex < suiteFilesElement.getLength(); nodeIndex++) {
				Node childNode = suiteFilesElement.item(nodeIndex);
				if (childNode.hasChildNodes()) {
					NodeList suiteFileList = childNode.getChildNodes();

					// add desired suite path to the List listSuiteFilesForVirtualTestng
					readAndFilterDesiredSuiteFiles(suiteFileList);
				}
			}
		}
	}

	private void readParentSuiteName(Node rootElement) {
		if (rootElement.hasAttributes() && rootElement.getLocalName().equals("suite")) {
			NamedNodeMap nameNodeMap = rootElement.getAttributes();
			Node suiteElement = nameNodeMap.getNamedItem("name");
			if (suiteElement != null) {
				parentSuiteName = suiteElement.getNodeValue();
			}
		}
	}

	/**
	 * Purpose: This method sets the parameters to a Map object. To use to set the
	 * parameters to suite objects
	 * 
	 * @param paramsList
	 */
	private void setParametersToMap(String device_udid) {

		mapToSubstitute.put("device_udid", device_udid);

	}
	private void setParametersToMap(String device_udid,String device_type) {
		mapToSubstitute.put("device_udid", device_udid);
		mapToSubstitute.put("device_type", device_type);


	}
	private File transformToXmlFile(Document document)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		// transform the data into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		File file = new File("testng1.xml");
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(domSource, streamResult);
		return file;
	}

}
