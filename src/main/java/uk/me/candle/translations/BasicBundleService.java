package uk.me.candle.translations;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author andrew
 */
public class BasicBundleService implements BundleService {
	private final BundleConfiguration configuration;
	private Locale current;

	public BasicBundleService(BundleConfiguration configuration) {
		this.configuration = configuration;
	}

	public BasicBundleService(BundleConfiguration configuration, Locale current) {
		this.configuration = configuration;
		this.current = current;
	}
	
	Map<Pair<Class<? extends Bundle>, Locale>, Bundle> cache;

	@Override
	public <T extends Bundle> T get(Class<T> bundleClass) {
		return get(bundleClass, current);
	}

	@Override
	public <T extends Bundle> T get(Class<T> bundleClass, Locale locale) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
