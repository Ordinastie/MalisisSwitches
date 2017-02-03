/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Ordinastie
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

import net.malisis.core.util.Vector;

/**
 * @author Ordinastie
 *
 */
public class SwitchData
{
	public static final SwitchData BASIC_SWITCH_1 = new SwitchData("basicSwitch1", "basic_switch1", new Vector(0.5F, 0.5F, 0.01F));
	public static final SwitchData LIGHT_SWITCH_1 = new SwitchData("lightSwitch1", "light_switch1", new Vector(0.3F, 0.5F, 0.01F));

	public String name;
	public String textureName;
	public Vector size;

	public SwitchData(String name, String textureName, Vector size)
	{
		this.name = name;
		this.textureName = textureName;
		this.size = size;
	}
}
