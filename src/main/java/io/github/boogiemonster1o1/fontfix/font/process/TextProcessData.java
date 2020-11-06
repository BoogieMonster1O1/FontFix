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

import java.util.ArrayList;
import java.util.List;

import io.github.boogiemonster1o1.fontfix.font.node.GlyphRenderInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Singleton in text processor
 */
public class TextProcessData {

    /**
     * Array of temporary formatting info
     */
    public final List<FormattingStyle> codes = new ArrayList<>();

    /**
     * List of all processing glyphs
     */
    public final List<GlyphRenderInfo> allList = new ArrayList<>();

    /**
     * List of processing glyphs with same layout direction
     */
    public final List<GlyphRenderInfo> layoutList = new ArrayList<>();

    /**
     * Used in layoutFont
     */
    public final List<GlyphRenderInfo> minimalList = new ArrayList<>();

    /*
     * All color states
     */
    //public final List<ColorStateInfo> colors = new ArrayList<>();

    /**
     * Indicates current style index in {@link #codes} for layout processing
     */
    public int codeIndex;

    /**
     * The total advance (horizontal width) of the processing text
     */
    public float advance;

    /**
     * Needed in RTL layout
     */
    public float layoutRight;

    /**
     * Mark whether this node should enable effect rendering
     */
    public boolean hasEffect;

    public void finishStyleLayout(float adjust) {
        if (adjust != 0) {
            this.layoutList.forEach(e -> e.offsetX += adjust);
        }
        this.allList.addAll(this.layoutList);
        this.layoutList.clear();
    }

    public void finishFontLayout(float adjust) {
        if (adjust != 0) {
            this.minimalList.forEach(e -> e.offsetX += adjust);
        }
        this.layoutList.addAll(this.minimalList);
        this.minimalList.clear();
    }

    /*private void merge(@Nonnull List<float[]> list, int color, byte type) {
        if (list.isEmpty()) {
            return;
        }
        list.sort((o1, o2) -> Float.compare(o1[0], o2[0]));
        float[][] res = new float[list.size()][2];
        int i = -1;
        for (float[] interval : list) {
            if (i == -1 || interval[0] > res[i][1]) {
                res[++i] = interval;
            } else {
                res[i][1] = Math.max(res[i][1], interval[1]);
            }
        }
        res = Arrays.copyOf(res, i + 1);
        for (float[] in : res) {
            effects.add(new EffectRenderInfo(in[0], in[1], color, type));
        }
        list.clear();
    }*/

    @NotNull
    public GlyphRenderInfo[] wrapGlyphs() {
        //return allList.stream().map(ProcessingGlyph::toGlyph).toArray(GlyphRenderInfo[]::new);
        return this.allList.toArray(new GlyphRenderInfo[0]);
    }

    /*@Nonnull
    public ColorStateInfo[] wrapColors() {
        if (colors.isEmpty()) {
            return ColorStateInfo.NO_COLOR_STATE;
        }
        return colors.toArray(new ColorStateInfo[0]);
    }*/

    public void release() {
        this.allList.clear();
        this.codes.clear();
        //colors.clear();
        this.codeIndex = 0;
        this.advance = 0;
        this.layoutRight = 0;
        this.hasEffect = false;
    }

}
