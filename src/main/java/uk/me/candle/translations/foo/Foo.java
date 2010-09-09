package uk.me.candle.translations.foo;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew Wheat
 */
public abstract class Foo extends Bundle {

	public static Foo get() {
		//return new Names$$Impl();
		return BundleCache.get(Foo.class);
	}

	public Foo(Locale locale) {
		super(locale);
	}

	public abstract String bar();
}
