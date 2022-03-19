var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'text_field_asm': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.gui.components.EditBox',
                'methodName': 'm_94120_',
                'methodDesc': '()V'
            },
            'transformer': function (method) {
                i1 = new VarInsnNode(Opcodes.ALOAD, 0)
                i2 = new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMCheckState',
                    'imTick',
                    '(Lnet/minecraft/client/gui/components/EditBox;)V',
                    false
                )
                il = new InsnList()
                il.add(i1)
                il.add(i2)
                method.instructions.insert(il)
                return method
            }
        }
    };
}