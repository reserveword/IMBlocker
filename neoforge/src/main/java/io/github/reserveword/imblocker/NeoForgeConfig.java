package io.github.reserveword.imblocker;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import io.github.reserveword.imblocker.common.ChatCommandInputType;
import io.github.reserveword.imblocker.common.Config;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeConfig extends Config {

	public static final ModConfigSpec clientSpec;
    public static final NeoForgeConfig.Client CLIENT;
    private final static Set<String> recoveredScreens = new LinkedHashSet<>();

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(NeoForgeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        Config.INSTANCE = new NeoForgeConfig();
    }

    private NeoForgeConfig() {
    }

    public void reload() {
        reloadScreenWhitelist(CLIENT.screenWhitelist.get());
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
            				AbstractSignEditScreen.class.getName(),
            				"net.mehvahdjukaar.supplementaries.client.screens.TextHolderEditScreen"
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
