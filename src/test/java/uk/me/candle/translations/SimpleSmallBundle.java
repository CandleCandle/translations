package uk.me.candle.translations;

import java.util.Locale;

/**
 *
 * @author Andrew
 */
public abstract class SimpleSmallBundle extends Bundle {
	public static SimpleSmallBundle get() {
		return BundleCache.get(SimpleSmallBundle.class);
	}

	public static SimpleSmallBundle get(Locale locale) {
		return BundleCache.get(SimpleSmallBundle.class, locale);
	}

	public SimpleSmallBundle(Locale locale) {
		super(locale);
	}

	public abstract String simple();
	public abstract String simpleOne(int i);
	public abstract String defaultOnly();
	public abstract String defaultBg();
	public abstract String defaultBgJa();
	public abstract String defaultBgJaJp();
	public abstract String defaultBgJaJpJp();
}