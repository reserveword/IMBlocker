package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@me.shedaniel.autoconfig.annotation.Config(name = Common.MODID)
public class FabricConfig extends Config implements ModMenuApi, ConfigData {
    ArrayList<String> screenBlacklist = new ArrayList<>(FabricCommon.defaultScreenBlacklist);
    ArrayList<String> screenWhitelist = new ArrayList<>(FabricCommon.defaultScreenWhitelist);
    ArrayList<String> inputBlacklist = new ArrayList<>();
    ArrayList<String> inputWhitelist = new ArrayList<>();
    boolean enableScreenRecovering = false;
    ArrayList<String> recoveredScreens = new ArrayList<>();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FabricConfig.class, parent).get();
    }

    @Override
    public void validatePostLoad() {
        for (List<String> list: Arrays.asList(screenBlacklist, screenWhitelist, inputBlacklist, inputWhitelist)) {
            for (int i = 0; i < list.size(); i++) {
                String cls = list.get(i);
                if (cls.contains(":")) {
                    String[] ss = cls.split(":");
                    list.set(i, ss[ss.length - 1]);
                }
            }
        }
    }

    @Override
    public boolean inScreenBlacklist(Class<?> cls) {
        return screenBlacklist.contains(cls == null ? null : cls.getName());
    }

    @Override
    public boolean inScreenWhitelist(Class<?> cls) {
        return cls != null && screenWhitelist.contains(cls.getName());
    }

    @Override
    public boolean inInputBlacklist(Class<?> cls) {
        return inputBlacklist.contains(cls == null ? null : cls.getName());
    }

    @Override
    public boolean inInputWhitelist(Class<?> cls) {
        return inputWhitelist.contains(cls == null ? null : cls.getName());
    }

    @Override
    public void recoverScreen(Class<?> cls) {
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
                if (!"minecraft".equals(modid)
                    && !"imblocker".equals(modid)
                    && mod.getRootPaths().stream().anyMatch(path -> path.toUri().equals(loc))
                ) {
                    name.set(modid + ":" + cls.getName());
                }
            });
        } catch (Throwable e) {
            Common.LOGGER.error(e);
            Common.LOGGER.error("enableScreenRecovering disabled.");
            enableScreenRecovering = false;
        }
        return name.get();
    }
}
