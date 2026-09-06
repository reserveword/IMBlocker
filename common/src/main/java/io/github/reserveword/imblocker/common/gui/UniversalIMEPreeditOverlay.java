package io.github.reserveword.imblocker.common.gui;

import java.util.Objects;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import imgui.moulberry92.ImDrawList;
import imgui.moulberry92.ImGui;
import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.InputSystem;
import io.github.reserveword.imblocker.common.ReflectionUtil;
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
	
	private static final BackgroundBlitter backgroundBlitter;
	private static final Style FOCUSED_STYLE = Style.EMPTY.withUnderlined(true);
	private static final int TEXT_COLOR = -16777216;
	private static final int HOT_AREA_MARGIN = 2;
	
	private final long initTimeMs;
	
	private final Font font;
	
	private int caretX;
	private int caretY;
	private int inputHeight;
	
	private String preEditText;
	private int preEditCaretPos;
	private boolean preEditContentUpdated = false;
	
	private Component preEditTextFormatted;
	private int preEditTextWidth;
	private int preEditCaretRenderX;
	private Rectangle overlayBounds = Rectangle.EMPTY;

	private UniversalIMEPreeditOverlay() {
		this.font = Minecraft.getInstance().font;
		this.initTimeMs = Util.getMillis();
	}

	public void updateCaretPosition(CaretInfo caretInfo) {
		this.caretX = caretInfo.caretX();
		this.caretY = caretInfo.caretY();
		this.inputHeight = caretInfo.inputHeight();
		updatePreeditArea();
	}
	
	public void preeditContentUpdated(PreeditEvent preeditContents) {
		if(preeditContents != null) {
			String compositionString = preeditContents.fullText();
			int caretPosition = preeditContents.caretPosition();
			if(!Objects.equals(preEditText, compositionString) || (preEditCaretPos != caretPosition)) {
				boolean startComposition = preEditText == null;
				preEditText = preeditContents.fullText();
				preEditCaretPos = preeditContents.caretPosition();
				
				if(FocusManager.isMinecraftContextFocused()) {
					preEditTextFormatted = preeditContents.toFormattedText(FOCUSED_STYLE).withColor(TEXT_COLOR);
					preEditTextWidth = font.width(preEditTextFormatted);
					preEditCaretRenderX = font.width(preEditText.substring(0, preEditCaretPos));
					updatePreeditArea();
				}else {
					if(startComposition) {
						preEditTextWidth = 0;
						updatePreeditArea(); // On macOS, we must provide preedit area immediately.
					}
					preEditContentUpdated = true;
				}
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
			int containerFontSize;
			double containerGuiScale;
			Rectangle compositionBorder;
			if(focusOwner instanceof FocusableWidget focusedWidget) {
				containerFontSize = focusedWidget.getFocusContainer().getFontHeight();
				containerGuiScale = focusedWidget.getFocusContainer().getGuiScale();
				compositionBorder = focusedWidget.getFocusContainer().getBoundsAbs();
			}else {
				containerFontSize = focusOwner.getFontHeight();
				containerGuiScale = focusOwner.getGuiScale();
				compositionBorder = focusOwner.getBoundsAbs();
			}
			
			int paddedInputHeight = (int) (inputHeight + 5 * containerGuiScale);
			int compositionX = caretX, compositionY = caretY + paddedInputHeight,
					compositionWidth = (int) (preEditTextWidth * containerGuiScale),
					compositionHeight = (int) (containerFontSize * containerGuiScale);
			if(compositionX + compositionWidth > compositionBorder.width()) {
				compositionX = compositionBorder.width() - compositionWidth;
			}
			if(compositionY + compositionHeight > compositionBorder.height()) {
				compositionY = (int) (caretY - (6 + containerFontSize) * containerGuiScale);
			}
			
			if(FocusManager.isMinecraftContextFocused()) {
				overlayBounds = new Rectangle(1.0 / containerGuiScale, compositionX, compositionY, compositionWidth, compositionHeight);			
			}else {
				overlayBounds = new Rectangle(compositionX, compositionY, compositionWidth, compositionHeight);
			}
			
			if(!IMBlockerConfig.INSTANCE.isIngameIMEEnabled()) {
				int scaledMargin = (int) (HOT_AREA_MARGIN * containerGuiScale), 
						compositionMinY = Math.min(caretY, compositionY);
				compositionX += compositionBorder.x();
				compositionY += compositionBorder.y();
				compositionMinY += compositionBorder.y();
				
				Rectangle preeditCursorRect;
				if(!IMBlockerConfig.INSTANCE.useStrictCursorRect()) {
					preeditCursorRect = new Rectangle(compositionX - scaledMargin, compositionMinY - scaledMargin,
							compositionWidth + scaledMargin * 2, compositionHeight + paddedInputHeight + scaledMargin * 2);
				}else {
					preeditCursorRect = new Rectangle(compositionX - scaledMargin, compositionY - scaledMargin,
							compositionWidth + scaledMargin * 2, compositionHeight + scaledMargin * 2);
				}
				
				InputSystem.setPreeditCursorRectangle(Minecraft.getInstance().getWindow().handle(), 
						preeditCursorRect.x(), preeditCursorRect.y(), preeditCursorRect.width(), preeditCursorRect.height());
			}
		}
	}

	public void renderOnMinecraftSurface(GuiGraphicsExtractor graphics) {
		if(preEditTextFormatted == null) {
			return;
		}
		
		backgroundBlitter.blit(graphics, overlayBounds.x() - 5, overlayBounds.y() - 5, 
				overlayBounds.width() + 10, overlayBounds.height() + 10);
		graphics.text(font, preEditTextFormatted, overlayBounds.x(), overlayBounds.y(), TEXT_COLOR, false);
		if (TextCursorUtils.isCursorVisible(Util.getMillis() - initTimeMs)) {
			TextCursorUtils.extractInsertCursor(graphics, 
					overlayBounds.x() + preEditCaretRenderX, overlayBounds.y(), TEXT_COLOR, 10);
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
	
	@FunctionalInterface
	private static interface BackgroundBlitter {
		void blit(GuiGraphicsExtractor graphics, int x, int y, int width, int height);
	}
	
	static {
		Identifier background = Identifier.withDefaultNamespace("widget/preedit");
		BackgroundBlitter backgroundBlitterImpl;
		try {
			RenderPipeline guiTexturedPipeline = RenderPipelines.GUI_TEXTURED;
			backgroundBlitterImpl = (graphics, x, y, width, height) -> graphics.blitSprite(guiTexturedPipeline, background, x, y, width, height);
		} catch (Throwable e) {
			com.mojang.renderpearl.api.pipeline.RenderPipeline guiTexturedPipeline = 
					ReflectionUtil.getFieldValue(RenderPipelines.class, null, null, "GUI_TEXTURED");
			backgroundBlitterImpl = (graphics, x, y, width, height) -> graphics.blitSprite(guiTexturedPipeline, background, x, y, width, height);
		}
		backgroundBlitter = backgroundBlitterImpl;
	}
}
