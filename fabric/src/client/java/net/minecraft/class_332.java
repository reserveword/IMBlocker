package net.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

/**net.minecraft.client.gui.DrawContext*/
public abstract class class_332 {
	
	/**getMatrices*/
	public abstract MatrixStack method_51448();
	
	/**draw*/
	public abstract void method_51741(Runnable runnable);
	
	/**fill*/
	public abstract void method_25294(int x1, int y1, int x2, int y2, int color);
	
	/**drawText*/
	public abstract int method_51433(TextRenderer font, String text, int x, int y, int color, boolean shadow);
	
	/**drawText*/
	public abstract void method_51430(TextRenderer font, OrderedText text, int x, int y, int color, boolean shadow);
	
	/**draw*/
	public abstract void method_51452();
}
