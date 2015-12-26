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

package net.malisis.switches.item;

import net.malisis.core.MalisisCore;
import net.malisis.core.item.MalisisItem;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.tileentity.SwitchTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
public class PowerLinker extends MalisisItem
{
	public PowerLinker()
	{
		setName("powerLinker");
		setTexture(MalisisSwitches.modid + ":items/power_linker");
		setCreativeTab(MalisisSwitches.tab);
	}

	protected NBTTagCompound getNBT(ItemStack itemStack)
	{
		if (itemStack.getTagCompound() == null)
			itemStack.setTagCompound(new NBTTagCompound());

		return itemStack.getTagCompound();
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!isStartSet(itemStack))
		{
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == MalisisSwitches.Blocks.switchBlock)
				setStartPosition(itemStack, pos, world.getTotalWorldTime());
			return !world.isRemote;
		}

		BlockPos start = getStartPosition(itemStack);
		if (start.equals(pos))
			return !world.isRemote;

		SwitchTileEntity te = TileEntityUtils.getTileEntity(SwitchTileEntity.class, world, start);
		if (te == null)
			return !world.isRemote;

		if (!player.isSneaking())
		{
			MalisisCore.message("Linking " + pos);
			te.linkPosition(pos);
		}
		else
		{
			MalisisCore.message("UnLinking " + pos);
			te.unlinkPosition(pos);
		}
		//clearStartPosition(itemStack);
		return !world.isRemote;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if (world.isRemote || !isStartSet(itemStack))
			return;

		if (world.getTotalWorldTime() - getNBT(itemStack).getLong("time") > 400)
			clearStartPosition(itemStack);
	}

	protected boolean isStartSet(ItemStack itemStack)
	{
		return getNBT(itemStack).hasKey("start");
	}

	protected boolean setStartPosition(ItemStack itemStack, BlockPos pos, long time)
	{
		MalisisCore.message("Start set");
		getNBT(itemStack).setLong("start", pos.toLong());
		getNBT(itemStack).setLong("time", time);
		return true;
	}

	protected BlockPos getStartPosition(ItemStack itemStack)
	{
		return BlockPos.fromLong(getNBT(itemStack).getLong("start"));
	}

	protected boolean clearStartPosition(ItemStack itemStack)
	{
		MalisisCore.message("Start cleared");
		getNBT(itemStack).removeTag("start");
		getNBT(itemStack).removeTag("time");
		return true;
	}

	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player)
	{
		return false;
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
}
