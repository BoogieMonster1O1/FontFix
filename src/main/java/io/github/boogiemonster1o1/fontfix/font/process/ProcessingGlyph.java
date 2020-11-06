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

package io.github.boogiemonster1o1.fontfix.font.process;

import java.util.Objects;

import io.github.boogiemonster1o1.fontfix.font.glyph.TexturedGlyph;
import io.github.boogiemonster1o1.fontfix.font.node.TextRenderEffect;
import org.jetbrains.annotations.Nullable;

/**
 * Temporary resulted glyph
 */
@Deprecated
public class ProcessingGlyph {

    public static final byte STATIC_TEXT = 0;

    public static final byte DYNAMIC_DIGIT = 1;

    public static final byte RANDOM_DIGIT = 2;

    /**
     * First assignment is stripIndex, and it will be adjusted to stringIndex later
     */
    public int stringIndex;

    /**
     * It will be adjusted in RTL layout
     */
    public float offsetX;

    /**
     * For type {@link #DYNAMIC_DIGIT} or {@link #RANDOM_DIGIT}
     * All glyphs are same width
     */
    public final TexturedGlyph[] glyphs;

    /**
     * For type {@link #STATIC_TEXT}
     */
    public final TexturedGlyph glyph;

    @Nullable
    public final TextRenderEffect effect;

    /**
     * Either {@link #STATIC_TEXT}, {@link #DYNAMIC_DIGIT} or {@link #RANDOM_DIGIT}
     */
    public final byte type;

    @Nullable
    public Integer color;

    public ProcessingGlyph(int stripIndex, float offsetX, TexturedGlyph glyph, @Nullable TextRenderEffect effect, byte type) {
        this.stringIndex = stripIndex;
        this.offsetX = offsetX;
        this.effect = effect;
        this.glyphs = null;
        this.glyph = glyph;
        this.type = type;
    }

    public ProcessingGlyph(int stripIndex, float offsetX, TexturedGlyph[] glyphs, @Nullable TextRenderEffect effect, byte type) {
        this.stringIndex = stripIndex;
        this.offsetX = offsetX;
        this.glyphs = glyphs;
        this.effect = effect;
        this.glyph = null;
        this.type = type;
    }

    /*public GlyphRenderInfo toGlyph() {
        switch (type) {
            case DYNAMIC_DIGIT:
                return new DigitGlyphInfo(glyphs, effect, color, offsetX, stringIndex);
            case RANDOM_DIGIT:
                return new RandomGlyphInfo(glyphs, effect, color, offsetX);
            default:
                return new StandardGlyphInfo(glyph, effect, color, offsetX);
        }
    }*/

    @Deprecated
    public float getAdvance() {
        if (this.glyph == null) {
            return Objects.requireNonNull(this.glyphs)[0].advance;
        }
        return this.glyph.advance;
    }

    public int getStringIndex() {
        return this.stringIndex;
    }
}
