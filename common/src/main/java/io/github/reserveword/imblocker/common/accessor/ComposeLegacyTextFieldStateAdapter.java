package io.github.reserveword.imblocker.common.accessor;

import androidx.compose.ui.text.input.OffsetMapping;
import androidx.compose.ui.text.input.TextFieldValue;

public interface ComposeLegacyTextFieldStateAdapter {
	void imblocker$updateCaretPos(TextFieldValue value, OffsetMapping offsetMapping);
}
