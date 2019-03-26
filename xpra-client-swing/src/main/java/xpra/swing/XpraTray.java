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
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;

import xpra.protocol.packets.Disconnect;
import xpra.protocol.packets.StartCommand;

public class XpraTray {
	private SwingXpraClient client;
	URL url = System.class.getResource("/images/xpra.png");
	Image img = Toolkit.getDefaultToolkit().getImage(url);
	final TrayIcon trayIcon = new TrayIcon(img);
	final SystemTray tray = SystemTray.getSystemTray();
	final PopupMenu mainMenu = new PopupMenu();

	public XpraTray(SwingXpraClient client) {
		this.client = client;
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		MenuItem disconnectItem = new MenuItem("Disconnect");

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

		trayIcon.setPopupMenu(mainMenu);

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
		Menu startMenu = new Menu("Start");

		MenuItem commandItem = new MenuItem("Command");

		commandItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				StartCommand startCommand = new StartCommand("Firefox", "firefox", false);

				client.getSender().send(startCommand);

			}

		});
		startMenu.add(commandItem);
		mainMenu.add(startMenu);
	}
}
