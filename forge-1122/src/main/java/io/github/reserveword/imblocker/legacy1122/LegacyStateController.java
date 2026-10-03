package io.github.reserveword.imblocker.legacy1122;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
final class LegacyStateController {
    static final LegacyStateController INSTANCE = new LegacyStateController();

    private Object focusOwner;
    private GuiScreen lastScreen;
    private boolean manualUnlock;
    private boolean initialized;

    private LegacyStateController() {}

    void initialize() {
        initialized = true;
        refresh(Minecraft.getMinecraft());
    }

    void onScreenChanged(GuiScreen screen) {
        lastScreen = screen;
        focusOwner = null;
        manualUnlock = false;
        refresh(Minecraft.getMinecraft());
    }

    void onTextFieldFocus(Object textField, boolean focused) {
        if (focused) {
            focusOwner = textField;
            manualUnlock = false;
        } else if (focusOwner == textField) {
            focusOwner = null;
        }
        refresh(Minecraft.getMinecraft());
    }

    void onTextFieldCharTyped(Object textField, char character, int keyCode) {
        if (textField instanceof GuiTextField) {
            GuiTextField input = (GuiTextField) textField;
            String current = input.getText();
            boolean command = CommandDetector.isCommandAfter(current, character);
            if (LegacyConfig.commandEnglishMode && isChatScreen(Minecraft.getMinecraft().currentScreen)) {
                ImeManager.INSTANCE.setEnglishMode(command);
            }
            updateCaret(Minecraft.getMinecraft(), input);
        }
    }

    void unlockImeForCurrentContext() {
        manualUnlock = true;
        ImeManager.INSTANCE.setEnabled(true);
    }

    void tick(Minecraft minecraft) {
        if (!initialized || minecraft == null) {
            return;
        }
        if (lastScreen != minecraft.currentScreen) {
            onScreenChanged(minecraft.currentScreen);
        }
        refresh(minecraft);
    }

    private void refresh(Minecraft minecraft) {
        if (!LegacyConfig.enabled || minecraft == null) {
            ImeManager.INSTANCE.setEnabled(false);
            return;
        }
        try {
            if (!Display.isActive()) {
                ImeManager.INSTANCE.setEnabled(false);
                return;
            }
        } catch (IllegalStateException ignored) {
            // The display may not be initialized during the first client tick.
        }
        GuiScreen screen = minecraft.currentScreen;
        if (focusOwner == null) {
            GuiTextField discovered = findTextField(screen);
            if (discovered != null && discovered.isFocused()) {
                focusOwner = discovered;
            }
        }
        boolean focusedTextField = focusOwner instanceof GuiTextField
                && ((GuiTextField) focusOwner).isFocused();
        boolean shouldEnable = manualUnlock || focusedTextField || ScreenClassifier.isSpecialInputScreen(screen);
        ImeManager.INSTANCE.setEnabled(shouldEnable);
        if (isChatScreen(screen)) {
            GuiTextField chatInput = findTextField(screen);
            if (chatInput != null) {
                ImeManager.INSTANCE.setEnglishMode(CommandDetector.isCommand(chatInput.getText()));
                updateCaret(minecraft, chatInput);
            }
        } else if (focusedTextField) {
            updateCaret(minecraft, (GuiTextField) focusOwner);
        }
    }

    private void updateCaret(Minecraft minecraft, GuiTextField textField) {
        if (minecraft == null || textField == null || !textField.isFocused()) {
            return;
        }
        int x = LegacyReflection.intField(textField, "field_146209_f", "x");
        int y = LegacyReflection.intField(textField, "field_146210_g", "y");
        int cursor = textField.getCursorPosition();
        int textWidth = minecraft.fontRenderer.getStringWidth(textField.getText().substring(0,
                Math.min(cursor, textField.getText().length())));
        ScaledResolution resolution = new ScaledResolution(minecraft);
        int guiScale = resolution.getScaleFactor();
        int screenX = (x + 2 + textWidth) * guiScale;
        int screenY = (y + 2) * guiScale;
        ImeManager.INSTANCE.updateCompositionWindow(screenX, screenY, minecraft.fontRenderer.FONT_HEIGHT * guiScale);
    }

    private static GuiTextField findTextField(GuiScreen screen) {
        Object value = LegacyReflection.findFieldValue(screen, GuiTextField.class);
        return value instanceof GuiTextField ? (GuiTextField) value : null;
    }

    private static boolean isChatScreen(GuiScreen screen) {
        return screen instanceof GuiChat;
    }

    static void log(String message, Throwable throwable) {
        if (LegacyConfig.debug) {
            System.err.println("[IMBlocker-1.12.2] " + message + ": " + throwable);
        }
    }
}
