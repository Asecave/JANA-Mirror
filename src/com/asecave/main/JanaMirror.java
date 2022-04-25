package com.asecave.main;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;

public class JanaMirror {

	private Window window;
	private Config config;
	private Robocopy robocopy;
	private Tray tray;

	private long lastSynced;
	private boolean autoSync = true;

	public static void main(String[] args) {

		new JanaMirror();
	}

	public JanaMirror() {

		window = new Window(this);

		config = new Config(this);

		window.setSourceDirButtonName(new File(config.get("sourceDir")).getName());
		window.setTargetDirButtonName(new File(config.get("targetDir")).getName());
		window.setSpeedButtonName(speedToString(Integer.parseInt(config.get("syncSpeed"))));

		robocopy = new Robocopy(this);
		
		tray = new Tray(this);

		lastSynced = System.currentTimeMillis();
		robocopy.startMirror(true);

		new Thread(() -> {
			while (autoSync) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (System.currentTimeMillis() - lastSynced >= getMinutesFromSpeed(Integer.parseInt(config.get("syncSpeed"))) * 60000) {
					lastSynced = System.currentTimeMillis();
					robocopy.startMirror(false);
				}
			}
		}, "Change checker").start();
	}

	public void sourceButtonPressed() {

		window.setSuspendClose(true);
		try {
			Desktop.getDesktop().open(new File(config.get("sourceDir")));
		} catch (IOException | IllegalArgumentException e) {
			log("Folder does not exist!");
			setStatus("Folder does not exist!", Color.RED);
		}
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.setSuspendClose(false);
		}, "Suspend Close").start();
	}

	public void targetButtonPressed() {

		window.setSuspendClose(true);
		try {
			Desktop.getDesktop().open(new File(config.get("targetDir")));
		} catch (IOException | IllegalArgumentException e) {
			log("Folder does not exist!");
			setStatus("Folder does not exist!", Color.RED);
		}
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.setSuspendClose(false);
		}, "Suspend Close").start();
	}

	public void changeSourceButtonPressed() {

		window.setSuspendClose(true);
		String file = chooseFile(config.get("sourceDir"));
		if (file != null) {
			config.set("sourceDir", file);
			window.setSourceDirButtonName(new File(config.get("sourceDir")).getName());
		}
	}

	public void changeTargetButtonPressed() {

		window.setSuspendClose(true);
		String file = chooseFile(config.get("targetDir"));
		if (file != null) {
			config.set("targetDir", file);
			window.setTargetDirButtonName(new File(config.get("targetDir")).getName());
		}
	}

	public void log(String s) {

		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
		System.out.println("[" + timeStamp + "] " + s);
	}

	private String chooseFile(String startDir) {

		JFileChooser fc = new JFileChooser(startDir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(window);
		window.setSuspendClose(false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().getPath();
		}
		return null;
	}

	public void syncButtonPressed() {

		robocopy.startMirror(true);
		lastSynced = System.currentTimeMillis();
	}

	public Config getConfig() {

		return config;
	}

	public void speedButtonPressed(boolean rightClick) {
		int speed = Integer.parseInt(config.get("syncSpeed"));
		if (rightClick) {
			speed--;
		} else {
			speed++;
		}
		if (speed == 8) {
			speed = 0;
		}
		if (speed == -1) {
			speed = 7;
		}
		window.setSpeedButtonName(speedToString(speed));
		config.set("syncSpeed", "" + speed);
	}

	private String speedToString(int speed) {
		return "Sync speed: " + getMinutesFromSpeed(speed) + " minutes";
	}

	private int getMinutesFromSpeed(int speed) {
		switch (speed) {
		case 0:
			return 1;
		case 1:
			return 2;
		case 2:
			return 5;
		case 3:
			return 10;
		case 4:
			return 15;
		case 5:
			return 30;
		case 6:
			return 45;
		case 7:
			return 60;
		}
		return -1;
	}

	public void setFileProgress(long transferredSize, long totalSize, int transferredFiles, int totalFiles) {
		window.setProgress(transferredSize / (float) totalSize);
		window.setProgress2(transferredFiles / (float) totalFiles);
	}
	
	public void setStatus(String status, Color color) {
		window.setStatus(status, color);
	}
	
	public void exit() {
		autoSync = false;
		robocopy.stop();
		window.dispose();
		tray.remove();
	}
	
	public void trayClicked() {
		window.showWindow();
	}
	
	public void setIcon(int n) {
		tray.setIcon(n);
	}
}
