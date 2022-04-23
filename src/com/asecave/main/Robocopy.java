package com.asecave.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class Robocopy {
	
	private Runnable runnable;
	private boolean fastSync;

	public Robocopy(JanaMirror jana) {
		
		runnable = () -> {
			String sourceDir = jana.getConfig().get("sourceDir");
			String targetDir = jana.getConfig().get("targetDir");
			String listCommand = "cmd /c robocopy.exe \"" + sourceDir + "\" \"" + targetDir + "\" /MIR /NJH /NJS /NP /NC /NDL /BYTES /L";
			String mirCommand = "cmd /c robocopy.exe \"" + sourceDir + "\" \"" + targetDir + "\" /MIR /NJH /NJS /NC /NDL /BYTES " + (fastSync ? "" : "/IPG:1");
			jana.log("Mirror command: " + mirCommand);
			try {
				Process p = Runtime.getRuntime().exec(listCommand);
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				long totalSize = 0;
				int totalFiles = 0;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					line = line.split("\t")[0];
					if (line.length() > 0) {
						totalSize += Long.parseLong(line);
						totalFiles++;
					}
				}
				if (totalSize == 0) {
					jana.log("No changes found");
					return;
				}
				DecimalFormat df = new DecimalFormat("###,###,###");
				jana.log("Found " + df.format(totalSize / 1000) + " KB of changed files");
				p = Runtime.getRuntime().exec(mirCommand);
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				line = null;
				long transferred = 0;
				int transferredFiles = 0;
				while ((line = br.readLine()) != null) {
					jana.log(line);
					line = line.trim();
					String[] parts = line.split("\t");
					if (parts.length == 2) {
						transferred += Long.parseLong(parts[0]);
						transferredFiles++;
					} else if (parts.length == 3 && !parts[0].endsWith("%")) {
						transferred += Long.parseLong(parts[1]);
						transferredFiles++;
					}
					jana.setFileProgress(transferred, totalSize, transferredFiles, totalFiles);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}

	public void startMirror(boolean fastSync) {

		this.fastSync = fastSync;
		
		new Thread(runnable).start();
	}
}
