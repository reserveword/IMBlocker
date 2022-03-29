package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

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

        public final Predicate<Object> checkClassForName = s -> {
            try {
                Class.forName((String) s);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        };

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings")
                    .push("client");

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

            builder.pop();
        }
    }

    public static Collection<Class<?>> screenBlacklist;
    public static Collection<Class<?>> screenWhitelist;
    public static Collection<Class<?>> inputBlacklist;
    public static Collection<Class<?>> inputWhitelist;
    private static Collection<Class<?>> bakeList(ForgeConfigSpec.ConfigValue<List<? extends String>> cfg, String name) {
        Collection<Class<?>> collection = new ArrayList<>();
        for (String s : cfg.get()) {
            try {
                collection.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        IMBlocker.LOGGER.debug("imblocker {} result {}", name, collection);
        return Collections.unmodifiableCollection(collection);
    }

    public static void reload() {
        screenWhitelist = bakeList(CLIENT.screenWhitelist, "screenWhitelist");
        screenBlacklist = bakeList(CLIENT.screenBlacklist, "screenBlacklist");
        inputWhitelist = bakeList(CLIENT.inputWhitelist, "inputWhitelist");
        inputBlacklist = bakeList(CLIENT.inputBlacklist, "inputBlacklist");
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static {
        final Pair<Config.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}
