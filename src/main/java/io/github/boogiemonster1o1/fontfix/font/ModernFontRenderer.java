/*
 * Modern UI.
 * Copyright (C) 2019-2020 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.boogiemonster1o1.fontfix.font;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.boogiemonster1o1.fontfix.font.node.TextRenderNode;
import io.github.boogiemonster1o1.fontfix.font.process.TextCacheProcessor;
import io.github.boogiemonster1o1.fontfix.mixin.EntityRenderDispatcherAccessor;
import io.github.boogiemonster1o1.fontfix.mixin.MinecraftClientAccessor;
import io.github.boogiemonster1o1.fontfix.mixin.TextHandlerAccessor;
import io.github.boogiemonster1o1.fontfix.mixin.TextRendererAccessor;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Replace vanilla renderer with Modern UI renderer
 */
@Environment(EnvType.CLIENT)
public class ModernFontRenderer extends TextRenderer {

    /**
     * Render thread instance
     */
    private static ModernFontRenderer instance;

    /**
     * Config value
     */
    // TODO
    public static boolean sAllowFontShadow = true;

    private final TextCacheProcessor processor = TextCacheProcessor.getInstance();

    private final MutableFloat tempFloat = new MutableFloat();

    private TextRenderer originalRenderer;

    private ModernFontRenderer() {
        super(id -> new FontStorage(MinecraftClient.getInstance().getTextureManager(), new Identifier("minecraft:dummy")));
    }

    /**
     * INTERNAL USE ONLY, developers can't use this for any reason
     *
     * @return instance
     * @see TrueTypeRenderer#getInstance()
     */
    public static ModernFontRenderer getInstance() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (instance == null) {
            instance = new ModernFontRenderer();
            TextHandler o = ((TextRendererAccessor) MinecraftClient.getInstance().textRenderer).getHandlerHandler();
            Function<Identifier, FontStorage> r = ((TextRendererAccessor) MinecraftClient.getInstance().textRenderer).getFontStorageAccessor();
            TextHandler.WidthRetriever c = ((TextHandlerAccessor) o).getWidthRetriever();
            ModernTextHandler t = new ModernTextHandler(c);
            ((TextRendererAccessor) MinecraftClient.getInstance().textRenderer).setHandler(t);
            ((TextRendererAccessor) MinecraftClient.getInstance().textRenderer).setFontStorageAccessor(r);
        }
        return instance;
    }

    void hook(boolean doHook) {
        boolean working = MinecraftClient.getInstance().textRenderer instanceof ModernFontRenderer;
        if (working == doHook) {
            return;
        }
        if (doHook) {
            this.originalRenderer = MinecraftClient.getInstance().textRenderer;
            ((MinecraftClientAccessor) MinecraftClient.getInstance()).setTextRenderer(this);
            ((EntityRenderDispatcherAccessor) MinecraftClient.getInstance().getEntityRenderDispatcher()).setTextRenderer(this);
        } else {
            ((MinecraftClientAccessor) MinecraftClient.getInstance()).setTextRenderer(this.originalRenderer);
            ((EntityRenderDispatcherAccessor) MinecraftClient.getInstance().getEntityRenderDispatcher()).setTextRenderer(this.originalRenderer);
        }
    }

    @Override
    public int draw(@NotNull String string, float x, float y, int color, boolean dropShadow, Matrix4f matrix,
                    @NotNull VertexConsumerProvider buffer, boolean seeThrough, int colorBackground, int packedLight, boolean bidiFlag) {
        // bidiFlag is useless, we have our layout system
        x += this.drawLayer0(string, x, y, color, dropShadow, matrix, buffer, seeThrough, colorBackground, packedLight, Style.EMPTY);
        return (int) x + (dropShadow ? 1 : 0);
    }

    @Override
    public int draw(@NotNull Text text, float x, float y, int color, boolean dropShadow, @NotNull Matrix4f matrix,
                    @NotNull VertexConsumerProvider buffer, boolean seeThrough, int colorBackground, int packedLight) {
        this.tempFloat.setValue(x);
        // iterate all siblings
        text.visit((style, string) -> {
            this.tempFloat.add(this.drawLayer0(string, this.tempFloat.floatValue(), y, color, dropShadow, matrix,
                    buffer, seeThrough, colorBackground, packedLight, style));
            // continue
            return Optional.empty();
        }, Style.EMPTY);
        return this.tempFloat.intValue() + (dropShadow ? 1 : 0);
    }

    @Override
    public int draw(@NotNull OrderedText text, float x, float y, int color, boolean dropShadow, @NotNull Matrix4f matrix,
                    @NotNull VertexConsumerProvider buffer, boolean seeThrough, int colorBackground, int packedLight) {
        this.tempFloat.setValue(x);
        this.processor.copier.copyAndConsume(text, (string, style) -> {
            this.tempFloat.add(this.drawLayer0(string, this.tempFloat.floatValue(), y, color, dropShadow, matrix,
                            buffer, seeThrough, colorBackground, packedLight, style));
                    // continue, equals to Optional.empty()
                    return false;
                }
        );
        return this.tempFloat.intValue() + (dropShadow ? 1 : 0);
    }

    private float drawLayer0(@NotNull CharSequence string, float x, float y, int color, boolean dropShadow, Matrix4f matrix,
                             @NotNull VertexConsumerProvider buffer, boolean transparent, int colorBackground, int packedLight, Style style) {
        if (string.length() == 0) {
            return 0;
        }
        // ensure alpha, color can be ARGB, or can be RGB
        // if alpha <= 1, make alpha = 255
        if ((color & 0xfe000000) == 0) {
            color |= 0xff000000;
        }

        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        TextRenderNode node = this.processor.lookupVanillaNode(string, style);
        if (dropShadow && sAllowFontShadow) {
            node.drawText(matrix, buffer, string, x + 1, y + 1, r >> 2, g >> 2, b >> 2, a, true,
                    transparent, colorBackground, packedLight);
            matrix = matrix.copy();
            matrix.addToLastColumn(TextRendererAccessor.getFORWARD_SHIFT());
        }

        return node.drawText(matrix, buffer, string, x, y, r, g, b, a, false, transparent, colorBackground, packedLight);
    }

    @Deprecated
    @Override
    public boolean isRightToLeft() {
        return false;
    }

    /**
     * Bidi always works no matter what language is in.
     * So we should analyze the original string without reordering.
     *
     * @param text text
     * @return text
     */
    @Deprecated
    @NotNull
    @Override
    public String mirror(@NotNull String text) {
        return text;
    }
}
