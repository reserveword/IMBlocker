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
                'class': 'net.minecraft.client.gui.widget.TextFieldWidget',
                'methodName': 'charTyped',
                'methodDesc': '(CI)Z'
            },
            'transformer': function (method) {
                var il = new InsnList();
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    'net/minecraft/client/gui/widget/TextFieldWidget',
                    ASMAPI.mapField('field_146226_p'),
                    'Z'
                ))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMBlocker$TextFieldConfirmEvent',
                    'fireEvent',
                    '(Lnet/minecraft/client/gui/widget/TextFieldWidget;Z)V',
                    false
                ))
                method.instructions.insert(il)
                ASMAPI.log('DEBUG','patched TextFieldWidget.chatTyped')
                return method
            }
        }
    };
}