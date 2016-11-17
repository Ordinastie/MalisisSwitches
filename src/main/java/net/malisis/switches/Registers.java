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

import static net.malisis.switches.MalisisSwitches.Blocks.*;
import static net.malisis.switches.MalisisSwitches.Items.*;
import net.malisis.switches.block.Relay;
import net.malisis.switches.block.Switch;
import net.malisis.switches.item.GreenStone;
import net.malisis.switches.item.PowerLinker;
import net.malisis.switches.tileentity.LinkedPowerTileEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Registers
{
	public static void init()
	{
		registerItems();

		registerSwitches();

		registerRelay();

		GameRegistry.registerTileEntityWithAlternatives(LinkedPowerTileEntity.class, "linkedPowerTileEntity", "switchTileEntity");
	}

	private static void registerItems()
	{
		greenStone = new GreenStone();
		greenStone.register();
		GameRegistry.addRecipe(new ItemStack(greenStone, 8), "AAA", "ABA", "AAA", 'A', Items.REDSTONE, 'B', Items.EMERALD);

		powerLinker = new PowerLinker();
		powerLinker.register();
		GameRegistry.addRecipe(new ItemStack(powerLinker), "AB ", "BA ", "  A", 'A', Items.IRON_INGOT, 'B', greenStone);
	}

	private static void registerSwitches()
	{
		ItemStack lever = new ItemStack(Blocks.LEVER);
		ItemStack green = new ItemStack(greenStone);
		ItemStack red = new ItemStack(Items.REDSTONE);

		basicSwitch1 = new Switch("basicSwitch1", 0.5F, 0.5F);
		basicSwitch1.register();
		GameRegistry.addShapelessRecipe(new ItemStack(basicSwitch1), lever, green, red, new ItemStack(Items.IRON_INGOT));

		lightSwitch1 = new Switch("lightSwitch1", 0.3F, 0.5F);
		lightSwitch1.register();
		GameRegistry.addShapelessRecipe(new ItemStack(lightSwitch1), lever, green, red, new ItemStack(Blocks.QUARTZ_BLOCK));

	}

	private static void registerRelay()
	{
		relay = new Relay();
		relay.register();

		GameRegistry.addShapedRecipe(new ItemStack(relay), "ABA", "BCB", "ABA", 'A', new ItemStack(greenStone), 'B', new ItemStack(
				Items.REDSTONE), 'C', new ItemStack(Items.REPEATER));
	}
}
