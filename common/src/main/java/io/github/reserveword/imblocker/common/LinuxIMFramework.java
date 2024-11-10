package io.github.reserveword.imblocker.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum LinuxIMFramework {
	IBUS("ibus engine", "ibus engine", "libpinyin", "xkb:us::eng", "libpinyin"),
	FCITX5("fcitx5-remote", "fcitx5-remote", "-o", "-c", "2");
	
	private final String setStateCommand;
	private final String[] getStateCommand;
	private final String onArgName;
	private final String offArgName;
	private final String onStateName;
	
	LinuxIMFramework(String setStateCommand, String getStateCommand, 
			String onArgName, String offArgName, String onStateName) {
		this.setStateCommand = setStateCommand;
		this.getStateCommand = getStateCommand.split(" ");
		this.onArgName = onArgName;
		this.offArgName = offArgName;
		this.onStateName = onStateName;
	}
	
	public void setState(boolean state) {
		String[] command = (setStateCommand + " " + (state ? onArgName : offArgName)).split(" ");
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getState() {
		String stateName = "";
		try {
			Process process = Runtime.getRuntime().exec(getStateCommand);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			stateName = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stateName != null && stateName.equals(onStateName);
	}
}
