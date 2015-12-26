/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.switches.renderer;

import java.util.Set;

import net.malisis.core.renderer.MalisisRenderer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.Vertex;
import net.malisis.core.renderer.element.shape.Cube;
import net.malisis.core.util.EntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.tileentity.SwitchTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;

import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class SwitchLinkRenderer extends MalisisRenderer
{
	@Override
	public void render()
	{
		if (!(tileEntity instanceof SwitchTileEntity))
			return;

		if (!EntityUtils.isEquipped(Minecraft.getMinecraft().thePlayer, MalisisSwitches.Items.powerLinker))
			return;

		disableTextures();
		GlStateManager.disableDepth();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		next(GL11.GL_LINES);
		Vertex start = new Vertex(0.5F, 0.5F, 0.5F, 0x339933FF, Vertex.BRIGHTNESS_MAX);
		Set<BlockPos> linkedPos = ((SwitchTileEntity) tileEntity).linkedPositions();
		for (BlockPos pos : linkedPos)
		{
			pos = pos.subtract(this.pos);
			drawLine(start, vertexFromPos(pos));
			Shape shape = new Cube();
			shape.translate(pos.getX(), pos.getY(), pos.getZ());
			drawShape(shape);
		}

		next();
		enableTextures();

	}

	private void drawLine(Vertex start, Vertex end)
	{
		RenderParameters rp = new RenderParameters();
		rp.usePerVertexColor.set(true);
		rp.usePerVertexBrightness.set(true);
		drawVertex(start, 0, rp);
		drawVertex(end, 1, rp);
	}

	private Vertex vertexFromPos(BlockPos pos)
	{
		return new Vertex(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0x990000FF, Vertex.BRIGHTNESS_MAX);
	}
}
