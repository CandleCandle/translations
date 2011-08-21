package uk.me.candle.translations.service;

import uk.me.candle.translations.conf.DefaultBundleConfiguration;
import uk.me.candle.translations.conf.BundleConfiguration;
import uk.me.candle.translations.maker.BundleMaker;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import uk.me.candle.translations.Bundle;

/**
 * Caches Bundle instances based on their Class and Locale.
 *
 *
 *
 * This class is intended to be thread-safe.
 *
 * @author Andrew Wheat
 */
public final class TlsBundleService implements BundleService {
	private final BundleConfiguration configuration;
	private final ThreadLocal<Locale> tlsLocale;

	public TlsBundleService() {
		this(new DefaultBundleConfiguration(), Locale.getDefault());
	}
	public TlsBundleService(BundleConfiguration configuration) {
		this(configuration, Locale.getDefault());
	}
	public TlsBundleService(BundleConfiguration configuration, Locale initial) {
		this.configuration = configuration;
		tlsLocale = new InheritableThreadLocalImpl(initial);
	}

	public synchronized Locale getThreadLocale() {
		return tlsLocale.get();
	}

	public synchronized void setThreadLocale(Locale locale) {
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
			Bundle b = BundleMaker.load(cls, locale, configuration);
			cache.get(cls).put(locale, b);
		}

		return (T) cache.get(cls).get(locale);
	}

	private static class InheritableThreadLocalImpl extends InheritableThreadLocal<Locale> {
		private final Locale initial;
		 InheritableThreadLocalImpl(Locale l) {
			this.initial = l;
		}
		@Override protected Locale initialValue() {
			return initial;
		}
	}
}
