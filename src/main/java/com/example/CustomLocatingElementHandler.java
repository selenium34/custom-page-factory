package com.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class CustomLocatingElementHandler implements InvocationHandler {

  private final CustomElementLocator locator;

  public CustomLocatingElementHandler(final CustomElementLocator locator) {
    this.locator = locator;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (proxy instanceof MissingWebElement) {
      System.out.println("Missing Web Element... returning null");
      throw ((MissingWebElement) proxy).getNoSuchElementException();
    }

    WebElement element = null;
    try {
      element = locator.findElement();
    } catch (final NoSuchElementException nsee) {
      System.out.println("Received exception looking for: " + locator + " returning null.");
      throw nsee;
    }

    if ("getWrappedElement".equals(method.getName())) {
      return element;
    }

    try {
      return method.invoke(element, args);
    } catch (final InvocationTargetException e) {
      throw e.getCause();
    }
  }

}
