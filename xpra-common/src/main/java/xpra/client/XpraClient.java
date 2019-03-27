/*
 * Copyright (C) 2019 Mark Harkin, 2017 Jakub Ksiezniak
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package xpra.client;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xpra.network.XpraConnector;
import xpra.protocol.PictureEncoding;
import xpra.protocol.XpraReceiver;
import xpra.protocol.XpraSender;
import xpra.protocol.handlers.HelloHandler;
import xpra.protocol.handlers.PacketHandler;
import xpra.protocol.handlers.PingHandler;
import xpra.protocol.packets.ChallengePacket;
import xpra.protocol.packets.ClipboardToken;
import xpra.protocol.packets.ConfigureWindowOverrideRedirect;
import xpra.protocol.packets.CursorPacket;
import xpra.protocol.packets.DesktopSize;
import xpra.protocol.packets.Disconnect;
import xpra.protocol.packets.DrawPacket;
import xpra.protocol.packets.HelloRequest;
import xpra.protocol.packets.HelloResponse;
import xpra.protocol.packets.LostWindow;
import xpra.protocol.packets.NewWindow;
import xpra.protocol.packets.NewWindowOverrideRedirect;
import xpra.protocol.packets.Notify;
import xpra.protocol.packets.OpenUrl;
import xpra.protocol.packets.Ping;
import xpra.protocol.packets.RaiseWindow;
import xpra.protocol.packets.SendFile;
import xpra.protocol.packets.SetDeflate;
import xpra.protocol.packets.StartupComplete;
import xpra.protocol.packets.WindowIcon;
import xpra.protocol.packets.WindowMetadata;
import xpra.util.ChallengeUtil;
import xpra.util.ConnectorUtil;

public abstract class XpraClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(XpraClient.class);
	private final Map<Integer, XpraWindow> windows = new HashMap<>();

	private final PictureEncoding[] pictureEncodings;
	private final XpraKeyboard keyboard;

	private static XpraSender sender;
	private static XpraReceiver receiver;

	/* Configuration options. */
	private PictureEncoding encoding;
	private int desktopWidth;
	private int desktopHeight;
	private int dpi = 96;
	private int xdpi;
	private int ydpi;

	private String user;
	private String password;

	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * It is set to true, when a disconnect packet is sent from a Server.
	 */
	private boolean disconnectedByServer;

	public XpraClient(int desktopWidth, int desktopHeight, PictureEncoding[] supportedPictureEncodings) {
		this(desktopWidth, desktopHeight, supportedPictureEncodings, null);
	}

	public XpraClient(int desktopWidth, int desktopHeight, PictureEncoding[] supportedPictureEncodings,
			XpraKeyboard keyboard) {
		this.desktopWidth = desktopWidth;
		this.desktopHeight = desktopHeight;
		this.pictureEncodings = supportedPictureEncodings;
		this.encoding = pictureEncodings[0];
		this.keyboard = keyboard;
		this.receiver = new XpraReceiver();

		clipboard.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				String clipboardString;
				try {
					clipboardString = (String) clipboard.getData(DataFlavor.stringFlavor);
					sender.send(new ClipboardToken(clipboardString));
				} catch (UnsupportedFlavorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		// setup packet handlers
		receiver.registerHandler(HelloResponse.class, new HelloHandler());
		receiver.registerHandler(Ping.class, new PingHandler());
		receiver.registerHandler(ChallengePacket.class, new PacketHandler<ChallengePacket>() {
			@Override
			public void process(ChallengePacket challenge) throws IOException {
				LOGGER.debug("Sending hello with challenge response");
				final HelloRequest hello = new HelloRequest(desktopWidth, desktopHeight, keyboard, encoding,
						pictureEncodings);
				hello.setDpi(dpi, xdpi, ydpi);

				int saltLength = challenge.serverSalt.length();
				String clientSalt = ChallengeUtil.getClientSalt(32);
				String challengeSalt = ChallengeUtil.generateDigest(challenge.saltDigest, clientSalt,
						challenge.serverSalt);
				String challengeResponse = ChallengeUtil.generateDigest(challenge.digest, password, challengeSalt);
				hello.setChallengeResponse(challengeResponse, clientSalt);
				sender.send(hello);
			}
		});

		receiver.registerHandler(ClipboardToken.class, new PacketHandler<ClipboardToken>() {
			@Override
			public void process(ClipboardToken response) throws IOException {
				LOGGER.info("Processing... " + response);
				if (response.data != null) {
					clipboard.setContents(new StringSelection(new String(response.data)), null);
				}

			}
		});

		receiver.registerHandler(Disconnect.class, new PacketHandler<Disconnect>() {
			@Override
			public void process(Disconnect response) throws IOException {
				LOGGER.debug("Server disconnected with msg: " + response.reason);
				disconnectedByServer = true;
				// TODO for now exit
				System.exit(0);
			}
		});
		receiver.registerHandler(NewWindow.class, new PacketHandler<NewWindow>() {
			@Override
			public void process(NewWindow response) throws IOException {
				LOGGER.info("Processing... " + response);
				final XpraWindow window = onCreateWindow(response);
				window.setSender(sender);
				windows.put(window.getId(), window);
				window.onStart(response);
				onWindowStarted(window);
			}
		});
		receiver.registerHandler(NewWindowOverrideRedirect.class, new PacketHandler<NewWindowOverrideRedirect>() {
			@Override
			public void process(NewWindowOverrideRedirect response) throws IOException {
				LOGGER.info("Processing... " + response);
				final XpraWindow window = onCreateWindow(response);
				window.setSender(sender);
				windows.put(window.getId(), window);
				window.onStart(response);
				onWindowStarted(window);
			}
		});

		receiver.registerHandler(Notify.class, new PacketHandler<Notify>() {
			@Override
			public void process(Notify response) throws IOException {
				LOGGER.info("Processing... " + response);
				XpraClient.this.notify(response.title, response.message);

			}

		});

		receiver.registerHandler(SendFile.class, new PacketHandler<SendFile>() {
			@Override
			public void process(SendFile response) throws IOException {
				LOGGER.info("Processing... " + response);
				// TODO cross-platform downloads folder
				// TODO rename if file exists.
				String home = System.getProperty("user.home");
				File file = new File(home + File.separator + "Downloads" + File.separator + response.name);
				try (FileOutputStream stream = new FileOutputStream(file)) {
					stream.write(response.data);
					stream.close();
					XpraClient.this.notify("Xpra File Download", "File saved to:\n" + file.getAbsolutePath());
				}

			}

		});

		receiver.registerHandler(SetDeflate.class, new PacketHandler<SetDeflate>() {
			@Override
			public void process(SetDeflate response) throws IOException {
				sender.setCompressionLevel(response.compressionLevel);
			}
		});
		receiver.registerHandler(DrawPacket.class, new PacketHandler<DrawPacket>() {
			@Override
			public void process(DrawPacket packet) throws IOException {
				final XpraWindow xpraWindow = windows.get(packet.getWindowId());
				if (xpraWindow != null) {
					xpraWindow.draw(packet);
				} else {
					LOGGER.error("Missing window when handling: " + packet);
					// XpraWindow.sendDamageSequence(sender, packet, 0);
				}
			}
		});
		receiver.registerHandler(WindowMetadata.class, new PacketHandler<WindowMetadata>() {
			@Override
			public void process(WindowMetadata meta) throws IOException {
				windows.get(meta.getWindowId()).onMetadataUpdate(meta);
			}
		});
		receiver.registerHandler(LostWindow.class, new PacketHandler<LostWindow>() {
			@Override
			public void process(LostWindow response) throws IOException {
				final XpraWindow window = windows.remove(response.getWindowId());
				if (window != null) {
					window.onStop();
					onDestroyWindow(window);
				}
			}
		});
		receiver.registerHandler(CursorPacket.class, this::onCursorUpdate);
		receiver.registerHandler(WindowIcon.class, new PacketHandler<WindowIcon>() {
			@Override
			public void process(WindowIcon response) throws IOException {
				XpraWindow window = getWindow(response.getWindowId());
				if (window != null) {
					window.onIconUpdate(response);
				}
			}
		});
		receiver.registerHandler(ConfigureWindowOverrideRedirect.class,
				new PacketHandler<ConfigureWindowOverrideRedirect>() {
					@Override
					public void process(ConfigureWindowOverrideRedirect response) throws IOException {
						XpraWindow window = windows.get(response.getWindowId());
						if (window != null) {
							window.onMoveResize(response);
						}
					}
				});
		receiver.registerHandler(RaiseWindow.class, new PacketHandler<RaiseWindow>() {
			@Override
			public void process(RaiseWindow response) throws IOException {
				LOGGER.info("raise-window: " + response.getWindowId());
			}
		});
		receiver.registerHandler(StartupComplete.class, new PacketHandler<StartupComplete>() {
			@Override
			public void process(StartupComplete response) throws IOException {
				LOGGER.info(response.toString());
			}
		});

		receiver.registerHandler(OpenUrl.class, new PacketHandler<OpenUrl>() {
			@Override
			public void process(OpenUrl response) throws IOException {
				LOGGER.info(response.toString());
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					try {
						Desktop.getDesktop().browse(new URI(response.url));
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * The DPI should be set before connecting to Server.
	 * 
	 * @param dpi
	 * @param xdpi
	 * @param ydpi
	 */
	protected void setDpi(int dpi, int xdpi, int ydpi) {
		this.dpi = dpi;
		this.xdpi = xdpi;
		this.ydpi = ydpi;
	}

	/**
	 * Called when a new window is created.
	 * 
	 * @param wndPacket - A new window packet.
	 * @return
	 */
	protected abstract XpraWindow onCreateWindow(NewWindow wndPacket);

	/**
	 * Called when a window is destroyed.
	 * 
	 * @param window
	 */
	protected void onDestroyWindow(XpraWindow window) {
	}

	protected void onWindowStarted(XpraWindow window) {
	}

	protected void onCursorUpdate(CursorPacket cursorPacket) {
		LOGGER.info(cursorPacket.toString());
	}

	public void onConnect(XpraSender sender) {
		this.sender = sender;
		final HelloRequest hello = new HelloRequest(desktopWidth, desktopHeight, keyboard, encoding, pictureEncodings);
		hello.setDpi(dpi, xdpi, ydpi);
		hello.setUser(user);
		sender.send(hello);
	}

	public void onDisconnect() {
		for (XpraWindow w : windows.values()) {
			w.onStop();
		}
		windows.clear();
		disconnectedByServer = false;
		sender = null;
	}

	public void onConnectionError(IOException e) {
		LOGGER.error("connection error", e);
	}

	public static XpraSender getSender() {
		return sender;
	}

	public XpraWindow getWindow(int windowId) {
		return windows.get(windowId);
	}

	public Map<Integer, XpraWindow> getWindows() {
		return windows;
	}

	public void setDesktopSize(int width, int height) {
		this.desktopWidth = width;
		this.desktopHeight = height;
		if (sender != null) {
			sender.send(new DesktopSize(width, height));
		}
	}

	public boolean isDisconnectedByServer() {
		return disconnectedByServer;
	}

	public void setPictureEncoding(PictureEncoding pictureEncoding) {
		this.encoding = pictureEncoding;
	}

	public void onPacketReceived(List<Object> list) throws IOException {
		receiver.onReceive(list);
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public abstract void notify(String title, String message);

	public void connect(String connString) {
		XpraConnector connector = ConnectorUtil.getConnector(this, connString);
		connector.connect();

		while (connector.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		;

	}

	public void setStartMenu(HashMap<String, Object> menu) {
		// TODO Auto-generated method stub

	}

}
