package uk.me.candle.translations.conf;

import java.util.Locale;
import uk.me.candle.translations.conf.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreMissing;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreParameterMisMatch;

/**
 *
 * @author andrew
 */
public class StandardBundleConfiguration implements BundleConfiguration {
	private final Locale defaultLocale;
	private final IgnoreMissing ignoreMissing;
	private final IgnoreExtra ignoreExtra;
	private final IgnoreParameterMisMatch ignoreParameterMisMatch;
	private final AllowDefaultLanguage allowDefaultLanguage;

	public StandardBundleConfiguration(Locale defaultLocale, IgnoreMissing ignoreMissing, IgnoreExtra ignoreExtra, IgnoreParameterMisMatch ignoreParameterMisMatch, AllowDefaultLanguage allowDefaultLanguage) {
		this.defaultLocale = defaultLocale;
		this.ignoreMissing = ignoreMissing;
		this.ignoreExtra = ignoreExtra;
		this.ignoreParameterMisMatch = ignoreParameterMisMatch;
		this.allowDefaultLanguage = allowDefaultLanguage;
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	@Override
	public AllowDefaultLanguage getAllowDefaultLanguage() {
		return allowDefaultLanguage;
	}

	@Override
	public IgnoreExtra getIgnoreExtra() {
		return ignoreExtra;
	}

	@Override
	public IgnoreMissing getIgnoreMissing() {
		return ignoreMissing;
	}

	@Override
	public IgnoreParameterMisMatch getIgnoreParameterMisMatch() {
		return ignoreParameterMisMatch;
	}
}
