package io.github.reserveword.imblocker.legacy1122;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

final class LegacyNames {
    private LegacyNames() {}

    static String runtimeMethod(String deobfuscated, String srg, String descriptor) {
        try {
            return FMLLaunchHandler.isDeobfuscatedEnvironment() ? deobfuscated : srg;
        } catch (Throwable ignored) {
            // The remapper is not ready during some early launch phases.
        }
        return srg;
    }
}
