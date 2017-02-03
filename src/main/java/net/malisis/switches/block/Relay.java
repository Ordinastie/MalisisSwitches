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
import net.malisis.core.renderer.icon.Icon;
import net.malisis.core.renderer.icon.provider.IBlockIconProvider;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.tileentity.LinkedPowerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
public class Relay extends MalisisBlock implements ITileEntityProvider
{
	@SideOnly(Side.CLIENT)
	private Icon relaySide;
	@SideOnly(Side.CLIENT)
	private Icon relayOff;
	@SideOnly(Side.CLIENT)
	private Icon relayOn;

	public Relay()
	{
		super(Material.CIRCUITS);

		setCreativeTab(MalisisSwitches.tab);
		setHardness(1.0F);
		setName("relay");

		addComponent(new DirectionalComponent(DirectionalComponent.ALL, IPlacement.BLOCKSIDE));
		addComponent(new PowerComponent(InteractionType.REDSTONE, ComponentType.BOTH));

		if (MalisisCore.isClient())
		{
			loadIcons();
			addComponent((IBlockIconProvider) this::getIcon);
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadIcons()
	{
		relaySide = Icon.from(MalisisSwitches.modid + ":blocks/relay_side");
		relayOff = Icon.from(MalisisSwitches.modid + ":blocks/relay_off");
		relayOn = Icon.from(MalisisSwitches.modid + ":blocks/relay_on");
	}

	@SideOnly(Side.CLIENT)
	public Icon getIcon(IBlockState state, EnumFacing facing)
	{
		return facing == EnumFacing.SOUTH || facing == null ? PowerComponent.isPowered(state) ? relayOn : relayOff : relaySide;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state, BoundingBoxType type)
	{
		return new AxisAlignedBB(0, 0, 0, 1, 1, 0.125F);
	}

	@Override
	public IBlockState getStateFromItemStack(ItemStack itemStack)
	{
		return getDefaultState();
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
	{
		side = side.getOpposite();
		return world.isSideSolid(pos.offset(side), side);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		for (EnumFacing side : EnumFacing.values())
			if (world.isSideSolid(pos.offset(side), side))
				return true;

		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos)
	{
		super.neighborChanged(state, world, pos, neighborBlock, fromPos);

		EnumFacing dir = DirectionalComponent.getDirection(world, pos).getOpposite();
		if (!world.isSideSolid(pos.offset(dir), dir))
		{
			dropBlockAsItem(world, pos, getDefaultState(), 0);
			world.setBlockToAir(pos);
			return;
		}

		//redstone check
		LinkedPowerTileEntity te = TileEntityUtils.getTileEntity(LinkedPowerTileEntity.class, world, pos);
		if (te != null)
			te.setPower(PowerComponent.isPowered(world, pos) ? 15 : 0); //use world,pos to get the updated state
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new LinkedPowerTileEntity();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		LinkedPowerTileEntity te = TileEntityUtils.getTileEntity(LinkedPowerTileEntity.class, world, pos);
		if (te != null)
			te.setPower(0, true);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
}
