package io.github.reserveword.imblocker.legacy1122;

interface ImeBackend {
    void setEnabled(boolean enabled);

    void setEnglishMode(boolean english);

    void updateCompositionWindow(int x, int y, int height);

    default boolean supportsConversionStatus() {
        return false;
    }
}
