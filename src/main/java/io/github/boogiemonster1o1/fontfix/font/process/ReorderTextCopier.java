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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

/**
 * Copy vanilla text from IReorderingProcessor
 */
public class ReorderTextCopier implements CharacterVisitor {

    private Behavior behavior;

    private final MutableString mutableString = new MutableString();

    @Nullable
    private Style lastStyle;

    /**
     * @return {@code false} if action stopped
     */
    public boolean copyAndConsume(@NotNull OrderedText reorderingProcessor, Behavior behavior) {
        this.behavior = behavior;
        if (!reorderingProcessor.accept(this)) {
            // stopped
            return false;
        }
        return this.finish();
    }

    @Override
    public boolean accept(int index, @NotNull Style style, int codePoint) {
        if (style != this.lastStyle) {
            if (!this.mutableString.chars.isEmpty() && this.lastStyle != null) {
                if (this.behavior.consumeText(this.mutableString, this.lastStyle)) {
                    this.mutableString.chars.clear();
                    this.lastStyle = style;
                    // stop
                    return false;
                }
            }
            this.mutableString.chars.clear();
            this.lastStyle = style;
        }
        if (Character.isBmpCodePoint(codePoint)) {
            this.mutableString.chars.add((char) codePoint);
        } else {
            this.mutableString.chars.add(Character.highSurrogate(codePoint));
            this.mutableString.chars.add(Character.lowSurrogate(codePoint));
        }
        // continue
        return true;
    }

    private boolean finish() {
        if (!this.mutableString.chars.isEmpty() && this.lastStyle != null) {
            if (this.behavior.consumeText(this.mutableString, this.lastStyle)) {
                this.mutableString.chars.clear();
                this.lastStyle = null;
                return false;
            }
        }
        this.mutableString.chars.clear();
        this.lastStyle = null;
        return true;
    }

    @FunctionalInterface
    public interface Behavior {

        /**
         * @return {@code true} to stop action
         */
        boolean consumeText(CharSequence t, Style s);
    }
}
