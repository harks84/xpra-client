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

public class SendFile extends xpra.protocol.IOPacket {

	public SendFile() {
		super("send-file");
	}

	public String name;
	public String mimeType;
	public boolean printIt;
	public boolean remoteOpen;
	public int dataSize;
	public byte[] data;
	
	@Override
	public void deserialize(Iterator<Object> iter) {
		super.deserialize(iter);

		name = asString(iter.next());
		mimeType = asString(iter.next());
		printIt = asBoolean(iter.next());
		remoteOpen = asBoolean(iter.next());
		dataSize = asInt(iter.next());
		data = asByteArray(iter.next());
	}
		
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		return  builder.toString();
	}

	@Override
	protected void serialize(Collection<Object> elems) {
		elems.add(name);
		elems.add(mimeType);
		elems.add(printIt);
		elems.add(remoteOpen);
		elems.add(dataSize);
		elems.add(data);
		
	}
}