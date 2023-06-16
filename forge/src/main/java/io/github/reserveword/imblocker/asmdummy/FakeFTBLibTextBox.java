package io.github.reserveword.imblocker.asmdummy;

import io.github.reserveword.imblocker.IMCheckState;

public class FakeFTBLibTextBox {
    public static class FakeSubClass extends FakeFTBLibTextBox {
        public boolean submethod() { return false; }
    }

    public void tick() {
        if (this instanceof FakeSubClass)
            IMCheckState.captureTick(this, ((FakeSubClass)this).submethod());
    }
}
