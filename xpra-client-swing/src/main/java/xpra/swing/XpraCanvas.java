package xpra.swing;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import xpra.client.XpraWindow;
import xpra.protocol.PictureEncoding;
import xpra.protocol.packets.DrawPacket;
import xpra.swing.keyboard.KeyMap;

/**
 * @author Jakub Księżniak
 *
 */
public class XpraCanvas extends Canvas implements HierarchyListener, MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private final XpraWindow xwnd;

	private Window window;

	public XpraCanvas(XpraWindow window) {
		this.xwnd = window;
		addHierarchyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setBackground(new Color(0, 0, 0, 0));
	}

	public void setCustomRoot(Window wnd) {
		window = wnd;
	}

	@Override
	public void hierarchyChanged(HierarchyEvent e) {
		if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && isDisplayable()) {
			createBufferStrategy(2);
			requestFocus();
		}
	}

	public void draw(DrawPacket packet) {
		try {
			
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(packet.data));

			Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
			
			//g.setPaintMode();
			//g.setComposite(AlphaComposite.Src);
			g.drawImage(img, packet.x, packet.y, this);
			g.dispose();
			getBufferStrategy().show();
		} catch (IOException e) {
			throw new RuntimeException("Failed decoding image: " + packet.encoding, e);
		}
	}

	private Point getTruePos(int x, int y) {
		JRootPane root = SwingUtilities.getRootPane(window != null ? window : this);
		// System.err.println("root insets: " + root.getLocation());

		// TODO popup fix for insets on parent frame
		// popup not correctly getting parent frame ... should then use parent insets
		if (this.xwnd instanceof SwingPopup) {
			if("Windows 10".equals(System.getProperty("os.name"))){
				return new Point(x - 8, y - 31);
			}
		}

		return new Point(x - root.getX(), y - root.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.mouseAction(e.getButton(), true, p.x, p.y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.movePointer(p.x, p.y);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// not used
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.mouseAction(e.getButton(), true, p.x, p.y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.mouseAction(e.getButton(), false, p.x, p.y);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.movePointer(p.x, p.y);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Point p = getTruePos(e.getXOnScreen(), e.getYOnScreen());
		xwnd.movePointer(p.x, p.y);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// not used
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyEvent(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyEvent(e, false);
	}
	
	private void keyEvent(KeyEvent e, boolean pressed) {
		String keyName = KeyMap.getName(e.getKeyCode());
		List<String> modifiers = KeyMap.getModifiers(e.getModifiers());
		if(keyName == null) {
			//TODO log key not found in keymap
			return;
		}
		xwnd.keyboardAction(e.getKeyCode(), keyName , pressed, modifiers);
	}

}
