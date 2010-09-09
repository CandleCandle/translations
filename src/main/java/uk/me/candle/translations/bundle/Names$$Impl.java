package uk.me.candle.translations.bundle;

import java.text.MessageFormat;

/**
 *
 * @author Andrew Wheat
 */
public class Names$$Impl extends Names {

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
		return MessageFormat.format("test param; {0} first (and only) arg.", o);
	}

	@Override
	public String params(Object o1, Object o2) {
		return MessageFormat.format("test param; {0} first (and only) arg.", o1, o2);
	}

	@Override
	public String params3(Object o1, Object o2, Object o3) {
		return MessageFormat.format("test param; {0} first (and only) arg.", o1, o2, o3);
	}

	@Override
	public String paramsTons(Object o1, Object o2, Object o3
			, Object o4, Object o5, Object o6
			, Object o7, Object o8, Object o9
			, Object o10, Object o11, Object o12
			, Object o13, Object o14, Object o15) {
		return MessageFormat.format("test param; {0} first (and only) arg."
				, o1, o2, o3
				, o4, o5, o6
				, o7, o8, o9
				, o10, o11, o12
				, o13, o14, o15);
	}
}
