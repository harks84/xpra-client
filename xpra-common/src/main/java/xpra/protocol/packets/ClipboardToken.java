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
import java.util.Iterator;
import java.util.List;

public class ClipboardToken extends xpra.protocol.IOPacket {
	
	public String a = "CLIPBOARD";
	public List<String> b = new ArrayList<String>();
	public String c = "UTF8_STRING";
	public String d = "UTF8_STRING";
	public int dataLength = 0;
	
	public String e2 = "bytes";
	public String data = null;
	public boolean e3 = false;
	public boolean e4 = true;
	public boolean e5 = true;
	//clipboard-token
//	var packet = ["clipboard-token", "CLIPBOARD", [], "UTF8_STRING", "UTF8_STRING", data.length, "bytes", data, false, true, true];
	
	public ClipboardToken() {
		super("clipboard-token");
	}
	
	public ClipboardToken(String data) {
		super("clipboard-token");
		this.data = data;
		this.dataLength=data.length();
	}
	
	@Override
	protected void serialize(Collection<Object> elems) {
		elems.add(a);
		elems.add(b);
		elems.add(c);
		elems.add(d);
		elems.add(dataLength);
		elems.add(e2);
		elems.add(data);
		elems.add(e3);
		elems.add(e4);
		elems.add(e5);
	}
	
	@Override
	public void deserialize(Iterator<Object> iter) {
		super.deserialize(iter);

		String a = asString(iter.next());
		Object o = iter.next();
		if(o instanceof ArrayList) {
			System.out.println();
			for(Object subO : (ArrayList)o) {
				
					System.out.println(asString(subO));
				
			}
		}
		if(iter.hasNext()) {
			iter.next();
			iter.next();
			iter.next();
			iter.next();
			data = asString(iter.next());
			dataLength = data.length();
		}
		
		

	}

}
