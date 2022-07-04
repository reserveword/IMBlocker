package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public class FakeFTBLibTextBox extends Widget {

    public FakeFTBLibTextBox(int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
    }

    public static class FakeSubClass extends FakeFTBLibTextBox {

        public FakeSubClass(int x, int y, int width, int height, ITextComponent title) {
            super(x, y, width, height, title);
        }

        public boolean submethod() { return false; }
    }

    public void tick() {
        if (this instanceof FakeSubClass)
            IMCheckState.captureTick(this, ((FakeSubClass)this).submethod());
    }
}
