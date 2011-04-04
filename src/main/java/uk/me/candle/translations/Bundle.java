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
	private static final Logger LOG = LoggerFactory.getLogger(Bundle.class);
	private static final BundleClassLoader BUNDLE_CLASS_LOADER = new BundleClassLoader();
  
	public enum LoadIgnoreMissing { YES, NO };
	public enum LoadIgnoreExtra { YES, NO };
	public enum LoadIgnoreParameterMisMatch { YES, NO };
	public enum AllowDefaultLanguage { YES, NO };

	/**
	 * The locale for this bundle. Required for formatting numbers in the subclasses.
	 */
	private final Locale locale;
	/**
	 *
	 * default value for new creations if the options are not specified
	 */
	public static LoadIgnoreMissing LOAD_IGNORE_MISSING = LoadIgnoreMissing.YES;
	public static LoadIgnoreExtra LOAD_IGNORE_EXTRA = LoadIgnoreExtra.YES;
	public static LoadIgnoreParameterMisMatch LOAD_IGNORE_PARAM_MISMATCH = LoadIgnoreParameterMisMatch.YES;
	public static AllowDefaultLanguage LOAD_ALLOW_DEFAULT = AllowDefaultLanguage.YES;

	public Locale getLocale() {
		return locale;
	}

	public Bundle(Locale locale) {
		if (locale == null) {
			throw new NullPointerException("The locale cannot be null");
		}
		this.locale = locale;
	}
	
	/**
	 * Constructs a bundle implementation for the class and locale.
	 * @throws MissingResourceException if the resource cannot be found.
	 * @throws IOException if there is an error loading the properties
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, boolean, boolean, boolean)
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, LOAD_IGNORE_MISSING, LOAD_IGNORE_EXTRA, LOAD_IGNORE_PARAM_MISMATCH, LOAD_ALLOW_DEFAULT);
	}
	static <T extends Bundle> T load(Class<T> cls, Locale locale, BundleConfiguration configuration)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale,
				getBundleProperties(cls, locale, configuration)
				, configuration);
	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified translations file.
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, boolean, boolean, boolean)
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(LOAD_IGNORE_MISSING, LOAD_IGNORE_EXTRA, LOAD_IGNORE_PARAM_MISMATCH, LOAD_ALLOW_DEFAULT));
	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified options.
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, uk.me.candle.translations.Bundle.LoadIgnoreMissing, uk.me.candle.translations.Bundle.LoadIgnoreExtra, uk.me.candle.translations.Bundle.LoadIgnoreParameterMisMatch)
	 */
	public static <T extends Bundle> T load(Class<T> cls, Locale locale, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch));

	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified options.
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, uk.me.candle.translations.Bundle.LoadIgnoreMissing, uk.me.candle.translations.Bundle.LoadIgnoreExtra, uk.me.candle.translations.Bundle.LoadIgnoreParameterMisMatch)
	 */
	public static <T extends Bundle> T load(Class<T> cls, Locale locale, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch, AllowDefaultLanguage allowDefaultLanguage)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch, allowDefaultLanguage));

	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified translations file.
	 *
	 *
	 * @param <T> The bundle type.
	 * @param cls the class of the bundle.
	 * @param locale the requested locale for the bundle.
	 * @param translations properties representing the translations.
	 * @param ignoreMissing true if missing keys are to be ignored.
	 * @param ignoreExtra true if extra keys are to be ignored.
	 * @param ignoreParamMismatch true if parameter mismatches are to be ignored.
	 *
	 * @return an implementation of the abstract class
	 *
	 * @throws MissingResourceException if the resource cannot be found, or there is: a missing key, a mismatch between the method parameters and the field entries in the pattern or there are extra keys that are not used in the bundle properties.
	 * @throws IllegalAccessException if the bundle class is not public and abstract
	 * @throws InstantiationException if the new bundle cannot be created
	 * @throws NoSuchMethodException if there is no available constructor
	 * @throws IOException if the source class cannot be loaded.
	 * @throws IllegalArgumentException 
	 * @throws InvocationTargetException if the constructor for the bundle throws an exception.
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch));
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch, AllowDefaultLanguage allowDefaultLanguage)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch, allowDefaultLanguage));
	}

	private static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, BundleConfiguration configuration)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		final Set<String> usedKeys = new HashSet<String>();

		ClassReader cr = new ClassReader(cls.getName());
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ImplementMethodsAdapter ca = new ImplementMethodsAdapter(cw, translations, usedKeys, locale, configuration);
		cr.accept(ca, 0);
		byte[] b2 = cw.toByteArray();

		if (!configuration.getIgnoreExtra().equals(LoadIgnoreExtra.YES)) {
			Set<String> extras = checkForExtras(translations, usedKeys);

			if (!extras.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String s : extras) {
					if (sb.length() != 0) {
						sb.append(", ");
					}
					sb.append(s);
				}
				throw new MissingResourceException("Extra keys in the bundle: " + sb.toString(), cls.getName(), sb.toString());
			}
		}

		Class<?> result = BUNDLE_CLASS_LOADER.defineClass(ca.getNewName().replace("/", "."), b2);

		Constructor c = result.getConstructor(new Class[]{Locale.class});
		return (T) c.newInstance(locale);
	}


	private static <T extends Bundle> Properties getBundleProperties(Class<T> clz, Locale locale, BundleConfiguration configuration) throws IOException, MissingResourceException {
		if (configuration.getAllowDefaultLanguage() == AllowDefaultLanguage.YES) {
			return getBundlePropertiesWithDefaults(clz, locale);
		} else {
			return getBundlePropertiesExact(clz, locale);
		}
	}
	private static <T extends Bundle> Properties getBundlePropertiesExact(Class<T> clz, Locale locale) throws IOException, MissingResourceException {
		StringBuilder sb = new StringBuilder();
		sb.append(clz.getPackage().getName().replace(".", "/"));
		sb.append("/");
		sb.append(clz.getSimpleName());
		if (!locale.getLanguage().isEmpty()) {
			sb.append("_");
			sb.append(locale.getLanguage().toLowerCase());
			if (!locale.getCountry().isEmpty()) {
				sb.append("_");
				sb.append(locale.getCountry().toLowerCase());
				if (!locale.getVariant().isEmpty()) {
					sb.append("_");
					sb.append(locale.getVariant().toLowerCase());
				}
			}
		}
		// this uses the classloader from the bundle class so it should avoid spurious
		// classloader issues. The properties should, therefrore, be available from the
		// classloader as the bundle class.
		InputStream main = clz.getClassLoader().getResourceAsStream(sb.toString() + ".properties");
		if (main == null) {
			throw new MissingResourceException("There was no resource for the path: " + sb.toString(), clz.getName(), "");
		}
		Properties props = new Properties();
		props.load(main);
		return props;
	}
	/**
	 * Loads the property file if it exists into the property values,
	 * @param <T> bundle type
	 * @param from existing properties, these act as defaults.
	 * @param clz class that is being used as the translations template.
	 * @param propertiesReference the start of the properties resource (missing the '.properties')
	 * @return the properties file that should be used as translations.
	 * @throws IOException
	 */
	private static <T extends Bundle> Properties appendValuesFrom(
			Properties from,
			Class<T> clz,
			String propertiesReference) throws IOException {
		LOG.debug("Attempting to add properties from: {}.properties", propertiesReference);
		Properties ret = new Properties();
		ret.putAll(from);
		InputStream in = clz.getClassLoader().getResourceAsStream(propertiesReference + ".properties");
		if (in != null) {
			LOG.debug("    Found.");
			Properties newProps = new Properties();
			newProps.load(in);
			ret.putAll(newProps);
		} else {
			LOG.debug("    Properties file not found.");
		}
		return ret;
	}

	private static <T extends Bundle> Properties getBundlePropertiesWithDefaults(Class<T> clz, Locale locale) throws IOException {
		LOG.debug("Fetching bundle [default allowed] for {} , {}", clz, locale);
		StringBuilder sb = new StringBuilder();
		sb.append(clz.getPackage().getName().replace(".", "/"));
		sb.append("/");
		sb.append(clz.getSimpleName());

		Properties p = new Properties(); // this is what will be returned.
		p = appendValuesFrom(p, clz, sb.toString());
		sb.append("_");
		sb.append(locale.getLanguage().toLowerCase(Locale.ENGLISH));
		p = appendValuesFrom(p, clz, sb.toString());
		sb.append("_");
		sb.append(locale.getCountry().toLowerCase(Locale.ENGLISH));
		p = appendValuesFrom(p, clz, sb.toString());
		sb.append("_");
		sb.append(locale.getVariant().toLowerCase(Locale.ENGLISH));
		return appendValuesFrom(p, clz, sb.toString());
	}

	/**
	 * Compares the used keys and translations for extra translations.
	 * @param translations
	 * @param usedKeys
	 */
	private static Set<String> checkForExtras(Properties translations, Set<String> usedKeys) {
		Set<String> extras = new HashSet<String>(translations.stringPropertyNames());
		extras.removeAll(usedKeys);
		return extras;
	}
}
