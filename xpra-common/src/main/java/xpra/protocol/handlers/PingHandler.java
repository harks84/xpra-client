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