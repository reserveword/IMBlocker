package io.github.reserveword.imblocker.legacy1122;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public final class IMBlockerLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{GuiTextFieldTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // No launch-time data is required.
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
