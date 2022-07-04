package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;

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
    private static WeakReference<IScreen> lastScreen = new WeakReference<>(null);
    private static void checkScreenList(TickEvent.ClientTickEvent cte, int countdown) {
        Screen s = Minecraft.getInstance().currentScreen;
        if (s != lastScreen.get()) {
            IMBlocker.LOGGER.debug("screen changed to {}", s);
            state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
            lastScreen = new WeakReference<>(s);
            if (checkInList(s, Config.screenWhitelist)) {
                IMBlocker.LOGGER.debug("found whitelisted screen");
                state.add(IMState.SCREEN_LIST);
                state.add(IMState.SCREEN_LIST_MASK);
            } else if (checkInList(s, Config.screenBlacklist)) {
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
    private static void checkSpecial(TickEvent.ClientTickEvent cte, int countdown) {
        // no rules for now
    }

    // process TICK rules
    private enum InputClassRule { NOT_LISTED, WHITELIST, BLACKLIST }
    private static final WeakHashMap<Object, InputClassRule> lastInput = new WeakHashMap<>();

    public static boolean checkInList(Object o, Collection<Class<?>> list) {
        if (o != null && list != null) {
            for (Class<?> c:list) {
                if (c.isInstance(o)) {
                    return true;
                }
            }
        }
        return false;
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
        // canWrite
        if (!canWrite) {
            return;
        }
        // input box whitelist/blacklist
        if (!lastInput.containsKey(input)) {
            IMBlocker.LOGGER.debug("input box {} ticking", input);
            if (checkInList(input, Config.inputWhitelist)) {
                IMBlocker.LOGGER.debug("found whitelisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.WHITELIST);
            } else if (checkInList(input, Config.inputBlacklist)) {
                IMBlocker.LOGGER.debug("found blacklisted input");
                state.remove(IMState.TICK);
                lastInput.put(input, InputClassRule.BLACKLIST);
            } else {
                lastInput.put(input, InputClassRule.NOT_LISTED);
            }
        } else if (lastInput.get(input) != InputClassRule.BLACKLIST) {
            // tick check
            state.add(IMState.TICK);
            state.remove(IMState.TICK_CHALLENGE);
        }
    }

    // process NON_PRINTABLE rules
    private static final char nonPrintable = '\0';

    private static void checkNonPrintable(TickEvent.ClientTickEvent cte, int countdown) {
        if (Config.CLIENT.useExperimental.get() && cte.phase == TickEvent.Phase.START)
        {
            if (state.contains(IMState.NON_PRINTABLE_CHALLENGE)) {
                state.remove(IMState.NON_PRINTABLE_CHALLENGE);
                state.remove(IMState.NON_PRINTABLE);
            } else if (state.contains(IMState.NON_PRINTABLE_CHALLENGE_PENDING)) {
                state.add(IMState.NON_PRINTABLE_CHALLENGE);
                IMBlocker.LOGGER.info("inject");
                Screen s = Minecraft.getInstance().currentScreen;
                if (s != null) {
                    s.charTyped(nonPrintable, 0);
                }
                state.remove(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
            }
        } else {
            state.remove(IMState.NON_PRINTABLE);
        }
    }

    public static void captureNonPrintable(Object tfw, char ch, boolean canWrite) {
        if (Config.CLIENT.useExperimental.get() && ch == nonPrintable && canWrite)
        {
            state.remove(IMState.NON_PRINTABLE_CHALLENGE);
            state.add(IMState.NON_PRINTABLE);
            IMBlocker.LOGGER.info("captured");
        }
    }

    // connect rules above
    private static int count = Config.CLIENT.checkInterval.get();
    public static void clientTick(TickEvent.ClientTickEvent cte) {
        if (cte.phase != TickEvent.Phase.START) return;
        checkScreenList(cte, count);
        checkSpecial(cte, count);
        checkTick(cte, count);
        checkNonPrintable(cte, count);
        syncState();
        // check interval
        if (count > 0) count --;
        else count = Config.CLIENT.checkInterval.get();
    }

    public static void mouseEvent(GuiScreenEvent.MouseInputEvent mie) {
        IMBlocker.LOGGER.info("mouse_event");
        state.add(IMState.NON_PRINTABLE_CHALLENGE_PENDING);
    }

}
