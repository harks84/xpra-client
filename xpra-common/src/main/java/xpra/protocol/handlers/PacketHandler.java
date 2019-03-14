package xpra.protocol.handlers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xpra.client.XpraClient;
import xpra.protocol.Packet;

public interface PacketHandler<T extends Packet> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

	void process(T packet) throws IOException;
}