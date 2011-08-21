package uk.me.candle.translations;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.translations.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.BundleConfiguration.IgnoreMissing;
import uk.me.candle.translations.BundleConfiguration.IgnoreParameterMisMatch;

/**
 * Handles translations.
 *
 * General usage notes:
 *
 * <ul>
 * <li>The bundle class must be public and abstract</li>
 * <li>for methods with no arguments the raw translation is returned.</li>
 * <li>Methods with arguments are passed to MessageFormat to format with the translation and the
 * arguments of the method as the parameters of the MessageFormat</li>
 * <li>Methods must be abstract and have a String return value.</li>
 * <li>Classes must have an accessible constructor that takes a java.util.Locale.</li>
 * </ul>
 *
 * Bundle class:
 * <pre>{@code
 *package com.example.foo
 *public abstract class Foo extends Bundle {
 *  public static Foo get() {
 *	   return BundleCache.get(Foo.class);
 *  }
 *  public static Foo get(Locale locale) {
 *	   return BundleCache.get(Foo.class, locale);
 *  }
 *  public Foo(Locale locale) {
 *	   super(locale);
 *  }
 *  public abstract String bar();
 *  public abstract String zit();
 *  public abstract String pony(String s);
 *  public abstract String iHaveSomeOranges(int i);
 *  public abstract String iHaveAFewArguments(Object o, boolean z, byte b, char c, short s, int i, long l, float f, double d);
 *}
 * }</pre>
 * Bundle resource:
 * <pre>{@code
 *bar=Moe's Tavern
 *zit=zap
 *pony=my horsie''s name is {0}
 *iHaveSomeOranges=I have {0,choice,0#are no oranges|1# one orange|1&lt;are {0,number,integer} oranges}.
 *iHaveAFewArguments=o={0} z={1} b={2} c={3} s={4} i={5} l={6} f={7} d={8}
 * }</pre>
 * Observe that it is the the MessageFormat syntax requires '' to output a single ' character.<br /><br />
 *
 * When the class is loaded (without a properties specified in the load() method)  it will look for a properties
 * file in the same package as the class, with the same name as the class and
 * the language code appended to the end.
 *
 * In the above example, the Foo bundle will look for the English properties file at
 * {@code /com/example/foo/Foo_en.properties }
 * the French will be:
 * {@code /com/example/foo/Foo_fr.properties }
 *
 * Usage of the bundle is intended to be as simple as possible.
 * <pre>{@code
 *public class App {
 *
 *  public static void main(String[] args) {
 *    BundleCache.setThreadLocale(Locale.ENGLISH);
 *    System.out.println("I know a bar called " + Foo.get().bar());
 *    System.out.println(Foo.get().pony("Sparky"));
 *    System.out.println(Foo.get().iHaveSomeOranges(5));
 *    System.out.println(Foo.get().iHaveAFewArguments("obj", true, (byte)4, 'q', (short)6, 1, 9999, 9.5f, 4.4d));
 *  }
 *}
 * }</pre>
 *
 * This will produce the output:
 * <pre>{@code
 *I know a bar called Moe's Tavern
 *my horsie's name is Sparky
 *I have one orange.
 *o=obj z=true b=4 c=q s=6 i=1 l=9,999 f=9.5 d=4.4
 * }</pre>
 *
 * A unit test for the bundle could be constructed using:
 * <pre>{@code
 *	&#40;Test
 *	public void testFooBundle_en() throws Exception {
 *		// Load the bundle with the check flags enabled.
 *		// this will throw an exception if there is an error
 *		// with either the abstract Class or the properties file
 *		Foo foo = Bundle.load(Foo.class, Locale.ENGLISH,
 *				LoadIgnoreMissing.NO,
 *				LoadIgnoreExtra.NO,
 *				LoadIgnoreParameterMisMatch.NO,
 *				AllowDefaultLanguage.NO);
 *		assertNotNull(foo.bar());
 *	}
 * }</pre>
 *
 * Using the above bundle, the 'bar' method would be implemented as:
 * <pre>{@code
 * public String bar() {
 *   return "Moe's Tavern";
 * }
 * }</pre>
 *
 * The 'pony' method would be implemented as:
 * <pre>{@code
 * public String pony(String s) {
 *   MessageFormat m = new MessageFormat("my horsie''s name is {0}", getLocale());
 *   return m.format(new Object[]{s});
 * }
 * }</pre>
 *
 * @see java.text.MessageFormat
 * @author Andrew Wheat
 */
public class Bundle {
	/**
	 * The locale for this bundle. Required for formatting numbers in the subclasses.
	 */
	private final Locale locale;

	public Locale getLocale() {
		return locale;
	}

	public Bundle(Locale locale) {
		if (locale == null) {
			throw new NullPointerException("The locale cannot be null");
		}
		this.locale = locale;
	}
}
