package io.github.reserveword.imblocker.mixin.forge;

import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface ChatScreenMixin {
    @Accessor("f_95576_")
    String getOriginalText();
}
