package uk.me.candle.translations.maker;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Andrew
 */
public abstract class NonPublicTranslation extends Bundle {
	public NonPublicTranslation(Locale locale) {
		super(locale);
	}

	abstract String bar();
}
