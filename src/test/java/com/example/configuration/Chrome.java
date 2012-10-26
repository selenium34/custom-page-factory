package com.example.configuration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "chrome", "default" })
public class Chrome {

  @Bean
  public WebDriver driver() {
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    return new ChromeDriver();
  }

}
