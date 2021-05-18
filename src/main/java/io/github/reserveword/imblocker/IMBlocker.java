package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMBlocker.MODID)
public class IMBlocker {
    public static final String MODID = "imblocker";
    // Directly reference a log4j logger.
    static final Logger LOGGER = LogManager.getLogger();
    static final ActiveTextFieldSniffer sniffer = ActiveTextFieldSniffer.getInstance();

    // KeyBind
//    private static KeyBinding kb;

    public IMBlocker() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onTextFieldNotify(TextFieldConfirmEvent tfce) {
            TextFieldWidget tfw = tfce.tfw;
            sniffer.textFieldConfirm(tfw, tfce.enabled);
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent gse) {
            LOGGER.debug(gse.getClass());
            sniffer.guiChallengeEvent(gse);
            sniffer.guiTryEvent(gse);
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.MouseInputEvent gse) {
            LOGGER.debug(gse.getClass());
            sniffer.guiChallengeEvent(gse);
            if (gse instanceof GuiScreenEvent.MouseReleasedEvent) {
                sniffer.guiTryEvent(gse);
            }
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.KeyboardKeyEvent gse) {
            LOGGER.debug(gse.getClass());
            sniffer.guiChallengeEvent(gse);
            if (gse instanceof GuiScreenEvent.KeyboardKeyReleasedEvent) {
                sniffer.guiTryEvent(gse);
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            if (cte.phase == TickEvent.Phase.START) {
                sniffer.makeChallenge();
            } else if (cte.phase == TickEvent.Phase.END) {
                sniffer.concludeState();
            }
        }
    }

    public static class TextFieldConfirmEvent extends GuiScreenEvent {
        public final TextFieldWidget tfw;
        public final boolean enabled;
        public TextFieldConfirmEvent(@Nonnull TextFieldWidget tfw, boolean enabled) {
            super(Minecraft.getInstance().currentScreen);
            this.tfw = tfw;
            this.enabled = enabled;
        }

        public static void fireEvent(TextFieldWidget tfw, boolean enabled) {
            MinecraftForge.EVENT_BUS.post(new TextFieldConfirmEvent(tfw, enabled));
        }
    }
}
