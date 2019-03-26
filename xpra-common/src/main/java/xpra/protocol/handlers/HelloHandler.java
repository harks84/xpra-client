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
package xpra.protocol.handlers;

import java.io.IOException;
import java.util.HashMap;

import xpra.Launcher;
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

		// TODO load system tray with
		HashMap<String, Object> menu = (HashMap<String, Object>) response.getCaps().get("xdg-menu");
		Launcher.getClient().setStartMenu(menu);
//			for(Map.Entry<String, Object> entry: menu.entrySet()) {
//				entry.getValue();
//			}
		// capabilities.get()
	}
}
