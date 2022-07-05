var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');
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
                il = new InsnList()
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    'net/minecraft/client/gui/widget/TextFieldWidget',
                    ASMAPI.mapMethod('func_212955_f'),
                    '()Z',
                    false
                ))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMCheckState',
                    'captureTick',
                    '(Ljava/lang/Object;Z)V',
                    false
                ))
                method.instructions.insert(il)
                return method
            }
        },
        'char_typed_asm': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.gui.widget.TextFieldWidget',
                'methodName': 'func_231042_a_',
                'methodDesc': '(CI)Z'
            },
            'transformer': function (method) {
                il = new InsnList()
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new VarInsnNode(Opcodes.ILOAD, 1))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    'net/minecraft/client/gui/widget/TextFieldWidget',
                    ASMAPI.mapMethod('func_212955_f'),
                    '()Z',
                    false
                ))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMCheckState',
                    'captureNonPrintable',
                    '(Ljava/lang/Object;CZ)V',
                    false
                ))
                method.instructions.insert(il)
                return method
            }
        },
        'ftblib_text_field_asm': {
            'target': {
                'type': 'METHOD',
                'class': 'dev.ftb.mods.ftblibrary.ui.Widget',
                'methodName': 'tick',
                'methodDesc': '()V'
            },
            'transformer': function (method) {
                label = new LabelNode()
                il = new InsnList()
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new TypeInsnNode(Opcodes.INSTANCEOF, 'dev/ftb/mods/ftblibrary/ui/TextBox'))
                il.add(new JumpInsnNode(Opcodes.IFEQ, label))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new TypeInsnNode(Opcodes.CHECKCAST, 'dev/ftb/mods/ftblibrary/ui/TextBox'))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    'dev/ftb/mods/ftblibrary/ui/TextBox',
                    'isFocused',
                    '()Z',
                    false
                ))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMCheckState',
                    'captureTick',
                    '(Ljava/lang/Object;Z)V',
                    false
                ))
                il.add(label)
                il.add(new FrameNode(Opcodes.F_SAME, 0, [], 0, []))
                method.instructions.insert(il)
                return method
            }
        },
        'ftblib_char_typed_asm': {
            'target': {
                'type': 'METHOD',
                'class': 'dev.ftb.mods.ftblibrary.ui.TextBox',
                'methodName': 'charTyped',
                'methodDesc': '(CLdev/ftb/mods/ftblibrary/ui/input/KeyModifiers;)Z'
            },
            'transformer': function (method) {
                il = new InsnList()
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new VarInsnNode(Opcodes.ILOAD, 1))
                il.add(new VarInsnNode(Opcodes.ALOAD, 0))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    'dev/ftb/mods/ftblibrary/ui/TextBox',
                    'isFocused',
                    '()Z',
                    false
                ))
                il.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'io/github/reserveword/imblocker/IMCheckState',
                    'captureNonPrintable',
                    '(Ljava/lang/Object;CZ)V',
                    false
                ))
                method.instructions.insert(il)
                return method
            }
        }
    };
}