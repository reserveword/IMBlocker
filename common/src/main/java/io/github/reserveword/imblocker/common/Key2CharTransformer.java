package io.github.reserveword.imblocker.common;

import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLKeycode;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_KeyboardEvent;

import io.github.reserveword.imblocker.common.gui.FocusManager;
import io.github.reserveword.imblocker.common.gui.FocusableObject;
import io.github.reserveword.imblocker.mixin.KeyboardHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;

public final class Key2CharTransformer {
	private static boolean imState;
	
	public static void evaluateSDLEvent(SDL_Event event) {
		if(event.type() == SDLEvents.SDL_EVENT_KEY_DOWN) {
			SDL_KeyboardEvent keyEvent = event.key();
			transformKeyEvent(keyEvent.scancode(), keyEvent.mod());
		}
	}
	
	private static void transformKeyEvent(int scancode, short modifiers) {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(!imState && (focusOwner != null) && focusOwner.getPreferredState()) {
			int realKey = SDLKeyboard.SDL_GetKeyFromScancode(scancode, modifiers, false);
			char translated = 0;
			if(realKey >= 32 && realKey <= 126) {
				translated = (char) realKey;
			}else {
				String keyName = SDLKeyboard.SDL_GetKeyName(realKey);
				if(keyName.startsWith("Keypad ") && 
						((modifiers & SDLKeycode.SDL_KMOD_NUM) != 0) &&
						((modifiers & SDLKeycode.SDL_KMOD_SHIFT) == 0) &&
						keyName.length() == 8) {
					translated = keyName.charAt(7);
				}
			}
			
			if(translated != 0) {
				Minecraft client = Minecraft.getInstance();
				((KeyboardHandlerAccessor) client.keyboardHandler).invokeCharTyped(
						client.getWindow().handle(), new CharacterEvent(translated));
			}
		}
	}
	
	static void syncIMState(boolean state) {
		imState = state;
	}
}
