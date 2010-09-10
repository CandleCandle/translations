package uk.me.candle.translations;

import java.util.Locale;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Wheat
 */
public class BundleTest {

	@Test
	public void testNoParamsEn() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("there are no parameters", b.noParams());
	}

	@Test
	public void testNoParamsFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		trns.setProperty("noParams", "Rien de truc");
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("Rien de truc", b.noParams());
	}

	@Test
	public void testMissing() {
		// XXX need to organise the exceptions
	}

	@Test
	public void testExtra() {
		// XXX need to organise the exceptions
	}

	@Test
	public void testOneParam() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("one parameter, and it is aa", b.oneParam((Object) "aa"));
	}

	@Test
	public void testTwoParams() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("two more params: aa bb", b.twoParams("aa", "bb"));
	}

	@Test
	public void testLotsOfParams() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("Fifteen params: a b c d e f g h i j k l m n o", b.lotsOfParams("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"));
	}

	@Test
	public void testIntegerObject() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("There are no elements.", b.integerObject(Integer.valueOf(0)));
		assertEquals("There are 2 elements.", b.integerObject(Integer.valueOf(2)));
		assertEquals("There is one element.", b.integerObject(Integer.valueOf(1)));
		assertEquals("There are 54 elements.", b.integerObject(Integer.valueOf(54)));
	}

	@Test
	public void testNonObject() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("This isn't a strict java.lang.Object: aa", b.nonObject("aa"));
	}

	@Test
	public void testNonObjects() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("Nor are these: aa. Container{s=bb i=3}", b.nonObjects("aa", new Container("bb", 3)));
	}

	@Test
	public void testTypes() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("oo ztrue b4 cr s54 i1,111 l99,999,999,999 f3.2 d4.6"
				, b.types("o", true, (byte)4, 'r', (short)54, 1111, 99999999999L, 3.2F, 4.6D));
	}

	@Test
	public void testPrimativeByte() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("byte: 4", b.primativeByte((byte) 4));
	}

	@Test
	public void testPrimativeShort() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("short: 5", b.primativeShort((short) 5));
	}

	@Test
	public void testPrimativeChar() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("char: a", b.primativeChar('a'));
	}

	@Test
	public void testPrimativeInt() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("int: 66", b.primativeInt(66));
	}

	@Test
	public void testPrimativeLong() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("long: 99,999,999,999,999", b.primativeLong(99999999999999L));
	}

	@Test
	public void testPrimativeFloat() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 1.2", b.primativeFloat(1.2f));
	}

	@Test
	public void testPrimativeFloat2() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 10,000.5", b.primativeFloat(10000.5f));
	}

	@Test
	public void testPrimativeFloatFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 1,2", b.primativeFloat(1.2f));
	}

	@Test
	public void testPrimativeFloatFr2() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("float: 10\u00a0000,5", b.primativeFloat(10000.5f));
	}

	@Test
	public void testPrimativeDouble() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 4.56", b.primativeDouble(4.56d));
	}

	@Test
	public void testPrimativeDoubleFr() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 4,56", b.primativeDouble(4.56d));
	}

	@Test
	public void testPrimativeDouble2() throws Exception {
		Locale locale = Locale.ENGLISH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		String s = b.primativeDouble(7654321.0987d);
		assertEquals("double: 7,654,321.099", b.primativeDouble(7654321.0987d));
	}

	@Test
	public void testPrimativeDoubleFr2() throws Exception {
		Locale locale = Locale.FRENCH;
		Properties trns = TranslationBundle.getProperties();
		TranslationBundle b = Bundle.load(TranslationBundle.class, locale, trns);
		assertEquals("double: 7\u00a0654\u00a0321,099", b.primativeDouble(7654321.0987d));
	}
}


