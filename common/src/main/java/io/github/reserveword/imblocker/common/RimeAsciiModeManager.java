package io.github.reserveword.imblocker.common;

import java.io.IOException;

import com.sun.jna.Platform;

public class RimeAsciiModeManager {
	public static final String IME_NAME;
	public static final String BASE_COMMAND;
	public static final String ON_ARG;
	public static final String OFF_ARG;
	
	public static void setAsciiMode(boolean isEN) {
		String command = BASE_COMMAND + (isEN ? ON_ARG : OFF_ARG);
		try {
			Runtime.getRuntime().exec(command.split(" "));
		} catch (IOException e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] {} not found!", IME_NAME);
		}
	}
	
	static {
		if (Platform.isWindows()) {
			IME_NAME = "Weasel";
			BASE_COMMAND = "WeaselServer.exe ";
			ON_ARG = "/ascii";
			OFF_ARG = "/nascii";
		} else if (Platform.isMac()) {
			IME_NAME = "Squirrel";
			BASE_COMMAND = "/Library/Input\\ Methods/Squirrel.app/Contents/MacOS/Squirrel ";
			ON_ARG = "--ascii";
			OFF_ARG = "--nascii";
		} else if (Platform.isLinux()) {
			IME_NAME = "Fcitx5-Rime";
			BASE_COMMAND = "gdbus call --session --dest org.fcitx.Fcitx5 --object-path /rime --method org.fcitx.Fcitx.Rime1.SetAsciiMode ";
			ON_ARG = "true";
			OFF_ARG = "false";
		} else {
			IME_NAME = "";
			BASE_COMMAND = "";
			ON_ARG = "";
			OFF_ARG = "";
		}
	}
}
