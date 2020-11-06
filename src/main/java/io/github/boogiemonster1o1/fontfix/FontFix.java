package io.github.boogiemonster1o1.fontfix;

import java.security.AccessController;
import java.security.PrivilegedAction;

import io.github.boogiemonster1o1.fontfix.font.ModernFontRenderer;
import io.github.boogiemonster1o1.fontfix.font.TrueTypeRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FontFix implements PreLaunchEntrypoint, ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onPreLaunch() {
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			System.setProperty("java.awt.headless", "false");
			return null;
		});
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("fontfix", "resource_manager");
			}

			@Override
			public void apply(ResourceManager manager) {
				ModernFontRenderer.getInstance();
				TrueTypeRenderer.getInstance();
			}
		});
	}
}
