import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

public class GoogleSearchTest {

	static RemoteWebDriver driver;

	// TODO: Provide your cloud's information: host and security token.
	static String host = "<<CLOUD_NAME>>.perfectomobile.com";
	static String token = "<<SECURITY_TOKEN>>";

	// Old school credentials
	// static String user = "My_User";
	// static String pass = "My_Pass";

	// Driver initializing.
	public static void initDriver() throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("securityToken", token);

		// Old school credentials login:
		// capabilities.setCapability("user", user);
		// capabilities.setCapability("password", pass);

		// TODO: Change the capabilities.
		// Desktop capabilities for example.
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("platformVersion", "10");
		capabilities.setCapability("browserName", "Chrome");
		capabilities.setCapability("browserVersion", "latest");

		driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().window().maximize();

	}

	// TEST.
	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Creating the driver");
		initDriver();
		PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
				.withProject(new Project("Sample Reportium project", "1.0")).withWebDriver(driver).build();
		ReportiumClient reportiumClient = new ReportiumClientFactory()
				.createPerfectoReportiumClient(perfectoExecutionContext);
		System.out.println("Driver Created Successfully");
		try {
			reportiumClient.testStart("Sample FIrst Run",
					new TestContext.Builder().withTestExecutionTags("Sanity", "Nightly")
							.withCustomFields(new CustomField("version", "OS11")).build());

			reportiumClient.stepStart("Searching Google");

			driver.get("https://google.com");
			driver.findElement(By.name("q")).sendKeys("perfecto mobile"); // Finding the search bar and sends perfecto
																			// mobile.
			reportiumClient.testStop(TestResultFactory.createSuccess());
			// stopping the test - failure
		} catch (Exception t) {
			t.printStackTrace();
			reportiumClient.testStop(TestResultFactory.createFailure(t.getMessage(), t));
		}
		System.out.println("Report URL - " + reportiumClient.getReportUrl());
		EndTest();
	}

	// End test , closing the driver and downloads the report.
	public static void EndTest() {
		try {
			driver.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.quit();
	}
}