package com.example;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.example.configuration.Chrome;
import com.example.configuration.Firefox;
import com.example.configuration.InternetExplorer;

@ContextConfiguration(classes = { Firefox.class, InternetExplorer.class, Chrome.class })
public class JQueryHomePageTest extends AbstractTestNGSpringContextTests {

  @Resource
  private WebDriver driver;
  private JQueryHomePage homePage;

  @BeforeClass
  public void setup() {
    homePage = new JQueryHomePage(driver);
  }

  @Test
  public void ensureItemsAreSame() {
    Assert.assertEquals(homePage.getIntroDivWithJQuery(), homePage.getIntroDivWithDefault());
  }

  @Test
  public void ensureMissingWebElement() {
    Assert.assertTrue(homePage.getOptionalIntroDivWithDefault() instanceof MissingWebElement,
        "A MissingWebElement is expected for optional elements which do not exist on the page.");

  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void ensureNoSuchElementExceptionThrown() {
    homePage.getOptionalIntroDivWithDefault().getText();
  }

  @Test
  public void getText() {
    final String text = StringUtils.replaceEach(homePage.getTagline().getText(), new String[] { "\r", "\n" },
        new String[] { " ", " " });
    Assert.assertTrue("jQuery is a new kind of JavaScript Library.".equalsIgnoreCase(text));
  }

  @AfterClass
  public void shutdown() {
    driver.quit();
  }
}
