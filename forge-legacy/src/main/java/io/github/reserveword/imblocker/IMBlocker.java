package io.github.reserveword.imblocker;

import io.github.reserveword.imblocker.common.IMBlockerCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IMBlockerCore.MODID)
public class IMBlocker {

	public IMBlocker() {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> IMBlocker::initializeClient);
	}

	private static void initializeClient() {
		try {
			Class.forName("io.github.reserveword.imblocker.IMBlockerClient")
					.getMethod("initialize", FMLJavaModLoadingContext.class)
					.invoke(null, FMLJavaModLoadingContext.get());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to initialize IMBlocker client hooks", e);
		}
	}
}
