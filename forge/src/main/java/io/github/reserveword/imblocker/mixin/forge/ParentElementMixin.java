package io.github.reserveword.imblocker.mixin.forge;

import io.github.reserveword.imblocker.Common;
import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerEventHandler.class)
@Implements(@Interface(iface = ContainerEventHandler.class, prefix = "ceh$"))
public interface ParentElementMixin {
    @Shadow
    @Nullable
    GuiEventListener getFocused();

    @Shadow
    boolean charTyped(char chr, int modifiers);
    /**
     * @author reservword
     * @reason inject charTyped to capture events
     * because I can't use inject for interfaces
     */
    @Intrinsic(displace = true)
    default boolean ceh$charTyped(char chr, int modifiers) {
        GuiEventListener focused = getFocused();
        if (focused != null && Common.classIsTextField(focused.getClass()) && IMCheckState.captureNonPrintable(focused, chr, true)) {
            return false;
        }
        return charTyped(chr, modifiers);
    }
}
