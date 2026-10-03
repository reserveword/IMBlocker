package io.github.reserveword.imblocker.legacy1122;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SideOnly(Side.CLIENT)
final class ImeManager {
    static final ImeManager INSTANCE = new ImeManager();

    private final ImeBackend backend;
    private final ScheduledExecutorService conversionExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "IMBlocker-1.12.2-IME");
        thread.setDaemon(true);
        return thread;
    });
    private boolean enabled;
    private boolean requestedEnabled;
    private boolean english = true;
    private long lastEnabledAt;

    private ImeManager() {
        ImeBackend selected;
        if (System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("win")) {
            try {
                selected = new WindowsImeBackend();
            } catch (Throwable throwable) {
                selected = new NoopImeBackend();
                LegacyStateController.log("Windows IME backend is unavailable", throwable);
            }
        } else {
            selected = new NoopImeBackend();
        }
        backend = selected;
    }

    void setEnabled(boolean value) {
        requestedEnabled = value;
        applyRequestedState();
    }

    private void applyRequestedState() {
        boolean shouldEnable = requestedEnabled
                && !("DISABLE_IM".equalsIgnoreCase(LegacyConfig.englishStateMode) && english);
        if (enabled == shouldEnable) {
            if (shouldEnable) {
                scheduleEnglishState();
            }
            return;
        }
        enabled = shouldEnable;
        try {
            backend.setEnabled(shouldEnable);
            if (shouldEnable) {
                lastEnabledAt = System.currentTimeMillis();
                scheduleEnglishState();
            }
        } catch (Throwable throwable) {
            LegacyStateController.log("Failed to update input method state", throwable);
        }
    }

    void setEnglishMode(boolean value) {
        if (english == value) {
            return;
        }
        english = value;
        applyRequestedState();
    }

    void updateCompositionWindow(int x, int y, int height) {
        if (!enabled || !LegacyConfig.cursorTracking) {
            return;
        }
        try {
            backend.updateCompositionWindow(x, y, height);
        } catch (Throwable throwable) {
            LegacyStateController.log("Failed to update input method composition position", throwable);
        }
    }

    private void scheduleEnglishState() {
        if (!backend.supportsConversionStatus()
                || !"CONVERSION_STATUS".equalsIgnoreCase(LegacyConfig.englishStateMode)) {
            return;
        }
        long elapsed = System.currentTimeMillis() - lastEnabledAt;
        long delay = Math.max(0L, LegacyConfig.conversionStatusCooldownMs - elapsed);
        conversionExecutor.schedule(() -> {
            try {
                if (enabled) {
                    backend.setEnglishMode(english);
                }
            } catch (Throwable throwable) {
                LegacyStateController.log("Failed to update input method conversion mode", throwable);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
