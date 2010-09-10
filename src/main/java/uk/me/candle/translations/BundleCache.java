package uk.me.candle.translations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
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
	static Map<Class<? extends Bundle>, Map<Locale, Bundle>> cache = new HashMap<Class<? extends Bundle>, Map<Locale, Bundle>>();

	public static <T extends Bundle> T get(Class<T> cls) {
		return get(cls, getThreadLocale());
	}

	@SuppressWarnings("unchecked") // cast in the return is safe.
	public static <T extends Bundle> T get(Class<T> cls, Locale locale) {
		if (!cache.containsKey(cls)) {
			cache.put(cls, new HashMap<Locale, Bundle>());
		}
		if (!cache.get(cls).containsKey(locale)) {
			try {
				Bundle b = Bundle.load(cls, locale);
				cache.get(cls).put(locale, b);
			} catch (NoSuchMethodException ex) {
				throw new Error(ex);
			} catch (IOException ex) {
				throw new Error(ex);
			} catch (IllegalArgumentException ex) {
				throw new Error(ex);
			} catch (InvocationTargetException ex) {
				throw new Error(ex);
			} catch (InstantiationException ex) {
				throw new Error(ex);
			} catch (IllegalAccessException ex) {
				throw new Error(ex);
			}
		}

		return (T) cache.get(cls).get(locale);
	}
}
