package uk.me.candle.translations;

import java.util.Locale;
import java.util.Properties;

/**
 *
 * @author Andrew Wheat
 */
public abstract class SubPatternBundle extends Bundle {

	static Properties getProperties() {
		Properties p = new Properties();
    p.setProperty("subPatternParameter", "{0} {1}{2,choice,0#|1# flag}{3,choice,0#|0< - {4}}");

		return p;
	}

	public SubPatternBundle(Locale locale) {
		super(locale);
	}

  public abstract String subPatternParameter(String programName, String programVersion, int portable, int profileCount, String activeProfileName);
}