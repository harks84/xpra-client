/*
 * Copyright (C) 2019 Mark Harkin
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

import java.util.Collection;
import java.util.Iterator;

public class StartCommand extends xpra.protocol.IOPacket {

	public String name;
	public String command;
	public Boolean ignore;

	public StartCommand(String name, String command, Boolean ignore) {
		super("start-command");
		this.name = name;
		this.command = command;
		this.ignore = ignore;
	}

	@Override
	protected void serialize(Collection<Object> elems) {
		elems.add(name);
		elems.add(command);
		elems.add(ignore);
	}

	@Override
	protected void deserialize(Iterator<Object> iter) {
		super.deserialize(iter);
		name = asString(iter.next());
		command = asString(iter.next());
		ignore = asBoolean(iter.next());
	}

}
