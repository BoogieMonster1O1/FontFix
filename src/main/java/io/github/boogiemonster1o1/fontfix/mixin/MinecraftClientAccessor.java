package io.github.boogiemonster1o1.fontfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
	@Mutable
	@Accessor
	void setTextRenderer(TextRenderer textRenderer);
}
