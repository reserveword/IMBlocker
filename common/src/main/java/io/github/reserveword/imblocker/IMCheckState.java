package io.github.reserveword.imblocker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

public class IMCheckState {
    // process CLICK rules
    private static final ArrayList<BooleanSupplier> actives = new ArrayList<>();
    private static final EnumSet<IMState> state = EnumSet.copyOf(IMState.NONE);
    
    public static FocusableWidgetAccessor focusedInputWidget = null;
    
    private static boolean axiomGuiCaptureKeyboard = false;
    private static boolean axiomGuiTextFieldFocused = false;
    
    public static boolean isWhiteListScreenShowing = false;
    
    public static boolean isChatScreenShowing = false;
    public static ChatState chatState = ChatState.NONE;
    
    private static Runnable deferredOp = null;

    // check overall state
    private static void syncState() {
        if(deferredOp != null) {
        	deferredOp.run();
        	deferredOp = null;
        }
        
        boolean state;
        if(axiomGuiCaptureKeyboard) {
        	state = axiomGuiTextFieldFocused;
        }else {
        	state = (focusedInputWidget != null && focusedInputWidget.isWidgetEditable()) 
            		|| isWhiteListScreenShowing;
        }
        IMManager.makeState(state);
        updateChatState();
    }
    
    public static void focusGained(FocusableWidgetAccessor widget) {
    	focusedInputWidget = widget;
    }
    
    public static void focusLost(FocusableWidgetAccessor widget) {
    	if(focusedInputWidget == widget) {
    		focusedInputWidget = null;
    	}
    }
    
    private static void updateChatState() {
    	ChatState currentChatState = !isChatScreenShowing || axiomGuiCaptureKeyboard ? ChatState.NONE :
    			(focusedInputWidget.getText().trim().startsWith("/") ? ChatState.COMMAND : ChatState.CHAT);
    	if(currentChatState != ChatState.NONE && chatState != currentChatState) {
    		//Executing at the same tick as imstate change will nullify this operation, thus move to next tick.
			deferredOp = () -> IMManager.makeImmOnState(currentChatState == ChatState.COMMAND);
    	}
    	chatState = currentChatState;
	}
    
    private static void checkAxiomGuiState() {
    	AxiomGuiAccessor axiomGuiAccessor = AxiomGuiAccessor.instance;
    	if(axiomGuiAccessor != null) {
	    	axiomGuiCaptureKeyboard = axiomGuiAccessor.isCaptureKeyboard();
			axiomGuiTextFieldFocused = axiomGuiAccessor.isTextFieldFocused();
    	}
    }

    // process SCREEN_LIST rules
    // notice that nonPrintable rule triggers at screen change, here.
    // notice that special rule triggers at screen change, here too.
    private static WeakReference<Object> lastScreen = new WeakReference<>(null);
    private static void checkScreenList(ScreenInfo screen) {
        if (screen.get() != lastScreen.get()) {
            Common.LOGGER.debug("screen changed to {}", screen.get());
            state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING); // nonPrintable
            checkSpecialScreenChange(screen); // special
            lastScreen = new WeakReference<>(screen.get());
            if (Config.INSTANCE.inScreenWhitelist(screen.type())) {
                Common.LOGGER.debug("found whitelisted screen");
                state.add(IMState.SCREEN_LIST);
                state.add(IMState.SCREEN_LIST_MASK);
            } else if (Config.INSTANCE.inScreenBlacklist(screen.type())) {
                Common.LOGGER.debug("found blacklisted screen");
                state.remove(IMState.SCREEN_LIST);
                state.add(IMState.SCREEN_LIST_MASK);
            } else {
                state.remove(IMState.SCREEN_LIST_MASK);
                if (screen.get() != null) {
                    Config.INSTANCE.checkScreen(screen.type());
                }
            }
        }
    }

    // process SPECIAL rules
    private static void checkSpecial() { }

    private static void checkSpecialScreenChange(ScreenInfo screen) {
        if (screen.isChatScreen()) try {
            String text = screen.defaultText();
            if (text.startsWith("/") && Config.INSTANCE.getCheckCommandChat()) {
                Common.LOGGER.debug("Specially disabled IME for command input");
                state.remove(IMState.SPECIAL);
                state.add(IMState.SPECIAL_MASK);
            }
            else {
                state.remove(IMState.SPECIAL_MASK);
            }
        } catch (Throwable e) {
            Common.LOGGER.warn("ChatScreen hook running exception:", e);
        } else {
            state.remove(IMState.SPECIAL_MASK);
        }
    }

    // process TICK rules
    private enum InputClassRule { NOT_LISTED, WHITELIST, BLACKLIST }
    private static final WeakHashMap<Object, InputClassRule> lastInput = new WeakHashMap<>();
    private static InputClassRule checkInputClass(Object input) {
        if (!lastInput.containsKey(input)) {
            Common.LOGGER.debug("input box {} ticking", input);
            if (Config.INSTANCE.inInputWhitelist(input.getClass())) {
                Common.LOGGER.debug("found whitelisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.WHITELIST);
                return InputClassRule.WHITELIST;
            } else if (Config.INSTANCE.inInputBlacklist(input.getClass())) {
                Common.LOGGER.debug("found blacklisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.BLACKLIST);
                return InputClassRule.BLACKLIST;
            } else {
                lastInput.put(input, InputClassRule.NOT_LISTED);
                return InputClassRule.NOT_LISTED;
            }
        } else return lastInput.get(input);
    }

    private static void checkTick() {
        if (state.contains(IMState.TICK_CHALLENGE)) {
            Common.LOGGER.debug("test failed, im off");
            state.remove(IMState.TICK_CHALLENGE);
            state.remove(IMState.TICK);
        } else if (state.contains(IMState.TICK)) {
            state.add(IMState.TICK_CHALLENGE);
        }
    }

    public static void captureTick(Object input, boolean canWrite) {
        if (canWrite && checkInputClass(input) != InputClassRule.BLACKLIST) {
            state.add(IMState.TICK);
            state.remove(IMState.TICK_CHALLENGE);
        }
    }

    // process NON_PRINTABLE rules
    public static final char nonPrintable = '\0';

    private static void checkNonPrintable(ScreenInfo screen) {
        if (Config.INSTANCE.getUseExperimental())
        {
            if (state.contains(IMState.NON_PRINTABLE_CHALLENGE)) {
                state.remove(IMState.NON_PRINTABLE_CHALLENGE);
                state.remove(IMState.NON_PRINTABLE);
            } else if (state.contains(IMState.NON_PRINTABLE_CHALLENGE_PENDING)) {
                state.add(IMState.NON_PRINTABLE_CHALLENGE);
                Common.LOGGER.debug("inject");
                if (screen.get() != null) {
                    try {
                        screen.charTyped(nonPrintable, 0); // charTyped
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                state.remove(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
            }
        } else {
            state.remove(IMState.NON_PRINTABLE);
        }
    }

    public static boolean captureNonPrintable(Object tfw, char ch, boolean canWrite) {
        if (Config.INSTANCE.getUseExperimental()
                && checkInputClass(tfw) != InputClassRule.BLACKLIST
                && ch == nonPrintable
                && canWrite)
        {
            state.remove(IMState.NON_PRINTABLE_CHALLENGE);
            state.add(IMState.NON_PRINTABLE);
            Common.LOGGER.debug("captured");
            return true;
        }
        return false;
    }

    public static void checkClick() {
        for (BooleanSupplier active: actives) {
            if (active.getAsBoolean()) {
                state.add(IMState.CLICK);
                break;
            }
        }
        actives.clear();
    }

    public static void captureClick(BooleanSupplier active) {
        actives.add(active);
    }

    public static void clientTick(ScreenInfo screen) {
        /*checkScreenList(screen);
        checkSpecial();
        if (count == 0) checkTick();
        checkNonPrintable(screen);
        checkClick();*/
    	checkAxiomGuiState();
        syncState();
        // check interval
        if (count > 0) count --;
        else count = Config.INSTANCE.getCheckInterval();
    }

    // connect rules above
    private static int count = Config.INSTANCE.getCheckInterval();

    public static void mouseEvent() {
        state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
        state.remove(IMState.CLICK);
    }

    private enum IMState {
        SCREEN_LIST, SPECIAL, TICK, NON_PRINTABLE, CLICK,
        SCREEN_LIST_MASK, SPECIAL_MASK,
        TICK_CHALLENGE, NON_PRINTABLE_CHALLENGE,
        NON_PRINTABLE_CHALLENGE_PENDING;
        public static final EnumSet<IMState> NONE = EnumSet.noneOf(IMState.class);
    }
    
    private enum ChatState {
    	NONE, CHAT, COMMAND
    }

    public interface ScreenInfo {
        Object get();
        boolean isChatScreen();
        Class<?> type();
        String defaultText() throws Throwable;
        void charTyped(char codePoint, int modifiers);
    }
}
