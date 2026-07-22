package io.github.reserveword.imblocker.common;

import java.io.IOException;

public final class LinuxEnglishStateManager {
	static void setEnglishState(boolean isEN) {
		String command = isEN ? IMBlockerConfig.INSTANCE.getEnglishStateOnCommand() : 
			IMBlockerConfig.INSTANCE.getEnglishStateOffCommand();
		try {
			Runtime.getRuntime().exec(command.split(" "));
		} catch (IOException e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Invalid Command: {}", command);
		}
	}
}
