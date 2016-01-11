package net.malisis.switches;

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

import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.malisis.core.item.MalisisItem;
import net.malisis.core.network.MalisisNetwork;
import net.malisis.switches.block.Switch;
import net.malisis.switches.item.PowerLinker;
import net.malisis.switches.renderer.SwitchLinkRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author Ordinastie
 *
 */
@Mod(modid = MalisisSwitches.modid, name = MalisisSwitches.modname, version = MalisisSwitches.version, dependencies = "required-after:malisiscore")
public class MalisisSwitches implements IMalisisMod
{

	public static final String modid = "malisisswitches";
	public static final String modname = "Malisis Switches";
	public static final String version = "${version}";

	public static MalisisSwitches instance;
	public static MalisisNetwork network;

	public static CreativeTabs tab = new MalisisSwitchesTab();

	public MalisisSwitches()
	{
		instance = this;
		network = new MalisisNetwork(this);
		MalisisCore.registerMod(this);
	}

	@Override
	public String getModId()
	{
		return modid;
	}

	@Override
	public String getName()
	{
		return modname;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public Settings getSettings()
	{
		return null;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Registers.init();
		PowerManager.registerBlockData();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if (MalisisCore.isClient())
			new SwitchLinkRenderer();

	}

	public static class Blocks
	{
		public static Switch basicSwitch1;
	}

	public static class Items
	{
		public static PowerLinker powerLinker;
		public static MalisisItem greenStone;
	}

}
