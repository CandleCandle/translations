package com.patchmanager.experiments.translations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 *
 * @author Andrew Wheat
 */
public class Bundle {
  Locale locale;
  static final boolean LOAD_IGNORE_MISSING = true;
  static final boolean LOAD_IGNORE_EXTRA = true;
  static final boolean LOAD_IGNORE_PARAM_MISMATCH = true;

  static <T extends Bundle> T load(Class<T> cls, Locale locale) throws Exception { // XXX throw more specific exceptions.

    Properties translations = new Properties();
    InputStream translationsIn = Bundle.class.getClassLoader()
            .getResourceAsStream(
            Bundle.class.getPackage().getName().replaceAll(Pattern.quote("."), "/")
            + "/bundle/names.properties"
            ); // vary this per locale.
    translations.load(translationsIn);
    Set<String> usedKeys = new HashSet<String>();

    String newSimpleName = cls.getSimpleName() + "__Impl"; // the new name
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
    cv.visit(Opcodes.V1_6 // class version
            , Opcodes.ACC_PUBLIC // access modifiers
            , cls.getPackage().getName().replaceAll(Pattern.quote("."), "/") + "/" + newSimpleName // new name
            , null // no generics
            , cls.toString().replaceAll(Pattern.quote("."), "/") // extends this.
            , new String[]{} // no interfaces.
            );

    for (Method m : cls.getDeclaredMethods()) {
      if (!m.getReturnType().equals(String.class)) continue;
      String translation = translations.getProperty(m.getName());
      if (LOAD_IGNORE_MISSING && translation == null) {
        translation = m.getName();
      }

      if (LOAD_IGNORE_PARAM_MISMATCH && checkMethodParamsAndStringFields(translation, m)) {
        // failure
      }
      handleMethodCreation(cv, m, translation);
      
      System.out.println("assigning trn: " + translation + " to method " + m);
    }

    cv.visitEnd();

    if (!LOAD_IGNORE_EXTRA) checkForExtras(translations, usedKeys);

    File f = new File("target/classes/"
            //target\classes\com\patchmanager\experiments\translations\bundle
            //+ "com/patchmanager/experiments/translations/bundle/"
            + cls.getPackage().getName().replace(".", File.separator)
            + File.separator
            + newSimpleName
            + ".class");
    System.out.println(f.getAbsolutePath());
    f.createNewFile();
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(cw.toByteArray());
    fos.close();

    System.out.println(f.getAbsolutePath());
    String n = cls.getPackage().getName() + "." + newSimpleName;
    Class<?> result = Class.forName(n);

//    BundleClassLoader bcl = new BundleClassLoader();
//    Class<?> result = bcl.defineClass(cls.getPackage().getName() + "." + newSimpleName, cw.toByteArray());

    return (T) result.newInstance();
  }

  private static String getSignature(Method m) {
    if (m.getParameterTypes().length == 0) {
      return "()Ljava/lang/String;";
    } else if (m.getParameterTypes().length == 1) {
      return "(Ljava/lang/Object;)Ljava/lang/String;";
    } else {
      return "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;";
    }
  }

  private static void handleMethodCreation(ClassVisitor cv, Method m, String translation) {

      MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, m.getName(), getSignature(m), getSignature(m), new String[]{});
      mv.visitCode();
      mv.visitLdcInsn(translation);
      mv.visitInsn(Opcodes.IRETURN);
      mv.visitMaxs(0, 0); // (1, 1) // calculated due to ClassWriter.COMPUTE_MAXS
      mv.visitEnd();
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
        if (sb.length() != 0) sb.append(", ");
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
