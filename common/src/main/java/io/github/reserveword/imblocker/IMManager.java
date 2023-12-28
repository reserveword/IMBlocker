package io.github.reserveword.imblocker;

import com.sun.jna.Platform;

public final class IMManager {
    public sealed interface PlatformIMManager permits IMManagerMac, IMManagerWindows {

        PlatformIMManager INSTANCE = getInstance();

        private static PlatformIMManager getInstance() {
            if (Platform.isWindows())
                return new IMManagerWindows();
            if (Platform.isMac())
                return new IMManagerMac();
            throw new UnsupportedOperationException("Unsupported platform");
        }


        void makeOn();

        void makeOff();

        void setState(boolean on);

        void syncState();

        boolean getState();

    }

    public static void makeOn() {
        PlatformIMManager.INSTANCE.makeOn();
    }

    public static void makeOff() {
        PlatformIMManager.INSTANCE.makeOff();
    }

    public static void setState(boolean on) {
        PlatformIMManager.INSTANCE.setState(on);
    }

    public static void syncState() {
        PlatformIMManager.INSTANCE.syncState();
    }

    boolean getState() {
        return PlatformIMManager.INSTANCE.getState();
    }
}
