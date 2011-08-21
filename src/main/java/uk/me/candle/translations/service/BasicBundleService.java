package uk.me.candle.translations.service;

import java.util.HashMap;
import uk.me.candle.translations.conf.BundleConfiguration;
import java.util.Locale;
import java.util.Map;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.Pair;
import uk.me.candle.translations.maker.BundleMaker;

/**
 *
 * @author andrew
 */
public final class BasicBundleService implements BundleService {
	private final BundleConfiguration configuration;
	private Locale current;

	private final Map<Pair<Class<? extends Bundle>, Locale>, Bundle> cache;

	public BasicBundleService(BundleConfiguration configuration) {
		this(configuration, Locale.getDefault());
	}

	public BasicBundleService(BundleConfiguration configuration, Locale current) {
		this.configuration = configuration;
		this.current = current;
		this.cache = new HashMap<Pair<Class<? extends Bundle>, Locale>, Bundle>();
	}

	@Override
	public <T extends Bundle> T get(Class<T> bundleClass) {
		return get(bundleClass, current);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Bundle> T get(Class<T> bundleClass, Locale locale) {
		Pair<Class<? extends Bundle>, Locale> key = new Pair<Class<? extends Bundle>, Locale>(bundleClass, locale);
		if (!cache.containsKey(key)) {
			cache.put(key, BundleMaker.load(bundleClass, locale, configuration));
		}
		return (T)cache.get(key);
	}
}
