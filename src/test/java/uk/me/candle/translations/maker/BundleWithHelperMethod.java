package uk.me.candle.translations.maker;

import java.util.Locale;
import uk.me.candle.translations.Bundle;

/**
 *
 * @author Andrew
 */
public abstract class BundleWithHelperMethod extends Bundle {
	public BundleWithHelperMethod(Locale locale) {
		super(locale);
	}

	public abstract String something();
	
	public String thisIsAHelperMethod(String s) {
		return s.toUpperCase(getLocale());
	}
}
