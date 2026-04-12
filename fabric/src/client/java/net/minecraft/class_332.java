package net.minecraft;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**net.minecraft.client.gui.DrawContext*/
public abstract class class_332 {
	
	/**getMatrices*/
	public abstract MatrixStack method_51448();
	
	/**fill*/
	public abstract void method_25294(int x1, int y1, int x2, int y2, int color);
	
	/**drawText*/
	public abstract int method_51433(TextRenderer font, String text, int x, int y, int color, boolean shadow);
	/*Fabric only black magic, allow a method with same signature at compile time.*/
	public abstract void method_51433(class_327 font, String text, int x, int y, int color, boolean shadow);
	
	/**draw*/
	public abstract void method_51452();
}
