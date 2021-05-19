package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ActiveTextFieldSniffer {

    private static final ActiveTextFieldSniffer INSTANCE = new ActiveTextFieldSniffer();
    private ActiveTextFieldSniffer() { }
    public static ActiveTextFieldSniffer getInstance() {
        return INSTANCE;
    }

    public enum State {NONE, OPEN, CLOSE};
    // basic flags
    private int needCheck = Config.CLIENT.checkDelay.get();
    private boolean on = true;
    private boolean forceSync = false;
    // screen cache
    private State screenState = State.NONE;
    private WeakReference<Object> lastScreen = new WeakReference<>(null);
    // TFW cache
    private final Collection<WeakReference<TextFieldWidget>> allTFW = new HashSet<>();
    private WeakReference<TextFieldWidget> lastTFW = new WeakReference<>(null);

    public void listen(TextFieldWidget tfw) {
        allTFW.add(new WeakReference<>(tfw));
    }

    public void scheduleCheck() {
        needCheck = Config.CLIENT.checkDelay.get();
    }

    public void scheduleCheck(boolean sync) {
        needCheck = Config.CLIENT.checkDelay.get();
        forceSync = sync;
    }

    public State checkScreen() {
        IGuiEventListener gel = Minecraft.getInstance().currentScreen;
        // screen cache
        if (lastScreen.get() != gel) {
            lastScreen = new WeakReference<>(gel);
            IMBlocker.LOGGER.debug("currentScreen {}", gel);
        } else {
            return screenState;
        }
        if (gel != null) {
            // check if is whitelist screen
            for (Class<?> c:Config.getScreenWhitelist()) {
                if (c.isInstance(gel)) {
                    IMBlocker.LOGGER.debug("found whitelisted screen");
                    screenState = State.OPEN;
                    return State.OPEN;
                }
            }
            screenState = State.NONE;
            return State.NONE;
        } else { // if no screen
            screenState = State.CLOSE;
            return State.CLOSE;
        }
    }

    public State checkTFW() {
        TextFieldWidget tfw = lastTFW.get();
        if (tfw != null && tfw.canWrite()) {
            return State.OPEN;
        }
        IMBlocker.LOGGER.debug("allTFW size {}", allTFW.size());
        Iterator<WeakReference<TextFieldWidget>> iterator = allTFW.iterator();
        while (iterator.hasNext()) {
            WeakReference<TextFieldWidget> wr = iterator.next();
            tfw = wr.get();
            if (tfw == null) {
                iterator.remove();
            } else if (tfw.canWrite()) {
                lastTFW = wr;
                return State.OPEN;
            }
        }
        return State.CLOSE;
    }

    public State checkState() {
        if (needCheck <= 0) {
            return State.NONE;
        }
        needCheck --;
        if (needCheck % Config.CLIENT.checkInterval.get() != 0) {
            return State.NONE;
        }
        IMBlocker.LOGGER.debug("state check, on={}", on);
        State currentState = checkScreen();
        if (currentState == State.NONE) {
            currentState = checkTFW();
        }
        if (currentState == State.OPEN && (!on || forceSync)) {
            IMManager.makeOn();
            on = true;
            forceSync = false;
        } else if (currentState == State.CLOSE && (on || forceSync)) {
            IMManager.makeOff();
            on = false;
            forceSync = false;
        } else {
            currentState = State.NONE;
        }
        IMBlocker.LOGGER.debug("state -> {}, on={}", currentState, on);
        return currentState;
    }

}
