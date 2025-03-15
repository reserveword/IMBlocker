package io.github.reserveword.imblocker.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.gui.ChatState;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
	
	@Shadow
	protected TextFieldWidget chatField;
	
	@Shadow
	private String originalChatText;
	
	private ChatState chatState = ChatState.NONE;
	
	@Inject(method = "init", at = @At("TAIL"))
	private void initChatState(CallbackInfo ci) {
		updateChatState(originalChatText, ci);
	}
	
    @Inject(method = "onChatFieldUpdate(Ljava/lang/String;)V", at = @At("HEAD"))
    public void updateChatState(String chatText, CallbackInfo ci) {
    	ChatState currentChatState = chatField.getText().trim().startsWith("/") ? 
    			ChatState.COMMAND : ChatState.CHAT;
    	if(chatState != currentChatState) {
    		((MinecraftFocusableWidget) chatField).setPreferredEnglishState(currentChatState == ChatState.COMMAND);
    		chatState = currentChatState;
    	}
    }
    
    @Inject(method = "removed", at = @At("HEAD"))
    public void removeChatState(CallbackInfo ci) {
    	chatState = ChatState.NONE;
    }
}
