package uk.me.candle.translations.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import uk.me.candle.translations.conf.BundleConfiguration;
import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.maker.BundleMaker;

/**
 *
 * @author andrew
 */
public final class BasicBundleService implements BundleService {
	private final BundleConfiguration configuration;
	private Locale current;

	private final Table<Class<? extends Bundle>, Locale, Bundle> cache;

	public BasicBundleService(BundleConfiguration configuration) {
		this(configuration, Locale.getDefault());
	}

	public BasicBundleService(BundleConfiguration configuration, Locale current) {
		this.configuration = configuration;
		this.current = current;
		this.cache = HashBasedTable.create();
	}

	@Override
	public <T extends Bundle> T get(Class<T> bundleClass) {
		return get(bundleClass, current);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Bundle> T get(Class<T> bundleClass, Locale locale) {
		if (!cache.contains(bundleClass, locale)) {
			cache.put(bundleClass, locale, BundleMaker.load(bundleClass, locale, configuration));
		}
		return (T)cache.get(bundleClass, locale);
	}
}
