package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.EnumSet;
import java.util.WeakHashMap;

public class IMCheckState {
    private enum IMState {
        SCREEN_LIST, SPECIAL, TICK, NON_PRINTABLE,
        SCREEN_LIST_MASK, SPECIAL_MASK,
        SPECIAL_CHALLENGE, TICK_CHALLENGE, NON_PRINTABLE_CHALLENGE,
        NON_PRINTABLE_CHALLENGE_PENDING;
        public static final EnumSet<IMState> NONE = EnumSet.noneOf(IMState.class);
    }
    private static final EnumSet<IMState> state = EnumSet.copyOf(IMState.NONE);

    // check overall state
    private static void syncState() {
        boolean im;
        if (state.contains(IMState.SCREEN_LIST_MASK)) im = state.contains(IMState.SCREEN_LIST);
        else if (state.contains(IMState.SPECIAL_MASK)) im = state.contains(IMState.SPECIAL);
        else im = (state.contains(IMState.TICK) || state.contains(IMState.NON_PRINTABLE));
        IMManager.makeState(im);
    }

    // process SCREEN_LIST rules
    // notice that nonPrintable rule triggers at screen change, here.
    // notice that special rule triggers at screen change, here too.
    private static WeakReference<Screen> lastScreen = new WeakReference<>(null);
    private static void checkScreenList(TickEvent.ClientTickEvent cte, int countdown) {
        Screen s = Minecraft.m_91087_().f_91080_;
        if (s != lastScreen.get()) {
            IMBlocker.LOGGER.debug("screen changed to {}", s);
            state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING); // nonPrintable
            checkSpecialScreenChange(s); // special
            lastScreen = new WeakReference<>(s);
            if (Config.inScreenWhitelist(s.getClass())) {
                IMBlocker.LOGGER.debug("found whitelisted screen");
                state.add(IMState.SCREEN_LIST);
                state.add(IMState.SCREEN_LIST_MASK);
            } else if (Config.inScreenBlacklist(s.getClass())) {
                IMBlocker.LOGGER.debug("found blacklisted screen");
                state.remove(IMState.SCREEN_LIST);
                state.add(IMState.SCREEN_LIST_MASK);
            } else {
                state.remove(IMState.SCREEN_LIST_MASK);
                if (s != null) {
                    Config.checkScreen(s.getClass());
                }
            }
        }
    }

    // process SPECIAL rules
    private static final MethodHandle getDefaultInputFieldText;
    static {
        MethodHandle getDefaultInputFieldText_tmp;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            //noinspection JavaLangInvokeHandleSignature
            getDefaultInputFieldText_tmp = lookup.findVirtual(ChatScreen.class, "getDefaultInputFieldText", MethodType.methodType(String.class));
        } catch (java.lang.NoSuchMethodException | java.lang.IllegalAccessException e) {
            getDefaultInputFieldText_tmp = null;
            IMBlocker.LOGGER.warn("ChatScreen command hook failed:", e);
        }
        getDefaultInputFieldText = getDefaultInputFieldText_tmp;
    }

    private static void checkSpecial(TickEvent.ClientTickEvent cte, int countdown) { }
    private static void checkSpecialScreenChange(Screen s) {
        if (getDefaultInputFieldText == null) return;
        if (s instanceof ChatScreen) try {
            String text = (String) getDefaultInputFieldText.invoke(s);
            if (text.startsWith("/") && Config.getCheckCommandChat().get()) {
                IMBlocker.LOGGER.debug("Specially disabled IME for command input");
                state.remove(IMState.SPECIAL);
                state.add(IMState.SPECIAL_MASK);
            }
            else {
                state.remove(IMState.SPECIAL_MASK);
            }
        } catch (Throwable e) {
            IMBlocker.LOGGER.warn("ChatScreen hook running exception:", e);
        } else {
            state.remove(IMState.SPECIAL_MASK);
        }
    }

    // process TICK rules
    private enum InputClassRule { NOT_LISTED, WHITELIST, BLACKLIST }
    private static final WeakHashMap<Object, InputClassRule> lastInput = new WeakHashMap<>();
    private static InputClassRule checkInputClass(Object input) {
        if (!lastInput.containsKey(input)) {
            IMBlocker.LOGGER.debug("input box {} ticking", input);
            if (Config.inInputWhitelist(input.getClass())) {
                IMBlocker.LOGGER.debug("found whitelisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.WHITELIST);
                return InputClassRule.WHITELIST;
            } else if (Config.inInputBlacklist(input.getClass())) {
                IMBlocker.LOGGER.debug("found blacklisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.BLACKLIST);
                return InputClassRule.BLACKLIST;
            } else {
                lastInput.put(input, InputClassRule.NOT_LISTED);
                return InputClassRule.NOT_LISTED;
            }
        } else return lastInput.get(input);
    }

    private static void checkTick(TickEvent.ClientTickEvent cte, int countdown) {
        if (cte.phase == TickEvent.Phase.START) {
            if (state.contains(IMState.TICK_CHALLENGE)) {
                IMBlocker.LOGGER.debug("test failed, im off");
                state.remove(IMState.TICK_CHALLENGE);
                state.remove(IMState.TICK);
            } else if (state.contains(IMState.TICK)) {
                state.add(IMState.TICK_CHALLENGE);
            }
        }
    }

    public static void captureTick(Object input, boolean canWrite) {
        if (canWrite && checkInputClass(input) != InputClassRule.BLACKLIST) {
            state.add(IMState.TICK);
            state.remove(IMState.TICK_CHALLENGE);
        }
    }

    // process NON_PRINTABLE rules
    private static final char nonPrintable = '\0';

    private static void checkNonPrintable(TickEvent.ClientTickEvent cte, int countdown) {
        if (Config.getUseExperimental().get() && cte.phase == TickEvent.Phase.START)
        {
            if (state.contains(IMState.NON_PRINTABLE_CHALLENGE)) {
                state.remove(IMState.NON_PRINTABLE_CHALLENGE);
                state.remove(IMState.NON_PRINTABLE);
            } else if (state.contains(IMState.NON_PRINTABLE_CHALLENGE_PENDING)) {
                state.add(IMState.NON_PRINTABLE_CHALLENGE);
                IMBlocker.LOGGER.debug("inject");
                Screen s = Minecraft.m_91087_().f_91080_;
                if (s != null) {
                    s.m_5534_(nonPrintable, 0); // charTyped
                }
                state.remove(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
            }
        } else {
            state.remove(IMState.NON_PRINTABLE);
        }
    }

    public static void captureNonPrintable(Object tfw, char ch, boolean canWrite) {
        if (Config.getUseExperimental().get()
                && checkInputClass(tfw) != InputClassRule.BLACKLIST
                && ch == nonPrintable
                && canWrite)
        {
            state.remove(IMState.NON_PRINTABLE_CHALLENGE);
            state.add(IMState.NON_PRINTABLE);
            IMBlocker.LOGGER.debug("captured");
        }
    }

    // connect rules above
    private static int count = Config.getCheckInterval().get();
    public static void clientTick(TickEvent.ClientTickEvent cte) {
        if (cte.phase != TickEvent.Phase.START) return;
        checkScreenList(cte, count);
        checkSpecial(cte, count);
        checkTick(cte, count);
        checkNonPrintable(cte, count);
        syncState();
        // check interval
        if (count > 0) count --;
        else count = Config.getCheckInterval().get();
    }

    public static void mouseEvent(ScreenEvent mie) {
        state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
    }

}
