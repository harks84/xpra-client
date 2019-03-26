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
package xpra.util;

import xpra.client.XpraClient;
import xpra.network.SshXpraConnector;
import xpra.network.TcpXpraConnector;
import xpra.network.WsXpraConnector;
import xpra.network.XpraConnector;

public class ConnectorUtil {
	
	private static final String WS_PROTOCOL = "ws";
	private static final String WSS_PROTOCOL = "wss";
	private static final String TCP_PROTOCOL = "tcp";
	private static final String SSH_PROTOCOL = "ssh";
	
	public static XpraConnector getConnector(XpraClient client, String connString) {
		
		String[] splitConnString = connString.split(":");
		String protocol = splitConnString[0];
		String userAndHost = splitConnString[1];
		Integer port = null;
		if(splitConnString.length == 3) {
			port = Integer.parseInt(splitConnString[2]);
		}
		
		String user = "";
		String host = "";
		if(userAndHost.contains("@")) {
			
			String[] splitUserAndHost = userAndHost.split("@");
			user = splitUserAndHost[0];
			host = splitUserAndHost[splitUserAndHost.length-1];
		}else {
			host = userAndHost;
		}
		
		
		switch(protocol) {
			case TCP_PROTOCOL:
				if(port == null) {
					port=14500;
				}
				return new TcpXpraConnector(client, host, port);
			case WS_PROTOCOL:
				if(port == null) {
					port=80;
				}
				return new WsXpraConnector(client, host, port, false);
			case WSS_PROTOCOL:
				if(port == null) {
					port=443;
				}
				return new WsXpraConnector(client, host, port, true);
			case SSH_PROTOCOL:
				if(port == null) {
					port=22;
				}
				return new SshXpraConnector(client, host, port, user);
			
		}
		
		return null;
	}

}
