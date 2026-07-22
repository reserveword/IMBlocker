package com.terraformersmc.modmenu.api;

public interface ModMenuApi {
	default ConfigScreenFactory<?> getModConfigScreenFactory() { return null; }
}
