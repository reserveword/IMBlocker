package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class Config {

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {

        public final ForgeConfigSpec.ConfigValue<Integer> checkInterval;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenBlacklist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> inputWhitelist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> inputBlacklist;

        public final ForgeConfigSpec.ConfigValue<Boolean> enableScreenRecovering;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> recoveredScreens;

        public final ForgeConfigSpec.ConfigValue<Boolean> useExperimental;

        public final Predicate<Object> checkClassForName = str -> {
            try {
                String s = (String) str;
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                Class.forName(s);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        };

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
                            EditBookScreen.class.getName(),
                            EditSignScreen.class.getName()
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
        }
    }

    public static Collection<Class<?>> screenBlacklist;
    public static Collection<Class<?>> screenWhitelist;
    public static Collection<Class<?>> inputBlacklist;
    public static Collection<Class<?>> inputWhitelist;
    private final static Set<Class<?>> recoveredScreens = new HashSet<>();
    private static Thread screenSaveThread = null;
    private static Collection<Class<?>> bakeList(ForgeConfigSpec.ConfigValue<List<? extends String>> cfg, String name) {
        Collection<Class<?>> collection = new ArrayList<>();
        for (String s : cfg.get()) {
            try {
                if (s.contains(":")) {
                    String[] ss = s.split(":");
                    s = ss[ss.length - 1];
                }
                collection.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        IMBlocker.LOGGER.info("imblocker bakelist {} result {}", name, collection);
        return Collections.unmodifiableCollection(collection);
    }

    public static void checkScreen(Class<? extends Screen> cls) {
        if (!CLIENT.enableScreenRecovering.get() || recoveredScreens.contains(cls)) {
            return;
        } else {
            recoveredScreens.add(cls);
        }
        if (screenSaveThread == null || !screenSaveThread.isAlive()) {
            screenSaveThread = new Thread(Config::dump);
            screenSaveThread.start();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void dumpSet(ForgeConfigSpec.ConfigValue<List<? extends String>> cfg, Set<Class<?>> set, String name) {
        HashSet<String> cfg_new = new HashSet<>(cfg.get());
        set.forEach((cls) -> {
            CodeSource source = cls.getProtectionDomain().getCodeSource();
            if (source == null || cls.getName().startsWith("net.minecraft.")) {
                cfg_new.add("minecraft" + ":" + cls.getName());
                return;
            }
            URL loc = source.getLocation();
            ModList.get().forEachModContainer((modid, mod) -> {
                try {
                    if (!"minecraft".equals(modid) && !"imblocker".equals(modid) && loc.equals(mod.getMod().getClass()
                            .getProtectionDomain().getCodeSource().getLocation())) {
                        cfg_new.add(modid + ":" + cls.getName());
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
                    CLIENT.enableScreenRecovering.set(false);
                }
            });
        });
        ArrayList<String> cfg_list = new ArrayList<>(cfg_new);
        cfg_list.sort(null);
        cfg.set(cfg_list);
        cfg.save();
        IMBlocker.LOGGER.info("imblocker dumpmap {} result {}", name, cfg_list);
    }

    public static void reload() {
        screenWhitelist = bakeList(CLIENT.screenWhitelist, "screenWhitelist");
        screenBlacklist = bakeList(CLIENT.screenBlacklist, "screenBlacklist");
        inputWhitelist = bakeList(CLIENT.inputWhitelist, "inputWhitelist");
        inputBlacklist = bakeList(CLIENT.inputBlacklist, "inputBlacklist");
    }

    public static void dump() {
        dumpSet(CLIENT.recoveredScreens, recoveredScreens, "recoveredScreens");
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static {
        final Pair<Config.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}
