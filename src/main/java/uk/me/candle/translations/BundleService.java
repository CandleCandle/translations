package uk.me.candle.translations;

import java.util.Locale;

/**
 *
 * @author andrew
 */
public interface BundleService {
	<T extends Bundle> T get(Class<T> bundleClass);
	<T extends Bundle> T get(Class<T> bundleClass, Locale locale);
}
