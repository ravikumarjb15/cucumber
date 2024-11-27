package cucumberFramework.testRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;


public class Test {
    public static void main(String[] args) throws IOException {
//        System.setProperty("webdriver.chrome.driver", "C:\\Data\\Java\\Projects\\chromedriver.exe");
//        WebDriver driver = new ChromeDriver();
//		driver.manage().window().maximize();
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
//		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(30));
//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("")))).click();
//
//		Wait wait1 = new FluentWait(driver).pollingEvery(Duration.ofSeconds(10)).withTimeout(Duration.ofSeconds(10)).ignoring(NoSuchElementException.class);
//		Boolean data = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("")));
//        driver.get("https://google.com");
//        driver.switchTo().alert().accept();
//
//        driver.switchTo().newWindow(WindowType.TAB);
//        driver.switchTo().frame("");
//        driver.manage().window().maximize();
////		driver.manage().addCookie("");
//
//        driver.navigate().refresh();
//
//        driver.findElement(By.xpath("")).click();
//
//        Actions action = new Actions(driver);
//        action.keyDown(Keys.ARROW_DOWN);
//
//        Select select = new Select(driver.findElement(By.xpath("")));
//        select.getFirstSelectedOption();
//        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//        FileUtils.copyFile(src, new File(""));
//
//        action.contextClick(driver.findElement(By.xpath("")));

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
////    	classificationFiles.add("properties-2.properties");
//    	configuration.addClassificationFiles(classificationFiles);

    	// optionally specify qualifiers for each of the report json files
    	        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
    	        configuration.setQualifier("cucumber-report-1","First report");
//    	        configuration.setQualifier("cucumber-report-2","Second report");

    	        ReportBuilder reportBuilder=new ReportBuilder(jsonFiles,configuration);
    	        reportBuilder.generateReports();
    	// and here validate 'result' to decide what to do if report has failed
    }
}
