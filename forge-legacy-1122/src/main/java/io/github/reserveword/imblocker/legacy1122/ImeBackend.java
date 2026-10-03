package io.github.reserveword.imblocker.legacy1122;

interface ImeBackend {
    /**
     * Applies the requested state and reports whether a native window was
     * available. A false result lets the manager retry during the next client
     * tick instead of permanently caching an early-launch failure.
     */
    boolean setEnabled(boolean enabled);

    void setEnglishMode(boolean english);

    void updateCompositionWindow(int x, int y, int height);

    default void onWindowFocusGained() {}

    default void reconcile(boolean desiredEnabled, boolean englishLocked, boolean english) {}

    default boolean supportsConversionStatus() {
        return false;
    }
}
