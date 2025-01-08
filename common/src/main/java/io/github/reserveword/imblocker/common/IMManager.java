package io.github.reserveword.imblocker.common;

import com.sun.jna.Platform;

public final class IMManager {
    private static final PlatformIMManager INSTANCE;

    static {
        if (Platform.isWindows()) {
            INSTANCE = new IMManagerWindows();
        } else if (Platform.isMac()) {
            INSTANCE = new IMManagerMac();
        } else if (Platform.isLinux()) {
            INSTANCE = new IMManagerLinux();
        } else {
            Common.LOGGER.warn("Unsupported platform, using stub");
            INSTANCE = new IMManagerStub();
        }
    }

    private IMManager() {
    }

    public static void setEnglish(boolean english) {
        INSTANCE.setEnglishState(english);
    }

    public static void syncState() {
        INSTANCE.syncState();
    }

    public static boolean getState() {
        return INSTANCE.getState();
    }

    public static void setState(boolean on) {
        INSTANCE.setState(on);
    }

    public sealed interface PlatformIMManager permits IMManagerWindows, IMManagerMac, IMManagerLinux, IMManagerStub {

        void setEnglishState(Boolean english);

        void syncState();

        boolean getState();

        void setState(boolean on);
    }
}
