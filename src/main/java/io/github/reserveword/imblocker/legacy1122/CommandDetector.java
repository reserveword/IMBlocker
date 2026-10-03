package io.github.reserveword.imblocker.legacy1122;

final class CommandDetector {
    private CommandDetector() {}

    static boolean isCommand(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (String prefix : LegacyConfig.commandPrefixes) {
            if (prefix != null && !prefix.isEmpty() && text.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    static boolean isCommandAfter(String text, char character) {
        return isCommand((text == null ? "" : text) + character);
    }
}
