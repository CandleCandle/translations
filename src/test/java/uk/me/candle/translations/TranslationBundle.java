package uk.me.candle.translations;

import java.util.Locale;
import java.util.Properties;

/**
 *
 * @author Andrew Wheat
 */
public abstract class TranslationBundle extends Bundle {

	static Properties getProperties() {
		Properties p = new Properties();

		p.setProperty("noParams", "there are no parameters");
		p.setProperty("oneParam", "one parameter, and it is {0}");
		p.setProperty("twoParams", "two more params: {0} {1}");
		p.setProperty("lotsOfParams", "Fifteen params: {0} {1} {2} {3} {4} {5} {6} {7} {8} {9} {10} {11} {12} {13} {14}");
		p.setProperty("nonObject", "This isn''t a strict java.lang.Object: {0}");
		p.setProperty("nonObjects", "Nor are these: {0}. {1}");
		p.setProperty("integerObject", "There {0,choice,0#are no elements|1#is one element|1<are {0,number,integer} elements}.");
		p.setProperty("types", "o{0} z{1} b{2} c{3} s{4} i{5} l{6} f{7} d{8}");
		p.setProperty("primativeByte", "byte: {0}");
		p.setProperty("primativeShort", "short: {0}");
		p.setProperty("primativeChar", "char: {0}");
		p.setProperty("primativeInt", "int: {0}");
		p.setProperty("primativeLong", "long: {0}");
		p.setProperty("primativeFloat", "float: {0}");
		p.setProperty("primativeDouble", "double: {0}");

		return p;
	}

	public TranslationBundle(Locale locale) {
		super(locale);
	}

	public abstract String noParams();
	public abstract String oneParam(Object o1);
	public abstract String twoParams(Object o1, Object o2);
	public abstract String lotsOfParams(
			Object o1, Object o2, Object o3,
			Object o4, Object o5, Object o6,
			Object o7, Object o8, Object o9,
			Object o10, Object o11, Object o12,
			Object o13, Object o14, Object o15
			);
	public abstract String integerObject(Integer i);
	public abstract String nonObject(String s);
	public abstract String nonObjects(String s, Container c);
	public abstract String types(Object o, boolean z, byte b, char c, short s, int i, long l, float f, double d);
	public abstract String primativeByte(byte b);
	public abstract String primativeShort(short s);
	public abstract String primativeChar(char c);
	public abstract String primativeInt(int i);
	public abstract String primativeLong(long l);
	public abstract String primativeFloat(float f);
	public abstract String primativeDouble(double d);

}