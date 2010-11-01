package uk.me.candle.translations.tools;

import java.util.List;
import java.util.Properties;
import uk.me.candle.translations.tools.Converter.I18nString;

/**
 *
 * @author andrew
 */
public class StringConverter extends Converter<String, String> {

	public StringConverter(String bundleClassName, String bundlePackageName) {
		super(bundleClassName, bundlePackageName);
	}

	@Override
	public String getCodeResult() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getBundleClassResult() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Properties getBundlePropertiesResult() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	protected List<I18nString> getAllTokens() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
