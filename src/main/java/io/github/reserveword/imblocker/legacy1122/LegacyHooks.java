package io.github.reserveword.imblocker.legacy1122;

public final class LegacyHooks {
    private LegacyHooks() {}

    public static void onTextFieldFocus(Object textField, boolean focused) {
        LegacyStateController.INSTANCE.onTextFieldFocus(textField, focused);
    }

    public static void onTextFieldCharTyped(Object textField, char character, int keyCode) {
        LegacyStateController.INSTANCE.onTextFieldCharTyped(textField, character, keyCode);
    }
}
