package io.github.reserveword.imblocker.common.gui;

public interface MinecraftRenderApi {
	void fillRect(int x1, int y1, int x2, int y2, int color);
	void drawText(String text, int x, int y, int color);
}
