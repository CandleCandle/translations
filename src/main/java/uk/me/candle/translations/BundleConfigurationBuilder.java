package uk.me.candle.translations;

import java.util.Locale;
import uk.me.candle.translations.BundleConfiguration.AllowDefaultLanguage;
import uk.me.candle.translations.BundleConfiguration.IgnoreExtra;
import uk.me.candle.translations.BundleConfiguration.IgnoreMissing;
import uk.me.candle.translations.BundleConfiguration.IgnoreParameterMisMatch;

/**
 *
 * @author andrew
 */
public class BundleConfigurationBuilder {
	Locale defaultLocale;
	IgnoreMissing ignoreMissing = IgnoreMissing.NO;
	IgnoreExtra ignoreExtra = IgnoreExtra.NO;
	IgnoreParameterMisMatch ignoreParameterMisMatch = IgnoreParameterMisMatch.NO;
	AllowDefaultLanguage allowDefaultLanguage = AllowDefaultLanguage.YES;

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

	public BundleConfigurationBuilder defaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
		return this;
	}
	
	public BundleConfiguration build() {
		return new StandardBundleConfiguration(defaultLocale, ignoreMissing, ignoreExtra, ignoreParameterMisMatch, allowDefaultLanguage);
	}
}
