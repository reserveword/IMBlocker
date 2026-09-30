package io.github.reserveword.imblocker.mixin.compat;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.EnhancedPoseStack;

import io.github.reserveword.imblocker.common.accessor.LDLibGUIContextAccessor;

@Pseudo
@Mixin(targets = "com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext", remap = false)
public abstract class LDLibGUIContextMixin implements LDLibGUIContextAccessor {
	@Shadow
	public EnhancedPoseStack pose;
	
	@Override
	public Matrix3x2f imblocker$getPose() {
		Matrix4f m = pose.pose.last().pose();
		return new Matrix3x2f(m.m00(), m.m01(), m.m10(), m.m11(), m.m30(), m.m31());
	}
}
