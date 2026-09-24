package io.github.reserveword.imblocker.common.accessor;

public interface ImGuiGraphicsAccessor {
	void addRectFilled(float pMinX, float pMinY, float pMaxX, float pMaxY, int col);
	void addText(float posX, float posY, int col, String textBegin);
	float getTextWidth(String text);
	int getColorU32i(int color);
	int getColorU32(float r, float g, float b, float a);
}
