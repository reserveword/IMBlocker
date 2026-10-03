package io.github.reserveword.imblocker.legacy1122;

final class NoopImeBackend implements ImeBackend {
    @Override
    public void setEnabled(boolean enabled) {}

    @Override
    public void setEnglishMode(boolean english) {}

    @Override
    public void updateCompositionWindow(int x, int y, int height) {}

    @Override
    public boolean supportsConversionStatus() {
        return false;
    }
}
