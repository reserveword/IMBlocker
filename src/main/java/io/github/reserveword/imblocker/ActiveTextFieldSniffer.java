package io.github.reserveword.imblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.lang.ref.WeakReference;

public class ActiveTextFieldSniffer {

    private static final ActiveTextFieldSniffer INSTANCE = new ActiveTextFieldSniffer();
    private ActiveTextFieldSniffer() { }
    public static ActiveTextFieldSniffer getInstance() {
        return INSTANCE;
    }

    private static final int OFF            = 0b0000;
    private static final int ON             = 0b0001;
    private static final int POSITIVE       = 0b0010;
    private static final int INPUTING       = 0b0100;
    private static final int WHITELIST      = 0b1000;
    private int state = ON;
    private WeakReference<Object> lastScreen = new WeakReference<>(null);

    public void textFieldConfirm(TextFieldWidget tfw, boolean enabled) {
        if ((state & WHITELIST) != 0) {
            return;
        }
        if (tfw.func_212955_f() && enabled) {
            IMBlocker.LOGGER.debug("state {} -> confirm", state);
            state = (state & ON) | POSITIVE | INPUTING;
        } else {
            IMBlocker.LOGGER.debug("textFieldWidget state: visible={}, active={}, focused={}, enabled={}", tfw.visible, tfw.active, tfw.isFocused(), enabled);
            IMBlocker.LOGGER.debug("state {} -> confirm inactive", state);
            state = (state & ON) | POSITIVE;
        }
    }

    public void guiChallengeEvent(GuiScreenEvent gse) {
        if (state == (ON | INPUTING)) {
            IMBlocker.LOGGER.debug("state {} -> challenge", state);
            state &= ~INPUTING;
        }
    }
    public void guiTryEvent(GuiScreenEvent gse) {
        if (state == OFF) {
            IMBlocker.LOGGER.debug("state {} -> try confirm", state);
            state |= INPUTING;
        }
    }

    public void makeChallenge() {
        if (state == ON || state == (OFF | INPUTING)) {
            IGuiEventListener gel = Minecraft.getInstance().currentScreen;
            if (gel != null) {
                IMBlocker.LOGGER.debug("state {} -> sendchallenge", state);
                state = (state & ON) | POSITIVE;
                try {
                    gel.charTyped('\0', 0);
                } catch (Exception e) {
                    state = (state & ON);
                }
            } else {
                IMBlocker.LOGGER.debug("currentScreen is null");
                state = (state & ON) | POSITIVE;
            }
        }
    }

    public boolean checkWhitelist() {
        IGuiEventListener gel = Minecraft.getInstance().currentScreen;
        // screen cache
        if (lastScreen.get() != gel) {
            lastScreen = new WeakReference<>(gel);
            IMBlocker.LOGGER.debug("currentScreen {}", gel);
        } else {
            return (state & WHITELIST) != 0;
        }
        if (gel != null) {
            // check if is whitelist screen
            for (Class<?> c:Config.getScreenWhitelist()) {
                if (c.isInstance(gel)) {
                    IMBlocker.LOGGER.debug("found whitelisted screen");
                    if ((state & ON) == OFF) {
                        IMManager.makeOn();
                    }
                    state = ON | POSITIVE | INPUTING | WHITELIST;
                    return true;
                }
            }
            state &= ~WHITELIST;
        } else { // if no screen
            IMBlocker.LOGGER.debug("currentScreen is null");
            IMManager.makeOff();
            state = OFF | POSITIVE | WHITELIST;
            return true;
        }
        return false;
    }

    public void concludeState() {
        if (checkWhitelist()) {
            return;
        }
        if ((state & POSITIVE) != 0) {
            IMBlocker.LOGGER.debug("state {} -> conclude", state);
            if (state == (ON | POSITIVE)) {
                IMManager.makeOff();
                state = OFF;
            } else if (state == (OFF | POSITIVE | INPUTING)) {
                IMManager.makeOn();
                state = ON | INPUTING;
            }
            state &= ~POSITIVE;
        }
    }
}
