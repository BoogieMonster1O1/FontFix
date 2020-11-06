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

import io.github.boogiemonster1o1.fontfix.font.glyph.TexturedGlyph;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;

public class RandomGlyphInfo extends GlyphRenderInfo {
    private static final Random RANDOM = new Random();

    /**
     * Array of glyphs with same advance
     */
    private final TexturedGlyph[] glyphs;

    public RandomGlyphInfo(TexturedGlyph[] glyphs, TextRenderEffect effect, int stringIndex, float offsetX) {
        super(effect, stringIndex, offsetX);
        this.glyphs = glyphs;
    }

    @Override
    public void drawGlyph(@NotNull BufferBuilder builder, @NotNull String raw, float x, float y, int r, int g, int b, int a) {
        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        glyphs[RANDOM.nextInt(glyphs.length)].drawGlyph(builder, x + offsetX, y, r, g, b, a);
        builder.end();
        BufferRenderer.draw(builder);
    }

    @Override
    public void drawGlyph(Matrix4f matrix, @NotNull VertexConsumerProvider buffer, @NotNull CharSequence raw, float x, float y, int r, int g, int b, int a, boolean seeThrough, int light) {
        glyphs[RANDOM.nextInt(glyphs.length)].drawGlyph(matrix, buffer, x + offsetX, y, r, g, b, a, seeThrough, light);
    }

    @Override
    public float getAdvance() {
        return glyphs[0].advance;
    }
}
