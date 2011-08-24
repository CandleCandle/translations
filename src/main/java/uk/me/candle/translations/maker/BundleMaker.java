package uk.me.candle.translations.maker;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.conf.BundleConfiguration;
import uk.me.candle.translations.conf.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.BundleCreationException;

/**
 *
 * @author Andrew
 */
public final class BundleMaker {
	private static final Logger LOG = LoggerFactory.getLogger(BundleMaker.class);
	private static BundleClassLoader bundleClassLoader = new BundleClassLoader();

	private BundleMaker() {
		throw new AssertionError("Must not call this constructor");
	}

	public static <T extends Bundle> T load(
		Class<T> cls,
		Locale locale,
		BundleConfiguration configuration
		) {
		try {
			return load(
				cls,
				locale,
				getBundleProperties(cls, locale, configuration),
				configuration
				);
		} catch (IOException ioe) {
			throw new BundleCreationException(ioe.getMessage(), ioe);
		}
	}
	@SuppressWarnings("unchecked") // the return statement. it's safe.
	public static <T extends Bundle> T load(
		Class<T> cls,
		Locale locale,
		Properties translations,
		BundleConfiguration configuration
		) {
		final String newName = getClassNameFor(cls, locale);

		if (bundleClassLoader.isClassDefined(newName)) {
			Class<?> result = bundleClassLoader.defineClass(newName, new byte[]{});
			return getInstance((Class<T>)result, locale);
		}

		final Set<String> usedKeys = new HashSet<String>();

		final ClassReader cr;
		try {
			cr = new ClassReader(cls.getName());
		} catch (IOException ioe) {
			throw new BundleCreationException(ioe.getMessage(), ioe);
		}
		final ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		final ImplementMethodsAdapter ca = new ImplementMethodsAdapter(cw, translations, usedKeys, locale, configuration);
		cr.accept(ca, 0);

		final byte[] b2 = cw.toByteArray();

		if (!configuration.getIgnoreExtra().equals(IgnoreExtra.YES)) {
			final Set<String> extras = checkForExtras(translations, usedKeys);

			if (!extras.isEmpty()) {
				final StringBuilder sb = new StringBuilder();
				for (String s : extras) {
					if (sb.length() != 0) {
						sb.append(", ");
					}
					sb.append(s);
				}
				throw new MissingResourceException("Extra keys in the bundle: " + sb.toString(), cls.getName(), sb.toString());
			}
		}

		Class<?> result = bundleClassLoader.defineClass(newName, b2);

		return getInstance((Class<T>)result, locale);
	}
	private static <T extends Bundle> T getInstance(Class<T> clz, Locale locale) {
		try {
			Constructor<T> c = clz.getConstructor(new Class<?>[]{Locale.class});
			return c.newInstance(locale);
		} catch (InstantiationException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		} catch (InvocationTargetException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		} catch (NoSuchMethodException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		} catch (SecurityException ex) {
			throw new BundleCreationException(ex.getMessage(), ex);
		}
	}
	static <T extends Bundle> String getClassNameFor(Class<T> clz, Locale locale) {
		// if the method is being called from here then the returned value's packages will be '.' separated
		return getClassNameFor(clz.getName(), locale);
	}
	static <T extends Bundle> String getClassNameFor(String name, Locale locale) {
		// if the method is being called from here then the returned value's packages will be '/' separated
		StringBuilder sb = new StringBuilder(name);
		sb.append("__");
		sb.append(locale.getLanguage().toLowerCase(Locale.ENGLISH));
		if (!locale.getCountry().isEmpty()) {
			sb.append("__");
			sb.append(locale.getCountry().toLowerCase(Locale.ENGLISH));
			if (!locale.getVariant().isEmpty()) {
				sb.append("__");
				sb.append(locale.getLanguage().toLowerCase(Locale.ENGLISH));
			}
		}
		sb.append("__Impl");
		return sb.toString();
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
	private static <T extends Bundle> Properties getBundleProperties(Class<T> clz, Locale locale, BundleConfiguration configuration) throws IOException {
		if (configuration.getAllowDefaultLanguage() == AllowDefaultLanguage.YES) {
			return getBundlePropertiesWithDefaults(clz, locale);
		} else {
			return getBundlePropertiesExact(clz, locale);
		}
	}
	private static <T extends Bundle> Properties getBundlePropertiesExact(Class<T> clz, Locale locale) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(clz.getPackage().getName().replace(".", "/"));
		sb.append("/");
		sb.append(clz.getSimpleName());
		if (!locale.getLanguage().isEmpty()) {
			sb.append("_");
			sb.append(locale.getLanguage().toLowerCase(Locale.ENGLISH));
			if (!locale.getCountry().isEmpty()) {
				sb.append("_");
				sb.append(locale.getCountry().toLowerCase(Locale.ENGLISH));
				if (!locale.getVariant().isEmpty()) {
					sb.append("_");
					sb.append(locale.getVariant().toLowerCase(Locale.ENGLISH));
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

		Properties p = new Properties();
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
}
