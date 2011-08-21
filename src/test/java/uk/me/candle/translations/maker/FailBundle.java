package uk.me.candle.translations.maker;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Andrew
 */
public abstract class FailBundle extends Bundle {
	public FailBundle(Locale locale) {
		super(locale);
	}
	public abstract String foo();
	public abstract Integer fail();
}
