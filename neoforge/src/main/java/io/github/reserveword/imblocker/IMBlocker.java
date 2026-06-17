package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;

@Mod(IMBlockerCore.MODID)
public class IMBlocker {

	public IMBlocker(ModContainer container) {
		if(FMLLoader.getDist() == Dist.CLIENT || FMLEnvironment.dist == Dist.CLIENT) {
			initializeClient(container);
		}
	}

	private static void initializeClient(ModContainer container) {
		try {
			Class.forName("io.github.reserveword.imblocker.IMBlockerClient")
					.getMethod("initialize", ModContainer.class)
					.invoke(null, container);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to initialize IMBlocker client hooks", e);
		}
	}
}
