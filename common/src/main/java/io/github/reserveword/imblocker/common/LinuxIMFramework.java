package io.github.reserveword.imblocker.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Supplier;

public enum LinuxIMFramework {
	IBUS("ibus engine", "ibus engine",
			IMBlockerConfig.INSTANCE::getIBusOnArgName,
			IMBlockerConfig.INSTANCE::getIBusOffArgName,
			IMBlockerConfig.INSTANCE::getIBusOnStateName),
	FCITX5("fcitx5-remote", "fcitx5-remote",
			IMBlockerConfig.INSTANCE::getFcitx5OnArgName,
			IMBlockerConfig.INSTANCE::getFcitx5OffArgName,
			IMBlockerConfig.INSTANCE::getFcitx5OnStateName);

	private final String setStateCommand;
	private final String[] getStateCommand;
	private final Supplier<String> onArgSupplier;
	private final Supplier<String> offArgSupplier;
	private final Supplier<String> onStateSupplier;

	LinuxIMFramework(String setStateCommand, 
			String getStateCommand,
			Supplier<String> onArgSupplier, 
			Supplier<String> offArgSupplier, 
			Supplier<String> onStateSupplier) {
		this.setStateCommand = setStateCommand;
		this.getStateCommand = getStateCommand.split(" ");
		this.onArgSupplier = onArgSupplier;
		this.offArgSupplier = offArgSupplier;
		this.onStateSupplier = onStateSupplier;
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
		return stateName != null && stateName.equals(onStateSupplier.get());
	}

	public void setState(boolean state) {
		String[] command = (setStateCommand + " " + (state ? 
				onArgSupplier.get() : offArgSupplier.get())).split(" ");
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
