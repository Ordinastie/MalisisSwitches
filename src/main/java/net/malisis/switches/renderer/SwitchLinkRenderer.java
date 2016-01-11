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

import net.malisis.core.MalisisRegistry;
import net.malisis.core.block.IBlockDirectional;
import net.malisis.core.renderer.MalisisRenderer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.Vertex;
import net.malisis.core.renderer.element.shape.Cube;
import net.malisis.core.renderer.icon.IIconRegister;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.malisis.core.util.AABBUtils;
import net.malisis.core.util.EntityUtils;
import net.malisis.core.util.Point;
import net.malisis.core.util.raytrace.Raytrace;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.block.Switch;
import net.malisis.switches.tileentity.SwitchTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class SwitchLinkRenderer extends MalisisRenderer implements IIconRegister
{

	private BlockPos linkedPos;
	private Pair<EnumFacing, Point> closest;
	private MalisisIcon linkedPosIcon = new MalisisIcon(MalisisSwitches.modid + ":blocks/linked_pos");
	private MalisisIcon linkedPosPointerIcon = new MalisisIcon(MalisisSwitches.modid + ":blocks/linked_pos_pointer");
	private Shape linkedPosCube = new Cube();
	private RenderParameters rp = new RenderParameters();

	public SwitchLinkRenderer()
	{
		registerFor(SwitchTileEntity.class);
		MalisisRegistry.registerIconRegister(this);

		rp.useEnvironmentBrightness.set(false);
		rp.brightness.set(Vertex.BRIGHTNESS_MAX);
		rp.interpolateUV.set(false);
	}

	@Override
	public void registerIcons(TextureMap textureMap)
	{
		linkedPosIcon = linkedPosIcon.register(textureMap);
		linkedPosPointerIcon = linkedPosPointerIcon.register(textureMap);
	}

	@Override
	public void render()
	{
		if (!(tileEntity instanceof SwitchTileEntity))
			return;

		if (!EntityUtils.isEquipped(Minecraft.getMinecraft().thePlayer, MalisisSwitches.Items.powerLinker))
			return;

		Point startPoint = posToPoint(pos);
		EnumFacing side = IBlockDirectional.getDirection(blockState);
		startPoint.x -= 0.5F * side.getFrontOffsetX();
		startPoint.y -= 0.5F * side.getFrontOffsetY();
		startPoint.z -= 0.5F * side.getFrontOffsetZ();
		int powerColor = ((Switch) block).isPowered(blockState) ? 0x339933 : 0x990000;
		Set<BlockPos> linkedPositions = ((SwitchTileEntity) tileEntity).linkedPositions();

		//DRAWING LINES

		next(GL11.GL_LINES);
		disableTextures();
		GlStateManager.disableDepth();

		rp.colorMultiplier.set(powerColor);
		rp.brightness.set(Vertex.BRIGHTNESS_MAX);

		for (BlockPos p : linkedPositions)
		{
			setup(p, startPoint);
			if (closest == null)
				continue;
			drawVertex(pointToVertex(startPoint), 0, rp);
			drawVertex(pointToVertex(closest.getRight()), 1, rp);
		}

		//DRAWING CONTACT

		next(GL11.GL_QUADS);
		enableTextures();
		enableBlending();

		rp.icon.set(linkedPosPointerIcon);

		for (BlockPos p : linkedPositions)
		{
			setup(p, startPoint);
			if (closest == null)
				continue;
			linkedPosCube.resetState();
			Point c = closest.getRight();
			EnumFacing s = closest.getLeft();
			Face f = linkedPosCube.getFace(Face.nameFromDirection(s));
			if (f == null)
				continue;

			float x = (float) c.x - pos.getX() - (s.getAxis() == Axis.X ? 0 : 0.5F);
			float y = (float) c.y - pos.getY() - (s.getAxis() == Axis.Y ? 0 : 0.5F);
			float z = (float) c.z - pos.getZ() - (s.getAxis() == Axis.Z ? 0 : 0.5F);

			if (s == EnumFacing.SOUTH)
				z -= s.getFrontOffsetZ();
			else if (s == EnumFacing.EAST)
				x -= s.getFrontOffsetX();
			else if (s == EnumFacing.UP)
				y -= s.getFrontOffsetY();

			f.translate(x, y, z);

			//f.translate(linkedPos.getX(), linkedPos.getY(), linkedPos.getZ());
			//f.scale(1.01F);
			drawFace(f, rp);
		}

		//DRAWING CUBE
		next();
		GlStateManager.enableDepth();

		rp.icon.set(linkedPosIcon);

		for (BlockPos p : linkedPositions)
		{
			setup(p, startPoint);
			linkedPosCube.resetState();
			linkedPosCube.translate(linkedPos.getX(), linkedPos.getY(), linkedPos.getZ());
			linkedPosCube.scale(1.01F);

			drawShape(linkedPosCube, rp);
		}

	}

	private void setup(BlockPos pos, Point startPoint)
	{
		linkedPos = pos.subtract(this.pos);
		closest = new Raytrace(startPoint, posToPoint(pos)).trace(AABBUtils.identities(pos));
	}

	private Vertex pointToVertex(Point p)
	{
		return new Vertex(p.x - pos.getX(), p.y - pos.getY(), p.z - pos.getZ());
	}

	private Point posToPoint(BlockPos pos)
	{
		return new Point(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
	}
}
