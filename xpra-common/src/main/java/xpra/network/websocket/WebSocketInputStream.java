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
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;

public class WebSocketInputStream extends PipedInputStream {
	
	PipedOutputStream outStream;
	
	public WebSocketInputStream(WebSocket ws) {
		try {
			outStream = new PipedOutputStream(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ws.addListener(new WebSocketAdapter() {
			
			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
				System.out.println();
			}
			
			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] bytes) throws Exception {
				outStream.write(bytes);
				outStream.flush();
			}
			
			@Override
			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				System.out.println();
			}

		});
	}

}
