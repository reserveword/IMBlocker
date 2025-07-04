package io.github.reserveword.imblocker.common.gui;

/**
 * <p>This interface represents the focus destination of the {@code FocusContainer}.
 * If a {@code FocusableWidget} is targeted by its {@code FocusContainer},
 * focus be delivered to its container will be transferred to it and the global
 * {@code focusOwner} will be assign to it.
 * 
 * <p>The corresponding Minecraft GUI context of a {@code FocusableWidget} is a
 * standard textfield-like widget, which contains concrete input data like text
 * content, cursor index, scroll amount etc. Subclasses of this class should
 * always provide these data in detail to accurately determine the position
 * of IME's composition window.
 * 
 * <p>Typically, the implementer of this interface is a 
 * {@link org.spongepowered.asm.mixin.Mixin Mixin} of a Minecraft text field
 * widget. It always contains some boolean flags like {@code isFocused},
 * {@code isVisible} and {@code isEditable}, a {@code FocusableWidget} is
 * focusable (i.e. be able to hold focus/be the {@code focusOwner}) 
 * <b><i>only if</i></b> all these flags as well as
 * {@link FocusableWidget#isRenderable()} are true.
 * 
 * <p>This class is a base part of <b>IMBlocker's focus management system</b>.
 */
public interface FocusableWidget extends FocusableObject {
	
	FocusContainer getFocusContainer();
	
	default double getGuiScale() {
		return getFocusContainer().getGuiScale();
	}
	
	/**
	 * Whether this widget is <b>ACTUALLY</b> visible (i.e. will be rendered by
	 * the game). It's <i>different</i> from {@code isVisible()} methods which
	 * are generally shadowed from injected Minecraft widgets.
	 */
	default boolean isRenderable() {
		return true;
	}
}
