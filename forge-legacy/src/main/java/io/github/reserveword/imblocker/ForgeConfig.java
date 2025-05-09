package io.github.reserveword.imblocker;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeConfig extends Config {

	public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfig.Client CLIENT;
    private final static Set<String> recoveredScreens = new LinkedHashSet<>();
    private static Set<Class<?>> screenWhitelist;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ForgeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        Config.INSTANCE = new ForgeConfig();
    }

    private ForgeConfig() {
    }

    public static void reload() {
        screenWhitelist = bakeList(CLIENT.screenWhitelist, "screenWhitelist");
    }

    private static Set<Class<?>> bakeList(ForgeConfigSpec.ConfigValue<List<? extends String>> cfg, String name) {
        Set<Class<?>> clsSet = new HashSet<>();
        for (String s: cfg.get()) {
            try {
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                clsSet.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                Common.LOGGER.warn("Class {} not found, ignored.", s);
            } catch (Throwable e) {
                Common.LOGGER.warn(e);
            }
        }
        Common.LOGGER.info("imblocker bakelist {} result {}", name, clsSet);
        return clsSet;
    }

    @Override
    public boolean inScreenWhitelist(Class<?> cls) {
        return screenWhitelist != null && screenWhitelist.contains(cls);
    }
    
    @Override
    public void recoverScreen(String screenClsName) {
    	CLIENT.recoveredScreens.get().forEach(recoveredScreens::add);
    	recoveredScreens.add(screenClsName);
    	CLIENT.recoveredScreens.set(Lists.newArrayList(recoveredScreens));
    	recoveredScreens.clear();
    }
    
    @Override
    public boolean isScreenRecoveringEnabled() {
    	return CLIENT.enableScreenRecovering.get();
    }
    
    @Override
    public ChatCommandInputType getChatCommandInputType() {
    	return CLIENT.chatCommandInputType.get();
    }

    public String getClassName(Class<?> cls) {
        CodeSource source = cls.getProtectionDomain().getCodeSource();
        if (source == null || cls.getName().startsWith("net.minecraft.")) {
            return "minecraft" + ":" + cls.getName();
        }
        URL loc = source.getLocation();
        AtomicReference<String> name = new AtomicReference<>("UNKNOWN_SCREEN");
        ModList.get().forEachModContainer((modid, mod) -> {
            try {
                if (!"minecraft".equals(modid) && !"imblocker".equals(modid) && loc == mod.getMod().getClass()
                                                                                               .getProtectionDomain().getCodeSource().getLocation()) {
                    name.set(modid + ":" + cls.getName());
                }
            } catch (NullPointerException npe) {
                Common.LOGGER.error("something is null when grabbing mod jar:");
                Object modobj = mod.getMod();
                Class<?> modcls = modobj != null ? modobj.getClass() : null;
                ProtectionDomain pd = modcls != null ? modcls.getProtectionDomain() : null;
                CodeSource cs = pd != null ? pd.getCodeSource() : null;
                Common.LOGGER.warn("modid {}, mod {}, class {}, domain {}, source {}",
                        modid, modobj, modcls, pd, cs);
                Common.LOGGER.error("enableScreenRecovering disabled.");
                CLIENT.enableScreenRecovering.set(false);
            }
        });
        return name.get();
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableScreenRecovering;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> recoveredScreens;
        
        private final ForgeConfigSpec.EnumValue<ChatCommandInputType> chatCommandInputType;

        Client(ForgeConfigSpec.Builder builder) {
        	Minecraft.getInstance().getLaunchedVersion();
            screenWhitelist = builder
            		.comment("Matched screens would enable your IME")
            		.translation("key.imblocker.screenWhitelist")
            		.defineList("screenWhitelist", getDefaultScreenWhitelist(), checkClassForName);
            
            enableScreenRecovering = builder
            		.comment("Do we output recoveredScreens? because it may cause lag")
            		.translation("key.imblocker.enableScreenRecovering")
            		.define("enableScreenRecovering", false);

            recoveredScreens = builder
            		.comment("Here lists all Screens that is not in whitelist nor blacklist, ",
            				"so you may easily add those to whitelist/blacklist.")
            		.translation("key.imblocker.recoveredScreens")
            		.defineList("recoveredScreens", Collections.emptyList(), s -> true);
            
            chatCommandInputType = builder
            		.comment("If your input method can't auto-switch to English state when using command syntax "
            				+ "in chat field, set this option to DISABLE_IM may help, "
            				+ "but note that you can only type English when typing command in this mode.")
            		.translation("key.imblocker.chatCommandInputType")
            		.defineEnum("chatCommandInputType", ChatCommandInputType.IM_ENG_STATE);
        }
        
        private List<String> getDefaultScreenWhitelist() {
        	List<String> defaultScreenWhitelist = Lists.newArrayList(
        			EditBookScreen.class.getName(),
        			EditSignScreen.class.getName(),
        			"journeymap.client.ui.waypoint.WaypointEditor",
        			"com.ldtteam.blockout.BOScreen");
        	return defaultScreenWhitelist;
        }
    }
}
