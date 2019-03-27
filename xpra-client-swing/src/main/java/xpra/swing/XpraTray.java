/*
 * Copyright (C) 2019 Mark Harkin
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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import xpra.protocol.packets.Disconnect;
import xpra.protocol.packets.StartCommand;
import xpra.util.ImageUtil;

public class XpraTray {
	private SwingXpraClient client;
	URL url = System.class.getResource("/images/xpra.png");
	Image img = Toolkit.getDefaultToolkit().getImage(url);
	final TrayIcon trayIcon = new TrayIcon(img);
	final SystemTray tray = SystemTray.getSystemTray();
	final JPopupMenu mainMenu = new JPopupMenu();

	// https://stackoverflow.com/questions/19868209/cannot-hide-systemtray-jpopupmenu-when-it-loses-focus
	final protected JDialog hiddenDialog = new JDialog();

	public XpraTray(SwingXpraClient client) {
		this.client = client;
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		hiddenDialog.setSize(10, 10);

		hiddenDialog.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent we) {
				hiddenDialog.setVisible(false);
			}

			@Override
			public void windowGainedFocus(WindowEvent we) {
			}
		});

		JMenuItem disconnectItem = new JMenuItem("Disconnect");

		disconnectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Disconnect disconnect = new Disconnect();
				disconnect.reason = "Requested by user";
				if (client != null && client.getSender() != null) {
					client.getSender().send(disconnect);
					// wait a little to capture server shutdown
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				// force close as we created the disconnect so don't need server response
				System.exit(0);
			}

		});

		mainMenu.add(disconnectItem);

		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {

				hiddenDialog.setLocation(e.getX(), e.getY());
				hiddenDialog.setVisible(true);
				mainMenu.setLocation(e.getX(), e.getY());
				mainMenu.setInvoker(hiddenDialog);
				mainMenu.setVisible(true);

			}
		});

		try {
			tray.add(trayIcon);

		} catch (AWTException e) {
			// TODO handle error
		}
	}

	public void notify(String title, String message) {
		trayIcon.displayMessage(title, message, MessageType.INFO);
	}

	public void setStartMenu(HashMap<String, Object> menu) {
		// TODO Auto-generated method stub
		JMenu startMenu = new JMenu("Start");

		// MenuItem commandItem = new MenuItem("Command");
		for (String category : menu.keySet()) {
			JMenu categoryMenu = new JMenu(category);
			HashMap<String, Object> categoryMap = (HashMap<String, Object>) menu.get(category);
			HashMap<String, Object> entryMap = (HashMap<String, Object>) categoryMap.get("Entries");

			for (String entryName : entryMap.keySet()) {
				HashMap<String, Object> entry = (HashMap<String, Object>) entryMap.get(entryName);
				String name = new String((byte[]) entry.get("Name"));
				String command = new String((byte[]) entry.get("command")).replaceAll("%[uUfF]", "");

				JMenuItem commandItem = new JMenuItem(name);
				try {
					BufferedImage image = ImageIO.read(new ByteArrayInputStream((byte[]) entry.get("IconData")));
					ImageIcon imageIcon = new ImageIcon(ImageUtil.resize(image, 16, 16));

					commandItem.setIcon(imageIcon);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				commandItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						StartCommand startCommand = new StartCommand(name, command, false);

						client.getSender().send(startCommand);

					}

				});
				categoryMenu.add(commandItem);
			}
			startMenu.add(categoryMenu);

		}

		mainMenu.insert(startMenu, 0);

	}
}
