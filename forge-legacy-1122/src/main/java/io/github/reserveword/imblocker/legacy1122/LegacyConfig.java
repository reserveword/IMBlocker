package io.github.reserveword.imblocker.legacy1122;

import net.minecraftforge.common.config.Configuration;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;

final class LegacyConfig {
    static boolean enabled = true;
    static boolean commandEnglishMode = true;
    static boolean cursorTracking = true;
    static boolean conversionStatusApi = true;
    static String englishStateMode = "CONVERSION_STATUS";
    static boolean preserveConversionFlags = true;
    static int conversionStatusCooldownMs = 60;
    static boolean watchdogEnabled = true;
    static int watchdogIntervalTicks = 5;
    static int watchdogCooldownMs = 150;
    static int compositionWindowYOffset = 0;
    static boolean debug = false;
    static String[] screenWhitelist = new String[0];
    static String[] commandPrefixes = new String[]{"/"};
    static KeyBinding unlockKey = new KeyBinding("key.imblocker.unlock", Keyboard.KEY_RSHIFT,
            "key.categories.imblocker");

    private static Configuration configuration;

    private LegacyConfig() {}

    static void load(File file) {
        configuration = new Configuration(file);
        reload();
    }

    static void reload() {
        if (configuration == null) {
            return;
        }
        configuration.load();
        enabled = configuration.getBoolean("enabled", "general", true,
                "Enable automatic input method state management.");
        commandEnglishMode = configuration.getBoolean("commandEnglishMode", "general", true,
                "Prefer English input when the chat line is a command.");
        cursorTracking = configuration.getBoolean("cursorTracking", "general", true,
                "Move the native IME composition window near the focused text field.");
        conversionStatusApi = configuration.getBoolean("conversionStatusApi", "windows", true,
                "Use the IMM32 conversion-status API for switching Chinese and English mode.");
        englishStateMode = configuration.getString("englishStateMode", "windows", "CONVERSION_STATUS",
                "CONVERSION_STATUS works with most Windows IMEs. Use DISABLE_IM if an IME ignores conversion-status changes.",
                new String[]{"CONVERSION_STATUS", "DISABLE_IM"});
        preserveConversionFlags = configuration.getBoolean("preserveConversionFlags", "windows", true,
                "Preserve IME-specific conversion flags used by Sogou, iFlytek, Rime and similar IMEs.");
        conversionStatusCooldownMs = configuration.getInt("conversionStatusCooldownMs", "windows", 60, 0, 500,
                "Delay conversion-status updates after enabling an IME context.");
        watchdogEnabled = configuration.getBoolean("watchdogEnabled", "windows", true,
                "Periodically verify the native IME state and repair external changes.");
        watchdogIntervalTicks = configuration.getInt("watchdogIntervalTicks", "windows", 5, 1, 40,
                "Client ticks between native IME state checks (20 ticks = about one second).");
        watchdogCooldownMs = configuration.getInt("watchdogCooldownMs", "windows", 150, 0, 1000,
                "Minimum delay between watchdog corrections after an IME notification.");
        compositionWindowYOffset = configuration.getInt("compositionWindowYOffset", "windows", 0, -32, 32,
                "Additional native composition-window Y offset in physical pixels.");
        debug = configuration.getBoolean("debug", "general", false,
                "Write state transitions to the client log.");
        commandPrefixes = configuration.getStringList("commandPrefixes", "chat", new String[]{"/"},
                "Prefixes that identify command input.");
        screenWhitelist = configuration.getStringList("screenWhitelist", "chat", new String[0],
                "Additional screen class names that should enable the input method.");
        configuration.save();
    }
}
