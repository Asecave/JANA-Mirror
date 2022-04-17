package com.asecave.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Robocopy {

	private JanaMirror jana;

	public Robocopy(JanaMirror jana) {

		this.jana = jana;

		startMirror();
	}

	public void startMirror() {

		String sourceDir = jana.getConfig().get("sourceDir");
		String targetDir = jana.getConfig().get("targetDir");
		String command = "cmd /c robocopy.exe \"" + sourceDir + "\" \"" + targetDir + "\" /MIR";
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				jana.log(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
