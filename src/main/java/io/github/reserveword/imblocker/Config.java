package io.github.reserveword.imblocker;

import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
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

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> imeBlacklist;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings")
                    .push("client");

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
                            EditBookScreen.class.getName(),
                            EditSignScreen.class.getName()
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
