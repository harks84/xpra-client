/*
 * Copyright (C) 2017 Jakub Ksiezniak
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

package xpra.protocol.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import xpra.client.XpraKeyboard;
import xpra.client.XpraKeyboard.KeyDesc;
import xpra.protocol.PictureEncoding;
import xpra.protocol.ProtocolConstants;

public class HelloRequest extends xpra.protocol.IOPacket {

	private final Map<String, Object> caps = new LinkedHashMap<>();

	public HelloRequest(int screenWidth, int screenHeight, XpraKeyboard keyboard, PictureEncoding defaultEncoding,
			PictureEncoding[] encodings) {
		super("hello");

		caps.put("challenge", true);
		caps.put("share", false);
		caps.put("steal", true);

		caps.put("version", ProtocolConstants.VERSION);

		caps.put("notify-startup-complete", true);

		caps.put("websocket.multi-packet", true);
		// if (enc_pass != null) {
		// caps.put("challenge_response", enc_pass);
		// }
		caps.put("metadata.supported",
				new String[] { "fullscreen", "maximized", "above", "below", "group-leader", "title", "size-hints",
						"class-instance", "transient-for", "window-type", "has-alpha", "decorations",
						"override-redirect", "tray", "modal", "opacity", "desktop" });

		caps.put("server-window-resize", true);
		caps.put("window.raise", true);
		caps.put("window.initiate-moveresize", true);

		String[] digests = new String[] { "hmac", "hmac+md5", "hmac+sha256", "xor" };
		caps.put("digest", digests);
		caps.put("salt-digest", digests);
		final int[] screenDims = new int[] { screenWidth, screenHeight };
		// caps.put("auto_refresh_delay", 500);
		caps.put("desktop_size", screenDims);
		caps.put("dpi", 96);
//		caps.put("dpi.x", 0);
//		caps.put("dpi.y", 0);
		caps.put("client_type", "Java");
		caps.put("screen_sizes", new int[][] { screenDims });
		caps.put("encodings", PictureEncoding.toString(encodings));
		caps.put("raw_window_icons", true);
		caps.put("encodings.window-icon", new String[] { "png" });
		// caps.put("encoding.generic", true);
		// caps.put("encoding.core", PictureEncoding.toString(encodings));
		// caps.put("encoding.eos", true);
		caps.put("system_tray", true);
		caps.put("encodings.rgb_formats", new String[] {"RGBA",  "RGBX" });

		// caps.put("encodings.cursor", new String[] { "png" });
		caps.put("encoding.transparency", true);
		// caps.put("encoding.client_options", true);
		// caps.put("encoding.csc_atoms", true);
		// caps.put("encoding.scrolling", true);
		// caps.put("encoding.flush", true);
		caps.put("generic-rgb-encodings", true);
		caps.put("encoding.rgb_lz4", false);
		caps.put("encoding.rgb_zlib", true);
		caps.put("encoding.rgb24zlib", true);
		caps.put("lz4", false);
		caps.put("zlib", true);
		caps.put("clipboard", true);
		caps.put("clipboard.want_targets", true);
		caps.put("clipboard.greedy", true);
		caps.put("clipboard.selections", new String[] { "CLIPBOARD", "PRIMARY" });
		caps.put("notifications", true);
		caps.put("cursors", true);
		caps.put("named_cursors", true);
		caps.put("bell", true);
		caps.put("bencode", false);
		caps.put("rencode", true);
		caps.put("chunked_compression", true);

		caps.put("file-transfer", true);
		caps.put("file-size-limit", 10);

		caps.put("open-url", true);

		if (defaultEncoding != null) {
			caps.put("encoding", defaultEncoding.toString());
			if (PictureEncoding.jpeg.equals(defaultEncoding)) {
				caps.put("jpeg", 40);
			}
		}
		caps.put("platform", System.getProperty("os.name").toLowerCase());
		caps.put("uuid", UUID.randomUUID().toString().replace("-", ""));
		setKeyboard(keyboard);
	}

	public void setDpi(int dpi, int xdpi, int ydpi) {
		caps.put("dpi", dpi);
//		caps.put("dpi.x", xdpi);		
//		caps.put("dpi.y", ydpi);		
	}

	private void setKeyboard(XpraKeyboard keyboard) {
		if (keyboard != null) {
			caps.put("keyboard", true);
			caps.put("keyboard_sync", false);
			caps.put("xkbmap_layout", "gb");
			caps.put("xkbmap_keycodes", buildKeycodes(keyboard.getKeycodes()));
		} else {
			caps.put("keyboard", false);
			caps.put("keyboard_sync", false);
		}
	}

	private Object buildKeycodes(List<KeyDesc> keycodes) {
		List<Object> list = new ArrayList<>();
		for (KeyDesc kd : keycodes) {
			list.add(kd.toList());
		}
		return list;
	}
//	
//	private Object buildKeycodes2(List<KeyDesc> keycodes) {
//		Map<Object, Object> out = new HashMap<Object, Object>();
//		List<Object> list = new ArrayList<>();
//		list.add("a");
//		list.add("A");
//		out.put("10", list);
//		return out;
//	}

	@Override
	protected void serialize(Collection<Object> elems) {
		elems.add(caps);
	}

	public void setChallengeResponse(String challengeResponse, String challengeSalt) {
		// TODO Auto-generated method stub
		caps.put("challenge_response", challengeResponse);
		caps.put("challenge_client_salt", challengeSalt);

	}

	public void setUser(String user) {
		caps.put("username", user);
	}

}
