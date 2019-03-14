package xpra.protocol.packets;

import java.util.Iterator;

public class ReceiveFile extends xpra.protocol.Packet {

	public String name;
	public String mimeType;
	public boolean printIt;
	public boolean unknown;
	public int dataSize;
	public byte[] data;
	
	@Override
	public void deserialize(Iterator<Object> iter) {
		super.deserialize(iter);

		name = asString(iter.next());
		//TODO assign these
		mimeType = asString(iter.next());
		printIt = asBoolean(iter.next());
		unknown = asBoolean(iter.next());
		dataSize = asInt(iter.next());
		data = asByteArray(iter.next());



	}
		
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		return  builder.toString();
	}
}