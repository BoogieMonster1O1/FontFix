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

package io.github.boogiemonster1o1.fontfix.font.node;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;

import io.github.boogiemonster1o1.fontfix.font.glyph.GlyphManager;
import io.github.boogiemonster1o1.fontfix.font.process.FormattingStyle;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * The complete node, including final rendering results and layout information
 */
public class TextRenderNode {

    /**
     * Sometimes naive, too simple
     */
    public static final TextRenderNode EMPTY = new TextRenderNode(new GlyphRenderInfo[0], 0, false) {

        @Override
        public float drawText(@NotNull BufferBuilder builder, @NotNull String raw, float x, float y, int r, int g, int b, int a) {
            return 0;
        }

        @Override
        public float drawText(Matrix4f matrix, VertexConsumerProvider buffer, @NotNull CharSequence raw, float x, float y,
                              int r, int g, int b, int a, boolean isShadow, boolean seeThrough, int colorBackground, int packedLight) {
            return 0;
        }
    };

    /**
     * Vertical adjustment to string position.
     */
    private static final int BASELINE_OFFSET = 7;

    /**
     * Vertical adjustment to string position.
     */
    private static final int VANILLA_BASELINE_OFFSET = 6;


    /**
     * All glyphs to render.
     */
    public final GlyphRenderInfo[] glyphs;

    /*
     * Switch current color
     */
    //private final ColorStateInfo[] colors;

    /**
     * Total advance of this text node.
     */
    public final float advance;

    private final boolean hasEffect;

    public TextRenderNode(GlyphRenderInfo[] glyphs, float advance, boolean hasEffect) {
        this.glyphs = glyphs;
        //this.colors = colors;
        this.advance = advance;
        this.hasEffect = hasEffect;
    }

    public float drawText(@NotNull BufferBuilder builder, @NotNull String raw, float x, float y, int r, int g, int b, int a) {
        final int startR = r;
        final int startG = g;
        final int startB = b;

        y += BASELINE_OFFSET;
        x -= GlyphManager.GLYPH_OFFSET;
        RenderSystem.enableTexture();

        for (GlyphRenderInfo glyph : this.glyphs) {
            if (glyph.color != null) {
                int color = glyph.color;
                if (color == FormattingStyle.NO_COLOR) {
                    r = startR;
                    g = startG;
                    b = startB;
                } else {
                    r = color >> 16 & 0xff;
                    g = color >> 8 & 0xff;
                    b = color & 0xff;
                }
            }
            glyph.drawGlyph(builder, raw, x, y, r, g, b, a);
        }

        if (this.hasEffect) {
            r = startR;
            g = startG;
            b = startB;
            x += GlyphManager.GLYPH_OFFSET;
            RenderSystem.disableTexture();
            builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
            for (GlyphRenderInfo glyph : this.glyphs) {
                if (glyph.color != null) {
                    int color = glyph.color;
                    if (color == FormattingStyle.NO_COLOR) {
                        r = startR;
                        g = startG;
                        b = startB;
                    } else {
                        r = color >> 16 & 0xff;
                        g = color >> 8 & 0xff;
                        b = color & 0xff;
                    }
                }
                glyph.drawEffect(builder, x, y, r, g, b, a);
            }
            builder.end();
            BufferRenderer.draw(builder);
        }
        return this.advance;
    }

    public float drawText(Matrix4f matrix, VertexConsumerProvider buffer, @NotNull CharSequence raw, float x, float y,
                          int r, int g, int b, int a, boolean isShadow, boolean seeThrough, int colorBackground, int packedLight) {
        final int startR = r;
        final int startG = g;
        final int startB = b;
        // I found only sign now, maybe there will be other types
        if (buffer instanceof VertexConsumerProvider.Immediate) {
            ((VertexConsumerProvider.Immediate) buffer).draw(TexturedRenderLayers.getSign());
        }

        y += VANILLA_BASELINE_OFFSET;
        x -= GlyphManager.GLYPH_OFFSET;

        for (GlyphRenderInfo glyph : this.glyphs) {
            if (glyph.color != null) {
                int color = glyph.color;
                if (color == FormattingStyle.NO_COLOR) {
                    r = startR;
                    g = startG;
                    b = startB;
                } else {
                    r = color >> 16 & 0xff;
                    g = color >> 8 & 0xff;
                    b = color & 0xff;
                    if (isShadow) {
                        r >>= 2;
                        g >>= 2;
                        b >>= 2;
                    }
                }
            }
            glyph.drawGlyph(matrix, buffer, raw, x, y, r, g, b, a, seeThrough, packedLight);
        }

        VertexConsumer builder = null;
        x += GlyphManager.GLYPH_OFFSET;

        if (this.hasEffect) {
            r = startR;
            g = startG;
            b = startB;
            builder = buffer.getBuffer(EffectRenderType.getRenderType(seeThrough));
            for (GlyphRenderInfo glyph : this.glyphs) {
                if (glyph.color != null) {
                    int color = glyph.color;
                    if (color == FormattingStyle.NO_COLOR) {
                        r = startR;
                        g = startG;
                        b = startB;
                    } else {
                        r = color >> 16 & 0xff;
                        g = color >> 8 & 0xff;
                        b = color & 0xff;
                        if (isShadow) {
                            r >>= 2;
                            g >>= 2;
                            b >>= 2;
                        }
                    }
                }
                glyph.drawEffect(matrix, builder, x, y, r, g, b, a, packedLight);
            }
        }

        if (colorBackground != 0) {
            y -= VANILLA_BASELINE_OFFSET;
            a = colorBackground >> 24 & 0xff;
            r = colorBackground >> 16 & 0xff;
            g = colorBackground >> 8 & 0xff;
            b = colorBackground & 0xff;
            if (builder == null) {
                builder = buffer.getBuffer(EffectRenderType.getRenderType(seeThrough));
            }
            builder.vertex(matrix, x - 1, y + 9, TextRenderEffect.EFFECT_DEPTH).color(r, g, b, a).light(packedLight).next();
            builder.vertex(matrix, x + this.advance + 1, y + 9, TextRenderEffect.EFFECT_DEPTH).color(r, g, b, a).light(packedLight).next();
            builder.vertex(matrix, x + this.advance + 1, y, TextRenderEffect.EFFECT_DEPTH).color(r, g, b, a).light(packedLight).next();
            builder.vertex(matrix, x - 1, y, TextRenderEffect.EFFECT_DEPTH).color(r, g, b, a).light(packedLight).next();
        }

        return this.advance;
    }
}
