package uk.me.candle.translations.tools;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andrew
 */
public class ConverterTest {
	private final static String BUNDLE_CLASS_NAME = "FooBundle";
	private final static String BUNDLE_PACKAGE_NAME = "uk.me.candle.translations.tools.tests";
	private final static String BUNDLE_CODE_HEADER = ""
				+ "package uk.me.candle.translations.tools.tests;"
				+ "import java.util.Locale;\n"
				+ "import uk.me.candle.translations.Bundle;\n"
				+ "import uk.me.candle.translations.BundleCache;\n"
				+ "public abstract class FooBundle extends Bundle {\n"
				+ "	public static FooBundle get() {\n"
				+ "		return BundleCache.get(FooBundle.class);\n"
				+ "	}\n"
				+ "	public static FooBundle get(Locale locale) {\n"
				+ "		return BundleCache.get(FooBundle.class, locale);\n"
				+ "	}\n"
				+ "	public FooBundle(Locale locale) {\n"
				+ "		super(locale);\n"
				+ "	}\n"
				;
	private final static String BUNDLE_CODE_FOOTER = ""
				+ "}\n"
				;

	@Test
	public void testBasic() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something());"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something();\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testBasicMultiple() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something\");"
				+ "    System.out.println(\"Something Else\");"
				+ "    System.out.println(\"Something\");"
				+ "    System.out.println(\"More\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something());"
				+ "    System.out.println(FooBundle.get().something2());"
				+ "    System.out.println(FooBundle.get().something());"
				+ "    System.out.println(FooBundle.get().more());"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String more();\n"
				+ " public abstract String something();\n"
				+ " public abstract String something2();\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something");
		expectedProperties.setProperty("something2", "Something Else");
		expectedProperties.setProperty("more", "More");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testLinearCompound() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something\" + \" Something else\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something());"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something();\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something Something else");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testMultilineLinearCompound() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something\"\n + \" Something else\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something());"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something();\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something Something else");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testCompoundVariable() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something\" + args[0] + \" Something else\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something(args[0]));"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something(String arg1);\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something {0} Something else");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testCompoundVariableMultiline() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something \"\n"
				+ "\t\t + args[0]\n"
				+ "\t\t + \" Something else\");"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something(args[0]));"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something(String arg1);\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something {0} Something else");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	@Test
	public void testCompoundVariableDuplicatedVariable() throws Exception {
		String code = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(\"Something \"\n"
				+ "\t\t + args[0]\n"
				+ "\t\t + \" Something else \"\n"
				+ "\t\t + args[0]);"
				+ "  }"
				+ "}";
		String expectedCode = ""
				+ "public class Foo { "
				+ "  public static void main(String[] args) {"
				+ "    System.out.println(FooBundle.get().something(args[0]));"
				+ "  }"
				+ "}";
		String expectedBundle = BUNDLE_CODE_HEADER
				+ " public abstract String something(String arg1);\n"
				+ BUNDLE_CODE_FOOTER
				;

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("something", "Something {0} Something else {0}");
		executeTest(code, expectedCode, expectedBundle, expectedProperties);
	}

	private void executeTest(String code, String expectedCode, String expectedBundle, Properties expectedProperties) {
		StringConverter converter = new StringConverter(BUNDLE_PACKAGE_NAME, BUNDLE_CLASS_NAME);
		converter.addCode(new ByteArrayInputStream(code.getBytes()));
		converter.execute();
		String actualCode = converter.getCodeResult();
		String actualBundle = converter.getBundleClassResult();
		Properties actualProperties = converter.getBundlePropertiesResult();
		assertEquals(expectedCode, actualCode);
		assertEquals(expectedBundle, actualBundle);
		assertPropertiesEqual(expectedProperties, actualProperties);
	}

	public void assertPropertiesEqual(Properties expected, Properties actual) {
		assertEquals("properties size", expected.size(), actual.size());
		for (Object k : expected.keySet()) {
			assertTrue("Contains " + k, actual.contains(k));
			assertEquals(expected.getProperty(k.toString()), actual.getProperty(k.toString()));
		}
	}
}
