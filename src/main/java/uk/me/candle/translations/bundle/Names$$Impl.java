package uk.me.candle.translations.bundle;

import java.text.MessageFormat;

/**
 *
 * @author Andrew Wheat
 */
public class Names$$Impl extends Names {

  @Override
  public String title() {
    return "test title";
  }

  @Override
  public String username() {
    return "test username";
  }

  @Override
  public String password() {
    return "test password";
  }

  @Override
  public String param(Object o) {
    return MessageFormat.format("test param; {0} first (and only) arg.", o);
  }

  @Override
  public String param(Object o1, Object o2) {
    return MessageFormat.format("test params; {0} first arg; {1} second arg.", o1, o2);
  }

}
