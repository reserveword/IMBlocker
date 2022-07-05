var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'chat_screen_asm': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.gui.screens.ChatScreen',
            },
            'transformer': function (node) {
                var method = new MethodNode(
                    /* access = */ Opcodes.ACC_PUBLIC,
                    /* name = */ 'getDefaultInputFieldText',
                    /* descriptor = */ '()Ljava/lang/String;',
                    /* signature = */ null,
                    /* exceptions = */ null
                )
                node.methods.add(method)
                il = method.instructions
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    'net/minecraft/client/gui/screens/ChatScreen',
                    ASMAPI.mapField('f_95576_'),
                    'Ljava/lang/String;'
                ))
                il.add(new InsnNode(Opcodes.ARETURN))
                return node
            }
        }
    };
}