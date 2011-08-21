package uk.me.candle.translations.conf;

/**
 *
 * @author Andrew
 */
public final class DefaultBundleConfiguration implements BundleConfiguration {
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
