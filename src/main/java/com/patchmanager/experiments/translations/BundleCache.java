package com.patchmanager.experiments.translations;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Andrew Wheat
 */
public class BundleCache {

  static Map<Class<? extends Bundle>, Map<Locale, Bundle>> cache = new HashMap<Class<? extends Bundle>, Map<Locale, Bundle>>();


  @SuppressWarnings("unchecked") // cast in the return is safe.
  public static <T extends Bundle> T get(Class<T> cls, Locale locale) {
    if (!cache.containsKey(cls)) {
      cache.put(cls, new HashMap<Locale, Bundle>());
    }
    if (!cache.get(cls).containsKey(locale)) {
      try {
        Bundle b = Bundle.load(cls, locale);
        cache.get(cls).put(locale, b);
      } catch (InstantiationException ex) {
        throw new Error(ex);
      } catch (IllegalAccessException ex) {
        throw new Error(ex);
      } catch (Exception e) {
        throw new Error(e);
      }
    }

    return (T) cache.get(cls).get(locale);
  }
}
