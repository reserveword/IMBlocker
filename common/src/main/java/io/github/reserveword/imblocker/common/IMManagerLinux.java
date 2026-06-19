package io.github.reserveword.imblocker.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

final class IMManagerLinux implements IMManager.PlatformIMManager {
	private LinuxIMFramework imFramework = LinuxIMFramework.IBUS;
	private static boolean state = false;
	
	@Override
	public void setState(boolean on) {
		if(state != on) {
			switch (IMBlockerConfig.INSTANCE.getLinuxIMCommandType()) {
				case DEFAULT:
					checkIMFramework();
					imFramework.setState(on);
					break;
				case FULL:
					setStateWithFullCommand(on);
					break;
			}
			IMManagerLinux.state = on;
		}
	}

	@Override
	public void setEnglishState(boolean isEN) {
		
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
	
	private void setStateWithFullCommand(boolean on) {
		String command = on ? IMBlockerConfig.INSTANCE.getFullEnableCommand() : 
					IMBlockerConfig.INSTANCE.getFullDisableCommand();
		try {
			Runtime.getRuntime().exec(command.split(" "));
		} catch (IOException e) {
			IMBlockerCore.LOGGER.error("[IMBlocker] Invalid Command: {}", command);
		}
	}
}
