package uk.me.candle.translations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;
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
 *
 * @author Andrew Wheat
 */
public class Bundle {

	final Locale locale;
	static boolean LOAD_IGNORE_MISSING = true;
	static boolean LOAD_IGNORE_EXTRA = true;
	static boolean LOAD_IGNORE_PARAM_MISMATCH = true;

	public Locale getLocale() {
		return locale;
	}

	public Bundle(Locale locale) {
		this.locale = locale;
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		final Properties translations = new Properties();
		InputStream translationsIn = cls.getClassLoader().getResourceAsStream(
				cls.getPackage().getName().replace(".", "/")
				+ "/"
				+ cls.getSimpleName().toLowerCase()
				+ "_"
				+ locale.getLanguage()
				+ ".properties"
				);
 		translations.load(translationsIn);
		return load(cls, locale, translations);
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(LOAD_IGNORE_MISSING, LOAD_IGNORE_EXTRA, LOAD_IGNORE_PARAM_MISMATCH));
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, boolean ignoreMissing, boolean ignoreExtra, boolean ignoreParamMismatch)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		return load(cls, locale, translations, new BundleConfiguration(ignoreMissing, ignoreExtra, ignoreParamMismatch));
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations, BundleConfiguration configuration)
			throws IllegalAccessException, InstantiationException
			, NoSuchMethodException, IOException
			, IllegalArgumentException, InvocationTargetException
			{
		final Set<String> usedKeys = new HashSet<String>();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		{
			InputStream classIn = Bundle.class.getClassLoader().getResourceAsStream(
					cls.getName().replace(".", "/")
					+ ".class"
					);
			byte[] buff = new byte[1024];
			int read = -1;
			while (0 < (read = classIn.read(buff))) {
				baos.write(buff, 0, read);
			}
		}

		byte[] b1 = baos.toByteArray();
		ClassReader cr = new ClassReader(b1);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ImplementMethodsAdapter ca = new ImplementMethodsAdapter(cw, translations, usedKeys, locale, configuration);
		cr.accept(ca, 0);
		byte[] b2 = cw.toByteArray();

		if (!configuration.isIgnoreExtra()) {
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

		ImplementMethodsAdapter(ClassVisitor cv, Properties translations, Set<String> usedKeys, Locale locale, BundleConfiguration configuration) {
			super(cv);
			this.translations = translations;
			this.usedKeys = usedKeys;
			this.configuration = configuration;
		}

		String getNewName() {
			return newName;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			newName = name + "__Impl";
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
					if (!configuration.isIgnoreMissing()) {
						throw new MissingResourceException("The translation file for " + "" + " is missing a key.", baseName, name);
					}
					translation = name;
				}
				if (!configuration.isIgnoreParamMismatch()) {
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
			if (LOAD_IGNORE_MISSING && translation == null) {
				throw new NullPointerException("Method '" + name + "' Must not have a null translation");
			}
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
		private boolean ignoreMissing;
		private boolean ignoreExtra;
		private boolean ignoreParamMismatch;

		public BundleConfiguration(boolean ignoreMissing, boolean ignoreExtra, boolean ignoreParamMismatch) {
			this.ignoreMissing = ignoreMissing;
			this.ignoreExtra = ignoreExtra;
			this.ignoreParamMismatch = ignoreParamMismatch;
		}

		/**
		 * Are extra keys in the source properties file ignored?
		 * @return
		 */
		public boolean isIgnoreExtra() {
			return ignoreExtra;
		}

		/**
		 * Are missing keys in the properties file ignored?
		 * @return
		 */
		public boolean isIgnoreMissing() {
			return ignoreMissing;
		}

		/**
		 * Are mismatches between parameter counts ignored?
		 * @return
		 */
		public boolean isIgnoreParamMismatch() {
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
