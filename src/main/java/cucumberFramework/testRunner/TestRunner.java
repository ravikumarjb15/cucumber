package cucumberFramework.testRunner;


import io.cucumber.testng.*;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/resources/features/login/login_logout.feature", 
glue = { "cucumberFramework/stepdefinitions" }, 
plugin = { "pretty", "html:target/cucumber-reports/cucumber-pretty.html",
		"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
		"json:target/cucumber-reports/CucumberTestReport.json", "rerun:target/cucumber-reports/rerun.txt", "message:target/results.ndjson" },
		monochrome = true, dryRun = false)
public class TestRunner extends AbstractTestNGCucumberTests {
	
	private TestNGCucumberRunner testNGCucumberRunner;
	   
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		testNGCucumberRunner = new  TestNGCucumberRunner(this.getClass());
	}

	@Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void scenario(PickleWrapper pickle, FeatureWrapper cucumberFeature) {
		testNGCucumberRunner.runScenario(pickle.getPickle());

	}

	@DataProvider
	public Object[][] scenarios() {
		return testNGCucumberRunner.provideScenarios();
	}

	@AfterClass(alwaysRun = true)
	public void testDownClass() {
		testNGCucumberRunner.finish();
	}

	@AfterSuite
	public void test() {
		File reportOutputDirectory = new File("target");
    	List<String> jsonFiles = new ArrayList<>();
    	jsonFiles.add("C:\\Data\\Java\\Projects\\CucumberTestNGRunner\\target\\cucumber-reports\\CucumberTestReport.json");
//    	jsonFiles.add("cucumber-report-2.json");

    	String buildNumber = "1";
    	String projectName = "cucumberProject";

    	Configuration configuration = new Configuration(reportOutputDirectory, projectName);
    	// optional configuration - check javadoc for details
    	configuration.addPresentationModes(PresentationMode.RUN_WITH_JENKINS);
    	// do not make scenario failed when step has status SKIPPED
    	configuration.setNotFailingStatuses(Collections.singleton(Status.SKIPPED));
    	configuration.setBuildNumber(buildNumber);
    	// addidtional metadata presented on main page
    	configuration.addClassifications("Platform", "Windows");
    	configuration.addClassifications("Browser", "Firefox");
    	configuration.addClassifications("Branch", "release/1.0");

    	// optionally add metadata presented on main page via properties file
//    	List<String> classificationFiles = new ArrayList<>();
//    	classificationFiles.add("properties-1.properties");
//    	classificationFiles.add("properties-2.properties");
//    	configuration.addClassificationFiles(classificationFiles);

    	// optionally specify qualifiers for each of the report json files
    	        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
    	        configuration.setQualifier("cucumber-report-1","First report");
//    	        configuration.setQualifier("cucumber-report-2","Second report");

    	        ReportBuilder reportBuilder=new ReportBuilder(jsonFiles,configuration);
    	        reportBuilder.generateReports();
	}
}
