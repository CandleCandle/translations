package uk.me.candle.translations;

import uk.me.candle.translations.maker.BundleClassLoader;
import uk.me.candle.translations.conf.BundleConfiguration;
import uk.me.candle.translations.conf.DefaultBundleConfiguration;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Wheat
 */
public class BundleTest {
	private static final Logger LOG = LoggerFactory.getLogger(BundleTest.class);
	private static final BundleConfiguration configuration = DefaultBundleConfiguration.INSTANCE;

	@Before
	public void setup() {
		try {
			Field f = Bundle.class.getDeclaredField("BUNDLE_CLASS_LOADER");
			f.setAccessible(true);
			f.set(null, new BundleClassLoader());
		} catch (IllegalArgumentException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (NoSuchFieldException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (SecurityException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	@Test
	public void testNoParamsEn() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns, configuration);
		assertEquals("there are no parameters", b.noParams());
	}

	@Test
	public void testNoParamsFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("noParams", "Rien de truc");
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns, configuration);
		assertEquals("Rien de truc", b.noParams());
	}

	@Test(expected=MissingResourceException.class)
	public void testMissing() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = new Properties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns, configuration);
		assertEquals("there are no parameters", b.noParams());
	}

	@Test
	public void testMissingAllowed() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = new Properties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns,
				BundleConfiguration.IgnoreMissing.YES,
				BundleConfiguration.IgnoreExtra.YES,
				BundleConfiguration.IgnoreParameterMisMatch.YES
				);
		assertEquals("noParams", b.noParams());
	}

	@Test(expected=MissingResourceException.class)
	public void testParamMisMatchA() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("noParams", "{0}, {1}");
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns,
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO
				);
		assertEquals("there are no parameters", b.noParams());
	}

	@Test(expected=MissingResourceException.class)
	public void testParamMisMatchB() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("oneParam", "no parameters");
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns,
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO
				);
		assertEquals("no parameters", b.oneParam("ss"));
	}

	@Test
	public void testParamMisMatchBAllowed() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("oneParam", "no parameters");
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("no parameters", b.oneParam("ss"));
	}

	@Test(expected=MissingResourceException.class)
	public void testExtra() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("this is extra", "more then enough");
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns,
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO
				);
		assertEquals("there are no parameters", b.noParams());
	}

	@Test
	public void testOneParam() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("one parameter, and it is aa", b.oneParam((Object) "aa"));
	}

	@Test
	public void testTwoParams() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("two more params: aa bb", b.twoParams("aa", "bb"));
	}

	@Test
	public void testLotsOfParams() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("Fifteen params: a b c d e f g h i j k l m n o", b.lotsOfParams("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"));
	}

	@Test
	public void testIntegerObject() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("There are no elements.", b.integerObject(Integer.valueOf(0)));
		assertEquals("There are 2 elements.", b.integerObject(Integer.valueOf(2)));
		assertEquals("There is one element.", b.integerObject(Integer.valueOf(1)));
		assertEquals("There are 54 elements.", b.integerObject(Integer.valueOf(54)));
	}

	@Test
	public void testNonObject() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("This isn't a strict java.lang.Object: aa", b.nonObject("aa"));
	}

	@Test
	public void testNonObjects() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("Nor are these: aa. Container{s=bb i=3}", b.nonObjects("aa", new Container("bb", 3)));
	}

	@Test
	public void testTypes() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("oo ztrue b4 cr s54 i1,111 l99,999,999,999 f3.2 d4.6"
				, b.types("o", true, (byte)4, 'r', (short)54, 1111, 99999999999L, 3.2F, 4.6D));
	}

	@Test
	public void testPrimitiveByte() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("byte: 4", b.primitiveByte((byte) 4));
	}

	@Test
	public void testPrimitiveShort() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("short: 5", b.primitiveShort((short) 5));
	}

	@Test
	public void testPrimitiveChar() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("char: a", b.primitiveChar('a'));
	}

	@Test
	public void testPrimitiveInt() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("int: 66", b.primitiveInt(66));
	}

	@Test
	public void testPrimitiveLong() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("long: 99,999,999,999,999", b.primitiveLong(99999999999999L));
	}

	@Test
	public void testPrimitiveFloat() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundBundleMakerle.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 1.2", b.primitiveFloat(1.2f));
	}

	@Test
	public void testPrimitiveFloat2() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 10,000.5", b.primitiveFloat(10000.5f));
	}

	@Test
	public void testPrimitiveFloatFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 1,2", b.primitiveFloat(1.2f));
	}

	@Test
	public void testPrimitiveFloatFr2() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 10\u00a0000,5", b.primitiveFloat(10000.5f));
	}

	@Test
	public void testPrimitiveDouble() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 4.56", b.primitiveDouble(4.56d));
	}

	@Test
	public void testPrimitiveDoubleFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 4,56", b.primitiveDouble(4.56d));
	}

	@Test
	public void testPrimitiveDouble2() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 7,654,321.099", b.primitiveDouble(7654321.0987d));
	}

	@Test
	public void testPrimitiveDoubleFr2() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 7\u00a0654\u00a0321,099", b.primitiveDouble(7654321.0987d));
	}

	@Test
	public void testOverloadObject() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("overloaded Container{s=aa i=4}", b.overload(new Container("aa", 4)));
	}

	@Test
	public void testOverloadString() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("overloaded ss", b.overload("ss"));
	}

	@Test
	public void testOverloadDouble() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("overloaded 5.5", b.overload(5.5));
	}

	@Test
	public void testOverloadInt() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("overloaded 5", b.overload(5));
	}

	@Test
	public void testSubParameter() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = BundleMaker.load(TranslationBundle.class, locale, trns);
		assertEquals("name version", b.subPatternParameter("name", "version", 0, 0, null));
		assertEquals("name version - profile", b.subPatternParameter("name", "version", 0, 1, "profile"));
		assertEquals("name version flag", b.subPatternParameter("name", "version", 1, 0, null));
		assertEquals("name version flag - profile", b.subPatternParameter("name", "version", 1, 1, "profile"));
	}
	@Test
	public void testSubParameterFailBundle() throws Exception {
		//{0} {1}{2,choice,1# portable}{3,choice,1< - {4}}
		Locale locale = Locale.ENGLISH;
		Properties trns = SubPatternBundle.getProperties();
		SubPatternBundle b = BundleMaker.load(SubPatternBundle.class, locale, trns,
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO);
		assertEquals("name version", b.subPatternParameter("name", "version", 0, 0, null));
		assertEquals("name version - profile", b.subPatternParameter("name", "version", 0, 1, "profile"));
		assertEquals("name version flag", b.subPatternParameter("name", "version", 1, 0, null));
		assertEquals("name version flag - profile", b.subPatternParameter("name", "version", 1, 1, "profile"));
	}

	// tests to see if it is picking up the default language

	Locale getLocale(String language, String country, String varient) {
		Locale bestMatch = null;
		for (Locale l : Locale.getAvailableLocales()) {
			if (l.getLanguage().equals(language)) {
				bestMatch = l;
				if (l.getCountry().equals(country)) {
					bestMatch = l;
					if (l.getVariant().equals(varient)) {
						return l;
					}
				}
			}
		}
		return bestMatch;
	}

	@Test
	public void testDefaultLanguageSimple1() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.KOREAN); // no translations
		assertEquals("simple", ssb.simple());
	}
	@Test
	public void testDefaultLanguageSimple2() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.KOREAN); // no translations
		assertEquals("simple int 1.", ssb.simpleOne(1));
	}
	@Test
	public void testDefaultLanguageSimple3() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("bg simple int 1.", ssb.simpleOne(1));
	}
	@Test
	public void testDefaultLanguageSimple4() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("de simple int 1.", ssb.simpleOne(1));
	}

	@Test
	public void testDefaultLanguageDefaultOnly() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.ENGLISH);
		assertEquals("default only", ssb.defaultOnly());
		SimpleSmallBundle ssbBG = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("default only", ssbBG.defaultOnly());
		SimpleSmallBundle ssbJA = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "", ""));
		assertEquals("default only", ssbJA.defaultOnly());
		SimpleSmallBundle ssbJAJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", ""));
		assertEquals("default only", ssbJAJP.defaultOnly());
		SimpleSmallBundle ssbJAJPJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", "JP"));
		assertEquals("default only", ssbJAJPJP.defaultOnly());
		SimpleSmallBundle ssbDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("de default only", ssbDE.defaultOnly());
		SimpleSmallBundle ssbDEDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "DE", ""));
		assertEquals("de_de default only", ssbDEDE.defaultOnly());
	}
	
	@Test
	public void testDefaultLanguageDefaultBg() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.ENGLISH);
		assertEquals("default and Bulgarian", ssb.defaultBg());
		SimpleSmallBundle ssbBG = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("Bulgarian", ssbBG.defaultBg());
		SimpleSmallBundle ssbJA = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "", ""));
		assertEquals("default and Bulgarian", ssbJA.defaultBg());
		SimpleSmallBundle ssbJAJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", ""));
		assertEquals("default and Bulgarian", ssbJAJP.defaultBg());
		SimpleSmallBundle ssbJAJPJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", "JP"));
		assertEquals("default and Bulgarian", ssbJAJPJP.defaultBg());
		SimpleSmallBundle ssbDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("de default and Bulgarian", ssbDE.defaultBg());
		SimpleSmallBundle ssbDEDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "DE", ""));
		assertEquals("de_de default and Bulgarian", ssbDEDE.defaultBg());
	}
	
	@Test
	public void testDefaultLanguageDefaultBgJa() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.ENGLISH);
		assertEquals("default, Bulgarian and Japanese", ssb.defaultBgJa());
		SimpleSmallBundle ssbBG = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("Bulgarian", ssbBG.defaultBgJa());
		SimpleSmallBundle ssbJA = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "", ""));
		assertEquals("Japanese", ssbJA.defaultBgJa());
		SimpleSmallBundle ssbJAJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", ""));
		assertEquals("Japanese", ssbJAJP.defaultBgJa());
		SimpleSmallBundle ssbJAJPJP = Bundle.load(SimpleSmallBundle.class, getLocale("ja", "JP", "JP"));
		assertEquals("Japanese", ssbJAJPJP.defaultBgJa());
		SimpleSmallBundle ssbDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("de default, Bulgarian and Japanese", ssbDE.defaultBgJa());
		SimpleSmallBundle ssbDEDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "DE", ""));
		assertEquals("de_de default, Bulgarian and Japanese", ssbDEDE.defaultBgJa());
	}

	@Test
	public void testDefaultLanguageDefaultBgJaJp() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.ENGLISH);
		assertEquals("default, Bulgarian and japanese variant", ssb.defaultBgJaJp());
		SimpleSmallBundle ssbBG = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("Bulgarian", ssbBG.defaultBgJaJp());
		SimpleSmallBundle ssbJA = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "", ""));
		assertEquals("Japanese", ssbJA.defaultBgJaJp());
		SimpleSmallBundle ssbJAJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", ""));
		assertEquals("Japanese varient", ssbJAJP.defaultBgJaJp());
		SimpleSmallBundle ssbJAJPJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", "JP"));
		assertEquals("Japanese varient", ssbJAJPJP.defaultBgJaJp());
		SimpleSmallBundle ssbDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("default, Bulgarian and japanese variant", ssbDE.defaultBgJaJp());
		SimpleSmallBundle ssbDEDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "DE", ""));
		assertEquals("de_de default, Bulgarian and japanese variant", ssbDEDE.defaultBgJaJp());
	}

	@Test
	public void testDefaultLanguageDefaultBgJaJpJp() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class, Locale.ENGLISH);
		assertEquals("default, Bulgarian and japanese sub-variant", ssb.defaultBgJaJpJp());
		SimpleSmallBundle ssbBG = BundleMaker.load(SimpleSmallBundle.class, getLocale("bg", "", ""));
		assertEquals("Bulgarian", ssbBG.defaultBgJaJpJp());
		SimpleSmallBundle ssbJA = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "", ""));
		assertEquals("Japanese", ssbJA.defaultBgJaJpJp());
		SimpleSmallBundle ssbJAJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", ""));
		assertEquals("Japanese varient", ssbJAJP.defaultBgJaJpJp());
		SimpleSmallBundle ssbJAJPJP = BundleMaker.load(SimpleSmallBundle.class, getLocale("ja", "JP", "JP"));
		assertEquals("Japanese sub-varient", ssbJAJPJP.defaultBgJaJpJp());
		SimpleSmallBundle ssbDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "", ""));
		assertEquals("default, Bulgarian and japanese sub-variant", ssbDE.defaultBgJaJpJp());
		SimpleSmallBundle ssbDEDE = BundleMaker.load(SimpleSmallBundle.class, getLocale("de", "DE", ""));
		assertEquals("default, Bulgarian and japanese sub-variant", ssbDEDE.defaultBgJaJpJp());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsNoBundle() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				Locale.FRENCH,
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleBg() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("bg", "", ""),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleJa() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("ja", "", ""),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleJaJp() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("ja", "JP", ""),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleJaJpJp() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("ja", "JP", "JP"),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleDe() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("de", "", ""),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test(expected=MissingResourceException.class)
	public void testFailToLoadWithoutDefaultsPartialBundleDeDe() throws Exception {
		SimpleSmallBundle ssb = BundleMaker.load(SimpleSmallBundle.class,
				getLocale("de", "DE", ""),
				BundleConfiguration.IgnoreMissing.NO,
				BundleConfiguration.IgnoreExtra.NO,
				BundleConfiguration.IgnoreParameterMisMatch.NO,
				BundleConfiguration.AllowDefaultLanguage.NO
				);
		System.out.println(ssb.defaultBg());
	}

	@Test
	public void checkClassLoader() throws Exception {
		SimpleSmallBundle ssb1 = BundleMaker.load(SimpleSmallBundle.class, getLocale("en", "", ""));
		ClassLoader cl1 = ssb1.getClass().getClassLoader();
		assertNotNull(cl1);
		SimpleSmallBundle ssb2 = BundleMaker.load(SimpleSmallBundle.class, getLocale("en", "", ""));
		ClassLoader cl2 = ssb2.getClass().getClassLoader();
		assertNotNull(cl2);
		assertEquals(ssb1.getClass(), ssb2.getClass());
	}
}

