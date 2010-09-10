package uk.me.candle.translations.bundle;

import java.text.MessageFormat;
import java.util.Locale;

/**
 *
 * @author Andrew Wheat
 */
public class Names$$Impl extends Names {

	public Names$$Impl(Locale locale) {
		super(locale);
	}

	@Override
	public String title() {
		return "test title";
	}

	@Override
	public String username() {
		return "test username";
	}

	@Override
	public String password() {
		return "test password";
	}

	@Override
	public String param(Object o) {
		MessageFormat mf = new MessageFormat("test param; {0} first (and only) arg.", getLocale());
		return mf.format(new Object[]{
			o
		});
	}

	@Override
	public String params(Object o1, Object o2) {
		MessageFormat mf = new MessageFormat("test param; {0} first (and only) arg.", getLocale());
		return mf.format(new Object[]{
			o1, o2
		});
	}

	@Override
	public String params3(Object o1, Object o2, Object o3) {
		MessageFormat mf = new MessageFormat("test param; {0} first (and only) arg.", getLocale());
		return mf.format(new Object[]{
			o1, o2, o3
		});
	}

	@Override
	public String paramsTons(Object o1, Object o2, Object o3
			, Object o4, Object o5, Object o6
			, Object o7, Object o8, Object o9
			, Object o10, Object o11, Object o12
			, Object o13, Object o14, Object o15) {
		MessageFormat mf = new MessageFormat("test param; {0} first (and only) arg.", getLocale());
		return mf.format(new Object[]{
			o1, o2, o3,
			o4, o5, o6,
			o7, o8, o9,
			o10, o11, o12,
			o13, o14, o15
		});
	}
	
	@Override
	public String types(Object o, boolean z, byte b, char c, short s, int i, long l, float f, double d) {
		MessageFormat mf = new MessageFormat("o{0} z{1} b{2} c{3} s{4} i{5} l{6} f{7} d(8)", getLocale());
		return mf.format(new Object[]{
			o,z,b,c,s,i,l,f,d
		});
	}
}
