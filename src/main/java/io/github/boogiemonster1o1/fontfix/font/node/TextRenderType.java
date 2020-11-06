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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Objects;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;

public class TextRenderType extends RenderLayer {

    /**
     * Texture id to render type map
     */
    //TODO remove some old textures depend on put order
    private static final Map<Integer, TextRenderType> TYPES             = new Int2ObjectLinkedOpenHashMap<>();
    private static final Map<Integer, TextRenderType> SEE_THROUGH_TYPES = new Int2ObjectLinkedOpenHashMap<>();

    /**
     * Only the texture id is different, the rest state are same
     */
    private static final ImmutableList<RenderPhase> GENERAL_STATES;
    private static final ImmutableList<RenderPhase> SEE_THROUGH_STATES;

    static {
        GENERAL_STATES = ImmutableList.of(
                RenderPhase.TRANSLUCENT_TRANSPARENCY,
                RenderPhase.DISABLE_DIFFUSE_LIGHTING,
                RenderPhase.SHADE_MODEL,
                RenderPhase.ONE_TENTH_ALPHA,
                RenderPhase.LEQUAL_DEPTH_TEST,
                RenderPhase.ENABLE_CULLING,
                RenderPhase.ENABLE_LIGHTMAP,
                RenderPhase.ENABLE_OVERLAY_COLOR,
                RenderPhase.FOG,
                RenderPhase.NO_LAYERING,
                RenderPhase.MAIN_TARGET,
                RenderPhase.DEFAULT_TEXTURING,
                RenderPhase.ALL_MASK,
                RenderPhase.FULL_LINE_WIDTH
        );
        SEE_THROUGH_STATES = ImmutableList.of(
                RenderPhase.TRANSLUCENT_TRANSPARENCY,
                RenderPhase.DISABLE_DIFFUSE_LIGHTING,
                RenderPhase.SHADE_MODEL,
                RenderPhase.ONE_TENTH_ALPHA,
                RenderPhase.ALWAYS_DEPTH_TEST,
                RenderPhase.ENABLE_CULLING,
                RenderPhase.ENABLE_LIGHTMAP,
                RenderPhase.DISABLE_OVERLAY_COLOR,
                RenderPhase.FOG,
                RenderPhase.NO_LAYERING,
                RenderPhase.MAIN_TARGET,
                RenderPhase.DEFAULT_TEXTURING,
                RenderPhase.COLOR_MASK,
                RenderPhase.FULL_LINE_WIDTH
        );
    }

    private final int hashCode;

    /**
     * The OpenGL texture ID that contains this glyph image.
     */
    public final int textureName;

    private TextRenderType(int textureName) {
        super("modern_text",
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
                GL11.GL_QUADS, 256, false, true,
                () -> {
                    GENERAL_STATES.forEach(RenderPhase::startDrawing);
                    RenderSystem.enableTexture();
                    RenderSystem.bindTexture(textureName);
                },
                () -> GENERAL_STATES.forEach(RenderPhase::endDrawing));
        this.textureName = textureName;
        this.hashCode = Objects.hash(super.hashCode(), GENERAL_STATES, textureName);
    }

    private TextRenderType(int textureName, String t) {
        super(t,
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
                GL11.GL_QUADS, 256, false, true,
                () -> {
                    SEE_THROUGH_STATES.forEach(RenderPhase::startDrawing);
                    RenderSystem.enableTexture();
                    RenderSystem.bindTexture(textureName);
                },
                () -> SEE_THROUGH_STATES.forEach(RenderPhase::endDrawing));
        this.textureName = textureName;
        this.hashCode = Objects.hash(super.hashCode(), SEE_THROUGH_STATES, textureName);
    }

    public static TextRenderType getOrCacheType(int textureName, boolean seeThrough) {
        if (seeThrough) {
            return SEE_THROUGH_TYPES.computeIfAbsent(textureName, n -> new TextRenderType(n, "modern_text_see_through"));
        }
        return TYPES.computeIfAbsent(textureName, TextRenderType::new);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Singleton, the constructor is private
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
