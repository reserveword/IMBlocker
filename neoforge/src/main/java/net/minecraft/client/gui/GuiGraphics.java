package net.minecraft.client.gui;

public abstract class GuiGraphics {
	public abstract void fill(int x1, int y1, int x2, int y2, int color);
	public abstract void drawString(Font font, String text, int x, int y, int color, boolean shadow);
}
