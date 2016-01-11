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

import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.IBlockDirectional;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.renderer.DefaultRenderer;
import net.malisis.core.renderer.MalisisRendered;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.malisis.core.renderer.icon.provider.IBlockIconProvider;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.tileentity.SwitchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
@MalisisRendered(block = DefaultRenderer.Block.class, item = DefaultRenderer.Item.class)
public class Switch extends MalisisBlock implements ITileEntityProvider, IBlockDirectional
{
	public static PropertyBool POWERED = PropertyBool.create("POWER");

	public Switch(String name)
	{
		super(Material.iron);
		setCreativeTab(MalisisSwitches.tab);
		setHardness(1.0F);
		setName(name);

		setDefaultState(getDefaultState().withProperty(POWERED, false));
	}

	@Override
	public PropertyDirection getPropertyDirection()
	{
		return ALL;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, ALL, POWERED);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void createIconProvider(Object object)
	{
		iconProvider = new SwitchIconProvider(getRegistryName());
	}

	@Override
	public EnumFacing getPlacingDirection(EnumFacing side, EntityLivingBase placer)
	{
		return side;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		boolean powered = !isPowered(state);
		world.setBlockState(pos, state.withProperty(POWERED, powered));
		world.notifyNeighborsOfStateChange(pos, this);
		world.notifyNeighborsOfStateChange(pos.offset(IBlockDirectional.getDirection(state).getOpposite()), this);

		SwitchTileEntity te = TileEntityUtils.getTileEntity(SwitchTileEntity.class, world, pos);
		if (te != null)
			te.setPower(powered ? 15 : 0);
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;

		EnumFacing direction = IBlockDirectional.getDirection(world, pos);
		if (direction == EnumFacing.DOWN)
			return new AxisAlignedBB(0.25F, 0.99F, 0.25F, 0.75F, 1F, 0.75F);
		else if (direction == EnumFacing.UP)
			return new AxisAlignedBB(0.25F, 0F, 0.25F, 0.75F, 0.01F, 0.75F);
		else
			return new AxisAlignedBB(0.25F, 0.25F, 0, 0.75F, 0.75F, 0.01F);
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
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing dir = IBlockDirectional.getDirection(state);
		if (world.isSideSolid(pos.offset(dir.getOpposite()), dir, true))
			return;
		this.dropBlockAsItem(world, pos, state, 0);
		world.setBlockToAir(pos);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
	{
		return isPowered(state) && IBlockDirectional.getDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new SwitchTileEntity();
	}

	@Override
	public int getMetaFromState(Block block, IBlockState state)
	{
		return IBlockDirectional.super.getMetaFromState(block, state);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return super.getMetaFromState(state) + (isPowered(state) ? (1 << 3) : 0);
	}

	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		SwitchTileEntity te = TileEntityUtils.getTileEntity(SwitchTileEntity.class, world, pos);
		if (te != null)
			te.setPower(0);
	}

	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer)
	{
		return layer == EnumWorldBlockLayer.CUTOUT_MIPPED;
	}

	public boolean isPowered(World world, BlockPos pos)
	{
		return isPowered(world.getBlockState(pos));
	}

	public boolean isPowered(IBlockState state)
	{
		return state.getBlock() == this && (boolean) state.getValue(POWERED);
	}

	public static class SwitchIconProvider implements IBlockIconProvider
	{
		private MalisisIcon switchOn;
		private MalisisIcon switchOff;

		public SwitchIconProvider(String name)
		{
			switchOn = new MalisisIcon(MalisisSwitches.modid + ":blocks/" + name + "_on");
			switchOff = new MalisisIcon(MalisisSwitches.modid + ":blocks/" + name + "_off");
		}

		@Override
		public void registerIcons(TextureMap textureMap)
		{
			switchOn = switchOn.register(textureMap);
			switchOff = switchOff.register(textureMap);
		}

		@Override
		public MalisisIcon getIcon()
		{
			return switchOn;
		}

		@Override
		public MalisisIcon getIcon(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
		{
			return ((Switch) state.getBlock()).isPowered(state) || side != IBlockDirectional.getDirection(state) ? switchOn : switchOff;
		}
	}
}