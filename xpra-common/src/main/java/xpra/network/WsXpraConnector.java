package xpra.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import xpra.client.XpraClient;
import xpra.network.websocket.WebSocketInputStream;
import xpra.network.websocket.WebSocketOutputStream;
import xpra.protocol.packets.Disconnect;

public class WsXpraConnector extends XpraConnector implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(WsXpraConnector.class);

	private final String host;
	private final int port;

	private Thread thread;

	private WebSocketOutputStream outputStream;
	private WebSocketInputStream inputStream;

	public WsXpraConnector(XpraClient client, String hostname, int port) {
		super(client);
		this.host = hostname;
		this.port = port;

	}

	@Override
	public synchronized boolean connect() {
		if (thread != null) {
			return false;
		}
		thread = new Thread(this);
		thread.start();
		return true;
	}

	@Override
	public synchronized void disconnect() {
		if (thread != null) {
			if (!disconnectCleanly()) {
				thread.interrupt();
			}
			thread = null;
		}
	}

	private boolean disconnectCleanly() {
		final xpra.protocol.XpraSender s = client.getSender();
		if (s != null) {
			s.send(new Disconnect());
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			WebSocketFactory wsf = new com.neovisionaries.ws.client.WebSocketFactory();
			WebSocket ws = wsf.createSocket("ws://" + host + ":" + port, 5000);
			ws.addProtocol("binary");

			ws.connect();

			outputStream = new WebSocketOutputStream(ws);
			client.onConnect(new xpra.protocol.XpraSender(outputStream));
			fireOnConnectedEvent();
			inputStream = new WebSocketInputStream(ws);
			PacketReader reader = new PacketReader(inputStream);
			logger.info("Start Xpra connection...");
			while (!Thread.interrupted() && !client.isDisconnectedByServer()) {
				List<Object> dp = reader.readList();
				onPacketReceived(dp);
			}
			logger.info("Finished Xpra connection!");
		} catch (IOException e) {
			client.onConnectionError(e);
			fireOnConnectionErrorEvent(e);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null)
				try {
					socket.close();
					if (client.getSender() != null) {
						client.getSender().close();
					}
				} catch (Exception ignored) {
				}
			client.onDisconnect();
			fireOnDisconnectedEvent();
		}
	}

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

}
