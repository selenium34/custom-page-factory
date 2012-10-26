package com.example;

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import com.example.annotations.ChildOf;

public class CustomElementLocatorFactory implements ElementLocatorFactory {

  private final SearchContext searchContext;

  public CustomElementLocatorFactory(final SearchContext searchContext) {
    this.searchContext = searchContext;
  }

  @Override
  public ElementLocator createLocator(final Field field) {
    if (!field.isAnnotationPresent(ChildOf.class)) {
      return new CustomElementLocator(searchContext, field);
    }
    return null;
  }

}
