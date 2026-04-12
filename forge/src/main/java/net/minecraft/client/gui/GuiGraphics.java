package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class GuiGraphics {
	
	public GuiGraphics(Minecraft minecraft, BufferSource bufferSource) {}

	public PoseStack pose() { return null; }
	public PoseStack m_280168_() { return null; }

	public void fill(int x1, int y1, int x2, int y2, int color) {}
	public void m_280509_(int x1, int y1, int x2, int y2, int color) {}

	public int drawString(Font font, String text, int x, int y, int color, boolean shadow) { return 0; }
	public int m_280056_(Font font, String text, int x, int y, int color, boolean shadow) { return 0; }

	public void flush() {}
	public void m_280262_() {}
}
