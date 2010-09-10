package uk.me.candle.translations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Caches Bundle instances based on their Class and Locale.
 *
 *
 *
 * This class is intended to be thread-safe.
 *
 * @author Andrew Wheat
 */
public class BundleCache {

	private static final ThreadLocal<Locale> tlsLocale = new InheritableThreadLocal<Locale>();

	public static Locale getThreadLocale() {
		if (tlsLocale.get() == null) {
			setThreadLocale(Locale.getDefault());
		}
		return tlsLocale.get();
	}

	public static void setThreadLocale(Locale locale) {
		tlsLocale.set(locale);
	}

	private static final Map<Class<? extends Bundle>, Map<Locale, Bundle>> cache = new HashMap<Class<? extends Bundle>, Map<Locale, Bundle>>();

	public static synchronized <T extends Bundle> T get(Class<T> cls) {
		return get(cls, getThreadLocale());
	}

	@SuppressWarnings("unchecked") // cast in the return is safe because T is defined in the method decleration.
	public static synchronized <T extends Bundle> T get(Class<T> cls, Locale locale) {
		if (!cache.containsKey(cls)) {
			cache.put(cls, new HashMap<Locale, Bundle>());
		}
		if (!cache.get(cls).containsKey(locale)) {
			try {
				Bundle b = Bundle.load(cls, locale);
				cache.get(cls).put(locale, b);
			} catch (NoSuchMethodException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException(ex);
			} catch (InvocationTargetException ex) {
				throw new RuntimeException(ex);
			} catch (InstantiationException ex) {
				throw new RuntimeException(ex);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
		}

		return (T) cache.get(cls).get(locale);
	}
}
