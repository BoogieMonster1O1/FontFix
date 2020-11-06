package io.github.boogiemonster1o1.fontfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.font.TextHandler;

@Mixin(TextHandler.class)
public interface TextHandlerAccessor {
	@Accessor
	TextHandler.WidthRetriever getWidthRetriever();
}
