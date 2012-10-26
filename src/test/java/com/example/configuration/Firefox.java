package com.example.configuration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("firefox")
public class Firefox {

  @Bean
  public WebDriver driver() {
    return new FirefoxDriver();
  }

}
