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
