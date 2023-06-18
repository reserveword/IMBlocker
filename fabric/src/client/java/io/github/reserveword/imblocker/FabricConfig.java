package io.github.reserveword.imblocker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@me.shedaniel.autoconfig.annotation.Config(name = Common.MODID)
public class FabricConfig extends Config implements ModMenuApi, ConfigData {
    int checkInterval = 2;
    ArrayList<String> screenBlacklist = new ArrayList<>();
    ArrayList<String> screenWhitelist = new ArrayList<>(FabricCommon.defaultScreenWhitelist);
    ArrayList<String> inputBlacklist = new ArrayList<>();
    ArrayList<String> inputWhitelist = new ArrayList<>();
    boolean enableScreenRecovering = false;
    ArrayList<String> recoveredScreens = new ArrayList<>();
    boolean useExperimental = true;
    boolean checkCommandChat = true;

    private static final Map<Class<?>, String> classCache = new HashMap<>();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FabricConfig.class, parent).get();
    }

    @Override
    public void validatePostLoad() {
        for (Collection<String> list: Arrays.asList(screenBlacklist, screenWhitelist, inputBlacklist, inputWhitelist)) {
            for (String cls: list) {
                String className = cls;
                try {
                    if (cls.contains(":")) {
                        String[] ss = cls.split(":");
                        cls = ss[ss.length - 1];
                    }
                    classCache.put(Class.forName(cls), className);
                } catch (ClassNotFoundException e) {
                    Common.LOGGER.warn("Class {} not found, ignored.", cls);
                }
            }
        }
    }

    @Override
    public boolean inScreenBlacklist(Class<?> cls) {
        return screenBlacklist.contains(classCache.get(cls));
    }

    @Override
    public boolean inScreenWhitelist(Class<?> cls) {
        return screenWhitelist.contains(classCache.get(cls));
    }

    @Override
    public boolean inInputBlacklist(Class<?> cls) {
        return inputBlacklist.contains(classCache.get(cls));
    }

    @Override
    public boolean inInputWhitelist(Class<?> cls) {
        return inputWhitelist.contains(classCache.get(cls));
    }

    @Override
    public Integer getCheckInterval() {
        return checkInterval;
    }

    @Override
    public Boolean getUseExperimental() {
        return useExperimental;
    }

    @Override
    public Boolean getCheckCommandChat() {
        return checkCommandChat;
    }

    @Override
    public void checkScreen(Class<?> cls) {
        if (enableScreenRecovering) {
            recoveredScreens.add(getClassName(cls));
        }
    }

    public String getClassName(Class<?> cls) {
        CodeSource source = cls.getProtectionDomain().getCodeSource();
        if (source == null || cls.getName().startsWith("net.minecraft.")) {
            return "minecraft" + ":" + cls.getName();
        }
        URL locUrl = source.getLocation();
        if (locUrl == null) {
            return cls.getName();
        }
        AtomicReference<String> name = new AtomicReference<>(cls.getName());
        try {
            URI loc = locUrl.toURI();
            FabricLoader.getInstance().getAllMods().forEach(mod -> {
                String modid = mod.getMetadata().getId();
                try {
                    if (!"minecraft".equals(modid) && !"imblocker".equals(modid) &&
                            mod.getRootPaths().stream().anyMatch(path -> path.toUri().equals(loc))) {
                        name.set(modid + ":" + cls.getName());
                    }
                } catch (NullPointerException npe) {
                    Common.LOGGER.error("something is null when grabbing mod jar: {}", mod.getMetadata().getId());
                    Common.LOGGER.error("enableScreenRecovering disabled.");
                    enableScreenRecovering = false;
                }
            });
        } catch (URISyntaxException e) {
            Common.LOGGER.error(e);
            Common.LOGGER.error("enableScreenRecovering disabled.");
            enableScreenRecovering = false;
        }
        return name.get();
    }
}
