package com.example.configuration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ie")
public class InternetExplorer {

  @Bean
  public WebDriver driver() {
    System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
    return new InternetExplorerDriver();
  }
}
