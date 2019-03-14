package xpra.protocol.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import xpra.client.XpraClient;
import xpra.protocol.packets.HelloResponse;
import xpra.protocol.packets.SetDeflate;

public class HelloHandler implements PacketHandler<HelloResponse> {

		private final SetDeflate setDeflate = new SetDeflate(3);
		
		@Override
		public void process(HelloResponse response) throws IOException {
			LOGGER.debug(response.toString());
			XpraClient.getSender().useRencode(response.isRencode());
			XpraClient.getSender().send(setDeflate);
			
			//TODO load system tray with
			HashMap<String, Object> menu = (HashMap<String, Object>)response.getCaps().get("xdg-menu");
//			for(Map.Entry<String, Object> entry: menu.entrySet()) {
//				entry.getValue();
//			}
			//		capabilities.get()
		}
	}