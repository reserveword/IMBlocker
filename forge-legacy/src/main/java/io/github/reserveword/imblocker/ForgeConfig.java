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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeConfig extends Config {

	public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfig.Client CLIENT;
    private final static Set<String> recoveredScreens = new LinkedHashSet<>();

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ForgeConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
        Config.INSTANCE = new ForgeConfig();
    }

    private ForgeConfig() {
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

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> screenWhitelist;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableScreenRecovering;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> recoveredScreens;
        
        private final ForgeConfigSpec.EnumValue<ChatCommandInputType> chatCommandInputType;

        Client(ForgeConfigSpec.Builder builder) {
        	Minecraft.getInstance().getLaunchedVersion();
            screenWhitelist = builder
            		.comment("Matched screens would enable your IME")
            		.translation("key.imblocker.screenWhitelist")
            		.defineList("screenWhitelist", Arrays.asList(
            				EditBookScreen.class.getName(),
                			EditSignScreen.class.getName()
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
