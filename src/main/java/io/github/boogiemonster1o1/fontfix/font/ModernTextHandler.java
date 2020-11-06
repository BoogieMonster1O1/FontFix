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

import io.github.boogiemonster1o1.fontfix.font.node.GlyphRenderInfo;
import io.github.boogiemonster1o1.fontfix.font.process.TextCacheProcessor;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.font.TextHandler;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Handle line breaks, get text width, etc.
 */
@Environment(EnvType.CLIENT)
public class ModernTextHandler extends TextHandler {

    private final TextCacheProcessor processor = TextCacheProcessor.getInstance();

    private final MutableFloat mutableFloat = new MutableFloat();

    /**
     * Constructor
     *
     * @param widthRetriever retrieve char width with given codePoint
     */
    //TODO remove width retriever as long as complex line wrapping finished
    public ModernTextHandler(WidthRetriever widthRetriever) {
        super(widthRetriever);
    }

    /**
     * Get text width
     *
     * @param text text
     * @return text width
     */
    @Override
    public float getWidth(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return this.processor.lookupVanillaNode(text, Style.EMPTY).advance;
    }

    /**
     * Get text width
     *
     * @param text text
     * @return total width
     */
    @Override
    public float getWidth(@NotNull StringVisitable text) {
        this.mutableFloat.setValue(0);
        // iterate all siblings
        text.visit((s, t) -> {
            if (!t.isEmpty()) {
                this.mutableFloat.add(this.processor.lookupVanillaNode(t, s).advance);
            }
            // continue
            return Optional.empty();
        }, Style.EMPTY);
        return this.mutableFloat.floatValue();
    }

    /**
     * Get text width
     *
     * @param text text
     * @return total width
     */
    @Override
    public float getWidth(@NotNull OrderedText text) {
        this.mutableFloat.setValue(0);
        this.processor.copier.copyAndConsume(text, (t, s) -> {
            if (t.length() != 0) {
                this.mutableFloat.add(this.processor.lookupVanillaNode(t, s).advance);
            }
            return false;
        });
        return this.mutableFloat.floatValue();
    }

    /**
     * Get trimmed length / size to width.
     * <p>
     * Return the number of characters in a text that will completely fit inside
     * the specified width when rendered.
     *
     * @param text  the text to trim
     * @param width the max width
     * @param style the style of the text
     * @return the length of the text when it is trimmed to be at most /
     * the number of characters from text that will fit inside width
     */
    @Override
    public int getTrimmedLength(@NotNull String text, int width, @NotNull Style style) {
        return this.sizeToWidth0(text, width, style);
    }

    private int sizeToWidth0(@NotNull CharSequence text, float width, @NotNull Style style) {
        if (text.length() == 0) {
            return 0;
        }
        /* The glyph array for a string is sorted by the string's logical character position */
        GlyphRenderInfo[] glyphs = this.processor.lookupVanillaNode(text, style).glyphs;

        /* Add up the individual advance of each glyph until it exceeds the specified width */
        float advance = 0;
        int glyphIndex = 0;
        while (glyphIndex < glyphs.length) {

            advance += glyphs[glyphIndex].getAdvance();
            if (advance <= width) {
                glyphIndex++;
            } else {
                break;
            }
        }

        /* The string index of the last glyph that wouldn't fit gives the total desired length of the string in characters */
        return glyphIndex < glyphs.length ? glyphs[glyphIndex].stringIndex : text.length();
    }

    /**
     * Trim a text so that it fits in the specified width when rendered.
     *
     * @param text  the text to trim
     * @param width the max width
     * @param style the style of the text
     * @return the trimmed text
     */
    @NotNull
    @Override
    public String trimToWidth(@NotNull String text, int width, @NotNull Style style) {
        return text.substring(0, this.sizeToWidth0(text, width, style));
    }

    /**
     * Trim a text backwards so that it fits in the specified width when rendered.
     *
     * @param text  the text to trim
     * @param width the max width
     * @param style the style of the text
     * @return the trimmed text
     */
    @NotNull
    @Override
    public String trimToWidthBackwards(@NotNull String text, int width, @NotNull Style style) {
        if (text.isEmpty()) {
            return text;
        }
        /* The glyph array for a string is sorted by the string's logical character position */
        GlyphRenderInfo[] glyphs = this.processor.lookupVanillaNode(text, style).glyphs;

        /* Add up the individual advance of each glyph until it exceeds the specified width */
        float advance = 0;
        int glyphIndex = glyphs.length - 1;
        while (glyphIndex >= 0) {

            advance += glyphs[glyphIndex].getAdvance();
            if (advance <= width) {
                glyphIndex--;
            } else {
                break;
            }
        }

        /* The string index of the last glyph that wouldn't fit gives the total desired length of the string in characters */
        int l = glyphIndex >= 0 ? glyphs[glyphIndex].stringIndex : 0;
        return text.substring(l);
    }

    /**
     * Trim a text to find the last sibling text style to handle its click or hover event
     *
     * @param text  the text to trim
     * @param width the max width
     * @return the last sibling text style
     */
    @Nullable
    @Override
    public Style getStyleAt(@NotNull StringVisitable text, int width) {
        this.mutableFloat.setValue(width);
        // iterate all siblings
        return text.visit((s, t) -> {
            if (this.sizeToWidth0(t, this.mutableFloat.floatValue(), s) < t.length()) {
                return Optional.of(s);
            }
            this.mutableFloat.subtract(this.processor.lookupVanillaNode(t, s).advance);
            // continue
            return Optional.empty();
        }, Style.EMPTY).orElse(null);
    }

    /**
     * Trim a text to find the last sibling text style to handle its click or hover event
     *
     * @param text  the text to trim
     * @param width the max width
     * @return the last sibling text style
     */
    @Nullable
    @Override
    public Style getStyleAt(@NotNull OrderedText text, int width) {
        this.mutableFloat.setValue(width);
        MutableObject<Style> sr = new MutableObject<>();
        // iterate all siblings
        if (!this.processor.copier.copyAndConsume(text, (t, s) -> {
            if (this.sizeToWidth0(t, this.mutableFloat.floatValue(), s) < t.length()) {
                sr.setValue(s);
                // break with result
                return true;
            }
            this.mutableFloat.subtract(this.processor.lookupVanillaNode(t, s).advance);
            // continue
            return false;
        })) {
            return sr.getValue();
        }
        return null;
    }

    /**
     * Trim to width
     *
     * @param textIn  the text to trim
     * @param width   the max width
     * @param styleIn the default style of the text
     * @return the trimmed multi text
     */
    //TODO further optimization is possible
    @NotNull
    @Override
    public StringVisitable trimToWidth(@NotNull StringVisitable textIn, int width, @NotNull Style styleIn) {
        TextCollector collector = new TextCollector();
        this.mutableFloat.setValue(width);
        // iterate all siblings
        return textIn.visit((style, text) -> {
            int size;
            if ((size = this.sizeToWidth0(text, this.mutableFloat.floatValue(), style)) < text.length()) {
                String sub = text.substring(0, size);
                if (!sub.isEmpty()) {
                    // add
                    collector.add(StringVisitable.styled(sub, style));
                }
                // combine and break
                return Optional.of(collector.getCombined());
            }
            if (!text.isEmpty()) {
                // add
                collector.add(StringVisitable.styled(text, style));
            }
            this.mutableFloat.subtract(this.processor.lookupVanillaNode(text, style).advance);
            // continue
            return Optional.empty();
        }, styleIn).orElse(textIn); // full text
    }

    /**
     * Wrap lines
     *
     * @param text      text to handle
     * @param wrapWidth max width of each line
     * @param style     style for the text
     * @param retainEnd retain the last word on each line
     * @param acceptor  accept each line result, params{current style, start index (inclusive), end index (exclusive)}
     */
    //TODO handle complex line wrapping, including bidi analysis, style splitting
    @Override
    public void wrapLines(String text, int wrapWidth, @NotNull Style style, boolean retainEnd, @NotNull TextHandler.LineWrappingConsumer acceptor) {
        super.wrapLines(text, wrapWidth, style, retainEnd, acceptor);
    }

    /**
     * Wrap lines
     *
     * @param text      text to handle
     * @param wrapWidth max width of each line
     * @param style     style for the text
     * @return a list of text for each line
     */
    @NotNull
    @Override
    public List<StringVisitable> wrapLines(String text, int wrapWidth, @NotNull Style style) {
        return super.wrapLines(text, wrapWidth, style);
    }
}
