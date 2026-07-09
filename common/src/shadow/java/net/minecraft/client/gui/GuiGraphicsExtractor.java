package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class GuiGraphicsExtractor {
	public abstract int guiWidth();
	public abstract int guiHeight();
	public abstract void blitSprite(RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height);
	public abstract void blitSprite(com.mojang.renderpearl.api.pipeline.RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height);
	public abstract void fill(int x0, int y0, int x1, int y1, int col);
	public abstract void text(Font font, String str, int x, int y, int color, boolean dropShadow);
	public abstract void text(Font font, Component str, int x, int y, int color, boolean dropShadow);
}
