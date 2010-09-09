package com.patchmanager.experiments.translations.foo;

import com.patchmanager.experiments.translations.Bundle;
import com.patchmanager.experiments.translations.BundleCache;
import java.util.Locale;

/**
 *
 * @author Andrew Wheat
 */
public abstract class Foo extends Bundle {
  public static Foo get() {
    //return new Names$$Impl();
    return BundleCache.get(Foo.class, Locale.ENGLISH);
  }

  public abstract String bar();
}
