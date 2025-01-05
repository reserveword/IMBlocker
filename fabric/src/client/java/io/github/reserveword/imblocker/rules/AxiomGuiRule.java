package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.IMManager;

public class AxiomGuiRule implements Rule {
    public static imgui.ImGuiIO imGuiIO;

    @Override
    public double Priority() {
        return 10000;
    }

    @Override
    public boolean apply() {
        if (imGuiIO == null || !imGuiIO.getWantCaptureKeyboard()) {
            return false;
        } else if(imGuiIO.getWantTextInput()) {
            IMManager.setState(true);
        } else {
            IMManager.setState(false);
        }
        return true;
    }
}
