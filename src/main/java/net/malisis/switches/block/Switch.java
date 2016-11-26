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

package net.malisis.switches.block;

import net.malisis.core.MalisisCore;
import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.block.component.DirectionalComponent;
import net.malisis.core.block.component.DirectionalComponent.IPlacement;
import net.malisis.core.block.component.PowerComponent;
import net.malisis.core.block.component.PowerComponent.ComponentType;
import net.malisis.core.block.component.PowerComponent.InteractionType;
import net.malisis.core.renderer.DefaultRenderer;
import net.malisis.core.renderer.MalisisRendered;
import net.malisis.core.renderer.icon.provider.IIconProvider;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.tileentity.LinkedPowerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
@MalisisRendered(block = DefaultRenderer.Block.class, item = DefaultRenderer.Item.class)
public class Switch extends MalisisBlock implements ITileEntityProvider
{
	private AxisAlignedBB aabb;

	public Switch(String name, float width, float height, float depth)
	{
		super(Material.IRON);
		setCreativeTab(MalisisSwitches.tab);
		setHardness(1.0F);
		setName(name);

		this.aabb = new AxisAlignedBB(0.5F - width / 2, 0.5F - height / 2, 0, 0.5F + width / 2, 0.5F + height / 2, depth);

		addComponent(new DirectionalComponent(DirectionalComponent.ALL, IPlacement.BLOCKSIDE));
		addComponent(new PowerComponent(InteractionType.RIGHT_CLICK, ComponentType.PROVIDER));

		if (MalisisCore.isClient())
		{
			addComponent(IIconProvider.create(MalisisSwitches.modid + ":blocks/", name + "_on")
										.forProperty(PowerComponent.getProperty(this))
										.withValue(false, name + "_off")
										.build());
		}
	}

	public Switch(String name, float width, float height)
	{
		this(name, width, height, 0.01F);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);

		LinkedPowerTileEntity te = TileEntityUtils.getTileEntity(LinkedPowerTileEntity.class, world, pos);
		if (te != null)
			te.setPower(PowerComponent.isPowered(world, pos) ? 15 : 0); //use world,pos to get the updated state

		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;

		return aabb;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
	{
		return world.isSideSolid(pos.offset(side.getOpposite()), side, true);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		for (EnumFacing side : EnumFacing.VALUES)
			if (world.isSideSolid(pos.offset(side), side.getOpposite(), true))
				return true;

		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock)
	{
		super.neighborChanged(state, world, pos, neighborBlock);

		EnumFacing dir = DirectionalComponent.getDirection(state);
		if (world.isSideSolid(pos.offset(dir.getOpposite()), dir, true))
			return;
		this.dropBlockAsItem(world, pos, state, 0);
		world.setBlockToAir(pos);
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return PowerComponent.isPowered(state) && DirectionalComponent.getDirection(state) == side ? 15 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new LinkedPowerTileEntity();
	}

	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		LinkedPowerTileEntity te = TileEntityUtils.getTileEntity(LinkedPowerTileEntity.class, world, pos);
		if (te != null)
			te.setPower(0, true);
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.CUTOUT_MIPPED;
	}
}