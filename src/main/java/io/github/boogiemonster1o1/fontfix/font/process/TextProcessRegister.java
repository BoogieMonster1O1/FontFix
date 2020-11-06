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

import java.util.List;

import io.github.boogiemonster1o1.fontfix.font.node.GlyphRenderInfo;
import io.github.boogiemonster1o1.fontfix.font.node.TextRenderEffect;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

/**
 * Temporary process results
 */
@Deprecated
public class TextProcessRegister {

    /**
     * Bit flag used with fontStyle to request the plain (normal) style
     */
    public static final byte PLAIN  = 0;
    /**
     * Bit flag used with fontStyle to request the bold style
     */
    public static final byte BOLD   = 1;
    /**
     * Bit flag used with fontStyle to request the italic style
     */
    public static final byte ITALIC = 1 << 1;

    /**
     * Represent to use default color
     */
    public static final int NO_COLOR = -1;


    private int defaultFontStyle;
    private int currentFontStyle;

    private int defaultColor;
    private int currentColor;
    private int lastColor;

    private boolean defaultStrikethrough;
    private boolean currentStrikethrough;

    private float strikethroughStart;

    private boolean defaultUnderline;
    private boolean currentUnderline;

    private float underlineStart;

    private boolean defaultObfuscated;
    private boolean currentObfuscated;

    private float advance;

    private final List<GlyphRenderInfo> glyphs  = new ObjectArrayList<>();
    private final List<TextRenderEffect> effects = new ObjectArrayList<>();
    //private final List<ColorStateInfo>   colors  = new ObjectArrayList<>();

    /**
     * Update style and set default values
     *
     * @param style s
     */
    public void beginProcess(@NotNull Style style) {
        if (style.getColor() != null) {
            this.defaultColor = style.getColor().getRgb();
            //colors.add(new ColorStateInfo(0, defaultColor));
        } else {
            this.defaultColor = NO_COLOR;
        }
        this.currentColor = this.defaultColor;

        this.defaultFontStyle = PLAIN;
        if (style.isBold()) {
            this.defaultFontStyle |= BOLD;
        }
        if (style.isItalic()) {
            this.defaultFontStyle |= ITALIC;
        }
        this.currentFontStyle = this.defaultFontStyle;

        this.defaultUnderline = style.isUnderlined();
        this.currentUnderline = this.defaultUnderline;
        this.underlineStart = 0;

        this.defaultStrikethrough = style.isStrikethrough();
        this.currentStrikethrough = this.defaultStrikethrough;
        this.strikethroughStart = 0;

        this.defaultObfuscated = style.isObfuscated();
        this.currentObfuscated = this.defaultObfuscated;

        this.advance = 0;
    }

    public void finishProcess() {
        if (this.currentStrikethrough) {
            //effects.add(EffectRenderInfo.strikethrough(strikethroughStart, advance, currentColor));
            this.strikethroughStart = this.advance;
        }
        if (this.currentUnderline) {
            //effects.add(EffectRenderInfo.underline(underlineStart, advance, currentColor));
            this.underlineStart = this.advance;
        }
    }

    @Nullable
    public TextRenderEffect[] wrapEffects() {
        if (this.effects.isEmpty()) {
            return null;
        }
        TextRenderEffect[] r = this.effects.toArray(new TextRenderEffect[0]);
        this.effects.clear();
        return r;
    }

    @NotNull
    public GlyphRenderInfo[] wrapGlyphs() {
        GlyphRenderInfo[] r = this.glyphs.toArray(new GlyphRenderInfo[0]);
        this.glyphs.clear();
        return r;
    }

    /*@Nullable
    public ColorStateInfo[] wrapColors() {
        if (colors.isEmpty()) {
            return null;
        }
        ColorStateInfo[] r = colors.toArray(new ColorStateInfo[0]);
        colors.clear();
        return r;
    }*/

    public void applyFormatting(@NotNull Formatting formatting, int glyphIndex) {
        if (formatting.getColorValue() != null) {
            if (this.setColor(formatting.getColorValue())) {
                //colors.add(new ColorStateInfo(glyphIndex, currentColor));
                if (this.currentStrikethrough) {
                    //effects.add(EffectRenderInfo.strikethrough(strikethroughStart, advance, lastColor));
                    this.strikethroughStart = this.advance;
                }
                if (this.currentUnderline) {
                    //effects.add(EffectRenderInfo.underline(underlineStart, advance, lastColor));
                    this.underlineStart = this.advance;
                }
            }
            this.setObfuscated(false);
        } else {
            switch (formatting) {
                case STRIKETHROUGH:
                    this.setStrikethrough(true);
                    break;
                case UNDERLINE:
                    this.setUnderline(true);
                    break;
                case BOLD:
                    this.setBold(true);
                    break;
                case ITALIC:
                    this.setItalic(true);
                    break;
                case OBFUSCATED:
                    this.setObfuscated(true);
                    break;
                case RESET: {
                    boolean p = false;
                    if (this.setDefaultColor()) {
                        //colors.add(new ColorStateInfo(glyphIndex, currentColor));
                        if (this.currentStrikethrough) {
                            //effects.add(EffectRenderInfo.strikethrough(strikethroughStart, advance, lastColor));
                            this.strikethroughStart = this.advance;
                        }
                        if (this.currentUnderline) {
                            //effects.add(EffectRenderInfo.underline(underlineStart, advance, lastColor));
                            this.underlineStart = this.advance;
                        }
                        p = true;
                    }
                    this.setDefaultObfuscated();
                    this.setDefaultFontStyle();
//                    if (this.setDefaultStrikethrough() && !p) {
//                        //effects.add(EffectRenderInfo.strikethrough(strikethroughStart, advance, currentColor));
//                    }
//                    if (this.setDefaultUnderline() && !p) {
//                        //effects.add(EffectRenderInfo.underline(underlineStart, advance, currentColor));
//                    }
                }
                break;
            }
        }
    }

    /**
     * Increase advance for current node
     *
     * @param adv a
     */
    @Deprecated
    private void addAdvance(float adv) {
        this.advance += adv;
    }

    /*public void depositGlyph(TexturedGlyph glyph) {
        glyphs.add(new StaticGlyphInfo(glyph, offsetX));
        advance += glyph.advance;
    }

    public void depositDigit(int stringIndex, TexturedGlyph[] glyphs) {
        if (currentObfuscated) {
            this.glyphs.add(new RandomGlyphInfo(glyphs, offsetX));
        } else {
            this.glyphs.add(new DigitGlyphInfo(glyphs, offsetX, stringIndex));
        }
        advance += glyphs[0].advance;
    }*/

    public boolean setDefaultFontStyle() {
        if (this.currentFontStyle != this.defaultFontStyle) {
            this.currentFontStyle = this.defaultFontStyle;
            return true;
        }
        return false;
    }

    /**
     * Set current bold
     *
     * @param b b
     * @return if bold changed
     */
    private boolean setBold(boolean b) {
        if (b) {
            b = (this.currentFontStyle & BOLD) == 0;
            this.currentFontStyle |= BOLD;
        } else {
            b = (this.currentFontStyle & BOLD) != 0;
            this.currentFontStyle &= ~BOLD;
        }
        return b;
    }

    /**
     * Set current italic
     *
     * @param b b
     * @return if italic changed
     */
    public boolean setItalic(boolean b) {
        if (b) {
            b = (this.currentFontStyle & ITALIC) == 0;
            this.currentFontStyle |= ITALIC;
        } else {
            b = (this.currentFontStyle & ITALIC) != 0;
            this.currentFontStyle &= ~ITALIC;
        }
        return b;
    }

    public boolean setDefaultColor() {
        return this.setColor(this.defaultColor);
    }

    /**
     * Set current color
     *
     * @param color c
     * @return if color changed
     */
    public boolean setColor(int color) {
        if (this.currentColor != color) {
            this.lastColor = this.currentColor;
            this.currentColor = color;
            return true;
        }
        return false;
    }

    /*public boolean setDigitMode(boolean b) {
        if (digitMode != b) {
            digitMode = b;
            return true;
        }
        return false;
    }

    public void setDigitGlyphs(TexturedGlyph[] digitGlyphs) {
        this.digitGlyphs = digitGlyphs;
    }

    public void addDigitIndex(int i) {
        digitIndexList.add(i);
    }

    public boolean isDigitMode() {
        return digitMode;
    }

    public boolean hasDigit() {
        return !digitIndexList.isEmpty();
    }

    public TexturedGlyph[] getDigitGlyphs() {
        return digitGlyphs;
    }

    public int[] toDigitIndexArray() {
        return digitIndexList.toArray(new int[0]);
    }*/

    public boolean setDefaultStrikethrough() {
        return this.setStrikethrough(this.defaultStrikethrough);
    }

    public boolean setStrikethrough(boolean b) {
        if (this.currentStrikethrough != b) {
            this.currentStrikethrough = b;
            if (b) {
                this.strikethroughStart = this.advance;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean setDefaultUnderline() {
        return this.setUnderline(this.defaultUnderline);
    }

    public boolean setUnderline(boolean b) {
        if (this.currentUnderline != b) {
            this.currentUnderline = b;
            if (b) {
                this.underlineStart = this.advance;
            } else {
                return true;
            }
        }
        return false;
    }

    public void setDefaultObfuscated() {
        this.setObfuscated(this.defaultObfuscated);
    }

    public void setObfuscated(boolean b) {
        if (this.currentObfuscated != b) {
            this.currentObfuscated = b;
            /*if (!b && obfuscatedCount > 0) {
                glyphs.add(GlyphRenderInfo.ofObfuscated(digitGlyphs, currentColor, obfuscatedCount));
                obfuscatedCount = 0;
            }*/
        }
    }

    /*public void addObfuscatedCount() {
        obfuscatedCount++;
    }

    public int getObfuscatedCount() {
        return obfuscatedCount;
    }*/

    public float getAdvance() {
        return this.advance;
    }

    public int getFontStyle() {
        return this.currentFontStyle;
    }

    public int getColor() {
        return this.currentColor;
    }

    public boolean isObfuscated() {
        return this.currentObfuscated;
    }

    /*public float getStrikethroughStart() {
        return strikethroughStart;
    }

    public float getUnderlineStart() {
        return underlineStart;
    }

    public boolean isStrikethrough() {
        return currentStrikethrough;
    }

    public boolean isUnderline() {
        return currentUnderline;
    }*/
}
