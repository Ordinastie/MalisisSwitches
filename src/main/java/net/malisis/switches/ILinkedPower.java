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

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
public interface ILinkedPower
{
	public Set<BlockPos> linkedPositions();

	public default BlockPos getPos()
	{
		return func_174877_v();
	}

	//obf for TileEntity.GetPos
	public default BlockPos func_174877_v()
	{
		throw new IllegalStateException();
	}

	public default World getWorld()
	{
		return func_145831_w();
	}

	//obf for TileEntity.getWorld()
	public default World func_145831_w()
	{
		throw new IllegalStateException();
	}

	public default Block getBlockType()
	{
		return func_145838_q();
	}

	//obf for TileEntity.getBlockType()
	public default Block func_145838_q()
	{
		throw new IllegalStateException();
	}

	public default void linkPosition(BlockPos pos)
	{
		linkedPositions().add(pos);
	}

	public default boolean unlinkPosition(BlockPos pos)
	{
		if (!linkedPositions().remove(pos))
			return false;

		PowerManager.setPower(getWorld(), pos, 0, false);
		getWorld().neighborChanged(pos, getBlockType(), getPos());
		getWorld().notifyNeighborsOfStateChange(pos, getBlockType(), true);

		return true;
	}

	public default void setPower(int power)
	{
		setPower(power, false);
	}

	public default void setPower(int power, boolean sendToClients)
	{
		for (BlockPos pos : linkedPositions())
		{
			PowerManager.setPower(getWorld(), pos, power, sendToClients);
			getWorld().neighborChanged(pos, getBlockType(), getPos());
			getWorld().notifyNeighborsOfStateChange(pos, getBlockType(), true);
		}
	}

}
