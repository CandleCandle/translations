package uk.me.candle.translations;

import java.text.MessageFormat;
import java.util.Locale;
import uk.me.candle.translations.bundle.Names;
import uk.me.candle.translations.foo.Foo;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {

		System.out.println(MessageFormat.format("this''s {0}", "screwed?"));

//		System.out.println(Locale.UK);
//		System.out.println(Locale.ENGLISH);
//		System.out.println(Locale.FRANCE);
//		System.out.println(Locale.FRENCH);
//
//		BundleCache.setThreadLocale(Locale.ENGLISH);
//
//		System.out.println("trn:" + Foo.get().bar());
//
//		System.out.println("Title trn : " + Names.get().title());
//		System.out.println("Password trn : " + Names.get().password());
//		System.out.println("Username trn : " + Names.get().username());
//		System.out.println("One param trn : " + Names.get().param("some"));
//		System.out.println("Two params trn : " + Names.get().params("some", "params"));
//		System.out.println("Three params trn : " + Names.get().params3("some", "params", "and another"));
//		int i = 0;
//		System.out.println("Three params trn : " + Names.get().paramsTons(
//				i++, i++, i++,
//				i++, i++, i++,
//				i++, i++, i++,
//				i++, i++, i++,
//				i++, i++, i++
//				));
	}
}
