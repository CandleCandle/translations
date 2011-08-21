package uk.me.candle.translations.service;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author andrew
 */
public interface BundleService {
	<T extends Bundle> T get(Class<T> bundleClass);
	<T extends Bundle> T get(Class<T> bundleClass, Locale locale);
}
