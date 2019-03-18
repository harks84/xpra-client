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
			}
			
			@Override
			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				System.out.println();
			}

		});
	}

}
