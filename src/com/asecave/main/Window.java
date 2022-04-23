package com.asecave.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JPanel implements FocusListener, MouseListener {

	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JanaMirror jana;

	private float progressBarValue = 0f;
	private float progressBar2Value = 0f;
	private float smoothProgressBarValue = 0f;
	private float smoothProgressBar2Value = 0f;
	private float opacity = 0f;
	private String sourceDir = "";
	private String targetDir = "";
	private String speed = "";
	private boolean overSourceButton;
	private boolean overTargetButton;
	private boolean overChangeSourceButton;
	private boolean overChangeTargetButton;
	private boolean suspendClose;
	private boolean overSyncButton;
	private boolean overSpeedButton;

	public Window(JanaMirror jana) {

		this.jana = jana;

		frame = new JFrame();

		frame.addFocusListener(this);
		frame.addMouseListener(this);

		frame.setUndecorated(true);
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);

		frame.add(this);

		frame.setVisible(true);

		new Thread(() -> {
			while (true) {
				if (frame.isVisible()) {
					Window.this.repaint();
				}
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {

		if (!suspendClose) {
			System.exit(0);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {

		// Interpolation calculations
		int div = 20;
		if (progressBarValue == 1f) {
			div = 4;
		}
		smoothProgressBarValue += (progressBarValue - smoothProgressBarValue) / div;
		div = 20;
		if (progressBar2Value == 1f) {
			div = 4;
		}
		smoothProgressBar2Value += (progressBar2Value - smoothProgressBar2Value) / div;
		
		if ((smoothProgressBarValue != 0f || smoothProgressBar2Value != 0f) && progressBarValue != 1f) {
			opacity += (1f - opacity) / 10;
		} else {
			opacity -=  opacity / 20;
		}
		
		// Mouse
		Point mouse = frame.getMousePosition();

		// Graphics
		Graphics2D g2d = (Graphics2D) g;

		// Font
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		// Background
		Color bg = new Color(30, 30, 30);
		g2d.setColor(bg);
		g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());

		// Progressbar
		g2d.setColor(new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), (int) (255 * opacity)));
		g2d.fillRect(0, 1, (int) (smoothProgressBarValue * frame.getWidth()), 3);
		g2d.fillRect(0, 4, (int) (smoothProgressBar2Value * frame.getWidth()), 3);

		// Open directories buttons
		if (mouse != null && mouse.x > 75 && mouse.x < 275 && mouse.y > 75 && mouse.y < 175) {
			g2d.setColor(new Color(80, 80, 80));
			overSourceButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overSourceButton = false;
		}
		g2d.fillRect(75, 75, 200, 100);
		if (mouse != null && mouse.x > 325 && mouse.x < 525 && mouse.y > 75 && mouse.y < 175) {
			g2d.setColor(new Color(80, 80, 80));
			overTargetButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overTargetButton = false;
		}
		g2d.fillRect(325, 75, 200, 100);
		g2d.setColor(Color.MAGENTA);
		g2d.drawRect(75, 75, 200, 100);
		g2d.drawRect(325, 75, 200, 100);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawString("Open source directory", 175 - g2d.getFontMetrics().stringWidth("Open source directory") / 2,
				110);
		g2d.drawString("Open target directory", 425 - g2d.getFontMetrics().stringWidth("Open target directory") / 2,
				110);
		g2d.setColor(Color.MAGENTA);
		g2d.drawString(sourceDir, 175 - g2d.getFontMetrics().stringWidth(sourceDir) / 2, 150);
		g2d.drawString(targetDir, 425 - g2d.getFontMetrics().stringWidth(targetDir) / 2, 150);

		// Change directories buttons
		if (mouse != null && mouse.x > 75 && mouse.x < 275 && mouse.y > 200 && mouse.y < 250) {
			g2d.setColor(new Color(80, 80, 80));
			overChangeSourceButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overChangeSourceButton = false;
		}
		g2d.fillRect(75, 200, 200, 50);
		if (mouse != null && mouse.x > 325 && mouse.x < 525 && mouse.y > 200 && mouse.y < 250) {
			g2d.setColor(new Color(80, 80, 80));
			overChangeTargetButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overChangeTargetButton = false;
		}
		g2d.fillRect(325, 200, 200, 50);
		g2d.setColor(Color.MAGENTA);
		g2d.drawRect(75, 200, 200, 50);
		g2d.drawRect(325, 200, 200, 50);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawString("Change source directory", 175 - g2d.getFontMetrics().stringWidth("Change source directory") / 2,
				230);
		g2d.drawString("Change target directory", 425 - g2d.getFontMetrics().stringWidth("Change target directory") / 2,
				230);

		// Synchronize now button
		if (mouse != null && mouse.x > 75 && mouse.x < 525 && mouse.y > 275 && mouse.y < 325) {
			g2d.setColor(new Color(80, 80, 80));
			overSyncButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overSyncButton = false;
		}
		g2d.fillRect(75, 275, 450, 50);
		g2d.setColor(Color.MAGENTA);
		g2d.drawRect(75, 275, 450, 50);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawString("Synchronize now", 300 - g2d.getFontMetrics().stringWidth("Synchronize now") / 2, 305);

		// Sync speed button
		if (mouse != null && mouse.x > 75 && mouse.x < 525 && mouse.y > 350 && mouse.y < 400) {
			g2d.setColor(new Color(80, 80, 80));
			overSpeedButton = true;
		} else {
			g2d.setColor(new Color(40, 40, 40));
			overSpeedButton = false;
		}
		g2d.fillRect(75, 350, 450, 50);
		g2d.setColor(Color.MAGENTA);
		g2d.drawRect(75, 350, 450, 50);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawString(speed, 300 - g2d.getFontMetrics().stringWidth(speed) / 2, 380);
		
		// Border
		g2d.setColor(new Color(100, 100, 100));
		g2d.drawRect(0, 0, frame.getWidth() - 1, frame.getHeight() - 1);
	}

	public void setProgress(float value) {
		
		progressBarValue = value;
	}
	
	public void setProgress2(float value) {

		progressBar2Value = value;
	}

	public void setSourceDirButtonName(String name) {

		sourceDir = name;
	}

	public void setTargetDirButtonName(String name) {

		targetDir = name;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (overSourceButton) {
			jana.sourceButtonPressed();
		} else if (overTargetButton) {
			jana.targetButtonPressed();
		} else if (overChangeSourceButton) {
			jana.changeSourceButtonPressed();
		} else if (overChangeTargetButton) {
			jana.changeTargetButtonPressed();
		} else if (overSyncButton) {
			jana.syncButtonPressed();
		} else if (overSpeedButton) {
			jana.speedButtonPressed(e.getButton() == MouseEvent.BUTTON3);
		}
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

	public void setSuspendClose(boolean suspendClose) {

		this.suspendClose = suspendClose;
	}
	
	public void setSpeedButtonName(String speed) {
		this.speed = speed;
	}
}
