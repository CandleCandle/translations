package uk.me.candle.translations;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
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
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 *
 * @author Andrew Wheat
 */
public class Bundle {

	Locale locale;
	static boolean LOAD_IGNORE_MISSING = true;
	static boolean LOAD_IGNORE_EXTRA = true;
	static boolean LOAD_IGNORE_PARAM_MISMATCH = true;

	public Locale getLocale() {
		return locale;
	}

	public Bundle(Locale locale) {
		this.locale = locale;
	}

	static <T extends Bundle> T load(Class<T> cls, Locale locale) throws Exception { // XXX throw more specific exceptions.

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

	static <T extends Bundle> T load(Class<T> cls, Locale locale, Properties translations) throws Exception { // XXX throw more specific exceptions.

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
		//TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
		//CheckClassAdapter cca = new CheckClassAdapter(cw);
		ImplementMethodsAdapter ca = new ImplementMethodsAdapter(cw, translations, usedKeys);
		cr.accept(ca, 0);
		byte[] b2 = cw.toByteArray();

		if (!LOAD_IGNORE_EXTRA) {
			checkForExtras(translations, usedKeys);
		}

		BundleClassLoader bcl = new BundleClassLoader();
		Class<?> result = bcl.defineClass(ca.getNewName().replace("/", "."), b2);

		Constructor c = result.getConstructor(new Class[]{Locale.class});
		return (T) c.newInstance(locale);
	}

	private static class ImplementMethodsAdapter extends ClassAdapter {
		private String newName;
		private Properties translations;
		private Set<String> usedKeys;

		ImplementMethodsAdapter(ClassVisitor cv, Properties translations, Set<String> usedKeys) {
			super(cv);
			this.translations = translations;
			this.usedKeys = usedKeys;
		}

		String getNewName() {
			return newName;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			newName = name + "__Impl";
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
	//			Type[] types = Type.getArgumentTypes(desc);

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
						, translations.getProperty(name)
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
	 * Checks to see if the string translation has all the required {x} fields for the method signature.
	 * @param translation
	 * @param method
	 * @return
	 */
	private static boolean checkMethodParamsAndStringFields(String translation, Method method) {
		return true;
	}

	/**
	 * Compares the used keys and translations for extra translations.
	 * @param translations
	 * @param usedKeys
	 */
	private static void checkForExtras(Properties translations, Set<String> usedKeys) {
		Set<String> extras = new HashSet<String>();
		for (String o : translations.stringPropertyNames()) {
			if (!usedKeys.contains(o)) {
				extras.add(o);
			}
		}
		if (!extras.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String s : extras) {
				if (sb.length() != 0) {
					sb.append(", ");
				}
				sb.append(s);
			}
			throw new RuntimeException("Extra keys in the bundle: " + sb.toString());
		}
	}
}

class BundleClassLoader extends ClassLoader {

	public Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
