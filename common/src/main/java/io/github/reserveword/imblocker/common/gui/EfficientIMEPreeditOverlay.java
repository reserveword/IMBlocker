package io.github.reserveword.imblocker.common.gui;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class EfficientIMEPreeditOverlay implements Renderable {
	private static final EfficientIMEPreeditOverlay INSTANCE = new EfficientIMEPreeditOverlay();
	
	private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("widget/preedit");
	private static final Style FOCUSED_STYLE = Style.EMPTY.withUnderlined(true);
	private static final int TEXT_COLOR = -16777216;
	private static final int HOT_AREA_MARGIN = 2;
	private final Font font;
	private int caretX;
	private int caretY;
	private final long initTimeMs;
	private Component preEditText;
	private int preEditTextWidth;
	private int preEditCaretPos;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private EfficientIMEPreeditOverlay() {
		this.font = Minecraft.getInstance().font;
		this.initTimeMs = Util.getMillis();
	}

	public void updateCaretPosition(int caretX, int caretY) {
		this.caretX = caretX;
		this.caretY = caretY;
		updatePreeditArea();
	}
	
	public void preeditContentUpdated(PreeditEvent preeditContents) {
		if(preeditContents != null) {
			this.preEditText = preeditContents.toFormattedText(FOCUSED_STYLE).withColor(TEXT_COLOR);
			this.preEditTextWidth = font.width(preEditText);
			this.preEditCaretPos = font.width(preeditContents.fullText().substring(0, preeditContents.caretPosition()));
			updatePreeditArea();
		}else {
			this.preEditText = null;
		}
	}
	
	private void updatePreeditArea() {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner != null && preEditText != null) {
			double guiScale = focusOwner.getGuiScale();
			Rectangle compositionBorder = focusOwner instanceof FocusableWidget ?
					((FocusableWidget) focusOwner).getFocusContainer().getBoundsAbs() : focusOwner.getBoundsAbs();
			int compositionX = caretX, compositionY = (int) (caretY + 14 * guiScale),
					compositionWidth = (int) (preEditTextWidth * guiScale),
					compositionHeight = (int) (9 * guiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY > compositionBorder.height()) {
				compositionY = (int) (caretY - 22 * guiScale);
			}
			
			overlayBounds = new Rectangle(1.0 / guiScale, compositionX, compositionY, compositionWidth, compositionHeight);
			
			compositionX += compositionBorder.x();
			compositionY += compositionBorder.y();
			GLFW.glfwSetPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(), 
					compositionX - HOT_AREA_MARGIN, 
					compositionY - HOT_AREA_MARGIN, 
					compositionWidth + HOT_AREA_MARGIN, 
					compositionHeight + HOT_AREA_MARGIN);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
		if(preEditText == null) {
			return;
		}
		
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 
				overlayBounds.x() - 5, overlayBounds.y() - 5, overlayBounds.width() + 10, overlayBounds.height() + 10);
		graphics.drawString(font, preEditText, overlayBounds.x(), overlayBounds.y(), TEXT_COLOR, false);
		if (TextCursorUtils.isCursorVisible(Util.getMillis() - initTimeMs)) {
			TextCursorUtils.drawInsertCursor(graphics, 
					overlayBounds.x() + preEditCaretPos, overlayBounds.y(), TEXT_COLOR, 10);
		}
	}
	
	public static EfficientIMEPreeditOverlay getInstance() {
		return INSTANCE;
	}
}
