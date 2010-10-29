package uk.me.candle.translations;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Handles translations.
 *
 * General usage:
 *
 * <ul>
 * <li>The bundle class must be public and abstract</li>
 * <li>Methods with no arguments the raw translation is returned.</li>
 * <li>Methods with arguments are passed to MessageFormat to format with the translation and the
 * arguments of the method as the parameters of the MessageFormat</li>
 * <li>Methods must be abstract and have a String return value.</li>
 * <li>Methods must be abstract and have a String return value.</li>
 * <li>Classes must have an accessible constructor that takes a java.util.Locale.</li>
 * </ul>
 *
 * Bundle class:
 * <pre>{@code
 *package com.example.foo
 *public abstract class Foo extends Bundle {
 *  public static Foo get() {
 *	   return BundleCache.get(Foo.class);
 *  }
 *  public static Foo get(Locale locale) {
 *	   return BundleCache.get(Foo.class, locale);
 *  }
 *  public Foo(Locale locale) {
 *	   super(locale);
 *  }
 *  public abstract String bar();
 *  public abstract String zit();
 *  public abstract String pony(String s);
 *  public abstract String iHaveSomeOranges(int i);
 *  public abstract String iHaveAFewArguments(Object o, boolean z, byte b, char c, short s, int i, long l, float f, double d);
 *}
 * }</pre>
 * Bundle resource:
 * <pre>{@code
 *bar=Moe''s Tavern
 *zit=zap
 *pony=my horsie''s name is {0}
 *iHaveSomeOranges=I have {0,choice,0#are no oranges|1# one orange|1&lt;are {0,number,integer} oranges}.
 *iHaveAFewArguments=o={0} z={1} b={2} c={3} s={4} i={5} l={6} f={7} d={8}
 * }</pre>
 *
 * When the class is loaded (without a properties specified in the load() method)  it will look for a properties
 * file in the same package as the class, with the same name as the class, but with the classname all lower-case, and
 * the language code appended to the end.
 *
 * In the above example, the Foo bundle will look for the English properties file at
 * {@code /com/example/foo/Foo_en.properties }
 * the French will be:
 * {@code /com/example/foo/Foo_fr.properties }
 *
 * Usage of the bundle is intended to be as simple as possible.
 *{@code
 *public class App {
 *
 *  public static void main(String[] args) {
 *    BundleCache.setThreadLocale(Locale.ENGLISH);
 *    System.out.println("I know a bar called " + Foo.get().bar());
 *    System.out.println(Foo.get().pony("Sparky"));
 *    System.out.println(Foo.get().iHaveSomeOranges(5));
 *    System.out.println(Foo.get().iHaveAFewArguments("obj", true, (byte)4, 'q', (short)6, 1, 9999, 9.5f, 4.4d));
 *  }
 *}
 * }
 *
 * This will produce the output:
 * {@code
 *I know a bar called Moe's Tavern
 *my horsie's name is Sparky
 *I have one orange.
 *o=obj z=true b=4 c=q s=6 i=1 l=9,999 f=9.5 d=4.4
 * }
 *
 *
 * @see java.text.MessageFormat
 * @author Andrew Wheat
 */
public class Bundle {
	public enum LoadIgnoreMissing { YES, NO };
	public enum LoadIgnoreExtra { YES, NO };
	public enum LoadIgnoreParameterMisMatch { YES, NO };

	/**
	 * The locale for this bundle. Required for formatting numbers in the subclasses.
	 */
	private final Locale locale;
	/**
	 *
	 * default value for new creations if the options are not specified
	 */
	public static LoadIgnoreMissing LOAD_IGNORE_MISSING = LoadIgnoreMissing.YES;
	public static LoadIgnoreExtra LOAD_IGNORE_EXTRA = LoadIgnoreExtra.YES;
	public static LoadIgnoreParameterMisMatch LOAD_IGNORE_PARAM_MISMATCH = LoadIgnoreParameterMisMatch.YES;

	public Locale getLocale() {
		return locale;
	}

	public Bundle(Locale locale) {
		if (locale == null) {
			throw new NullPointerException("The locale cannot be null");
		}
		this.locale = locale;
	}

	/**
	 * Constructs a bundle implementation for the class and locale.
	 * @throws MissingResourceException if the resource cannot be found.
	 * @throws IOException if there is an error loading the properties
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, boolean, boolean, boolean)
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, LOAD_IGNORE_MISSING, LOAD_IGNORE_EXTRA, LOAD_IGNORE_PARAM_MISMATCH);
	}
	static <T extends Bundle> T load(Class<T> cls, Locale locale, BundleConfiguration configuration)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		final Properties translations = new Properties();
		final String resourcePath =
				cls.getPackage().getName().replace(".", "/")
				+ "/"
				+ cls.getSimpleName()
				+ "_"
				+ locale.getLanguage()
				+ ".properties";
		InputStream translationsIn = cls.getClassLoader().getResourceAsStream(resourcePath);
		if (translationsIn == null) {
			throw new MissingResourceException("There was no resource for the path: " + resourcePath, cls.getName(), "");
		}
 		translations.load(translationsIn);
		return load(cls, locale, translations, configuration);
	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified translations file.
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, boolean, boolean, boolean)
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(LOAD_IGNORE_MISSING, LOAD_IGNORE_EXTRA, LOAD_IGNORE_PARAM_MISMATCH));
	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified options.
	 * @see #load(java.lang.Class, java.util.Locale, java.util.Properties, boolean, boolean, boolean)
	 */
	public static <T extends Bundle> T load(Class<T> cls, Locale locale, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch));

	}

	/**
	 * Constructs a bundle implementation for the class and locale with the specified translations file.
	 *
	 *
	 * @param <T> The bundle type.
	 * @param cls the class of the bundle.
	 * @param locale the requested locale for the bundle.
	 * @param translations properties representing the translations.
	 * @param ignoreMissing true if missing keys are to be ignored.
	 * @param ignoreExtra true if extra keys are to be ignored.
	 * @param ignoreParamMismatch true if parameter mismatches are to be ignored.
	 *
	 * @return an implementation of the abstract class
	 *
	 * @throws MissingResourceException if the resource cannot be found, or there is: a missing key, a mismatch between the method parameters and the field entries in the pattern or there are extra keys that are not used in the bundle properties.
	 * @throws IllegalAccessException if the bundle class is not public and abstract
	 * @throws InstantiationException if the new bundle cannot be created
	 * @throws NoSuchMethodException if there is no available constructor
	 * @throws IOException if the source class cannot be loaded.
	 * @throws IllegalArgumentException 
	 * @throws InvocationTargetException if the constructor for the bundle throws an exception.
	 */
	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch));
	}

	private static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, BundleConfiguration configuration)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		final Set<String> usedKeys = new HashSet<String>();

		ClassReader cr = new ClassReader(cls.getName());
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ImplementMethodsAdapter ca = new ImplementMethodsAdapter(cw, translations, usedKeys, locale, configuration);
		cr.accept(ca, 0);
		byte[] b2 = cw.toByteArray();

		if (!configuration.getIgnoreExtra().equals(LoadIgnoreExtra.YES)) {
			Set<String> extras = checkForExtras(translations, usedKeys);

			if (!extras.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String s : extras) {
					if (sb.length() != 0) {
						sb.append(", ");
					}
					sb.append(s);
				}
				throw new MissingResourceException("Extra keys in the bundle: " + sb.toString(), cls.getName(), sb.toString());
			}
		}

		BundleClassLoader bcl = new BundleClassLoader();
		Class<?> result = bcl.defineClass(ca.getNewName().replace("/", "."), b2);

		Constructor c = result.getConstructor(new Class[]{Locale.class});
		return (T) c.newInstance(locale);
	}

	private static class ImplementMethodsAdapter extends ClassAdapter {
		private String newName;
		private String baseName;
		private Properties translations;
		private Set<String> usedKeys;
		private BundleConfiguration configuration;
		private Locale locale;

		ImplementMethodsAdapter(ClassVisitor cv, Properties translations, Set<String> usedKeys, Locale locale, BundleConfiguration configuration) {
			super(cv);
			this.translations = translations;
			this.usedKeys = usedKeys;
			this.configuration = configuration;
			this.locale = locale;
		}

		String getNewName() {
			return newName;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			newName = name + "__" + locale.getLanguage() + "__Impl";
			baseName = name;
			cv.visit(Opcodes.V1_6
					, access - Opcodes.ACC_ABSTRACT // remove the abstract.
					, newName // new name
					, signature // generics? signature
					, name // extend this
					, interfaces
					);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name,
				String desc, String signature, String[] exceptions) {
			if ((access & Opcodes.ACC_ABSTRACT) > 0 &&
					Type.getReturnType(desc).equals(Type.getType(String.class))) {
				Type[] types = Type.getArgumentTypes(desc);
				String translation = translations.getProperty(name);
				// If we are ignoring the
				if (translation == null) {
					if (configuration.getIgnoreMissing().equals(LoadIgnoreMissing.NO)) {
						throw new MissingResourceException("The translation file for " + baseName + " in the language: " + locale + " is missing a key: " + name, baseName, name);
					}
					translation = name;
				}
				if (configuration.getIgnoreParamMismatch().equals(LoadIgnoreParameterMisMatch.NO)) {
					MessageFormat f = new MessageFormat(translation);
					if (f.getFormatsByArgumentIndex().length != types.length) {
						throw new MissingResourceException("The parameter lengths did not match method: " + types.length + " translation: " + f.getFormatsByArgumentIndex().length, baseName, name);
					}
				}

				usedKeys.add(name); // add the key for later use - checking for configuration.isIgnoreExtra().

				MethodVisitor mv = cv.visitMethod(
							access - Opcodes.ACC_ABSTRACT
							, name
							, desc
							, signature
							, exceptions);
				return new MethodImplementationAdapter(
						mv
						, name
						, desc
						, translation
						, newName
						);
			} else {
				return cv.visitMethod(access, name, desc, signature, exceptions);
			}
		}

	}

	private static class MethodImplementationAdapter extends MethodAdapter {
		private String translation;
		private String descriptor;
		private String generatedClassName;

		MethodImplementationAdapter(MethodVisitor mv, String name, String descriptor, String translation, String generatedClassName) {
			super(mv);
			this.translation = translation;
			this.descriptor = descriptor;
			this.generatedClassName = generatedClassName;
		}

		@Override
		public void visitEnd() {
			Type[] types = Type.getArgumentTypes(descriptor);
		if (types.length == 0) {
				simpleGenerate();
			} else {
				complexGenerate(types);
			}
		}

		private void simpleGenerate() {
			mv.visitCode();
			mv.visitLdcInsn(translation);
			mv.visitInsn(Opcodes.ARETURN);
			mv.visitMaxs(0, 0); // (1, 1) // calculated due to ClassWriter.COMPUTE_MAXS
		}

		private void complexGenerate(Type[] types) {
			int registers = countRegisters(types);
			mv.visitCode();
			mv.visitTypeInsn(Opcodes.NEW, "java/text/MessageFormat");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn(translation);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, generatedClassName, "getLocale", "()Ljava/util/Locale;");
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/text/MessageFormat", "<init>", "(Ljava/lang/String;Ljava/util/Locale;)V");
			mv.visitVarInsn(Opcodes.ASTORE, registers+2);
			mv.visitVarInsn(Opcodes.ALOAD, registers+2);
			mv.visitIntInsn(Opcodes.BIPUSH, types.length);
			mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
			int regCount = 0;
			for (int i = 0; i < types.length; ++i) {
				boxIfNeededAndAddToArray(types[i], i, regCount);
				regCount += getRegisters(types[i]);
			}
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/text/MessageFormat", "format", "(Ljava/lang/Object;)Ljava/lang/String;");
			mv.visitInsn(Opcodes.ARETURN);
			mv.visitMaxs(0, 0); // (1, 1) // calculated due to ClassWriter.COMPUTE_MAXS
		}

		private void boxIfNeededAndAddToArray(Type t, int idx, int reg) {
			mv.visitInsn(Opcodes.DUP);
			switch(idx) {
				case 0: mv.visitInsn(Opcodes.ICONST_0); break;
				case 1: mv.visitInsn(Opcodes.ICONST_1); break;
				case 2: mv.visitInsn(Opcodes.ICONST_2); break;
				case 3: mv.visitInsn(Opcodes.ICONST_3); break;
				case 4: mv.visitInsn(Opcodes.ICONST_4); break;
				case 5: mv.visitInsn(Opcodes.ICONST_5); break;
				default: mv.visitIntInsn(Opcodes.BIPUSH, idx); break;
			}
			switch (t.getSort()) {
				case Type.BOOLEAN:
				case Type.BYTE:
				case Type.CHAR:
				case Type.SHORT:
				case Type.INT:
					mv.visitVarInsn(Opcodes.ILOAD, reg+1); break;
				case Type.LONG:
					mv.visitVarInsn(Opcodes.LLOAD, reg+1); break;
				case Type.FLOAT:
					mv.visitVarInsn(Opcodes.FLOAD, reg+1); break;
				case Type.DOUBLE:
					mv.visitVarInsn(Opcodes.DLOAD, reg+1); break;
				case Type.OBJECT:
					mv.visitVarInsn(Opcodes.ALOAD, reg+1); break;
				default:
					throw new IllegalArgumentException("Invalid type: " + t);
			}
			switch (t.getSort()) {
				case Type.BOOLEAN:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;"); break;
				case Type.BYTE:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"); break;
				case Type.CHAR:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"); break;
				case Type.SHORT:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;"); break;
				case Type.INT:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"); break;
				case Type.LONG:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"); break;
				case Type.FLOAT:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"); break;
				case Type.DOUBLE:
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;"); break;
				case Type.OBJECT:
					break;
				default:
					throw new IllegalArgumentException("Invalid type: " + t);
			}
			mv.visitInsn(Opcodes.AASTORE);
		}

		private int getRegisters(Type t) {
			switch (t.getSort()) {
				case Type.BOOLEAN:
				case Type.BYTE:
				case Type.CHAR:
				case Type.SHORT:
				case Type.OBJECT:
				case Type.INT:
				case Type.FLOAT:
					return 1;
				case Type.DOUBLE:
				case Type.LONG:
					return 2;
				default:
					throw new IllegalArgumentException("Invalid type: " + t);
			}
		}

		private int countRegisters(Type[] types) {
			int c = 0;
			for (Type t : types) {
				c += getRegisters(t);
			}
			return c;
		}
	}

	/**
	 * Container for any configuration options.
	 *
	 * Note that if "ignoreMissing" is true and "ignoreParamMismatch" is false then the class
	 * generation will fail for keys that have arguments.
	 */
	private static class BundleConfiguration {
		private final LoadIgnoreMissing ignoreMissing;
		private final LoadIgnoreExtra ignoreExtra;
		private final LoadIgnoreParameterMisMatch ignoreParamMismatch;

		public BundleConfiguration(LoadIgnoreMissing ignoreMissing, LoadIgnoreExtra ignoreExtra, LoadIgnoreParameterMisMatch ignoreParamMismatch) {
			this.ignoreMissing = ignoreMissing;
			this.ignoreExtra = ignoreExtra;
			this.ignoreParamMismatch = ignoreParamMismatch;
		}

		/**
		 * Are extra keys in the source properties file ignored?
		 * @return
		 */
		public LoadIgnoreExtra getIgnoreExtra() {
			return ignoreExtra;
		}

		/**
		 * Are missing keys in the properties file ignored?
		 * @return
		 */
		public LoadIgnoreMissing getIgnoreMissing() {
			return ignoreMissing;
		}

		/**
		 * Are mismatches between parameter counts ignored?
		 * @return
		 */
		public LoadIgnoreParameterMisMatch getIgnoreParamMismatch() {
			return ignoreParamMismatch;
		}
	}

	/**
	 * Allows class definition from a byte array.
	 */
	private static class BundleClassLoader extends ClassLoader {
		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}

	/**
	 * Compares the used keys and translations for extra translations.
	 * @param translations
	 * @param usedKeys
	 */
	private static Set<String> checkForExtras(Properties translations, Set<String> usedKeys) {
		Set<String> extras = new HashSet<String>(translations.stringPropertyNames());
		extras.removeAll(usedKeys);
		return extras;
	}
}
