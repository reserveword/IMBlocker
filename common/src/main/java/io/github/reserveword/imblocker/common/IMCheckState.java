package io.github.reserveword.imblocker.common;

import java.util.EnumSet;
import java.util.WeakHashMap;

public class IMCheckState {
    // process NON_PRINTABLE rules
    public static final char nonPrintable = '\0';
    private static final EnumSet<IMState> state = EnumSet.copyOf(IMState.NONE);
    private static final WeakHashMap<Object, InputClassRule> lastInput = new WeakHashMap<>();
    public static FocusableWidgetAccessor focusedInputWidget = null;
    public static boolean isWhiteListScreenShowing = false;
    public static boolean isChatScreenShowing = false;
    public static ChatState chatState = ChatState.NONE;
    public static long lastIMStateChangeTimestamp;
    private static boolean axiomGuiCaptureKeyboard = false;
    private static boolean axiomGuiTextFieldFocused = false;
    private static Runnable setConversionState = null;

    // check overall state
    private static void syncState() {
        if (conversionStateCdDone() && setConversionState != null) {
            setConversionState.run();
            setConversionState = null;
        }

        boolean state;
        if (axiomGuiCaptureKeyboard) {
            state = axiomGuiTextFieldFocused;
        } else {
            state = (focusedInputWidget != null && focusedInputWidget.isWidgetEditable())
                    || isWhiteListScreenShowing;
        }
        IMManager.setState(state);
        updateChatState();
    }

    public static void cancelSetConversionState() {
        setConversionState = null;
    }

    private static boolean conversionStateCdDone() {
        return System.currentTimeMillis() - lastIMStateChangeTimestamp > 50;
    }

    public static void focusChanged(FocusableWidgetAccessor widget, boolean isFocused) {
        if (isFocused) {
            focusGained(widget);
        } else {
            focusLost(widget);
        }
    }

    public static void focusGained(FocusableWidgetAccessor widget) {
        focusedInputWidget = widget;
        syncState();
    }

    public static void focusLost(FocusableWidgetAccessor widget) {
        if (focusedInputWidget == widget) {
            focusedInputWidget = null;
            syncState();
        }
    }

    private static void updateChatState() {
        ChatState currentChatState = !isChatScreenShowing || axiomGuiCaptureKeyboard ? ChatState.NONE :
                                             (focusedInputWidget.getText().trim().startsWith("/") ? ChatState.COMMAND : ChatState.CHAT);
        if (currentChatState != ChatState.NONE && chatState != currentChatState) {
            // Executing at the same time as imstate change will nullify this operation, thus move to 50ms later.
            setConversionState = () -> IMManager.setImmOnState(currentChatState == ChatState.COMMAND);
        }
        chatState = currentChatState;
    }

    private static void checkAxiomGuiState() {
        AxiomGuiAccessor axiomGuiAccessor = AxiomGuiAccessor.instance;
        if (axiomGuiAccessor != null) {
            axiomGuiCaptureKeyboard = axiomGuiAccessor.isCaptureKeyboard();
            axiomGuiTextFieldFocused = axiomGuiAccessor.isTextFieldFocused();
        }
    }

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

    public static boolean captureNonPrintable(Object tfw, char ch, boolean canWrite) {
        if (Config.INSTANCE.getUseExperimental()
            && checkInputClass(tfw) != InputClassRule.BLACKLIST
            && ch == nonPrintable
            && canWrite
        ) {
            state.remove(IMState.NON_PRINTABLE_CHALLENGE);
            state.add(IMState.NON_PRINTABLE);
            Common.LOGGER.debug("captured");
            return true;
        }
        return false;
    }

    public static void clientTick() {
        checkAxiomGuiState();
        syncState();
    }

    public static void mouseEvent() {
        state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
        state.remove(IMState.CLICK);
    }

    // process TICK rules
    private enum InputClassRule {NOT_LISTED, WHITELIST, BLACKLIST}

    private enum IMState {
        TICK,
        NON_PRINTABLE,
        CLICK,
        NON_PRINTABLE_CHALLENGE,
        NON_PRINTABLE_CHALLENGE_PENDING;
        public static final EnumSet<IMState> NONE = EnumSet.noneOf(IMState.class);
    }

    private enum ChatState {
        NONE, CHAT, COMMAND
    }
}
