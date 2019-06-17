package com.cs.automation.testng.engine;

/**
 * TestNg Engine Factory to extract different types of Execution on the basis of selection
 * @author 
 */
public class TestNGEngineFactoryMobile {

    public AbstractTestNGEngineMobile getTestNGEngine(TestNGEngineTemplateTypeMobile template) {

        switch (template) {
            case DESIRED_SUITE_FOR_GIVEN_MODULES:
                return new TestNGEngineForDesiredSuitesMobile();
            case DESIRED_SUITE_FOR_GIVEN_MODULES_WITH_GROUPS:
            	return new TestNGEngineForDesiredSuitesWithGroupsMobile();
            default:
                break;
        }

        return null;

    }

}
