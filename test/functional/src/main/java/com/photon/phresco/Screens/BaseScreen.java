package com.photon.phresco.Screens;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.photon.phresco.selenium.util.Constants;
import com.photon.phresco.selenium.util.GetCurrentDir;
import com.photon.phresco.selenium.util.ScreenException;
import com.photon.phresco.uiconstants.UIConstants;
import com.photon.phresco.uiconstants.WidgetData;
import com.photon.phresco.uiconstants.PhrescoUiConstants;



public class BaseScreen {

	private static WebDriver driver;
	private ChromeDriverService chromeService;
	private static Log log = LogFactory.getLog("BaseScreen");
	private WebElement element;
	private WidgetData yuiWidgetConstants;
	private UIConstants uiConstants;
	private static PhrescoUiConstants phrsc;

	// private Log log = LogFactory.getLog(getClass());

	public BaseScreen() {

	}

	public BaseScreen(String selectedBrowser, String applicationURL,
			String applicationContext, WidgetData yuiWidgetConstants,
			UIConstants uiConstants) throws ScreenException {

		this.yuiWidgetConstants = yuiWidgetConstants;
		this.uiConstants = uiConstants;
		instantiateBrowser(selectedBrowser, applicationURL, applicationContext);
	}

	public void instantiateBrowser(String selectedBrowser,
			String applicationURL, String applicationContext)
			throws ScreenException {

		if (selectedBrowser.equalsIgnoreCase(Constants.BROWSER_CHROME)) {

			try {
				// "D:/Selenium-jar/chromedriver_win_19.0.1068.0/chromedriver.exe"
				chromeService = new ChromeDriverService.Builder()
						.usingDriverExecutable(new File(getChromeLocation()))

						.usingAnyFreePort().build();
				log.info("-------------***LAUNCHING GOOGLECHROME***--------------");
				driver = new ChromeDriver(chromeService);
				driver.manage().window().maximize();
				driver.navigate().to(applicationURL + applicationContext);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (selectedBrowser.equalsIgnoreCase(Constants.BROWSER_IE)) {
			log.info("---------------***LAUNCHING INTERNET EXPLORE***-----------");
			driver = new InternetExplorerDriver();
			driver.navigate().to(applicationURL + applicationContext);

		} else if (selectedBrowser.equalsIgnoreCase(Constants.BROWSER_FIREFOX)) {
			log.info("-------------***LAUNCHING FIREFOX***--------------");
			driver = new FirefoxDriver();
			//driver.manage().window().maximize();
			windowResize();
			driver.navigate().to(applicationURL + applicationContext);

		}

		else if (selectedBrowser.equalsIgnoreCase(Constants.BROWSER_OPERA)) {
			log.info("-------------***LAUNCHING OPERA***--------------");
			// WebDriver driver = new OperaDriver();

			System.out.println("******entering window maximize********");
			Robot robot;
			try {
				robot = new Robot();
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyRelease(KeyEvent.VK_SPACE);
				robot.keyPress(KeyEvent.VK_X);
				robot.keyRelease(KeyEvent.VK_X);
			} catch (AWTException e) {
				e.printStackTrace();
			}
			driver.navigate().to(applicationURL + applicationContext);

		} else {
			throw new ScreenException(
					"------Only FireFox,InternetExplore,Chrome and Opera  works-----------");
		}
	}

	
	public static void windowResize(){
		phrsc = new PhrescoUiConstants();		
		String resolution = phrsc.RESOLUTION;
		String[] tokens = resolution.split("x");
		String resolutionX=tokens[0];
		String resolutionY=tokens[1];		
		int x= Integer.parseInt(resolutionX);
		int y= Integer.parseInt(resolutionY);
		Dimension screenResolution = new Dimension(x,y);
		driver.manage().window().setSize(screenResolution);
	}
	/*
	 * public static void windowMaximizeFirefox() {
	 * driver.manage().window().setPosition(new Point(0, 0)); java.awt.Dimension
	 * screenSize = java.awt.Toolkit.getDefaultToolkit() .getScreenSize();
	 * Dimension dim = new Dimension((int) screenSize.getWidth(), (int)
	 * screenSize.getHeight()); driver.manage().window().setSize(dim); }
	 */

	public void closeBrowser() {
		log.info("-------------***BROWSER CLOSING***--------------");
		if (driver != null) {

			driver.quit();
		}
		if (chromeService != null) {
			chromeService.stop();
		}
	}

	public static String getChromeLocation() {

		log.info("getChromeLocation:*****CHROME TARGET LOCATION FOUND***");
		String directory = System.getProperty("user.dir");
		String targetDirectory = getChromeFile();
		String location = directory + targetDirectory;
		return location;
	}

	public static String getChromeFile() {
		if (System.getProperty("os.name").startsWith(Constants.WINDOWS_OS)) {
			log.info("*******WINDOWS MACHINE FOUND*************");

			return Constants.WINDOWS_DIRECTORY + "/chromedriver.exe";
		} else if (System.getProperty("os.name").startsWith(Constants.LINUX_OS)) {
			log.info("*******LINUX MACHINE FOUND*************");
			return Constants.LINUX_DIRECTORY_64 + "/chromedriver";
		} else if (System.getProperty("os.name").startsWith(Constants.MAC_OS)) {
			log.info("*******MAC MACHINE FOUND*************");
			return Constants.MAC_DIRECTORY + "/chromedriver";
		} else {
			throw new NullPointerException("******PLATFORM NOT FOUND********");
		}

	}

	public WebElement getXpathWebElement(String xpath) throws Exception {
		log.info("Entering:-----getXpathWebElement-------");
		try {

			element = driver.findElement(By.xpath(xpath));

		} catch (Throwable t) {
			log.info("Entering:---------Exception in getXpathWebElement()-----------");
			t.printStackTrace();

		}
		return element;
	}

	public void getIdWebElement(String id) throws ScreenException {
		log.info("Entering:---getIdWebElement-----");
		try {
			element = driver.findElement(By.id(id));

		} catch (Throwable t) {
			log.info("Entering:---------Exception in getIdWebElement()----------");
			t.printStackTrace();

		}

	}

	public void getcssWebElement(String selector) throws ScreenException {
		log.info("Entering:----------getIdWebElement----------");
		try {
			element = driver.findElement(By.cssSelector(selector));

		} catch (Throwable t) {
			log.info("Entering:---------Exception in getIdWebElement()--------");

			t.printStackTrace();

		}

	}

	public void waitForElementPresent(String locator, String methodName)
			throws IOException, Exception {
		try {
			log.info("Entering:--------waitForElementPresent()--------");
			By by = By.xpath(locator);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			log.info("Waiting:--------One second----------");
			wait.until(presenceOfElementLocated(by));
		}

		catch (Exception e) {
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile,
					new File(GetCurrentDir.getCurrentDirectory() + "\\"
							+ methodName + ".png"));
			throw new RuntimeException("waitForElementPresent"
					+ super.getClass().getSimpleName() + " failed", e);

		}
	}

	Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
		log.info("Entering:------presenceOfElementLocated()-----Start");
		return new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				log.info("Entering:*********presenceOfElementLocated()******End");
				return driver.findElement(locator);

			}

		};

	}

	public void billingInfo(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.EMAIL, methodName);
		getXpathWebElement(this.uiConstants.EMAIL);
		System.out.println("----element ---------->1" + element);

		element.sendKeys(yuiWidgetConstants.EMAIL_VALUE);
		getIdWebElement(this.uiConstants.FIRSTNAME);
		System.out.println("----element-------------> 2" + element);
		element.sendKeys(yuiWidgetConstants.FIRSTNAME_VALUE);
		getIdWebElement(this.uiConstants.LASTNAME);
		element.sendKeys(yuiWidgetConstants.LASTNAME_VALUE);
		getIdWebElement(this.uiConstants.COMPANY);
		element.sendKeys(this.uiConstants.COMPANY);
		getIdWebElement(this.uiConstants.ADDRESS1);
		element.sendKeys(yuiWidgetConstants.ADDRESS1_VALUE);
		getIdWebElement(this.uiConstants.ADDRESS2);
		element.sendKeys(yuiWidgetConstants.ADDRESS2_VALUE);
		getIdWebElement(this.uiConstants.CITY);
		element.sendKeys(yuiWidgetConstants.CITY_VALUE);
		getIdWebElement(this.uiConstants.STATE);
		element.sendKeys(yuiWidgetConstants.STATE_VALUE);
		getIdWebElement(this.uiConstants.POSTALCODE);
		element.sendKeys(yuiWidgetConstants.POSTALCODE_VALUE);
		getIdWebElement(this.uiConstants.PHONENUMBER);
		element.sendKeys(yuiWidgetConstants.PHONENUMBER_VALUE);
		getIdWebElement(this.uiConstants.CARDNUMBER);
		element.sendKeys(yuiWidgetConstants.CARDNUMBER_VALUE);
		getIdWebElement(this.uiConstants.SECURITYNUMBER);
		element.sendKeys(yuiWidgetConstants.SECURITYNUMBER_VALUE);
		getIdWebElement(this.uiConstants.NAMEONCARD);
		element.sendKeys(yuiWidgetConstants.NAMEONCARD_VALUE);
		waitForElementPresent(this.uiConstants.REVIEWORDER, methodName);
		getXpathWebElement(this.uiConstants.REVIEWORDER);
		element.click();
		waitForElementPresent(this.uiConstants.SUBMITORDER, methodName);
		getXpathWebElement(this.uiConstants.SUBMITORDER);
		element.click();

	}

	public void Television(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		log.info("Entering :***************Television()***********Start:");
		waitForElementPresent(this.uiConstants.TELEVISION, methodName);
		getXpathWebElement(this.uiConstants.TELEVISION);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();
	}

	public void Computers(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.COMPUTERS, methodName);
		getXpathWebElement(this.uiConstants.COMPUTERS);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void MobilePhones(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.MOBILE, methodName);
		getXpathWebElement(this.uiConstants.MOBILE);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void AudioDevices(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.AUDIO_DEVICES, methodName);
		getXpathWebElement(this.uiConstants.AUDIO_DEVICES);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void Cameras(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.CAMERAS, methodName);
		getXpathWebElement(this.uiConstants.CAMERAS);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void Tablets(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.TABLETS, methodName);
		getXpathWebElement(this.uiConstants.TABLETS);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void MoviesnMusic(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.MOVIESnMUSIC, methodName);
		getXpathWebElement(this.uiConstants.MOVIESnMUSIC);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void VideoGames(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.VIDEOGAMES, methodName);
		getXpathWebElement(this.uiConstants.VIDEOGAMES);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();
	}

	public void MP3Players(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.MP3PLAYERS, methodName);
		getXpathWebElement(this.uiConstants.MP3PLAYERS);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();
	}

	public void Accessories(String methodName) throws Exception {

		if (StringUtils.isEmpty(methodName)) {
			methodName = Thread.currentThread().getStackTrace()[1]
					.getMethodName();
			;
		}
		waitForElementPresent(this.uiConstants.MORE, methodName);
		getXpathWebElement(this.uiConstants.MORE);
		element.click();
		waitForElementPresent(this.uiConstants.ACCESSORIES, methodName);
		getXpathWebElement(this.uiConstants.ACCESSORIES);
		element.click();
		waitForElementPresent(this.uiConstants.PROD1_DETAILS, methodName);
		getXpathWebElement(this.uiConstants.PROD1_DETAILS);
		element.click();
		waitForElementPresent(this.uiConstants.DET_ADDTOCART, methodName);
		getXpathWebElement(this.uiConstants.DET_ADDTOCART);
		element.click();
		waitForElementPresent(this.uiConstants.CHECKOUT, methodName);
		getXpathWebElement(this.uiConstants.CHECKOUT);
		element.click();

	}

	public void click() throws ScreenException {
		log.info("Entering:********click operation start********");
		try {
			element.click();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		log.info("Entering:********click operation end********");

	}

	public void clear() throws ScreenException {
		log.info("Entering:********clear operation start********");
		try {
			element.clear();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		log.info("Entering:********clear operation end********");

	}

	public void sendKeys(String text) throws ScreenException {
		log.info("Entering:********enterText operation start********");
		try {
			clear();
			element.sendKeys(text);

		} catch (Throwable t) {
			t.printStackTrace();
		}
		log.info("Entering:********enterText operation end********");
	}

	public void submit() throws ScreenException {
		log.info("Entering:********submit operation start********");
		try {
			element.submit();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		log.info("Entering:********submit operation end********");

	}

	public void isTextPresent(String textValue) {
		if (textValue != null) {
			Boolean textCheck = driver.getPageSource().contains(textValue);
			Assert.assertTrue("Text present", textCheck);
		} else {

			throw new RuntimeException("----HelloWorld Text is not existed----");

		}
	}

	public void isElementPresent(String element) throws Exception {

		WebElement testElement = getXpathWebElement(element);
		if (testElement.isDisplayed() && testElement.isEnabled()) {

			log.info("---Element found---");
		} else {
			throw new RuntimeException("--Element not found---");
			// Assert.fail("--Element is not present--"+testElement);

		}

	}

}
