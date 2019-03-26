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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xpra.client.XpraClient;
import xpra.protocol.Packet;

public interface PacketHandler<T extends Packet> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

	void process(T packet) throws IOException;
}
