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

import javax.swing.JFrame;

import xpra.protocol.packets.DrawPacket;
import xpra.protocol.packets.NewWindow;
import xpra.protocol.packets.WindowMetadata;

/**
 * @author Jakub Księżniak
 *
 */
public class SwingFrame extends SwingWindow<JFrame> {

	private final XpraCanvas canvas;

	private Integer origX;
	private Integer origY;
	private Integer origWidth;
	private Integer origHeight;

	public SwingFrame(NewWindow wnd) {
		super(wnd, new JFrame());
		canvas = new XpraCanvas(this);
	}

	@Override
	protected void onStart(NewWindow wnd) {
		super.onStart(wnd);
		window.setLocation(wnd.getX(), wnd.getY());
		window.getContentPane().setPreferredSize(new Dimension(wnd.getWidth(), wnd.getHeight()));
		window.getContentPane().add(canvas);
		window.pack();
		window.setVisible(true);

		offsetX = window.getRootPane().getX();
		offsetY = window.getRootPane().getY();
	}

	@Override
	protected void onStop() {
		window.setVisible(false);
		window.dispose();
	}

	@Override
	protected void onMetadataUpdate(WindowMetadata metadata) {
		super.onMetadataUpdate(metadata);
		final String title = metadata.getAsString("title");

		if (title != null) {
			window.setTitle(title);
		}

		if (!metadata.isNull("fullscreen")) {
			final Boolean fullscreen = metadata.getAsBoolean("fullscreen");
			if (fullscreen == true) {
				// TODO probably a better way of doing this
				// need dispose() for setUndecorated() and so loose window bounds
				// and have to store for unmaximize
				origX = window.getX();
				origY = window.getY();
				origWidth = window.getWidth();
				origHeight = window.getHeight();
				window.dispose();
				window.setUndecorated(true);
				window.setExtendedState(JFrame.MAXIMIZED_BOTH);
				window.setVisible(true);

			} else {
				window.dispose();
				window.setUndecorated(false);
				if (origX != null) {
					window.setBounds(origX, origY, origWidth, origHeight);
					origX = null;
					origY = null;
					origWidth = null;
					origHeight = null;
				}
				window.setExtendedState(JFrame.NORMAL);
				window.setVisible(true);
			}

		}
// TODO correct
//		if(!metadata.getAsBoolean("decorations") && !window.isDisplayable()) {
//			window.setUndecorated(true);
//			window.setBackground(new Color(0, 0, 0, 0));
//		}
	}

	@Override
	public void draw(DrawPacket packet) {
		canvas.draw(packet);
		sendDamageSequence(packet, 0);
	}

}
