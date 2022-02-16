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
                'methodName': '<init>',
                'methodDesc': '(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/network/chat/Component;)V'
            },
            'transformer': function (method) {
                i1 = new VarInsnNode(Opcodes.ALOAD, 0)
                i2 = new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMBlocker$RegistryEvents',
                    'collectTextField',
                    '(Lnet/minecraft/client/gui/components/EditBox;)V',
                    false
                )
                iter = method.instructions.iterator(method.instructions.size())
                ASMAPI.log('INFO', 'instruction size ' + method.instructions.size())
                while (iter.hasPrevious()) {
                    var op = iter.previous()
                    if (op.getOpcode() === Opcodes.RETURN) {
                        iter.add(i1)
                        iter.add(i2)
                        ASMAPI.log('DEBUG','patched a return of TextFieldWidget.<init>')
                    }
                }
                ASMAPI.log('INFO', 'instruction size after patch ' + method.instructions.size())
                return method
            }
        }
    };
}