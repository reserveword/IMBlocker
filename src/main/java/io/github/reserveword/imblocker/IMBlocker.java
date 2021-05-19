package io.github.reserveword.imblocker;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
    private static KeyBinding kb;

    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        kb = new KeyBinding("key.imblocker.switchIMEState", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, "key.categories.misc");
        ClientRegistry.registerKeyBinding(kb);
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {

        public static void collectTextField(TextFieldWidget tfw) {
            sniffer.listen(tfw);
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.MouseInputEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.KeyboardKeyEvent gse) {
            sniffer.scheduleCheck();
        }

        @SubscribeEvent
        public static void onInput(InputEvent.KeyInputEvent kie) {
            if (kb.isPressed()) {
                sniffer.scheduleCheck(true);
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            ActiveTextFieldSniffer.State state = sniffer.checkState();
        }
    }
}
