package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.Config;
import io.github.reserveword.imblocker.common.gui.ChatState;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	
	@Shadow
	protected EditBox input;
	
	@Shadow
	private String initial;
	
	private ChatState chatState = ChatState.NONE;
	
	@Inject(method = "init", at = @At("TAIL"))
	private void initChatState(CallbackInfo ci) {
		updateChatState(initial, ci);
	}
	
    @Inject(method = "onEdited(Ljava/lang/String;)V", at = @At("HEAD"))
    public void updateChatState(String chatText, CallbackInfo ci) {
    	ChatState currentChatState = input.getValue().trim().startsWith("/") ? 
    			ChatState.COMMAND : ChatState.CHAT;
    	if(chatState != currentChatState) {
    		boolean engState = currentChatState == ChatState.COMMAND;
    		MinecraftFocusableWidget _chatField = (MinecraftFocusableWidget) input;
    		switch (Config.INSTANCE.getChatCommandInputType()) {
				case IM_ENG_STATE:
					_chatField.setPreferredEditState(true);
		    		break;
				case DISABLE_IM:
					_chatField.setPreferredEditState(!engState);
					break;
			}
			_chatField.setPreferredEnglishState(engState);
    		chatState = currentChatState;
    	}
    }
    
    @Inject(method = "removed", at = @At("HEAD"))
    public void removeChatState(CallbackInfo ci) {
    	chatState = ChatState.NONE;
    }
}
