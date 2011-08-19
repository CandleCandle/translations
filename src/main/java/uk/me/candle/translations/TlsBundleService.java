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
public class TlsBundleService implements BundleService {

	private static final ThreadLocal<Locale> tlsLocale = new InheritableThreadLocal<Locale>() {
		@Override protected Locale initialValue() {
			return Locale.getDefault();
		}
	};

	public static Locale getThreadLocale() {
		return tlsLocale.get();
	}

	public static void setThreadLocale(Locale locale) {
		tlsLocale.set(locale);
	}

	private final Map<Class<? extends Bundle>, Map<Locale, Bundle>> cache = new HashMap<Class<? extends Bundle>, Map<Locale, Bundle>>();

  @Override
	public synchronized <T extends Bundle> T get(Class<T> cls) {
		return get(cls, getThreadLocale());
	}

	@SuppressWarnings("unchecked") // cast in the return is safe because T is defined in the method decleration.
  @Override
	public synchronized <T extends Bundle> T get(Class<T> cls, Locale locale) {
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
