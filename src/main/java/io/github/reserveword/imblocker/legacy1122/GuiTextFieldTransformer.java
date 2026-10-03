package io.github.reserveword.imblocker.legacy1122;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public final class GuiTextFieldTransformer implements IClassTransformer {
    private static final String GUI_TEXT_FIELD = "net.minecraft.client.gui.GuiTextField";
    private static final String HOOKS = "io/github/reserveword/imblocker/legacy1122/LegacyHooks";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !(GUI_TEXT_FIELD.equals(name) || GUI_TEXT_FIELD.equals(transformedName)
                || "bib".equals(name))) {
            return basicClass;
        }

        final String focusName = LegacyNames.runtimeMethod("setFocused", "func_146195_b", "(Z)V");
        final String typedName = LegacyNames.runtimeMethod("textboxKeyTyped", "func_146203_a", "(CI)Z");
        final ClassReader reader = new ClassReader(basicClass);
        final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor,
                                             String signature, String[] exceptions) {
                MethodVisitor visitor = super.visitMethod(access, methodName, descriptor, signature, exceptions);
                if (("setFocused".equals(methodName) || focusName.equals(methodName))
                        && "(Z)V".equals(descriptor)) {
                    return new MethodVisitor(Opcodes.ASM5, visitor) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN) {
                                visitVarInsn(Opcodes.ALOAD, 0);
                                visitVarInsn(Opcodes.ILOAD, 1);
                                visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "onTextFieldFocus",
                                        "(Ljava/lang/Object;Z)V", false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                if (("textboxKeyTyped".equals(methodName) || typedName.equals(methodName))
                        && "(CI)Z".equals(descriptor)) {
                    return new MethodVisitor(Opcodes.ASM5, visitor) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            visitVarInsn(Opcodes.ALOAD, 0);
                            visitVarInsn(Opcodes.ILOAD, 1);
                            visitVarInsn(Opcodes.ILOAD, 2);
                            visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "onTextFieldCharTyped",
                                    "(Ljava/lang/Object;CI)V", false);
                        }
                    };
                }
                return visitor;
            }
        }, 0);
        return writer.toByteArray();
    }
}
