package com.patchmanager.experiments.translations.bundle;

import com.patchmanager.experiments.translations.Bundle;
import com.patchmanager.experiments.translations.BundleCache;
import java.util.Locale;

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
