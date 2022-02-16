package io.github.reserveword.imblocker;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlocker.MODID)
public class IMBlocker {
    public static final String MODID = "imblocker";
    // Directly reference a log4j logger.
    static final Logger LOGGER = LogManager.getLogger();
    static final ActiveTextFieldSniffer sniffer = ActiveTextFieldSniffer.getInstance();

    // KeyBind
    private static KeyMapping kb;

    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        kb = new KeyMapping("key.imblocker.switchIMEState", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, "key.categories.misc");
        ClientRegistry.registerKeyBinding(kb);
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {

        public static void collectTextField(EditBox tfw) {
            sniffer.listen(tfw);
        }

        @SubscribeEvent
        public static void onScreenEvent(ScreenEvent.InitScreenEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onScreenEvent(ScreenEvent.MouseInputEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onScreenEvent(ScreenEvent.KeyboardKeyEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onInput(InputEvent.KeyInputEvent kie) {
            if (kb.m_90859_()) {
                sniffer.scheduleCheck(true);
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            ActiveTextFieldSniffer.State state = sniffer.checkState();
        }
    }
}
