package com.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public class CustomPageFactory {

  public static void initElements(final WebDriver driver, final Object page) {
    final Map<String, List<WebElement>> locatedFields = initElements(new CustomElementLocatorFactory(driver), page);
    initElements(new ChildElementLocatorFactory(locatedFields), page);
  }
  public static void initElements(final ChildElementLocatorFactory factory, final Object page) {
    PageFactory.initElements(new DefaultFieldDecorator(factory), page);
  }

  public static Map<String, List<WebElement>> initElements(final CustomElementLocatorFactory factory, final Object page) {
    return initElements(new CustomFieldDecorator(factory), page);
  }

  public static Map<String, List<WebElement>> initElements(final CustomFieldDecorator decorator, final Object page) {
    Map<String, List<WebElement>> locatedFields = new HashMap<>();
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class) {
      locatedFields = mergeValues(locatedFields, proxyFields(decorator, page, proxyIn));
      proxyIn = proxyIn.getSuperclass();
    }
    return locatedFields;
  }

  private static Map<String, List<WebElement>> mergeValues(final Map<String, List<WebElement>> foundInSubclasses,
      final Map<String, List<WebElement>> currentlyLocated) {
    if (foundInSubclasses.entrySet().size() == 0) {
      return currentlyLocated;
    }

    for (final Map.Entry<String, List<WebElement>> entry : foundInSubclasses.entrySet()) {
      if (currentlyLocated.containsKey(entry.getKey())) {
        final List<WebElement> locatedInSubclasses = entry.getValue();
        final List<WebElement> justFound = currentlyLocated.get(entry.getKey());
        locatedInSubclasses.addAll(justFound);
      } else {
        entry.setValue(currentlyLocated.get(entry.getKey()));
      }
    }
    return foundInSubclasses;
  }

  private static Map<String, List<WebElement>> proxyFields(final FieldDecorator decorator, final Object page,
      final Class<?> proxyIn) {
    final Map<String, List<WebElement>> locatedFields = new HashMap<>();

    final Field[] fields = proxyIn.getDeclaredFields();
    for (final Field field : fields) {
      final Object value = decorator.decorate(page.getClass().getClassLoader(), field);
      if (value != null) {
        try {
          field.setAccessible(true);
          field.set(page, value);
          if (locatedFields.containsKey(field.getName())) {
            if (!(value instanceof List)) {
              locatedFields.get(field.getName()).add((WebElement) value);
            }
          } else {
            if (!(value instanceof List)) {
              locatedFields.put(field.getName(), new ArrayList<WebElement>(Arrays.asList((WebElement) value)));
            }
          }
        } catch (final IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return locatedFields;
  }
}
