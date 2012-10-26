package com.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import com.example.annotations.JQueryLocator;

public class CustomFieldDecorator extends DefaultFieldDecorator implements FieldDecorator {

  public CustomFieldDecorator(final CustomElementLocatorFactory factory) {
    super(factory);
  }

  @Override
  public Object decorate(final ClassLoader loader, final Field field) {
    if (!(WebElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field))) {
      return null;
    }

    final CustomElementLocator locator = (CustomElementLocator) factory.createLocator(field);
    if (locator == null) {
      return null;
    }

    if (WebElement.class.isAssignableFrom(field.getType())) {
      final WebElement proxy = proxyForLocator(loader, locator);
      try {
        proxy.isDisplayed();
        return proxy;
      } catch (final NoSuchElementException nsee) {
        if (!locator.isOptionalElement()) {
          throw nsee;
        }
        return new MissingWebElement(nsee);
      }
    } else if (List.class.isAssignableFrom(field.getType())) {
      return proxyForListLocator(loader, locator);
    } else {
      return null;
    }
  }

  private boolean isDecoratableList(final Field field) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    // Type erasure in Java isn't complete. Attempt to discover the generic
    // type of the list.
    final Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) {
      return false;
    }

    final Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

    if (!WebElement.class.equals(listType)) {
      return false;
    }

    if (field.getAnnotation(FindBy.class) == null && field.getAnnotation(FindBys.class) == null
        && field.getAnnotation(JQueryLocator.class) == null) {
      return false;
    }

    return true;
  }

  @Override
  protected WebElement proxyForLocator(final ClassLoader loader, final ElementLocator locator) {
    final InvocationHandler handler = new CustomLocatingElementHandler((CustomElementLocator) locator);

    WebElement proxy;
    proxy = (WebElement) Proxy.newProxyInstance(loader, new Class[] { WebElement.class, WrapsElement.class,
        Locatable.class }, handler);
    return proxy;
  }

}
