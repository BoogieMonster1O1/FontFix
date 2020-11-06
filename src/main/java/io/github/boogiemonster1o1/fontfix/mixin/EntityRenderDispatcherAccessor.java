package io.github.boogiemonster1o1.fontfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
	@Mutable
	@Accessor
	void setTextRenderer(TextRenderer textRenderer);
}
