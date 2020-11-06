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

import it.unimi.dsi.fastutil.chars.CharArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * Used for lookup
 */
public class MutableString implements CharSequence {
    public final CharArrayList chars = new CharArrayList();

    public void addString(@NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            this.chars.add(str.charAt(i));
        }
    }

    @Override
    public int length() {
        return this.chars.size();
    }

    @Override
    public char charAt(int index) {
        return this.chars.getChar(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method won't be called when querying/lookup
     * But new a String when caching a text node as a reference
     *
     * @return reference str key
     */
    @NotNull
    @Override
    public String toString() {
        return new String(this.chars.toCharArray());
    }
}
