package uk.me.candle.translations.bundle;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew Wheat
 */
public abstract class Names extends Bundle {
  public static Names get() {
    //return new Names$$Impl();
    return BundleCache.get(Names.class, Locale.ENGLISH);
  }

  public abstract String title();
  public abstract String username();
  public abstract String password();
  public abstract String param(Object o);
  public abstract String param(Object o1, Object o2);

}
