package xpra.protocol.packets;

import java.util.Iterator;

public class Notify extends xpra.protocol.Packet {

	
	public int id;
	public String channel;
	public String title;
	public String message;
	public String timeout;

	
	@Override
	public void deserialize(Iterator<Object> iter) {
		super.deserialize(iter);
		iter.next();
		id = asInt(iter.next());
		channel = asString(iter.next());
		iter.next();
		iter.next();
		title = asString(iter.next());
		message = asString(iter.next());


	}
		
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		return  builder.toString();
	}
}
