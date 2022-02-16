package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@Mod.EventBusSubscriber
public class Config {

    /**
     * Client specific configuration - only loaded clientside from forge-client.toml
     */
    public static class Client {

        public final ForgeConfigSpec.ConfigValue<Integer> checkDelay;

        public final ForgeConfigSpec.ConfigValue<Integer> checkInterval;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> imeBlacklist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings")
                    .push("client");

            checkDelay = builder
                    .comment("We want to check if any TextWidgetField comes to life after several ticks.",
                            "How many ticks should we check?",
                            "Larger number means more precise and more error-tolerant, but less efficiency.",
                            "Better be rounded to an exact multiple of `checkInterval`")
                    .translation("key.imblocker.checkDelay")
                    .defineInRange("checkDelayTime", 10, 1, Integer.MAX_VALUE);

            checkInterval = builder
                    .comment("Checking Every tick is not efficient, how about check once every several tick?")
                    .translation("key.imblocker.checkInterval")
                    .defineInRange("checkInterval", 2, 1, Integer.MAX_VALUE);

            imeBlacklist = builder
                    .comment("Matched screens would disable your IME")
                    .translation("key.imblocker.screenWhitelist")
                    .defineList("screenBlacklist", Collections.emptyList(), s -> {
                        try {
                            Class.forName((String) s);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    });

            screenWhitelist = builder
                    .comment("Matched screens would enable your IME")
                    .translation("key.imblocker.screenWhitelist")
                    .defineList("screenWhitelist", Arrays.asList(
                            BookEditScreen.class.getName(),
                            SignEditScreen.class.getName()
                    ), s -> {
                        try {
                            Class.forName((String) s);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    });

            builder.pop();
        }
    }

    private static final Collection<Class<?>> screens = new ArrayList<>();
    private static Collection<Class<?>> cache = null;
    private static void bakeScreenWhitelist() {
        screens.clear();
        for (String s: CLIENT.screenWhitelist.get()) {
            try {
                screens.add(Class.forName(s));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        IMBlocker.LOGGER.debug("imblocker config result {}", screens);
    }
    public static Collection<Class<?>> getScreenWhitelist() {
        if (cache == null) {
            cache = Collections.unmodifiableCollection(screens);
        }
        return cache;
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static {
        final Pair<Config.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        bakeScreenWhitelist();
    }
}
