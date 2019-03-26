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

import xpra.client.XpraClient;
import xpra.protocol.packets.HelloResponse;
import xpra.protocol.packets.Ping;
import xpra.protocol.packets.PingEcho;
import xpra.protocol.packets.SetDeflate;

public class PingHandler implements PacketHandler<Ping> {

	@Override
	public void process(Ping response) throws IOException {
		// TODO: load average:
		long l1 = 1;
		long l2 = 1;
		long l3 = 1;
		int serverLatency = -1;
		// if len(self.server_latency)>0:
		// sl = self.server_latency[-1]
		XpraClient.getSender().send(new PingEcho(response, l1, l2, l3, serverLatency));
	}
}
