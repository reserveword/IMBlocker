package io.github.reserveword.imblocker.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface ChatScreenMixin {
    @Accessor("originalChatText")
    String getOriginalText();
}
