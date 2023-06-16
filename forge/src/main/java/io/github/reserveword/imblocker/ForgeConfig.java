package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber
public class ForgeConfig extends Config {

    @Override
    public boolean inScreenBlacklist(Class<?> cls) {
        return screenBlacklist.contains(cls);
    }

    @Override
    public boolean inScreenWhitelist(Class<?> cls) {
        return screenWhitelist.contains(cls);
    }

    @Override
    public boolean inInputBlacklist(Class<?> cls) {
        return inputBlacklist.contains(cls);
    }

    @Override
    public boolean inInputWhitelist(Class<?> cls) {
        return inputWhitelist.contains(cls);
    }

    @Override
    public Integer getCheckInterval() {
        return CLIENT.checkInterval.get();
    }

    @Override
    public Boolean getEnableScreenRecovering() {
        return CLIENT.enableScreenRecovering.get();
    }

    @Override
    public void setEnableScreenRecovering(Boolean value) {
        CLIENT.enableScreenRecovering.set(value);
    }

    @Override
    public Boolean getUseExperimental() {
        return CLIENT.useExperimental.get();
    }

    @Override
    public Boolean getCheckCommandChat() {
        return CLIENT.checkCommandChat.get();
    }

    @Override
    public void checkScreen(Class<?> cls) {
        if (getEnableScreenRecovering() && !recoveredScreens.contains(cls)) {
            recoveredScreens.add(cls);
            List<String> screens = new ArrayList<>(CLIENT.recoveredScreens.get());
            screens.add(getClassName(cls));
            CLIENT.recoveredScreens.set(screens);
        }
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
                if (!"minecraft".equals(modid) && !"imblocker".equals(modid) && loc.equals(mod.getMod().getClass()
                        .getProtectionDomain().getCodeSource().getLocation())) {
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
                setEnableScreenRecovering(false);
            }
        });
        return name.get();
    }

    public static void reload() {
        screenWhitelist = bakeList(CLIENT.screenWhitelist, "screenWhitelist");
        screenBlacklist = bakeList(CLIENT.screenBlacklist, "screenBlacklist");
        inputWhitelist = bakeList(CLIENT.inputWhitelist, "inputWhitelist");
        inputBlacklist = bakeList(CLIENT.inputBlacklist, "inputBlacklist");
    }

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {

        private final ForgeConfigSpec.ConfigValue<Integer> checkInterval;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenBlacklist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> inputWhitelist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> inputBlacklist;

        private final ForgeConfigSpec.ConfigValue<Boolean> enableScreenRecovering;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> recoveredScreens;

        private final ForgeConfigSpec.ConfigValue<Boolean> useExperimental;

        private final ForgeConfigSpec.ConfigValue<Boolean> checkCommandChat;

        Client(ForgeConfigSpec.Builder builder) {
            checkInterval = builder
                    .comment("Checking every tick is not efficient, how about check once every several tick?")
                    .translation("key.imblocker.checkInterval")
                    .defineInRange("checkInterval", 2, 1, Integer.MAX_VALUE);

            screenBlacklist = builder
                    .comment("Matched screens would disable your IME")
                    .translation("key.imblocker.screenBlacklist")
                    .defineList("screenBlacklist", Collections.emptyList(), checkClassForName);

            screenWhitelist = builder
                    .comment("Matched screens would enable your IME")
                    .translation("key.imblocker.screenWhitelist")
                    .defineList("screenWhitelist", Arrays.asList(
                            BookEditScreen.class.getName(),
                            SignEditScreen.class.getName(),
                            "journeymap.client.ui.waypoint.WaypointEditor",
                            "com.ldtteam.blockout.BOScreen"
                    ), checkClassForName);

            inputBlacklist = builder
                    .comment("Matched input box would disable your IME")
                    .translation("key.imblocker.inputBlacklist")
                    .defineList("inputBlacklist", Collections.emptyList(), checkClassForName);

            inputWhitelist = builder
                    .comment("Matched input box would enable your IME")
                    .translation("key.imblocker.inputWhitelist")
                    .defineList("inputWhitelist", Collections.emptyList(), checkClassForName);

            enableScreenRecovering = builder
                    .comment("Do we output recoveredScreens? because it may cause lag")
                    .translation("key.imblocker.enableScreenRecovering")
                    .define("enableScreenRecovering", false);

            recoveredScreens = builder
                    .comment("Here lists all Screens that is not in whitelist nor blacklist, ",
                            "so you may easily add those to whitelist/blacklist.")
                    .translation("key.imblocker.recoveredScreens")
                    .defineList("recoveredScreens", Collections.emptyList(), s -> true);

            useExperimental = builder
                    .comment("Disable this and let me know if input or control is messed up")
                    .translation("key.imblocker.useExperimental")
                    .define("useExperimental", true);

            checkCommandChat = builder
                    .comment("Disable IME when typing commands")
                    .translation("key.imblocker.checkCommandChat")
                    .define("checkCommandChat", true);
        }
    }

    private static Set<Class<?>> screenBlacklist;
    private static Set<Class<?>> screenWhitelist;
    private static Set<Class<?>> inputBlacklist;
    private static Set<Class<?>> inputWhitelist;
    private final static Set<Class<?>> recoveredScreens = new HashSet<>();
    private static Set<Class<?>> bakeList(ForgeConfigSpec.ConfigValue<List<? extends String>> cfg, String name) {
        Set<Class<?>> clsSet = new HashSet<>();
        for (String s : cfg.get()) {
            try {
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                clsSet.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                Common.LOGGER.warn("Class {} not found, ignored.", s);
            }
        }
        Common.LOGGER.info("imblocker bakelist {} result {}", name, clsSet);
        return clsSet;
    }

    public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfig.Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ForgeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        Config.INSTANCE = new ForgeConfig();
    }

    private ForgeConfig() {}
}
