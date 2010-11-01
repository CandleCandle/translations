package uk.me.candle.translations.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * One instance of this class represents one Bundle.
 * If multiple bundles are needed, create multiple instances and add
 * the code to them.
 * 
 * This does assume that multiple bundles won't be needed in each source file.
 * 
 * @author andrew
 * @param <P> output type for the code result (File/String/?)
 * @param <Q> output type for the bundle code result (File/String/?)
 */
public abstract class Converter<P, Q> {
	String bundleClassName;
	String bundlePackageName;

	StringBuilder replacementCode;
	StringBuilder bundleCode;
	Properties translations;

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
		// 1) for each code input, parse and create I18nString instances
		// 2) collect the duplicated values
		// 3) go through the code inputs and create the new code with the method calls
		// 4) prepare the properties
		// 5) prepare the bundle class.

		List<I18nString> strings = getAllTokens();

		Set<I18nString> unique = new HashSet<I18nString>(strings);


		throw new UnsupportedOperationException("");
	}

	static class I18nElement {

		I18nString example;
		List<I18nString> rest;
	}

	protected abstract List<I18nString> getAllTokens();

	/**
	 * @return the replacement code
	 */
	public abstract P getCodeResult();

	/**
	 * @return the code that represents the bundle class.
	 */
	public abstract Q getBundleClassResult();

	/**
	 * @return the properties file that contains the translations.
	 */
	public abstract Properties getBundlePropertiesResult();

	/**
	 * defines an occurance of a string in the source code.
	 * equals() is defined as each java.lang.String segment being equal.
	 */
	public static class I18nString {

		private List<StringPortion> segments;
		private boolean hasVariable;
		private String I18nClassName;

		/**
		 * @return the method name for the key.
		 */
		String getI18nKey() {
			return getI18nValue().replaceAll(" .*", "").toLowerCase(); // XXX quick hack.
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
		public int getFirstPosition() {
			return segments.get(0).getStart();
		}
		public int getLastPosition() {
			return segments.get(segments.size()-1).getEnd();
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
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final I18nString other = (I18nString) obj;
			if (other.segments.size() != segments.size()) {
				return false;
			}

			for (int i = 0; i < segments.size(); ++i) {
				StringPortion t = segments.get(i);
				StringPortion o = other.segments.get(i);
				if (!t.getClass().equals(o.getClass())) {
					return false;
				}
				if (t instanceof VariableStringPortion) {
					return true; // FIXME check types? or just resolve to 'Object'
				} else if (t instanceof LiteralStringPortion) {
					if (!((LiteralStringPortion)t).getValue().equals(((LiteralStringPortion)o).getValue())) {
						return false;
					}
				}
			}

			return true;
		}
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 67 * hash + (this.segments != null ? this.segments.hashCode() : 0);
			return hash;
		}



	}

	public abstract static class StringPortion {

		private int start;
		private int end;
		public StringPortion(int start, int end) {
			this.start = start;
			this.end = end;
		}

		abstract String getValue();
		public int getEnd() {
			return end;
		}
		public int getStart() {
			return start;
		}

	}

	/**
	 * represents a literal String in the code, i.e. "Foo"
	 */
	public static class LiteralStringPortion extends StringPortion {
		String value;
		public LiteralStringPortion(int start, int end, String value) {
			super(start, end);
			this.value = value;
		}
		@Override
		String getValue() {
			return value;
		}
	}

	/**
	 * represents a variable that is used to create compound strings, i.e. "foo" + bar
	 */
	public static class VariableStringPortion extends StringPortion {
		String name;
		String type;

		public VariableStringPortion(int start, int end, String name, String type) {
			super(start, end);
			this.name = name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		@Override
		String getValue() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
