package io.github.reserveword.imblocker.legacy1122;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

final class LegacyEventHandler {
    static final LegacyEventHandler INSTANCE = new LegacyEventHandler();

    private LegacyEventHandler() {}

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        LegacyStateController.INSTANCE.onScreenChanged(event.getGui());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LegacyStateController.INSTANCE.tick(Minecraft.getMinecraft());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (LegacyConfig.unlockKey.isPressed()) {
            LegacyStateController.INSTANCE.unlockImeForCurrentContext();
        }
    }
}
