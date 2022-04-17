package com.asecave.main;

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

	public static void main(String[] args) {

		new JanaMirror();
	}

	public JanaMirror() {

		window = new Window(this);

		config = new Config(this);

		window.setSourceDirButtonName(new File(config.get("sourceDir")).getName());
		window.setTargetDirButtonName(new File(config.get("targetDir")).getName());

		robocopy = new Robocopy(this);
	}

	public void sourceButtonPressed() {

		window.setSuspendClose(true);
		try {
			Desktop.getDesktop().open(new File(config.get("sourceDir")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.setSuspendClose(false);
		}).start();
	}

	public void targetButtonPressed() {

		window.setSuspendClose(true);
		try {
			Desktop.getDesktop().open(new File(config.get("targetDir")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.setSuspendClose(false);
		}).start();
	}

	public void changeSourceButtonPressed() {

		window.setSuspendClose(true);
		String file = chooseFile(config.get("sourceDir"));
		if (file != null) {
			config.set("sourceDir", file);
			config.save();
			window.setSourceDirButtonName(new File(config.get("sourceDir")).getName());
		}
	}

	public void changeTargetButtonPressed() {

		window.setSuspendClose(true);
		String file = chooseFile(config.get("targetDir"));
		if (file != null) {
			config.set("targetDir", file);
			config.save();
			window.setTargetDirButtonName(new File(config.get("targetDir")).getName());
		}
	}

	public void log(String s) {

		System.out.println(s);
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
		window.append("[" + timeStamp + "] " + s);
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

		robocopy.startMirror();
	}

	public Config getConfig() {

		return config;
	}
}
