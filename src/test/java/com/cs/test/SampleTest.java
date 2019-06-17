package com.cs.test;

public class SampleTest {
	
	/*
	public static void main(String[] args) throws MalformedURLException {
		new SampleTest().testSomething();
	}
	
	
	@Test
	public void testSomething() throws MalformedURLException {
		String pack = "com.cs.test";
		String[] packages = pack.split(",");
		Collection<URL> urls = new ArrayList<>();
		for (String p : packages) {
			urls.addAll(ClasspathHelper.forPackage(p));
		}
		Iterator<URL> iter = urls.iterator();
		URL url = iter.next();
		urls.clear();

		ArrayList<URL> allUrls = new ArrayList<URL>();
		for (String p : packages) {
			URL newUrl = new URL(url.toString() + p.replaceAll("\\.", "/"));
			allUrls.add(newUrl);
		}

		Reflections reflections =
				new Reflections(new ConfigurationBuilder().setUrls(Lists.newArrayList(allUrls))
						.setScanners(new MethodAnnotationsScanner()));
		Set<Method> resources =
				reflections.getMethodsAnnotatedWith(org.testng.annotations.Test.class);
		
		Map<String, List<Method>> testsMap = new HashMap<>();
		for (Method method : resources) {
			List<Method> methodsList =
					testsMap.get(method.getDeclaringClass().getPackage().getName() + "."
							+ method.getDeclaringClass().getSimpleName());
			if (methodsList == null) {
				methodsList = new ArrayList<>();
				testsMap.put(method.getDeclaringClass().getPackage().getName() + "."
						+ method.getDeclaringClass().getSimpleName(), methodsList);
			}
			methodsList.add(method);
		}
		
		
		
		
		//TODO:
		
		Map<String, List<Method>> methods = testsMap;
		int deviceCount = 1;
		
		ArrayList<String> items = new ArrayList<>();
		if (PropertyReader.getProperty(Constants.LISTENERS) != null) {
			Collections.addAll(items, PropertyReader.getProperty(Constants.LISTENERS).split(
					"\\s*,\\s*"));
		}
		XmlSuite suite = new XmlSuite();
		suite.setName("TestNG Forum");
		suite.setThreadCount(deviceCount);
		suite.setParallel(ParallelMode.CLASSES);
		suite.setVerbose(2);
		items.add("com.cs.automation.appium.manager.AppiumParallelTest");
		suite.setListeners(items);
		if (PropertyReader.getProperty(Constants.LISTENERS) != null) {
			suite.setListeners(items);
		}
		XmlTest test = new XmlTest(suite);
		test.setName("TestNG Test");
		List<XmlClass> xmlClasses = new ArrayList<>();
		for (String className : methods.keySet()) {
			if (className.contains("Test")) {
				XmlClass clazz = new XmlClass();
				clazz.setName(className);
				xmlClasses.add(clazz);
			}
		}
		test.setXmlClasses(xmlClasses);
		
		
		
		System.out.println("-======");
		
		System.out.println(suite.toXml());
	}
	
	
	*/


}
