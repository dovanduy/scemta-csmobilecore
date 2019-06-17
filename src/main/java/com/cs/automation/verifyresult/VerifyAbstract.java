package com.cs.automation.verifyresult;

/**
 * IVerify is an Interface that provides the services of verifying Operations on
 * Selenium Web Elements
 * 
 * @author mallikarjun.patnam
 * 
 */
public abstract class VerifyAbstract extends VerificationManager{


	/**
	 * Verifies the Page Title
	 * 
	 * @param actual
	 *            is actual Page Title Value
	 * @param expected
	 *            is expected Page Title Value
	 * @param errorMessage
	 *            is the error message to be shown if assertion is failed
	 * @return Boolean value true if assertion is passed or false if assertion
	 *         is failed
	 */
	public abstract boolean verifyPageTitle(String actual, String expected, String errorMessage);

	/**
	 * Verifies the page URL
	 * 
	 * @param actual
	 *            is actual Page URL Value
	 * @param expected
	 *            is expected Page URL Value
	 * @param errorMessage
	 *            is the error message to be show if assertion is failed
	 * @return Boolean value true if assertion is passed or false if assertion
	 *         is failed
	 */
	public abstract boolean verifyPageUrl(String actual, String expected, String errorMessage);


}
