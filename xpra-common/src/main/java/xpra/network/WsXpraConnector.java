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

package xpra.network;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import xpra.client.XpraClient;
import xpra.network.websocket.WebSocketInputStream;
import xpra.network.websocket.WebSocketOutputStream;
import xpra.protocol.packets.Disconnect;

public class WsXpraConnector extends XpraConnector implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(WsXpraConnector.class);

	private final boolean wss;

	private Thread thread;

	private WebSocketOutputStream outputStream;
	private WebSocketInputStream inputStream;

	public WsXpraConnector(XpraClient client, String user, String hostname, int port, boolean wss) {
		super(client);
		this.user = user;
		this.host = hostname;
		this.port = port;
		this.wss = wss;

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
		WebSocket ws = null;
		try {
			WebSocketFactory wsf = new com.neovisionaries.ws.client.WebSocketFactory();

			String connString = "ws";
			if (wss) {
				connString = "wss";
			}
			connString += "://" + host + ":" + port;

			ws = wsf.createSocket(connString, 5000);
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
			ws.disconnect();
			client.onDisconnect();
			fireOnDisconnectedEvent();
		}
	}

	@Override
	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

}
