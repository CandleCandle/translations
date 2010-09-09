package uk.me.candle.translations.bundle;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew Wheat
 */
public abstract class Names extends Bundle {

	public static Names get() {
		//return new Names$$Impl();
		return BundleCache.get(Names.class, Locale.ENGLISH);
	}

	public Names(Locale locale) {
		super(locale);
	}

	public abstract String title();

	public abstract String username();

	public abstract String password();

	public abstract String param(Object o);

	public abstract String params(Object o1, Object o2);

	public abstract String params3(Object o1, Object o2, Object o3);

	public abstract String paramsTons(
			Object o1, Object o2, Object o3,
			Object o4, Object o5, Object o6,
			Object o7, Object o8, Object o9,
			Object o10, Object o11, Object o12,
			Object o13, Object o14, Object o15
			);
}
