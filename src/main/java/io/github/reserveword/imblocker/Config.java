package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModList;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public abstract class Config {
    public static final Predicate<Object> checkClassForName = str -> (str instanceof String) &&
            ((String) str).matches("^([\\p{L}_][\\p{L}\\p{N}_]*:)?([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*$");
    public static Config INSTANCE = null;

    public abstract boolean inScreenBlacklist(Class<?> cls);

    public abstract boolean inScreenWhitelist(Class<?> cls);

    public abstract boolean inInputBlacklist(Class<?> cls);

    public abstract boolean inInputWhitelist(Class<?> cls);

    public abstract Integer getCheckInterval();

    public abstract Boolean getEnableScreenRecovering();

    public abstract void setEnableScreenRecovering(Boolean value);

    public abstract Boolean getUseExperimental();

    public abstract Boolean getCheckCommandChat();

    public abstract void checkScreen(Class<? extends Screen> cls);

    public String getClassName(Class<?> cls) {
        CodeSource source = cls.getProtectionDomain().getCodeSource();
        if (source == null || cls.getName().startsWith("net.minecraft.")) {
            return "minecraft" + ":" + cls.getName();
        }
        URL loc = source.getLocation();
        AtomicReference<String> name = new AtomicReference<>("UNKNOWN_SCREEN");
        ModList.get().forEachModContainer((modid, mod) -> {
            try {
                if (!"minecraft".equals(modid) && !"imblocker".equals(modid) && loc.equals(mod.getMod().getClass()
                        .getProtectionDomain().getCodeSource().getLocation())) {
                    name.set(modid + ":" + cls.getName());
                }
            } catch (NullPointerException npe) {
                IMBlocker.LOGGER.error("something is null when grabbing mod jar:");
                Object modobj = mod.getMod();
                Class<?> modcls = modobj != null ? modobj.getClass() : null;
                ProtectionDomain pd = modcls != null ? modcls.getProtectionDomain() : null;
                CodeSource cs = pd != null ? pd.getCodeSource() : null;
                IMBlocker.LOGGER.warn("modid {}, mod {}, class {}, domain {}, source {}",
                        modid, modobj, modcls, pd, cs);
                IMBlocker.LOGGER.error("enableScreenRecovering disabled.");
                setEnableScreenRecovering(false);
            }
        });
        return name.get();
    }

    public abstract void reload();

}
