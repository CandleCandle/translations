package uk.me.candle.translations;

import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ImplementMethodsAdapter extends ClassAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(ImplementMethodsAdapter.class);
	private String newName;
	private String baseName;
	private Properties translations;
	private Set<String> usedKeys;
	private BundleConfiguration configuration;
	private Locale locale;
	ImplementMethodsAdapter(ClassVisitor cv, Properties translations,
			Set<String> usedKeys, Locale locale,
			BundleConfiguration configuration) {
		super(cv);
		this.translations = translations;
		this.usedKeys = usedKeys;
		this.configuration = configuration;
		this.locale = locale;
	}
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		newName = BundleMaker.getClassNameFor(name, locale);
		baseName = name;
		cv.visit(Opcodes.V1_6, access - Opcodes.ACC_ABSTRACT, newName, signature, name, interfaces);
	}
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ((access & Opcodes.ACC_ABSTRACT) > 0 && Type.getReturnType(desc).equals(Type.getType(String.class))) {
			Type[] types = Type.getArgumentTypes(desc);
			String translation = translations.getProperty(name);
			// If we are ignoring the
			if (translation == null) {
				LOG.debug("Missing property for: {} ", name);
				if (configuration.getIgnoreMissing().equals(BundleConfiguration.IgnoreMissing.NO)) {
					throw new MissingResourceException("The translation file for " + baseName + " in the language: " + locale + " is missing a key: " + name, baseName, name);
				}
				translation = name;
			}
			if (configuration.getIgnoreParameterMisMatch().equals(BundleConfiguration.IgnoreParameterMisMatch.NO)) {
				MessageFormat f = new MessageFormat(translation);
				int fieldCount = countFields(f);
				if (fieldCount != types.length) {
					throw new MissingResourceException("The parameter lengths did not match method: " + types.length + " translation: " + fieldCount + " baseName: " + baseName + " translation: " + translation, baseName, name);
				}
			}
			usedKeys.add(name); // add the key for later use - checking for configuration.isIgnoreExtra().
			MethodVisitor mv = cv.visitMethod(access - Opcodes.ACC_ABSTRACT, name, desc, signature, exceptions);
			return new MethodImplementationAdapter(mv, name, desc, translation, newName);
		} else {
			return cv.visitMethod(access, name, desc, signature, exceptions);
		}
	}
	/**
	 * Count the maximum fields that are used in a message format
	 * note that a ChoiceFormat can have sub-formats: {0,choice,0#{1}|1#{2}}
	 * has three parameters, yet the basic messageFormat.getFormatsByArgumentIndex().length
	 * call will have the value of one. This is because the sub-formats are
	 * handled in a recursive way. Since this is the case, this method needs
	 * to do something similar to count the fields used.
	 * @param messageFormat message format instance to count the maximum field number used.
	 * @return the maximum number of fields used in
	 */ private int countFields(MessageFormat messageFormat) {
		Format[] formats = messageFormat.getFormatsByArgumentIndex();
		int count = messageFormat.getFormatsByArgumentIndex().length;
		for (Format ff : formats) {
			if (ff instanceof ChoiceFormat) {
				ChoiceFormat cf = (ChoiceFormat) ff;
				for (Object o : cf.getFormats()) {
					// gets a list of the format choice values. the "foo" and "bar" parts of {0,choice,0#foo|1#bar}
					if (o != null) {
						MessageFormat subFormat = new MessageFormat(o.toString());
						count = Math.max(count, countFields(subFormat));
					}
				}
			}
		}
		return count;
	}
}
