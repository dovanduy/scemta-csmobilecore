package com.atmecs.falcon.automation.utils.appium;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;

/*
 * This class gets all the classpath for the tests 
 * under the package specified
 */
public class PackageUtil {
	public static Collection<Object> getClasses(final String pack) throws Exception {
		System.out.println("compiler = "+ToolProvider.getSystemJavaCompiler());
		final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null,
				null, null);
		Collection<Object> collectionObject = new ArrayList<Object>();
		Iterable<JavaFileObject> fileIterator = fileManager.list(StandardLocation.CLASS_PATH, pack,
				Collections.singleton(JavaFileObject.Kind.CLASS), false);
		while (fileIterator.iterator().hasNext()) {
			JavaFileObject javaFileObject = fileIterator.iterator().next();
			try {
				final String[] split = javaFileObject.getName().replace(".class", "").replace(")", "")
						.split(Pattern.quote(File.separator));

				final String fullClassName = pack + "." + split[split.length - 1];
				collectionObject.add(Class.forName(fullClassName));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return collectionObject;
	}
}
