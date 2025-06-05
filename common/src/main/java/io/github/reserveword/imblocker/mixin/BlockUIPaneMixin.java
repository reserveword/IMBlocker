package io.github.reserveword.imblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.reserveword.imblocker.common.ReflectionUtil;
import io.github.reserveword.imblocker.common.gui.MathHelper;
import io.github.reserveword.imblocker.common.gui.MinecraftFocusableWidget;
import io.github.reserveword.imblocker.common.gui.Rectangle;

@Mixin(targets = "com.ldtteam.blockui.Pane", remap = false)
public abstract class BlockUIPaneMixin implements MinecraftFocusableWidget {
	
	private static Class<?> paneClass;
	private static Class<?> viewClass;
	private static Class<?> scrollingContainerClass;
	private static Class<?> bowindowClass;
	private static Class<?> boscreenClass;

	@Shadow protected int x;
	@Shadow protected int y;
	@Shadow protected int width;
	@Shadow protected int height;
	
	@Inject(method = "onFocusLost", at = @At("TAIL"))
	public void focusLost(CallbackInfo ci) {}
	
	@Unique
	public Object getParentWidget(Object child) {
		return ReflectionUtil.getFieldValue(paneClass, child, Object.class, "parent");
	}
	
	@Unique
	protected double getBOScreenFieldValue(String fieldName) {
		Object bowindow = ReflectionUtil.getFieldValue(paneClass, this, Object.class, "window");
		Object boscreen = ReflectionUtil.getFieldValue(bowindowClass, bowindow, Object.class, "screen");
		return ReflectionUtil.getFieldValue(boscreenClass, boscreen, double.class, fieldName);
	}
	
	@Override
	public Rectangle getBoundsAbs() {
		int xAbs = x, yAbs = y;
		
		Object parent = getParentWidget(this);
		while(parent != null) {
			if(scrollingContainerClass.isInstance(parent)) {
				yAbs -= ReflectionUtil.getFieldValue(scrollingContainerClass, parent, double.class, "scrollY");
				yAbs = MathHelper.clamp(yAbs, 0, ((BlockUIPaneMixin) (Object) parent).height - 4);
			}
			xAbs += ((BlockUIPaneMixin) (Object) parent).x;
			yAbs += ((BlockUIPaneMixin) (Object) parent).y;
			if(viewClass.isInstance(parent)) {
				xAbs += ReflectionUtil.getFieldValue(viewClass, parent, int.class, "padding");
				yAbs += ReflectionUtil.getFieldValue(viewClass, parent, int.class, "padding");
			}
			parent = getParentWidget(parent);
		}
		
		double renderScale = getGuiScale();
		xAbs = (int) (xAbs * renderScale + getBOScreenFieldValue("x"));
		yAbs = (int) (yAbs * renderScale + getBOScreenFieldValue("y"));
		
		return new Rectangle(xAbs, yAbs, (int) (width * renderScale), (int) (height * renderScale));
	}
	
	@Override
	public double getGuiScale() {
		return getBOScreenFieldValue("renderScale");
	}
	
	static {
		try {
			paneClass = Class.forName("com.ldtteam.blockui.Pane");
			viewClass = Class.forName("com.ldtteam.blockui.views.View");
			scrollingContainerClass = Class.forName("com.ldtteam.blockui.views.ScrollingContainer");
			bowindowClass = Class.forName("com.ldtteam.blockui.views.BOWindow");
			boscreenClass = Class.forName("com.ldtteam.blockui.BOScreen");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
