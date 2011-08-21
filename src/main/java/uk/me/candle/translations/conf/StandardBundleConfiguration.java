package uk.me.candle.translations.conf;

import uk.me.candle.translations.conf.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreMissing;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreParameterMisMatch;

/**
 *
 * @author andrew
 */
public final class StandardBundleConfiguration implements BundleConfiguration {
	private final IgnoreMissing ignoreMissing;
	private final IgnoreExtra ignoreExtra;
	private final IgnoreParameterMisMatch ignoreParameterMisMatch;
	private final AllowDefaultLanguage allowDefaultLanguage;

	public StandardBundleConfiguration(IgnoreMissing ignoreMissing, IgnoreExtra ignoreExtra, IgnoreParameterMisMatch ignoreParameterMisMatch, AllowDefaultLanguage allowDefaultLanguage) {
		this.ignoreMissing = ignoreMissing;
		this.ignoreExtra = ignoreExtra;
		this.ignoreParameterMisMatch = ignoreParameterMisMatch;
		this.allowDefaultLanguage = allowDefaultLanguage;
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
