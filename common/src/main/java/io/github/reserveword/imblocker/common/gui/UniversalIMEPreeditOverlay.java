package io.github.reserveword.imblocker.common.gui;

import org.lwjgl.glfw.GLFW;

import imgui.ImDrawList;
import imgui.ImGui;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class UniversalIMEPreeditOverlay {
	private static final UniversalIMEPreeditOverlay INSTANCE = new UniversalIMEPreeditOverlay();
	
	private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("widget/preedit");
	private static final Style FOCUSED_STYLE = Style.EMPTY.withUnderlined(true);
	private static final int TEXT_COLOR = -16777216;
	private static final int HOT_AREA_MARGIN = 2;
	
	private final long initTimeMs;
	
	private final Font font;
	
	private int caretX;
	private int caretY;
	
	private String preEditText;
	private int preEditCaretPos;
	private boolean preEditContentUpdated = false;
	
	private Component preEditTextFormatted;
	private int preEditTextWidth;
	private int preEditCaretRenderX;
	private Rectangle ingameOverlayBounds = Rectangle.EMPTY;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMEPreeditOverlay() {
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
			preEditText = preeditContents.fullText();
			preEditCaretPos = preeditContents.caretPosition();
			
			if(FocusManager.getFocusedContainer() == FocusContainer.MINECRAFT) {
				preEditTextFormatted = preeditContents.toFormattedText(FOCUSED_STYLE).withColor(TEXT_COLOR);
				preEditTextWidth = font.width(preEditTextFormatted);
				preEditCaretRenderX = font.width(preEditText.substring(0, preEditCaretPos));
				updatePreeditArea();
			}else {
				preEditContentUpdated = true;
			}	
		}else {
			preEditText = null;
			preEditTextFormatted = null;
			preEditContentUpdated = false;
		}
	}
	
	private void updatePreeditArea() {
		FocusableObject focusOwner = FocusManager.getFocusOwner();
		if(focusOwner != null && preEditText != null) {
			double widgetGuiScale = focusOwner.getGuiScale();
			int widgetFontSize = focusOwner.getFontHeight();
			int containerFontSize;
			double containerGuiScale;
			Rectangle compositionBorder;
			if(focusOwner instanceof FocusableWidget focusedWidget) {
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = widgetFontSize;
				containerGuiScale = widgetGuiScale;
				compositionBorder = focusOwner.getBoundsAbs();
			}
			
			int inputHeight = (int) (widgetFontSize * widgetGuiScale + 5 * containerGuiScale);
			int compositionX = caretX, compositionY = caretY + inputHeight,
					compositionWidth = (int) (preEditTextWidth * containerGuiScale),
					compositionHeight = (int) (containerFontSize * containerGuiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY + compositionHeight > compositionBorder.height()) {
				compositionY = (int) (caretY - (6 + containerFontSize) * containerGuiScale);
			}
			
			ingameOverlayBounds = new Rectangle(1.0 / containerGuiScale, compositionX, compositionY, compositionWidth, compositionHeight);			
			compositionX += compositionBorder.x();
			compositionY += compositionBorder.y();
			overlayBounds = new Rectangle(compositionX, compositionY, compositionWidth, compositionHeight);
			
			if(!IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
				int scaledMargin = (int) (HOT_AREA_MARGIN * containerGuiScale);
				if(!IMBlockerConfig.INSTANCE.useStrictCursorRect()) {
					GLFW.glfwSetPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(),
							compositionX - scaledMargin, Math.min(caretY, compositionY) - scaledMargin,
							compositionWidth + scaledMargin * 2, compositionHeight + inputHeight + scaledMargin * 2);
				}else {
					GLFW.glfwSetPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(),
							compositionX - scaledMargin, compositionY - scaledMargin,
							compositionWidth + scaledMargin * 2, compositionHeight + scaledMargin * 2);
				}
			}
		}
	}

	public void renderOnMinecraftSurface(GuiGraphicsExtractor graphics) {
		if(preEditTextFormatted == null) {
			return;
		}
		
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 
				ingameOverlayBounds.x() - 5, ingameOverlayBounds.y() - 5, ingameOverlayBounds.width() + 10, ingameOverlayBounds.height() + 10);
		graphics.text(font, preEditTextFormatted, ingameOverlayBounds.x(), ingameOverlayBounds.y(), TEXT_COLOR, false);
		if (TextCursorUtils.isCursorVisible(Util.getMillis() - initTimeMs)) {
			TextCursorUtils.extractInsertCursor(graphics, 
					ingameOverlayBounds.x() + preEditCaretRenderX, ingameOverlayBounds.y(), TEXT_COLOR, 10);
		}
	}
	
	public void renderOnImGuiSurface(ImDrawList graphics) {
		if(preEditText == null) {
			return;
		}
		
		if(preEditContentUpdated) {
			preEditTextWidth = (int) ImGui.calcTextSize(preEditText).x;
			preEditCaretRenderX = (int) ImGui.calcTextSize(preEditText.substring(0, preEditCaretPos)).x;
			updatePreeditArea();
			preEditContentUpdated = false;
		}
		
		graphics.addRectFilled(
				overlayBounds.x() - 4, overlayBounds.y() - 4, 
				overlayBounds.x() + overlayBounds.width() + 4, overlayBounds.y() + overlayBounds.height() + 4, 
				ImGui.getColorU32(1, 1, 1, 1));
		graphics.addText(overlayBounds.x(), overlayBounds.y(), ImGui.getColorU32(0, 0, 0, 1), preEditText);
		if(TextCursorUtils.isCursorVisible(Util.getMillis() - initTimeMs)) {
			graphics.addRectFilled(
					overlayBounds.x() + preEditCaretRenderX, overlayBounds.y(), 
					overlayBounds.x() + preEditCaretRenderX + 2, overlayBounds.y() + overlayBounds.height(), 
					ImGui.getColorU32(0, 0, 0, 1));
		}
	}
	
	public static UniversalIMEPreeditOverlay getInstance() {
		return INSTANCE;
	}
}
