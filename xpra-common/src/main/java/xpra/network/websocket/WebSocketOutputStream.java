/*******************************************************************************
 * Copyright (C) 2019 Mark Harkin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package xpra.network.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.neovisionaries.ws.client.WebSocket;

public class WebSocketOutputStream extends OutputStream {
	
	private WebSocket ws;
	private ArrayList<Byte> byteList = new ArrayList<Byte>();
	
	public WebSocketOutputStream(WebSocket ws) {
		this.ws = ws;
	}

	
	
	@Override
	public void write(int b) throws IOException {
		byteList.add((byte)b);
	}
	
	@Override
	public void flush() throws IOException {
		//TODO can probably do this better
		byte[] bytes = new byte[byteList.size()];
		for(int i = 0; i < byteList.size(); i++) {
		    bytes[i] = byteList.get(i).byteValue();
		}
		byteList = new ArrayList<Byte>();
		ws.sendBinary(bytes);
		ws.flush();
    }

}
