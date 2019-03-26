package xpra;

import xpra.client.XpraClient;

public abstract class Launcher {

	protected static XpraClient client;

	public static XpraClient getClient() {
		return client;
	}

}
