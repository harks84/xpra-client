package xpra.network;

import xpra.client.XpraClient;

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
