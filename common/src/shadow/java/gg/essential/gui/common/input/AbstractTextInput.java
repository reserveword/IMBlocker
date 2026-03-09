package gg.essential.gui.common.input;

import kotlin.Pair;

public abstract class AbstractTextInput {
	public abstract class LinePosition {
		public abstract Pair<Float, Float> toScreenPos();
	}
}
