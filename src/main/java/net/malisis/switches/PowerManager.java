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

package net.malisis.switches;

import net.malisis.core.util.blockdata.BlockDataHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
public class PowerManager
{
	public static String POWER_BLOCK_DATA = MalisisSwitches.modid + ":powerAmount";

	private static PowerManager instance = new PowerManager();

	public int getPower(World world, BlockPos pos, EnumFacing side)
	{
		PowerData data = BlockDataHandler.getData(POWER_BLOCK_DATA, world, pos);

		return data != null ? data.getPower() : 0;
	}

	public static void setPower(World world, BlockPos pos, int amount)
	{
		if (amount <= 0)
		{
			BlockDataHandler.removeData(POWER_BLOCK_DATA, world, pos);
			return;
		}

		if (amount > 15)
			amount = 15;

		BlockDataHandler.setData(POWER_BLOCK_DATA, world, pos, new PowerData(15));
	}

	public static int getRedstonePower(World world, BlockPos pos, EnumFacing side)
	{
		int power = instance.getPower(world, pos, side);
		if (power >= 15)
			return power;
		power = Math.max(power, instance.getPower(world, pos.offset(side.getOpposite()), side));
		if (power >= 15)
			return power;
		int blockPower = 0;

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().shouldCheckWeakPower(world, pos, side))
			blockPower = world.getStrongPower(pos);
		else
			blockPower = state.getBlock().isProvidingWeakPower(world, pos, state, side);

		return Math.max(power, blockPower);
	}

	public static void registerBlockData()
	{
		BlockDataHandler.registerBlockData(POWER_BLOCK_DATA, PowerData::fromBytes, PowerData::toBytes);
	}
}
