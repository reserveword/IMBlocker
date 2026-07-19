package io.github.reserveword.imblocker.common;

import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_TextEditingCandidatesEvent;

import io.github.reserveword.imblocker.common.gui.UniversalIMECandidateOverlay;

public final class SDLEventListener {
	private static boolean processCandidateEvent = false;
	
	public static void enableCandidateEvent() {
		processCandidateEvent = true;
	}
	
	public static void processSDLEvent(SDL_Event event) {
		if(processCandidateEvent && event.type() == SDLEvents.SDL_EVENT_TEXT_EDITING_CANDIDATES) {
			SDL_TextEditingCandidatesEvent candidates = event.edit_candidates();
			String[] selectedPageCandidates = new String[candidates.num_candidates()];
			if(selectedPageCandidates.length == 0) {
				UniversalIMECandidateOverlay.getInstance().candidateListUpdated(null, 0);
			}else {
				PointerBuffer selectedPageCandidatesData = candidates.candidates();
				for(int i = 0; i < selectedPageCandidates.length; i++) {
					selectedPageCandidates[i] = selectedPageCandidatesData.getStringUTF8(i);
				}
				UniversalIMECandidateOverlay.getInstance().candidateListUpdated(selectedPageCandidates, candidates.selected_candidate());
			}
		}
	}
}
