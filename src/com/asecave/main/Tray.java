package com.asecave.main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tray implements MouseListener {

	private JanaMirror jana;
	private TrayIcon icon;
	private Image[] images = new Image[12];
	private Image white;
	private boolean spin = false;

	public Tray(JanaMirror jana) {
		this.jana = jana;

		PopupMenu menu = new PopupMenu();
		try {
			for (int i = 0; i < 12; i++) {
				images[i] = ImageIO.read(Tray.class.getResourceAsStream("icon-16p" + i + ".png"));
			}
			white = ImageIO.read(Tray.class.getResourceAsStream("icon-16w.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		icon = new TrayIcon(white);

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jana.exit();
			}
		});
		menu.add(exitItem);

		icon.setPopupMenu(menu);
		icon.setImageAutoSize(true);
		icon.addMouseListener(this);
		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			jana.trayClicked();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void remove() {
		SystemTray.getSystemTray().remove(icon);
		spin = false;
	}

	public void setIcon(int n) {
		if (n == 0) {
			spin = false;
			icon.setImage(white);
		} else {
			spin = true;
			new Thread(() -> {
				int i = 0;
				while (spin) {
					icon.setImage(images[i]);
					i++;
					if (i == 12) {
						i = 0;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, "Icon spin").start();
		}
	}
}
