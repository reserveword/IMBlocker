package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class GuiGraphics {

	/**pose*/
	public abstract PoseStack m_280168_();
	
	/**drawManaged*/
	public abstract void m_286007_(Runnable runnable);

	/**fill*/
	public abstract void m_280509_(int x1, int y1, int x2, int y2, int color);
	
	/**drawString*/
	public abstract int m_280056_(Font font, String text, int x, int y, int color, boolean shadow);

	/**flush*/
	public abstract void m_280262_();
}
