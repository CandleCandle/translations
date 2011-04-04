package uk.me.candle.translations;

/**
 * Container for any configuration options.
 *
 * Note that if "ignoreMissing" is true and "ignoreParamMismatch" is false then the class
 * generation will fail for keys that have arguments.
 */
class BundleConfiguration {
	private final Bundle.LoadIgnoreMissing ignoreMissing;
	private final Bundle.LoadIgnoreExtra ignoreExtra;
	private final Bundle.LoadIgnoreParameterMisMatch ignoreParamMismatch;
	private final Bundle.AllowDefaultLanguage allowDefaultLanguage;

	BundleConfiguration(Bundle.LoadIgnoreMissing ignoreMissing, Bundle.LoadIgnoreExtra ignoreExtra, Bundle.LoadIgnoreParameterMisMatch ignoreParamMismatch) {
		this.ignoreMissing = ignoreMissing;
		this.ignoreExtra = ignoreExtra;
		this.ignoreParamMismatch = ignoreParamMismatch;
		this.allowDefaultLanguage = Bundle.AllowDefaultLanguage.YES;
	}
	BundleConfiguration(Bundle.LoadIgnoreMissing ignoreMissing, Bundle.LoadIgnoreExtra ignoreExtra, Bundle.LoadIgnoreParameterMisMatch ignoreParamMismatch, Bundle.AllowDefaultLanguage allowDefaultLanguage) {
		this.ignoreMissing = ignoreMissing;
		this.ignoreExtra = ignoreExtra;
		this.ignoreParamMismatch = ignoreParamMismatch;
		this.allowDefaultLanguage = allowDefaultLanguage;
	}
	/**
	 * Are extra keys in the source properties file ignored?
	 * @return
	 */ public Bundle.LoadIgnoreExtra getIgnoreExtra() {
		return ignoreExtra;
	}
	/**
	 * Are missing keys in the properties file ignored?
	 * @return
	 */ public Bundle.LoadIgnoreMissing getIgnoreMissing() {
		return ignoreMissing;
	}
	/**
	 * Are mismatches between parameter counts ignored?
	 * @return
	 */ public Bundle.LoadIgnoreParameterMisMatch getIgnoreParamMismatch() {
		return ignoreParamMismatch;
	}
	/**
	 * If this is 'NO' then there has to be an exact properties bundle match for the language
	 * @return
	 */ public Bundle.AllowDefaultLanguage getAllowDefaultLanguage() {
		return allowDefaultLanguage;
	}
}
