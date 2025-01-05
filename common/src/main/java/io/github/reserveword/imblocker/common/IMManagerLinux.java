package io.github.reserveword.imblocker.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

final class IMManagerLinux implements IMManager.PlatformIMManager {
    private static boolean state = false;
    private LinuxIMFramework imFramework = LinuxIMFramework.IBUS;

    @Override
    public void setEnglishState(boolean english) {

    }

    @Override
    public void syncState() {

    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean on) {
        if (state != on) {
            checkIMFramework();
            imFramework.setState(on);
            IMManagerLinux.state = on;
        }
    }

    private void checkIMFramework() {
        String fcitx5State = "";
        try {
            Process process = Runtime.getRuntime().exec("pgrep -l fcitx5");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            fcitx5State = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imFramework = fcitx5State == null ? LinuxIMFramework.IBUS : LinuxIMFramework.FCITX5;
    }
}
