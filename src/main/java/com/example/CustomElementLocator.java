package com.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.example.annotations.JQueryLocator;
import com.example.annotations.OptionalElement;

public class CustomElementLocator implements ElementLocator {

  private final SearchContext searchContext;
  private final JQueryLocator jql;
  private final boolean isOptionalElement;
  private final Annotations annotations;

  public CustomElementLocator(final SearchContext searchContext, final Field field) {
    this.searchContext = searchContext;
    if (field.isAnnotationPresent(JQueryLocator.class)) {
      this.jql = field.getAnnotation(JQueryLocator.class);
      this.annotations = null;
    } else {
      this.jql = null;
      this.annotations = new Annotations(field);
    }
    this.isOptionalElement = field.isAnnotationPresent(OptionalElement.class);
  }

  @Override
  public WebElement findElement() {
    WebElement element = null;
    if (jql != null) {
      if (searchContext instanceof JavascriptExecutor) {
        final JavascriptExecutor jse = (JavascriptExecutor) searchContext;
        element = (WebElement) jse.executeScript("return $" + jql.$() + "[0]");
      } else {
        if (!isOptionalElement) {
          throw new IllegalArgumentException(
              "The SearchContext is not able to execute Javascript. Unable to locate element with jQuery");
        }
      }
    } else {
      element = searchContext.findElement(annotations.buildBy());
    }
    if (element == null && !isOptionalElement) {
      throw new NoSuchElementException("Unable to locate element with jQuery expression: " + jql.$());
    }
    return element;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<WebElement> findElements() {
    List<WebElement> elements = new ArrayList<>();
    if (jql != null) {
      if (searchContext instanceof JavascriptExecutor) {
        final JavascriptExecutor jse = (JavascriptExecutor) searchContext;
        elements = (List<WebElement>) jse.executeScript("return $" + jql.$());
      } else {
        if (!isOptionalElement) {
          throw new IllegalArgumentException(
              "The SearchContext is not able to execute Javascript. Unable to locate elements with jQuery");
        }
      }
    } else {
      elements = searchContext.findElements(annotations.buildBy());
    }
    return elements;
  }

  public boolean isOptionalElement() {
    return isOptionalElement;
  }

  @Override
  public String toString() {
    if (annotations != null) {
      return "Attempting to find element(s): " + annotations.buildBy() + "\tIs Optional Element: " + isOptionalElement;
    }
    return "Attempting to find element(s) with jQuery: " + jql.$() + "\tIs Optional Element: " + isOptionalElement;
  }
}
