package uk.me.candle.translations.example;

import java.util.Locale;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew Wheat
 */
public abstract class Foo extends Bundle {

	public static Foo get() {
		return BundleCache.get(Foo.class);
	}

	public static Foo get(Locale locale) {
		return BundleCache.get(Foo.class, locale);
	}

	public Foo(Locale locale) {
		super(locale);
	}

	public abstract String bar();
	public abstract String zit();
	public abstract String pony(String s);
	public abstract String iHaveSomeOranges(int i);
	public abstract String iHaveAFewArguments(Object o, boolean z, byte b, char c, short s, int i, long l, float f, double d);
}
