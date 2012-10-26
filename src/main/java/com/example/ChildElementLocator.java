package com.example;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.example.annotations.ChildOf;

public class ChildElementLocator implements ElementLocator {

  private final List<WebElement> parentCandidates;
  private final How how;
  private final String using;

  public ChildElementLocator(final Map<String, List<WebElement>> parentCandidates, final Field field) {
    final ChildOf childOf = field.getAnnotation(ChildOf.class);
    this.how = childOf.how();
    if (How.ID_OR_NAME.equals(how)) {
      this.using = field.getName();
    } else {
      this.using = childOf.using();
    }
    if (!parentCandidates.containsKey(childOf.fieldName())) {
      throw new NoSuchElementException("No parents with the field name '" + childOf.fieldName()
          + "' were located. Unable to locate element with: " + how + " using: " + using);
    }
    this.parentCandidates = parentCandidates.get(childOf.fieldName());
  }

  @Override
  public WebElement findElement() {
    NoSuchElementException finalException = null;
    for (final WebElement candidate : parentCandidates) {
      try {
        final WebElement e = candidate.findElement(buildBy());
        e.isDisplayed();
        return e;
      } catch (final NoSuchElementException nsee) {
        finalException = nsee;
      }
    }
    throw finalException;
  }

  @Override
  public List<WebElement> findElements() {
    for (final WebElement candidate : parentCandidates) {
      final List<WebElement> elements = candidate.findElements(buildBy());
      if (elements.size() > 0) {
        return elements;
      }
    }
    return Collections.emptyList();
  }

  private By buildBy() {
    switch (how) {
      case CLASS_NAME:
        return By.className(using);

      case CSS:
        return By.cssSelector(using);

      case ID:
        return By.id(using);

      case ID_OR_NAME:
        return new ByIdOrName(using);

      case LINK_TEXT:
        return By.linkText(using);

      case NAME:
        return By.name(using);

      case PARTIAL_LINK_TEXT:
        return By.partialLinkText(using);

      case TAG_NAME:
        return By.tagName(using);

      case XPATH:
        return By.xpath(using);

      default:
        throw new IllegalArgumentException("A new locator type has been created, and is not accounted for: " + how);
    }
  }
}
