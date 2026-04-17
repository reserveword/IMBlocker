package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.FormattedCharSequence;

public class GuiGraphics {
	public GuiGraphics(Minecraft minecraft, BufferSource vertexConsumers) {}
	public PoseStack pose() { return null; }
	public void fill(int x1, int y1, int x2, int y2, int color) {}
	public int drawString(Font font, String text, int x, int y, int color, boolean shadow) { return 0; }
	public void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {}
	public void flush() {}
}
