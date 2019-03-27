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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import xpra.client.XpraClient;
import xpra.client.XpraWindow;
import xpra.protocol.PictureEncoding;
import xpra.protocol.packets.CursorPacket;
import xpra.protocol.packets.NewWindow;
import xpra.swing.keyboard.SimpleXpraKeyboard;

public class SwingXpraClient extends XpraClient {

	private static XpraTray tray;

	private static final PictureEncoding[] PICTURE_ENCODINGS = { PictureEncoding.rgb32, PictureEncoding.png,
			PictureEncoding.pngP, PictureEncoding.pngL, PictureEncoding.jpeg };

	private static int getDesktopWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	private static int getDesktopHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	public SwingXpraClient() {
		super(getDesktopWidth(), getDesktopHeight(), PICTURE_ENCODINGS, new SimpleXpraKeyboard());
		tray = new XpraTray(this);
//		System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment()
//				.getDefaultScreenDevice()
//				.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT));
//		System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment()
//				.getDefaultScreenDevice()
//				.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT));
//		System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment()
//				.getDefaultScreenDevice()
//				.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT));
	}

	@Override
	protected XpraWindow onCreateWindow(NewWindow wnd) {
		if (wnd.isOverrideRedirect()) {
			final XpraWindow owner = getWindow(wnd.getMetadata().getParentId());
			if (owner instanceof SwingPopup) {
				return new SwingPopup(wnd, ((SwingPopup) owner).getOwner());
			} else {
				return new SwingPopup(wnd, (SwingWindow<?>) owner);
			}
		} else {
			return new SwingFrame(wnd);
		}
	}

	@Override
	protected void onCursorUpdate(CursorPacket cursorPacket) {
		super.onCursorUpdate(cursorPacket);
		if (cursorPacket.isEmpty()) {
			return;
		}
		int width = cursorPacket.width;
		int height = cursorPacket.height;

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		int[] nBits = { 8, 8, 8, 8 };
		int[] bOffs = { 1, 2, 3, 0 };
		ColorModel colorModel = new ComponentColorModel(cs, nBits, true, true, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, width * 4, 4, bOffs,
				null);

		BufferedImage img = new BufferedImage(colorModel, raster, true, null);
		img.getRaster().setDataElements(0, 0, width, height, cursorPacket.pixels);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension size = toolkit.getBestCursorSize(width, height);

		// TODO windows 32x32 best suits padding
		Point p = new Point((size.width - width) / 2, (size.height - height) / 2);
		BufferedImage newImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImg.createGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, size.width, size.height);
		g.drawImage(img, p.x, p.y, p.x + width, p.y + height, 0, 0, width, height, new Color(0, 0, 0, 0), null);
		g.dispose();
		img = newImg;
		// END TODO

		Point hotspot = new Point(cursorPacket.xHotspot, cursorPacket.yHotspot);
		Image outputImg = img;
		if (size.width != width || size.height != height) {
			outputImg = img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
			hotspot.x = hotspot.x * size.width / width;
			hotspot.y = hotspot.y * size.height / height;
		}

		if (hotspot.x == size.width) {
			hotspot.x -= 1;
		}

		if (hotspot.y == size.height) {
			hotspot.y -= 1;
		}

		Cursor c = toolkit.createCustomCursor(outputImg, hotspot, "xpra-cursor");
		for (XpraWindow window : getWindows().values()) {
			if (window instanceof SwingFrame) {
				((SwingFrame) window).window.setCursor(c);
			}
		}

	}

	public static XpraTray getTray() {
		return tray;
	}

	@Override
	public void notify(String title, String message) {
		getTray().notify(title, message);

	}

	@Override
	public void setStartMenu(HashMap<String, Object> menu) {
		getTray().setStartMenu(menu);

	}

	@Override
	public String passwordPrompt() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter a password:");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(null, panel, "The title", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		if (option == JOptionPane.OK_OPTION || option == JOptionPane.NO_OPTION) // pressing OK button
		{
			char[] password = pass.getPassword();
			return new String(password);
		}
		return null;
	}
}
