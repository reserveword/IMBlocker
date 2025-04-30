package io.github.reserveword.imblocker;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;

@me.shedaniel.autoconfig.annotation.Config(name = Common.MODID)
public class FabricConfig extends Config implements ModMenuApi, ConfigData {
    ArrayList<String> screenWhitelist = new ArrayList<>(FabricCommon.defaultScreenWhitelist);
    boolean enableScreenRecovering = false;
    ArrayList<String> recoveredScreens = new ArrayList<>();
    
    @ConfigEntry.Gui.Tooltip
    ChatCommandInputType chatCommandInputType = ChatCommandInputType.IM_ENG_STATE;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FabricConfig.class, parent).get();
    }

    @Override
    public void validatePostLoad() {
        for (List<String> list: Arrays.asList(screenWhitelist)) {
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
    public boolean inScreenWhitelist(Class<?> cls) {
        return screenWhitelist.contains(cls == null ? null : cls.getName());
    }
    
    @Override
    public ChatCommandInputType getChatCommandInputType() {
    	return chatCommandInputType;
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
                    if (!"minecraft".equals(modid)
                        && !"imblocker".equals(modid)
                        && mod.getRootPaths().stream().anyMatch(path -> path.toUri().equals(loc))
                    ) {
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
