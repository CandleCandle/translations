package uk.me.candle.translations.conf;

import java.util.Locale;

/**
 * @author andrew
 */
public interface BundleConfiguration {

	public enum IgnoreMissing { YES, NO };
	public enum IgnoreExtra { YES, NO };
	public enum IgnoreParameterMisMatch { YES, NO };
	public enum AllowDefaultLanguage { YES, NO };

	/**
	 * This is used in conjunction with the AllowDefaultLanguage parameter to
	 * provide the default translations.
	 */
	Locale getDefaultLocale();

	/**
	 * If this is 'YES' then keys that are defined in the class and not defined
	 * in the properties file will default to the bundle key name.
	 * If this is 'NO' then the an exception is thrown if there is a method
	 * defined and there is no key/value pair in the translations.
	 */
	IgnoreMissing getIgnoreMissing();

	/**
	 * If this is 'YES' then extra keys in the properties file are ignored.
	 * If this is 'NO' then an exception is thrown when building the bundle.
	 */
	IgnoreExtra getIgnoreExtra();

	/**
	 * If this is 'YES' then values and methods can have different parameter
	 * lengths: for example; a method:
	 * named(String name, int number)
	 * can have a key/value pair of
	 * name: {0}
	 * or it may have a value of:
	 * name: {0}; number: {1}; description: {2}
	 * 
	 * If this is 'NO' then the values and methods must have the same number of
	 * parameters. Using the above example, every value must use exactly the
	 * same number of parameters. An exception is thrown when there is a mismatch
	 * in this case.
	 */
	IgnoreParameterMisMatch getIgnoreParameterMisMatch();

	/**
	 * If this is 'YES' then the properties that is used is built up, starting
	 * with the default language, and replacing values with more specific
	 * values from the more specific properties files.
	 * 
	 * For example:
	 * Norway has a locale with a specified language, country and variant.
	 * Language: "no", Country: "NO", Variant: "NY"
	 * Bundle name: Translations
	 * Default Language: English.
	 * 
	 * On looking up a key, the most specific key value is used. In this example
	 * the ordering is as such:
	 * Translations_no_no_ny.properties
	 * Translations_no_no.properties
	 * Translations_no.properties
	 * Translations_en.properties
	 * 
	 * If this parameter is 'NO' then only the exact properties file is used.
	 */
	AllowDefaultLanguage getAllowDefaultLanguage();
}
