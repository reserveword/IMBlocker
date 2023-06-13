var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'window_focus_asm': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': 'm_7440_',
                'methodDesc': '(Z)V'
            },
            'transformer': function (method) {
                il = new InsnList()
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMManager',
                    'syncState',
                    '()V',
                    false
                ))
                method.instructions.insert(il)
                return method
            }
        }
    };
}