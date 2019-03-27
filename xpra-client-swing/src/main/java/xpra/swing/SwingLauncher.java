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

import xpra.Launcher;

public class SwingLauncher extends Launcher {

	public static void main(String[] args) {

		String connString = "";
		if (args.length > 0) {
			connString = args[args.length - 1];
		} else {
			// TODO launch gui
			connString = "tcp:user@localhost:10000";
		}

		client = new SwingXpraClient();

		client.connect(connString);

	}

}
