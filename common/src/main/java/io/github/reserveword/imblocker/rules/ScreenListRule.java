package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.IMManager;

public class ScreenListRule implements Rule {
    public static boolean isBlackListScreenShowing = false;
    public static boolean isWhiteListScreenShowing = false;

    public static void checkScreen(Object screen) {
        if (screen == null) {
            isBlackListScreenShowing = false;
            isWhiteListScreenShowing = false;
            return;
        }
        try {
            Config.INSTANCE.recoverScreen(screen.getClass());
            isWhiteListScreenShowing = Config.INSTANCE.inScreenWhitelist(screen.getClass());
            isBlackListScreenShowing = Config.INSTANCE.inScreenBlacklist(screen.getClass());
        } catch (Throwable e) {
            // ignore it. the game is still loading
            Common.LOGGER.info(e);
        }
    }

    @Override
    public double Priority() {
        return 100;
    }

    @Override
    public boolean apply() {
        if (isBlackListScreenShowing) {
            IMManager.setState(false);
            return true;
        }
        if (isWhiteListScreenShowing) {
            IMManager.setState(true);
            return true;
        }
        return false;
    }
}
