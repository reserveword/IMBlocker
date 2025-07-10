package io.github.reserveword.imblocker.common.gui;

/**
 * <p>This class is the core of <b>IMBlocker's focus management system</b>,
 * holds all global focus-related variables and acts as the root of Minecraft
 * <b>focus transfer path</b>. The focus transfer path determines where the
 * focus should be delivered to, this path is <b>strictly</b> maintained in
 * order to locate the real {@code focusOwner} accurately. After the focus
 * transfer path being modified, the focus will be re-assigned if original
 * {@code focusOwner} is no longer connected to focus source. A valid
 * {@code focusOwner} is an instance of {@code FocusableObject}.
 * 
 * <p>In Minecraft GUI context, the focus transfer path looks like this:
 * <pre>Operating System->[Game Window->FocusContainer->FocusableWidget]</pre>
 * Elements in square brackets are managed by IMBlocker. Each container-like
 * focus transfer node holds its focus destination to deliver the focus toward.
 * If the container itself is focusable and no destination is provided, the
 * {@code focusOwner} will be assigned to the container.
 * 
 * <p>Although the GUI system of vanilla and that of other mods have provided
 * their own focus implementations, they <b>cannot</b> be directly used as
 * focus management controllers due to these reasons:
 * <li>Ambiguous focus states: Their declared focus states are not always
 * reliable and additional validation is required.
 * <li>Different GUI architectures: It's not possible to manage all focus
 * states from so many various GUI implementations without any abstraction.
 * <li>Low quality GUI designs: Some mods have implemented their GUI
 * frameworks with unbelievably poor quality which are filled with inconsistent
 * focus states, hard-coded event dispatches, elusive semaphores etc. Their
 * focus states as well as other GUI-related values must be properly transformed.
 * 
 * @see FocusableObject
 * @see FocusContainer
 * @see FocusableWidget
 * @author LitnhJacuzzi
 * @since 5.1.0
 */
public class FocusManager {
	
	/**
	 * The ultimate focus transfer node. This is where keyboard inputs are
	 * eventually processed.
	 */
	private static FocusableObject focusOwner;
	
	private static boolean isWindowFocused = true;
	
	/**The focus destination of game window.*/
	private static FocusContainer focusedContainer = FocusContainer.MINECRAFT;
	
	/*Utility fields for focus tracking*/
	public static boolean isTrackingFocus = false;
	public static boolean isFocusLocated = false;
	public static boolean isGameRendering = false;
	
	/**
	 * <p>Request to change {@code FocusContainer} focus transfer node.
	 * 
	 * <p>Currently, this request is always successful.
	 * 
	 * @param container the new {@code FocusContainer} to be focused.
	 */
	public static void requestFocus(FocusContainer container) {
		if(focusedContainer != container) {
			if(isWindowFocused) {
				focusedContainer.lostFocus();
				container.deliverFocus();
			}
			focusedContainer = container;
		}
	}
	
	/**
	 * Called from GLFW's window callback, update the focus state of the game window.
	 * 
	 * @param windowFocused if {@code false}, remove all focus in focus transfer path,
	 *        otherwise deliver the focus through the transfer path.
	 */
	public static void setWindowFocused(boolean windowFocused) {
		isWindowFocused = windowFocused;
		if(isWindowFocused) {
			focusedContainer.deliverFocus();
		}else {
			focusedContainer.lostFocus();
		}
	}
	
	/**
	 * Change the {@code focusOwner}, the new focus owner should be strictly verified.
	 */
	public static void setFocusOwner(FocusableObject newFocusOwner) {
		focusOwner = newFocusOwner;
	}
	
	public static FocusableObject getFocusOwner() {
		return focusOwner;
	}
}
