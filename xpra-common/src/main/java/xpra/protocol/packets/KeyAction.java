/*
 * Copyright (C) 2017 Jakub Ksiezniak
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package xpra.protocol.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KeyAction extends WindowPacket {

	String keyname = "";
	boolean pressed;
	List<String> modifiers = new ArrayList<>();
	int keyval;
	String name = "";
	int keycode;

  int group = 0; // added in xpra 2.1
	
	public KeyAction(int windowId, int keycode, String keyname, boolean pressed, List<String>modifiers) {
		super("key-action", windowId);
		this.keyval = 0;
		this.keycode = keycode;
		this.keyname = keyname;
		this.pressed = pressed;
		this.modifiers = modifiers;
	}
	
	@Override
	public void serialize(Collection<Object> elems) {
		super.serialize(elems);
		elems.add(keyname);
		elems.add(pressed);
		elems.add(modifiers);
		elems.add(keyval);
		elems.add(name);
		elems.add(keycode);
		elems.add(group); // required by server 2.1
	}

}
