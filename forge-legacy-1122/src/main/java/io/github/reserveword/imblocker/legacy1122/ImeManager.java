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
    private volatile boolean enabled;
    private volatile boolean requestedEnabled;
    private volatile boolean english = true;
    private volatile boolean englishLocked;
    private volatile boolean nativeStateDirty = true;
    private boolean stateInitialized;
    private long lastEnabledAt;
    private long lastReconcileAt;
    private int watchdogTicks;

    private ImeManager() {
        ImeBackend selected;
        if (System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("win")) {
            try {
                selected = new WindowsImeBackend();
                System.out.println("[IMBlocker] Windows IMM32 input method backend initialized.");
            } catch (Throwable throwable) {
                selected = new NoopImeBackend();
                LegacyStateController.log("Windows IME backend is unavailable", throwable);
                System.err.println("[IMBlocker] Windows IMM32 backend unavailable: " + throwable);
            }
        } else {
            selected = new NoopImeBackend();
        }
        backend = selected;
    }

    synchronized void setEnabled(boolean value) {
        boolean changed = requestedEnabled != value;
        requestedEnabled = value;
        applyRequestedState();
        if (changed) {
            nativeStateDirty = true;
        }
    }

    private void applyRequestedState() {
        boolean shouldEnable = requestedEnabled
                && !("DISABLE_IM".equalsIgnoreCase(LegacyConfig.englishStateMode)
                && englishLocked && english);
        if (stateInitialized && enabled == shouldEnable) {
            if (shouldEnable) {
                scheduleEnglishState();
            }
            return;
        }
        try {
            if (!backend.setEnabled(shouldEnable)) {
                return;
            }
            enabled = shouldEnable;
            stateInitialized = true;
            if (shouldEnable) {
                lastEnabledAt = System.currentTimeMillis();
                scheduleEnglishState();
            }
        } catch (Throwable throwable) {
            LegacyStateController.log("Failed to update input method state", throwable);
        }
    }

    synchronized void setEnglishMode(boolean value) {
        setEnglishMode(value, true);
    }

    synchronized void setEnglishMode(boolean value, boolean lock) {
        if (english == value && englishLocked == lock) {
            return;
        }
        english = value;
        englishLocked = lock;
        applyRequestedState();
        nativeStateDirty = true;
    }

    synchronized void clearEnglishModeLock() {
        if (!englishLocked) {
            return;
        }
        englishLocked = false;
        applyRequestedState();
        nativeStateDirty = true;
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

    void onWindowFocusGained() {
        nativeStateDirty = true;
        try {
            backend.onWindowFocusGained();
        } catch (Throwable throwable) {
            LegacyStateController.log("Failed to refresh the native IME window", throwable);
        }
        reconcileNow(true);
    }

    void onNativeStateChanged() {
        nativeStateDirty = true;
    }

    void tick(boolean active) {
        if (!active || !LegacyConfig.watchdogEnabled) {
            return;
        }
        watchdogTicks++;
        if (nativeStateDirty || watchdogTicks >= LegacyConfig.watchdogIntervalTicks) {
            watchdogTicks = 0;
            reconcileNow(false);
        }
    }

    private synchronized void reconcileNow(boolean focusGained) {
        if (!LegacyConfig.watchdogEnabled && !focusGained) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!focusGained && now - lastReconcileAt < LegacyConfig.watchdogCooldownMs) {
            return;
        }
        nativeStateDirty = false;
        lastReconcileAt = now;
        boolean shouldEnable = requestedEnabled
                && !("DISABLE_IM".equalsIgnoreCase(LegacyConfig.englishStateMode)
                && englishLocked && english);
        try {
            backend.reconcile(shouldEnable, englishLocked, english);
        } catch (Throwable throwable) {
            LegacyStateController.log("Failed to reconcile native IME state", throwable);
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
                    if (englishLocked) {
                        backend.setEnglishMode(english);
                    }
                }
            } catch (Throwable throwable) {
                LegacyStateController.log("Failed to update input method conversion mode", throwable);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
