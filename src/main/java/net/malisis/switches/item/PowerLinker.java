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
import net.malisis.switches.ILinkedPower;
import net.malisis.switches.MalisisSwitches;
import net.malisis.switches.network.PowerLinkerMessage;
import net.malisis.switches.tileentity.LinkedPowerTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if (hand == EnumHand.OFF_HAND)
			return EnumActionResult.PASS;

		processClick(player, hand, world, pos, side);
		PowerLinkerMessage.sendClick(pos, hand, side);
		return EnumActionResult.SUCCESS;
	}

	public void processClick(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side)
	{
		ItemStack itemStack = player.getHeldItem(hand);
		if (!isStartSet(itemStack))
		{
			ILinkedPower lp = TileEntityUtils.getTileEntity(ILinkedPower.class, world, pos);
			if (lp != null)
				setStartPosition(itemStack, pos, world.getTotalWorldTime());
			else if (world.isRemote)
				MalisisCore.message("No switch selected.");
			return;
		}

		BlockPos start = getStartPosition(itemStack);
		if (start.equals(pos))
			return;

		LinkedPowerTileEntity te = TileEntityUtils.getTileEntity(LinkedPowerTileEntity.class, world, start);
		if (te == null)
			return;

		if (!player.isSneaking())
			te.linkPosition(pos);
		else
		{
			if (!te.unlinkPosition(pos))
				te.unlinkPosition(pos.offset(side));
		}
		//clearStartPosition(itemStack);
		return;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if (!isStartSet(itemStack))
			return;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getHeldItem(EnumHand.MAIN_HAND) != itemStack)
			clearStartPosition(itemStack);
	}

	protected boolean isStartSet(ItemStack itemStack)
	{
		return getNBT(itemStack).hasKey("start");
	}

	protected boolean setStartPosition(ItemStack itemStack, BlockPos pos, long time)
	{
		getNBT(itemStack).setLong("start", pos.toLong());
		return true;
	}

	protected BlockPos getStartPosition(ItemStack itemStack)
	{
		return BlockPos.fromLong(getNBT(itemStack).getLong("start"));
	}

	protected boolean clearStartPosition(ItemStack itemStack)
	{
		getNBT(itemStack).removeTag("start");
		return true;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return false;
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
}
