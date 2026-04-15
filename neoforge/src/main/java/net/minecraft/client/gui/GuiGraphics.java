package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.FormattedCharSequence;

public abstract class GuiGraphics {
	public abstract PoseStack pose();
	public abstract void fill(int x1, int y1, int x2, int y2, int color);
	public abstract int drawString(Font font, String text, int x, int y, int color, boolean shadow);
	public abstract void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow);
	public abstract void flush();
}
