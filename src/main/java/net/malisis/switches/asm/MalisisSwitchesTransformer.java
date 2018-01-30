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

package net.malisis.switches.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.malisis.core.asm.AsmHook;
import net.malisis.core.asm.MalisisClassTransformer;
import net.malisis.core.asm.mappings.McpMethodMapping;

/**
 * @author Ordinastie
 *
 */
public class MalisisSwitchesTransformer extends MalisisClassTransformer
{
	@Override
	public void registerHooks()
	{
		register(getRedstonePower());
	}

	private AsmHook getRedstonePower()
	{
		McpMethodMapping getRedstonePower = new McpMethodMapping(	"getRedstonePower",
																	"func_175651_c",
																	"net.minecraft.world.World",
																	"(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)I");

		AsmHook ah = new AsmHook(getRedstonePower);

		//return PowerManager.getRedstonePower(this, pos, side)
		//ALOAD 0
		//ALOAD 1
		//ALOAD 2
		//INVOKESTATIC net/malisis/switches/PowerManager.getRedstonePower (Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)I
		//IRETURN
		InsnList insert = new InsnList();
		insert.add(new VarInsnNode(ALOAD, 0));
		insert.add(new VarInsnNode(ALOAD, 1));
		insert.add(new VarInsnNode(ALOAD, 2));
		insert.add(new MethodInsnNode(	INVOKESTATIC,
										"net/malisis/switches/PowerManager",
										"getRedstonePower",
										"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)I",
										false));
		insert.add(new InsnNode(IRETURN));

		ah.insert(insert).debug();
		return ah;
	}
}
