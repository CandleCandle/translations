package uk.me.candle.translations.maker;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ConstructorImplementationAdapter extends MethodVisitor {
	private final MethodVisitor mv;
	private final String baseName;

	ConstructorImplementationAdapter(MethodVisitor mv, String baseName) {
		super(Opcodes.ASM4, mv);
		this.mv = mv;
		this.baseName = baseName;
	}

	@Override
	public void visitCode() {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, baseName, "<init>", "(Ljava/util/Locale;)V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0); // calculated due to ClassWriter.COMPUTE_MAXS
	}
}
