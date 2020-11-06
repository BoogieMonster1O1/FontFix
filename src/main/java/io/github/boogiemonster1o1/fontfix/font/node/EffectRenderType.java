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

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;

import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class EffectRenderType extends RenderLayer {

    private static final EffectRenderType INSTANCE    = new EffectRenderType();
    private static final EffectRenderType SEE_THROUGH = new EffectRenderType("modern_text_effect_see_through");

    private static final ImmutableList<RenderPhase> STATES;
    private static final ImmutableList<RenderPhase> SEE_THROUGH_STATES;

    static {
        STATES = ImmutableList.of(
                RenderPhase.NO_TEXTURE,
                RenderPhase.TRANSLUCENT_TRANSPARENCY,
                RenderPhase.DISABLE_DIFFUSE_LIGHTING,
                RenderPhase.SHADE_MODEL,
                RenderPhase.ONE_TENTH_ALPHA,
                RenderPhase.LEQUAL_DEPTH_TEST,
                RenderPhase.ENABLE_CULLING,
                RenderPhase.ENABLE_LIGHTMAP,
                RenderPhase.DISABLE_OVERLAY_COLOR,
                RenderPhase.FOG,
                RenderPhase.NO_LAYERING,
                RenderPhase.MAIN_TARGET,
                RenderPhase.DEFAULT_TEXTURING,
                RenderPhase.ALL_MASK,
                RenderPhase.FULL_LINE_WIDTH
        );
        SEE_THROUGH_STATES = ImmutableList.of(
                RenderPhase.NO_TEXTURE,
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

    private EffectRenderType() {
        super("modern_text_effect",
                VertexFormats.POSITION_COLOR_LIGHT,
                GL11.GL_QUADS, 256, false, true,
                () -> STATES.forEach(RenderPhase::startDrawing),
                () -> STATES.forEach(RenderPhase::endDrawing));
        this.hashCode = Objects.hash(super.hashCode(), STATES);
    }

    private EffectRenderType(String t) {
        super(t,
                VertexFormats.POSITION_COLOR_LIGHT,
                GL11.GL_QUADS, 256, false, true,
                () -> SEE_THROUGH_STATES.forEach(RenderPhase::startDrawing),
                () -> SEE_THROUGH_STATES.forEach(RenderPhase::endDrawing));
        this.hashCode = Objects.hash(super.hashCode(), SEE_THROUGH_STATES);
    }

    public static EffectRenderType getRenderType(boolean seeThrough) {
        return seeThrough ? SEE_THROUGH : INSTANCE;
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
