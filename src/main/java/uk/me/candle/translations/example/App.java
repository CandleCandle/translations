package uk.me.candle.translations.example;

import java.util.Locale;
import uk.me.candle.translations.BundleCache;

/**
 *
 * @author Andrew Wheat
 */
public class App {

	public static void main(String[] args) {
		BundleCache.setThreadLocale(Locale.ENGLISH);
		System.out.println("I know a bar called " + Foo.get().bar());
		System.out.println(Foo.get().pony("Sparky"));
		System.out.println(Foo.get().iHaveSomeOranges(5));
		System.out.println(Foo.get().iHaveAFewArguments("obj", true, (byte)4, 'q', (short)6, 1, 9999, 9.5f, 4.4d));
	}
}

