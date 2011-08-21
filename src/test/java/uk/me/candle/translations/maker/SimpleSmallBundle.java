package uk.me.candle.translations.maker;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Andrew
 */
public abstract class SimpleSmallBundle extends Bundle {

	public SimpleSmallBundle(Locale locale) {
		super(locale);
	}

	public abstract String simple();
	public abstract String simpleOne(int i);
	public abstract String defaultOnly();
	public abstract String defaultBg();
	public abstract String defaultBgJa();
	public abstract String defaultBgJaJp();
	public abstract String defaultBgJaJpJp();
}
