package io.github.reserveword.imblocker.common;

final class IMManagerStub implements IMManager.PlatformIMManager {
    private static boolean state = true;

    @Override
    public void makeOn() {
    }

    @Override
    public void makeOff() {
    }

    @Override
    public void setState(boolean on) {
        if (state == on) return;
        state = on;
    }

    @Override
    public void syncState() {
    }

    @Override
    public boolean getState() {
        return state;
    }
}
