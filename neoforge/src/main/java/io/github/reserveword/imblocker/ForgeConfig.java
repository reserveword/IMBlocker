package io.github.reserveword.imblocker;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.tuple.Pair;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Common;
import io.github.reserveword.imblocker.common.Config;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ForgeConfig extends Config {

	public static final ModConfigSpec clientSpec;
    public static final ForgeConfig.Client CLIENT;
    private final static Set<Class<?>> recoveredScreens = new HashSet<>();
    private static Set<Class<?>> screenWhitelist;

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ForgeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        Config.INSTANCE = new ForgeConfig();
    }

    private ForgeConfig() {
    }

    public static void reload() {
        screenWhitelist = bakeList(CLIENT.screenWhitelist, "screenWhitelist");
    }

    private static Set<Class<?>> bakeList(ModConfigSpec.ConfigValue<List<? extends String>> cfg, String name) {
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
        ModList.get().forEachModFile(mod -> {
            try {
                String modid = mod.getModInfos().get(0).getModId();
                if (!"minecraft".equals(modid) && !"imblocker".equals(modid) && Objects.equals(loc,
                        mod.getFilePath().toUri().toURL())) {
                    name.set(modid + ":" + cls.getName());
                }
            } catch (NullPointerException npe) {
                String modid = mod.getModInfos().get(0).getModId();
                Common.LOGGER.error("something is null when grabbing mod jar:");
                Common.LOGGER.warn("modid {}, file {}", modid, mod.getFileName());
                Common.LOGGER.error("enableScreenRecovering disabled.");
                CLIENT.enableScreenRecovering.set(false);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IndexOutOfBoundsException e) {
                // do nothing
            }
        });
        return name.get();
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {

        public final ModConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;
        private final ModConfigSpec.ConfigValue<Boolean> enableScreenRecovering;

        private final ModConfigSpec.ConfigValue<List<? extends String>> recoveredScreens;
        
        private final ModConfigSpec.EnumValue<ChatCommandInputType> chatCommandInputType;

        Client(ModConfigSpec.Builder builder) {
            screenWhitelist = builder
            		.comment("Matched screens would enable your IME")
            		.translation("key.imblocker.screenWhitelist")
            		.defineList("screenWhitelist", Arrays.asList(
            				BookEditScreen.class.getName(),
            				SignEditScreen.class.getName(),
            				HangingSignEditScreen.class.getName(),
            				"journeymap.client.ui.waypoint.WaypointEditor",
            				"com.ldtteam.blockout.BOScreen"
            				), checkClassForName);
            
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
    }
}
