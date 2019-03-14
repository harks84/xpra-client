package xpra.swing;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
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

import xpra.protocol.packets.Disconnect;

public class XpraTray {
	private SwingXpraClient client;
	URL url = System.class.getResource("/images/xpra.png");
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    final TrayIcon trayIcon = new TrayIcon(img);
    final SystemTray tray = SystemTray.getSystemTray();
	
	public XpraTray(SwingXpraClient client) {
		this.client = client;
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();

        MenuItem exitItem = new MenuItem("Exit");
        
        exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Disconnect disconnect = new Disconnect();
				disconnect.reason = "Requested by user";
				if(client != null && client.getSender() !=null) {
					client.getSender().send(disconnect);
				}
				// wait a little to capture server shutdown
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// force close as we created the disconnect so don't need server response
				System.exit(0);
			}
        	
        });
       
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        
       
       
        try {
            tray.add(trayIcon);
            
        } catch (AWTException e) {
            //TODO handle error
        }
	}
	
	public void notify(String title, String message) {
		trayIcon.displayMessage(title, message, MessageType.INFO);
	}
}
