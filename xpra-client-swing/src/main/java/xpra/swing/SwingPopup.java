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

package xpra.swing;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.Popup;
import javax.swing.PopupFactory;

import xpra.client.XpraWindow;
import xpra.protocol.packets.DrawPacket;
import xpra.protocol.packets.NewWindow;

public class SwingPopup extends XpraWindow {

	private final SwingWindow<?> owner;

	private XpraCanvas canvas;
	private Popup popup;

	public SwingPopup(NewWindow wnd, SwingWindow<?> rootWnd) {
		super(wnd);
		this.owner = rootWnd;
	}

	@Override
	protected void onStart(NewWindow wnd) {
		Window window = null;
		// TODO popup fix for insets on parent frame
		int offsetX = 0;
		int offsetY = 0;
		if ("Windows 10".equals(System.getProperty("os.name"))) {
			offsetX = 8;
			offsetY = 31;
		}

		if (owner != null) {
			window = owner.window;
			offsetX = owner.offsetX;
			offsetY = owner.offsetY;
		}

		canvas = new XpraCanvas(this);
		canvas.setCustomRoot(window);
		canvas.setPreferredSize(new Dimension(wnd.getWidth(), wnd.getHeight()));
		popup = PopupFactory.getSharedInstance().getPopup(window, canvas, wnd.getX() + offsetX, wnd.getY() + offsetY);
		popup.show();
	}

	@Override
	protected void onStop() {
		popup.hide();
		popup = null;
	}

	@Override
	public void draw(DrawPacket packet) {
		canvas.draw(packet);
		sendDamageSequence(packet, 0);
	}

	public SwingWindow<?> getOwner() {
		return owner;
	}

}
