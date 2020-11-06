package io.github.boogiemonster1o1.fontfix.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

@Mixin(TextRenderer.class)
public interface TextRendererAccessor {
	@Accessor
	static Vector3f getFORWARD_SHIFT() {
		throw new UnsupportedOperationException();
	}

	@Accessor
	Function<Identifier, FontStorage> getFontStorageAccessor();

	@Accessor("handler")
	TextHandler getHandlerHandler();

	@Mutable
	@Accessor
	void setHandler(TextHandler handler);

	@Mutable
	@Accessor
	void setFontStorageAccessor(Function<Identifier, FontStorage> fontStorageAccessor);
}
