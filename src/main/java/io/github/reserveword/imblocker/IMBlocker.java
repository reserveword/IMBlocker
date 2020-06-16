package io.github.reserveword.imblocker;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.event.KeyEvent;
import java.lang.reflect.*;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("imblocker")
public class IMBlocker
{
    // Directly reference a log4j logger.
    static final Logger LOGGER = LogManager.getLogger();

    //KeyBind
    private static KeyBinding kb;

    public IMBlocker() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        kb = new KeyBinding("key.imblocker.switchIMEState", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, "key.categories.misc");
        ClientRegistry.registerKeyBinding(kb);
    }


    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        /**
         * scans the screen's children and every field of every child, including collection fields.
         * @param s screen to scan
         * @return if there's any text field accepting input
         */
        private static boolean scanGui(Screen s) {
            if (s instanceof ChatScreen || s instanceof EditSignScreen || s instanceof EditBookScreen) {
                return true;
            }
            if (s == null) {
                return false;
            }
            for (IGuiEventListener widget: s.children()) {
                if (widget == null) continue;
                if (widget instanceof TextFieldWidget) {
                    if (((TextFieldWidget) widget).func_212955_f()) {
                        return true;
                    }
                } else {
                    for (Field f: widget.getClass().getDeclaredFields()) {
                        if (IGuiEventListener.class.isAssignableFrom(f.getType())) {
                            f.setAccessible(true);
                            try {
                                IGuiEventListener wid = (IGuiEventListener) f.get(widget);
                                if (wid instanceof TextFieldWidget) {
                                    if (((TextFieldWidget) wid).func_212955_f()) {
                                        return true;
                                    }
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Collection.class.isAssignableFrom(f.getType())
                                && f.getGenericType() instanceof ParameterizedType) {
                            Type t = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                            if ((t instanceof Class    // like List<Screen>
                                    && IGuiEventListener.class.isAssignableFrom((Class) t))
                                    || ((t instanceof WildcardType)    // like List<? extends Screen>
                                    && ((WildcardType) t).getUpperBounds().length > 0
                                    && IGuiEventListener.class.isAssignableFrom((Class) ((WildcardType) t).getUpperBounds()[0]))
                                    || t instanceof TypeVariable) {    // like List<T>
                                // There can be more cases but i don't want to write
                                f.setAccessible(true);
                                try {
                                    for (Object o : (Collection) f.get(widget)) {
                                        if (o instanceof TextFieldWidget) {
                                            if (((TextFieldWidget) o).func_212955_f()) {
                                                return true;
                                            }
                                        } else if (!(o instanceof IGuiEventListener)) break;
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

//        @SubscribeEvent
//        public static void onTick(TickEvent te) {
//            int i = 0;
//        }

        @SubscribeEvent
        public static void onInput(InputEvent.KeyInputEvent kie) {
            if (kb.isPressed()) {
                IMManager.toggle();
            }
        }

        @SubscribeEvent
        public static void onOpenGui(GuiOpenEvent goe) {
            // 临时措施之二
            LOGGER.info("onOpenGui");
            checkScreen(goe.getGui());
        }

        @SubscribeEvent
        public static void inGuiMouseInput(GuiScreenEvent.MouseInputEvent mie) {
            // 先用这个，screen反应之前就决定是否开启输入法，所以需要点两下才能切换好输入法。
            // 退出gui的时候可能需要esc进入菜单点一下才能关闭输入法
            LOGGER.debug("onClickGui");
            checkScreen(mie.getGui());
        }

        @SubscribeEvent
        public static void onClickGui(GuiScreenEvent.MouseReleasedEvent.Post mie) { // 在鼠标放开之后再处理，给screen足够的反应时间
            // 由于Forge的bug而不能捕捉到事件，暂时无效 https://github.com/MinecraftForge/MinecraftForge/issues/6060
            LOGGER.info("onClickGui");
            checkScreen(mie.getGui());
        }

    private static void checkScreen(Screen screen) {
        if (scanGui(screen)) {
            IMManager.makeOn();
            LOGGER.info("act");
        } else {
            IMManager.makeOff();
            LOGGER.info("deact");
        }
    }
}
}
