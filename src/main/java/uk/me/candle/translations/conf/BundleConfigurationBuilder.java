package uk.me.candle.translations.conf;

import uk.me.candle.translations.conf.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreMissing;
import uk.me.candle.translations.conf.BundleConfiguration.IgnoreParameterMisMatch;

/**
 *
 * @author andrew
 */
public final class BundleConfigurationBuilder {
	private IgnoreMissing ignoreMissing = IgnoreMissing.NO;
	private IgnoreExtra ignoreExtra = IgnoreExtra.NO;
	private IgnoreParameterMisMatch ignoreParameterMisMatch = IgnoreParameterMisMatch.NO;
	private AllowDefaultLanguage allowDefaultLanguage = AllowDefaultLanguage.YES;

	public BundleConfigurationBuilder allowDefaultLanguage(AllowDefaultLanguage allowDefaultLanguage) {
		this.allowDefaultLanguage = allowDefaultLanguage;
		return this;
	}

	public BundleConfigurationBuilder ignoreExtra(IgnoreExtra ignoreExtra) {
		this.ignoreExtra = ignoreExtra;
		return this;
	}

	public BundleConfigurationBuilder ignoreMissing(IgnoreMissing ignoreMissing) {
		this.ignoreMissing = ignoreMissing;
		return this;
	}

	public BundleConfigurationBuilder ignoreParameterMisMatch(IgnoreParameterMisMatch ignoreParameterMisMatch) {
		this.ignoreParameterMisMatch = ignoreParameterMisMatch;
		return this;
	}
	
	public BundleConfiguration build() {
		return new StandardBundleConfiguration(ignoreMissing, ignoreExtra, ignoreParameterMisMatch, allowDefaultLanguage);
	}
}
