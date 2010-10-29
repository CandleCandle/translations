package uk.me.candle.translations.tools;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author andrew
 * @param <P> output type for the code result (File/String/?)
 * @param <Q> output type for the bundle code result (File/String/?)
 */
public abstract class Converter<P, Q> {
	String bundleClassName;
	String bundlePackageName;

	public Converter(String bundleClassName, String bundlePackageName) {
		this.bundleClassName = bundleClassName;
		this.bundlePackageName = bundlePackageName;
	}

	public void addCode(InputStream code) {
		throw new UnsupportedOperationException("");
	}

	public void addCode(String code) {
		throw new UnsupportedOperationException("");
	}

	public String getBundleClassName() {
		return bundleClassName;
	}

	public void execute() {
		throw new UnsupportedOperationException("");
	}

	static class I18nElement {

		I18nString example;
		List<I18nString> rest;
	}

	public abstract P getCodeResult();

	public abstract Q getBundleClassResult();

	public abstract Properties getBundlePropertiesResult();

	/**
	 * defines an occurance of a string in the source code.
	 * equals() is defined as each java.lang.String segment being equal.
	 */
	static class I18nString {

		private List<StringPortion> segments;
		private boolean hasVariable;
		private String I18nClassName;

		/**
		 * @return the method name for the key.
		 */
		String getI18nKey() {
			throw new UnsupportedOperationException("");
		}

		String getI18nValue() {
			if (hasVariable) {
				throw new UnsupportedOperationException("variable substitution not currently supported.");
			} else {
				StringBuilder sb = new StringBuilder();
				for (StringPortion sp : segments) {
					sb.append(sp.getValue());
				}
				return sb.toString();
			}
		}

		String getPropertiesLine() {
			return getI18nKey() + "=" + getI18nValue();
		}

		String getCodeReplacement() {
			return I18nClassName + ".get()." + getI18nKey() + "(" + getMethodParameters() + ")";
		}

		String getMethodParameters() {
			throw new UnsupportedOperationException("");
		}
	}

	static class StringPortion {

		int start;
		int end;

		String getValue() {
			throw new UnsupportedOperationException();
		}
	}

	static class LiteralStringPortion extends StringPortion {
	}

	static class VariableStringPortion extends StringPortion {
	}
}
