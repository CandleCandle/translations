package uk.me.candle.translations.conf;

import java.util.Locale;

/**
 *
 * @author Andrew
 */
public enum DefaultBundleConfiguration implements BundleConfiguration {
	INSTANCE;

	@Override
	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}
	@Override
	public IgnoreMissing getIgnoreMissing() {
		return IgnoreMissing.NO;
	}
	@Override
	public IgnoreExtra getIgnoreExtra() {
		return IgnoreExtra.NO;
	}
	@Override
	public IgnoreParameterMisMatch getIgnoreParameterMisMatch() {
		return IgnoreParameterMisMatch.NO;
	}
	@Override
	public AllowDefaultLanguage getAllowDefaultLanguage() {
		return AllowDefaultLanguage.YES;
	}
}
