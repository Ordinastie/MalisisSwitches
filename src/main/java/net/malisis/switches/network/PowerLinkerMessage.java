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

package net.malisis.switches.network;

import io.netty.buffer.ByteBuf;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.network.MalisisMessage;
import net.malisis.core.util.EntityUtils;
import net.malisis.switches.MalisisSwitches;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@MalisisMessage
public class PowerLinkerMessage implements IMalisisMessageHandler<PowerLinkerMessage.Packet, IMessage>
{
	public PowerLinkerMessage()
	{
		MalisisSwitches.network.registerMessage(this, PowerLinkerMessage.Packet.class, Side.SERVER);
	}

	@Override
	public void process(Packet message, MessageContext ctx)
	{
		EntityPlayer player = IMalisisMessageHandler.getPlayer(ctx);
		if (!EntityUtils.isEquipped(player, MalisisSwitches.Items.powerLinker, EnumHand.MAIN_HAND))
			return;

		MalisisSwitches.Items.powerLinker.processClick(	player,
														message.hand,
														IMalisisMessageHandler.getWorld(ctx),
														message.pos,
														message.side);
	}

	public static void sendClick(BlockPos pos, EnumHand hand, EnumFacing side)
	{
		MalisisSwitches.network.sendToServer(new Packet(pos, hand, side));
	}

	public static class Packet implements IMessage
	{
		private BlockPos pos;
		private EnumHand hand;
		private EnumFacing side;

		public Packet(BlockPos pos, EnumHand hand, EnumFacing side)
		{
			this.pos = pos;
			this.hand = hand;
			this.side = side;
		}

		public Packet()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			pos = BlockPos.fromLong(buf.readLong());
			hand = EnumHand.values()[buf.readInt()];
			side = EnumFacing.getFront(buf.readInt());
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeLong(pos.toLong());
			buf.writeInt(hand.ordinal());
			buf.writeInt(side.getIndex());
		}
	}
}
