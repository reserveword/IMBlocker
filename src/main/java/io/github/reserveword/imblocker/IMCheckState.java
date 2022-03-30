package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraftforge.event.TickEvent;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.WeakHashMap;

public class IMCheckState {
    private static WeakReference<Screen> lastScreen = new WeakReference<>(null);
    private static boolean screenLock = false;
    private static final WeakHashMap<GuiEventListener, Boolean> lastInput = new WeakHashMap<>();

    private static int count = Config.CLIENT.checkInterval.get();
    private static boolean test = false;


    public static void clientTick(TickEvent.ClientTickEvent cte) {
        if (cte.phase == TickEvent.Phase.START) {
            // check interval
            if (count > 0) {
                count --;
                return;
            } else {
                count = Config.CLIENT.checkInterval.get();
            }
            // screen whitelist/blacklist
            Screen s = Minecraft.m_91087_().f_91080_;
            if (s != lastScreen.get()) {
                IMBlocker.LOGGER.debug("screen changed to {}", s);
                lastScreen = new WeakReference<>(s);
                if (checkInList(s, Config.screenWhitelist)) {
                    IMBlocker.LOGGER.debug("found whitelisted screen");
                    IMManager.makeOn();
                    screenLock = true;
                    return;
                } else if (checkInList(s, Config.screenBlacklist)) {
                    IMBlocker.LOGGER.debug("found blacklisted screen");
                    IMManager.makeOff();
                    screenLock = true;
                    return;
                } else {
                    screenLock = false;
                    if (s != null) {
                        Config.checkScreen(s.getClass());
                    }
                }
                IMManager.syncState(); // make it consistent
            } else if (screenLock) {
                return;
            }
            // tick check
            if (IMManager.getState()) {
                test = true;
            }
        } else if (cte.phase == TickEvent.Phase.END && test) {
            IMBlocker.LOGGER.debug("test failed, im off");
            test = false;
            IMManager.makeOff();
        }
    }

    public static void imTick(EditBox input) {
        // canWrite
        if (!input.m_94204_()) {
            return;
        }
        // screenlock
        if (screenLock) {
            return;
        }
        // input box whitelist/blacklist
        if (!lastInput.containsKey(input)) {
            IMBlocker.LOGGER.debug("input box {} ticking", input);
            if (checkInList(input, Config.inputWhitelist)) {
                IMBlocker.LOGGER.debug("found whitelisted input");
                IMManager.makeOn();
                lastInput.put(input, true);
            } else if (checkInList(input, Config.inputBlacklist)) {
                IMBlocker.LOGGER.debug("found blacklisted input");
                IMManager.makeOff();
                lastInput.put(input, true);
            } else {
                lastInput.put(input, false);
            }
        } else if (lastInput.get(input)) {
            return;
        }
        // tick check
        IMManager.makeOn();
        test = false;

    }

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

}
